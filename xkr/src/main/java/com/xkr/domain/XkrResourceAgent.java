package com.xkr.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hengyi.dzfilter.utils.TextUtils;
import com.xkr.core.IdGenerator;
import com.xkr.dao.cache.RemedyCacheLoader;
import com.xkr.dao.mapper.XkrResourceMapper;
import com.xkr.domain.dto.resource.ResourceStatusEnum;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.ResourceService;
import com.xkr.service.api.SearchApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
                searchApiService.getAndBuildIndexDTOByIndexId(resourceIndexDTO, String.valueOf(resource.getId()));
                resourceIndexDTO.setDownloadCount(notification.getValue());
                searchApiService.upsertIndex(resourceIndexDTO);
            })
            .build(new RemedyCacheLoader<Long, Integer>() {
                @Override
                public Integer load(Long key) throws Exception {
                    XkrResource resource = xkrResourceMapper.getResourceById(ImmutableMap.of(
                            "id",key,
                            "statuses", ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList())
                    ));
                    return Objects.isNull(resource) ? 0 : resource.getDownloadCount();
                }
            });

    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateResourceClassByIds(List<Long> resourceIds,Long newClassId){
        if (Objects.isNull(newClassId)) {
            return false;
        }
        if(CollectionUtils.isEmpty(resourceIds)){
            return true;
        }
        boolean isSuccess = xkrResourceMapper.batchUpdateResourceClassByIds(ImmutableMap.of(
                "list",resourceIds,"classId",newClassId
        )) > 0;
        if(isSuccess){
            Map<String,Object> map = Maps.newHashMap();
            map.put("classId",newClassId);
            map.put("updateTime",new Date().getTime());
            if (!searchApiService.bulkUpdateIndex("resource", resourceIds, map)) {
                logger.error("XkrResourceAgent batchUpdateResourceClassByIds failed ,resourceIds:{},newClassId:{}", JSON.toJSONString(resourceIds),newClassId);
                throw new RuntimeException();
            }
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean batchPhysicDeleteResourceByIds(List<Long> resourceIds){
        if (CollectionUtils.isEmpty(resourceIds) ) {
            return false;
        }
        logger.info("XkrResourceAgent batchPhysicDeleteResourceByIds params:{}",JSON.toJSONString(resourceIds));
        boolean isSuccess = xkrResourceMapper.batchDeleteResourceByIds(resourceIds) == 1;
        if(isSuccess){
            if (!searchApiService.bulkDeleteIndexStatus("resource", resourceIds)) {
                logger.error("XkrResourceAgent bulkDeleteIndexStatus failed ,resourceIds:{}", JSON.toJSONString(resourceIds));
                throw new RuntimeException();
            }
        }
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateResourceByIds(List<Long> resourceIds, ResourceStatusEnum status) {
        if (CollectionUtils.isEmpty(resourceIds) || Objects.isNull(status)) {
            return false;
        }
        boolean isSuccess = false;
        if (ResourceStatusEnum.TOUPDATE_STATUSED.contains(status)) {
            isSuccess = xkrResourceMapper.batchUpdateResourceByIds(ImmutableMap.of(
                    "list", resourceIds, "status", status.getCode()
            )) > 0;
        }
        if (isSuccess) {
            if (!searchApiService.bulkUpdateIndexStatus("resource", resourceIds, status.getCode())) {
                logger.error("XkrResourceAgent batchUpdateResourceByIds failed ,resourceIds:{},status:{}", JSON.toJSONString(resourceIds),status);
                throw new RuntimeException();
            }
        }
        return isSuccess;

    }

    @Transactional(rollbackFor = Exception.class)
    public XkrResource saveNewResource(String title, String detail, Integer cost, XkrClass xkrClass, Long userId,
                                       String cp, String fileSize,String up) {
        if(Objects.isNull(xkrClass)){
            return null;
        }
        //做敏感词过滤
//        title = TextUtils.filter(title);
//        detail = TextUtils.filter(detail);
        //获取资源分类：毕设或者资源
        Integer resType = Integer.valueOf(xkrClass.getPath().split("-")[1]);
        Long resourceId = idGenerator.generateId();
        XkrResource resource = new XkrResource();
        resource.setClassId(xkrClass.getId());
        resource.setCost(cost);
        resource.setTitle(title);
        resource.setDetail(detail);
        resource.setFileSize(fileSize);
        resource.setId(resourceId);
        resource.setReport((byte) REPORT_NORMAL);
        resource.setStatus((byte) ResourceStatusEnum.STATUS_UNVERIFIED.getCode());
        resource.setUserId(userId);
        JSONObject ext = new JSONObject();
        ext.put(ResourceService.EXT_FILE_NAME_KEY,up);
        resource.setExt(ext.toJSONString());
        resource.setResourceUrl(cp);
        resource.setDownloadCount(0);
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        if (xkrResourceMapper.insertSelective(resource) == 1) {

            //创建资源索引
            ResourceIndexDTO resourceIndexDTO = new ResourceIndexDTO();

            buildResourceIndexDTO(resourceIndexDTO, resource,resType);

            if (!searchApiService.upsertIndex(resourceIndexDTO)) {
                logger.error("ResourceService saveNewResource buildIndex failed, resourceIndexDTO:{}", JSON.toJSONString(resourceIndexDTO));
                throw new RuntimeException();
            }

            return resource;
        }

        return null;
    }

    public List<XkrResource> searchByFilter(Date startDate,String keyWord,Integer status,Integer report,List<Long> classIds) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("startDate",startDate);
        map.put("keyWord",keyWord);
        map.put("status",status);
        map.put("report",report);
        map.put("classIds",classIds);
        return xkrResourceMapper.searchByFilter(map);
    }


    public XkrResource getResourceById(Long resId,List<Integer> statuses) {
        if (Objects.isNull(resId)) {
            return null;
        }
        return xkrResourceMapper.getResourceById(ImmutableMap.of(
                "id",resId,"statuses",statuses
        ));
    }

    public XkrResource getResourceById(Long resId) {
                return getResourceById(resId,ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
    }

    public List<XkrResource> getResourceListByIds(List<Long> resIds,List<Integer> statuses) {
        if (CollectionUtils.isEmpty(resIds)) {
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "statuses",statuses,
                "ids", resIds
        );
        return xkrResourceMapper.getResourceByIds(params);

    }

    public List<XkrResource> getResourceListByIds(List<Long> resIds) {
        if(CollectionUtils.isEmpty(resIds)){
            return Lists.newArrayList();
        }
        return getResourceListByIds(resIds,ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));

    }

    public Integer getResourceTotal(){
        return xkrResourceMapper.getTotalResource(ImmutableMap.of("statuses",ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList())));
    }

    public Integer getResourceTotal(List<ResourceStatusEnum> resourceStatusEnums,Integer classId){
        if(Objects.isNull(classId) || CollectionUtils.isEmpty(resourceStatusEnums)){
            return null;
        }
        return xkrResourceMapper.getTotalResource(ImmutableMap.of(
                "statuses",ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()),
                "classIds", ImmutableList.of(classId))
        );
    }

    public List<XkrResource> getResourceListByClassIds(List<Long> classIds,List<Integer> statuses) {
        if (CollectionUtils.isEmpty(classIds)) {
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "statuses", statuses,
                "classIds", classIds
        );
        return xkrResourceMapper.getResourceByClassIds(params);

    }

    public List<XkrResource> getResourceByUserId(Long userId,List<Integer> statuses) {
        if (Objects.isNull(userId)) {
            return Lists.newArrayList();
        }
        Map<String, Object> params = ImmutableMap.of(
                "statuses", statuses,
                "userId", userId
        );
        return xkrResourceMapper.getResourceByUserId(params);
    }

    public boolean updateDownloadCountById(Long resourceId) {
        if (Objects.isNull(resourceId)) {
            return false;
        }
        try {
            downloadCountCache.put(resourceId, downloadCountCache.get(resourceId) + 1);
            return true;
        } catch (ExecutionException e) {
            logger.error("XkrResourceAgent updateDownloadCountById error on loading cache ,will use oldValue ,resourceId:{}", resourceId, e);
        }
        return false;
    }

    public boolean reportIllegalResource(Long resourceId) {
        if (Objects.isNull(resourceId)) {
            return false;
        }
        XkrResource resource = new XkrResource();
        resource.setId(resourceId);
        resource.setReport((byte) REPORT_INVALID);
        if (xkrResourceMapper.updateByPrimaryKeySelective(resource) == 1) {
            //更新索引
            ResourceIndexDTO resourceIndexDTO = new ResourceIndexDTO();
            searchApiService.getAndBuildIndexDTOByIndexId(resourceIndexDTO, String.valueOf(resource.getId()));
            resourceIndexDTO.setReport(REPORT_INVALID);
            searchApiService.upsertIndex(resourceIndexDTO);

            return true;
        }
        return false;
    }

    private void buildResourceIndexDTO(ResourceIndexDTO indexDTO, XkrResource resource,int resType) {
        indexDTO.setClassId(resource.getClassId());
        //对内容进行html标签过滤
        indexDTO.setContent(TextUtils.delHtmlTag(resource.getDetail()));
        indexDTO.setCost(resource.getCost());
        indexDTO.setDownloadCount(resource.getDownloadCount());
        indexDTO.setReport((int) resource.getReport());
        indexDTO.setResourceId(resource.getId());
        indexDTO.setStatus((int) resource.getStatus());
        indexDTO.setTitle(resource.getTitle());
        indexDTO.setUpdateTime(resource.getUpdateTime());
        indexDTO.setDownloadCount(resource.getDownloadCount());
        indexDTO.setUserId(resource.getUserId());
        indexDTO.setType(resType);
        XkrUser user = xkrUserAgent.getUserById(resource.getUserId());
        if (Objects.isNull(user)) {
            indexDTO.setUserName("未知账号");
        } else {
            indexDTO.setUserName(user.getUserName());
        }
    }


}
