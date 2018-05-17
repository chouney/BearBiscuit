package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.core.IdGenerator;
import com.xkr.dao.cache.RemedyCacheLoader;
import com.xkr.dao.mapper.XkrResourceMapper;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.ResourceService;
import com.xkr.service.api.SearchApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class XkrResourceAgent {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrResourceMapper xkrResourceMapper;

    @Autowired
    private SearchApiService searchApiService;

    @Autowired
    private XkrUserAgent xkrUserAgent;

    @Autowired
    private IdGenerator idGenerator;

    public static final int REPORT_NORMAL = 0;

    public static final int REPORT_INVALID = 1;

    public static final int STATUS_NORMAL = 1;

    public static final int STATUS_UNVERIFIED = 2;

    public static final int STATUS_FREEZED = 3;

    public static final int STATUS_DELETED = 4;

    //下载量缓存
    private LoadingCache<Long, Integer> downloadCountCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            //异步更新资源下载量
            .removalListener((RemovalListener<Long, Integer>) notification -> {
                //更新本地
                XkrResource resource = new XkrResource();
                resource.setId(notification.getKey());
                resource.setDownloadCount(notification.getValue());
                resource.setUpdateTime(new Date());
                xkrResourceMapper.updateByPrimaryKeySelective(resource);
                //更新索引
                ResourceIndexDTO resourceIndexDTO = new ResourceIndexDTO();
                searchApiService.getAndBuildIndexDTOByIndexId(resourceIndexDTO,resourceIndexDTO.getIndexName(),
                        resourceIndexDTO.getTypeName(),resourceIndexDTO.getIndexKey());
                resourceIndexDTO.setDownloadCount(notification.getValue());
                searchApiService.upsertIndex(resourceIndexDTO);
            })
            .build(new RemedyCacheLoader<Long, Integer>() {
                @Override
                public Integer load(Long key) throws Exception {
                    XkrResource resource = xkrResourceMapper.selectByPrimaryKey(key);
                    return Objects.isNull(resource) ? 0 : resource.getDownloadCount();
                }
            });

    public XkrResource saveNewResource(String title, String deital, Integer cost, Long classId, Long userId,
                                       String compressMd5, String unCompressMd5, String fileSize) {
        Long resourceId = idGenerator.generateId();
        XkrResource resource = new XkrResource();
        resource.setClassId(classId);
        resource.setCost(cost);
        resource.setTitle(title);
        resource.setDetail(deital);
        resource.setFileSize(fileSize);
        resource.setId(resourceId);
        resource.setReport((byte) REPORT_NORMAL);
        resource.setStatus((byte) STATUS_UNVERIFIED);
        resource.setUserId(userId);
        JSONObject ext = new JSONObject();
        ext.put(ResourceService.EXT_MD5_UNCOMPRESS_FILE_KEY, unCompressMd5);
        resource.setExt(ext.toJSONString());
        resource.setResourceUrl(compressMd5);
        if (xkrResourceMapper.insert(resource) == 1) {

            //创建资源索引
            ResourceIndexDTO resourceIndexDTO = new ResourceIndexDTO();

            buildResourceIndexDTO(resourceIndexDTO, resource);

            if (!searchApiService.upsertIndex(resourceIndexDTO)) {
                logger.warn("ResourceService saveNewResource buildIndex failed, resourceIndexDTO:{}", JSON.toJSONString(resourceIndexDTO));
            }

            return resource;
        }

        return null;
    }

    public XkrResource getResourceById(Long resId) {
        if (Objects.isNull(resId)) {
            return null;
        }
        return xkrResourceMapper.selectByPrimaryKey(resId);
    }

    public List<XkrResource> getResourceListByIds(List<Long> resIds) {
        if (CollectionUtils.isEmpty(resIds)) {
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "status", STATUS_NORMAL,
                "ids", resIds
        );
        return xkrResourceMapper.getResourceByIds(params);

    }


    public List<XkrResource> getResourceListByClassIds(List<Long> classIds) {
        if (CollectionUtils.isEmpty(classIds)) {
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "status", STATUS_NORMAL,
                "classIds", classIds
        );
        return xkrResourceMapper.getResourceByClassIds(params);

    }

    public List<XkrResource> getResourceByUserId(Long userId, int status) {
        if (Objects.isNull(userId)) {
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "status", status,
                "userId", userId
        );
        return xkrResourceMapper.getResourceByUserId(params);
    }

    public boolean updateDownloadCountById(Long resourceId){
        if (Objects.isNull(resourceId)) {
            return false;
        }
        try {
            downloadCountCache.put(resourceId,downloadCountCache.get(resourceId)+1);
            return true;
        } catch (ExecutionException e) {
            logger.error("XkrResourceAgent updateDownloadCountById error on loading cache ,will use oldValue ,resourceId:{}",resourceId,e);
        }
        return false;
    }

    public boolean reportIllegalResource(Long resourceId){
        if(Objects.isNull(resourceId)){
            return false;
        }
        XkrResource resource = new XkrResource();
        resource.setId(resourceId);
        resource.setReport((byte) REPORT_INVALID);
        if(xkrResourceMapper.updateByPrimaryKeySelective(resource) == 1){
            //更新索引
            ResourceIndexDTO resourceIndexDTO = new ResourceIndexDTO();
            searchApiService.getAndBuildIndexDTOByIndexId(resourceIndexDTO,resourceIndexDTO.getIndexName(),
                    resourceIndexDTO.getTypeName(),resourceIndexDTO.getIndexKey());
            resourceIndexDTO.setReport(REPORT_INVALID);
            searchApiService.upsertIndex(resourceIndexDTO);

            return true;
        }
        return false;
    }

    private void buildResourceIndexDTO(ResourceIndexDTO indexDTO, XkrResource resource) {
        indexDTO.setClassId(resource.getClassId());
        indexDTO.setContent(resource.getDetail());
        indexDTO.setCost(resource.getCost());
        indexDTO.setDownloadCount(resource.getDownloadCount());
        indexDTO.setReport((int) resource.getReport());
        indexDTO.setResourceId(resource.getId());
        indexDTO.setStatus((int) resource.getStatus());
        indexDTO.setTitle(resource.getTitle());
        indexDTO.setUpdateTime(resource.getUpdateTime());
        indexDTO.setUserId(resource.getUserId());
        XkrUser user = xkrUserAgent.getUserById(resource.getUserId());
        if (Objects.isNull(user)) {
            indexDTO.setUserName("未知账号");
        } else {
            indexDTO.setUserName(user.getUserName());
        }
    }


}
