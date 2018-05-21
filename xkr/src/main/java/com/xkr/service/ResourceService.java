package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.XkrResourceAgent;
import com.xkr.domain.XkrResourceUserAgent;
import com.xkr.domain.XkrUserAgent;
import com.xkr.domain.dto.FileDownloadResponseDTO;
import com.xkr.domain.dto.ListResourceDTO;
import com.xkr.domain.dto.ResourceDTO;
import com.xkr.domain.dto.ResourceDetailDTO;
import com.xkr.domain.dto.file.FileInfoDTO;
import com.xkr.domain.dto.file.FolderItemDTO;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.dto.search.SearchResultListDTO;
import com.xkr.domain.entity.XkrClass;
import com.xkr.domain.entity.XkrResource;
import com.xkr.domain.entity.XkrResourceUser;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.SearchApiService;
import com.xkr.service.api.UpLoadApiService;
import main.java.com.upyun.UpException;
import main.java.com.upyun.UpYunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class ResourceService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private XkrClassAgent xkrClassAgent;

    @Autowired
    private XkrUserAgent xkrUserAgent;

    @Autowired
    private XkrResourceAgent xkrResourceAgent;

    @Autowired
    private XkrResourceUserAgent xkrResourceUserAgent;

    @Autowired
    private SearchApiService searchApiService;

    @Autowired
    private UpLoadApiService upLoadApiService;

    @Value("${upyun.opt.user}")
    private String optUser;

    @Value("${upyun.opt.password}")
    private String optPassword;

    public static final int ORDER_BY_UPDATE_TIME = 1;

    public static final int ORDER_BY_DOWNLOAD_COUNT = 2;

    public static final int USER_TYPE_UPLOAD = 1;

    public static final int USER_TYPE_DOWNLOAD = 2;

    public static final String EXT_MD5_UNCOMPRESS_FILE_KEY = "unCompressMd5";


    /**
     * 生成签名，放在Authorization头部
     * @param downloadUser
     * @param resourceId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public FileDownloadResponseDTO downloadResouce(XkrUser downloadUser, Long resourceId) {
        //资源检查1.是否存在,2是否异常
        XkrResource resource = xkrResourceAgent.getResourceById(resourceId);
        if (Objects.isNull(resource)) {
            logger.error("ResourceService downloadResouce resource not found,resouceId:{}", resourceId);
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_NOT_FOUND);
        }
        if (XkrResourceAgent.STATUS_NORMAL != resource.getStatus().intValue()) {
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_FREEZED);
        }

        //用户检查1.账户异常,
        if (XkrUserAgent.USER_STATUS_NORMAL != downloadUser.getStatus().intValue()) {
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_USER_FREEZED);
        }
        //下载鉴权,已购买直接生成token
        List<XkrResourceUser> payedList = xkrResourceUserAgent.getResourceByUserId(downloadUser.getId(), XkrResourceUserAgent.STATUS_PAYED);
        boolean alreadyPayed = payedList.stream().anyMatch(xkrResourceUser -> Objects.equals(xkrResourceUser.getResourceId(), resourceId));

        if (!alreadyPayed) {
            //用户检查2积分充足
            if (downloadUser.getWealth() - resource.getCost() < 0) {
                return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_PAY_FAILED);
            }

            if(!xkrUserAgent.dealUserPurchase(downloadUser,resource.getCost())){
                throw new RuntimeException("dealUserPruchase failed");
            }

            if(!xkrResourceUserAgent.saveNewPayRecord(downloadUser.getId(),resourceId)){
                throw new RuntimeException("saveNew userRelationShip failed");
            }

            //3更新资源库以及索引的下载数
            xkrResourceAgent.updateDownloadCountById(resourceId);

        }
        try {
            return new FileDownloadResponseDTO(UpYunUtils.sign("GET",
                    LocalDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                    String.format(UpLoadApiService.getCompressFilePathFormat(),resource.getUserId(),resource.getResourceUrl()),
                    optUser,optPassword,null));
        } catch (UpException e) {
            logger.error("ResourceService build response token failed",e);
            throw new RuntimeException("generate download token failed",e);
        }

        //生成下载token
    }

    /**
     * 资源上传保存
     *
     * @param title
     * @param detail
     * @param cost
     * @param classId
     * @param userId
     * @param compressMd5
     * @param unCompressMd5
     * @return
     */
    public XkrResource saveNewResource(String title, String detail, Integer cost, Long classId, Long userId,
                                       String compressMd5, String unCompressMd5) {
        String filePath = String.format(UpLoadApiService.getCompressFilePathFormat(), userId, compressMd5);
        try {
            FileInfoDTO fileInfoDTO = upLoadApiService.getFileInto(filePath);
            if (Objects.isNull(fileInfoDTO)) {
                logger.error("ResourceService saveNewResource failed : classId:{},userId:{},compressMd5:{},unCompressMd5:{},filePath:{}",
                        classId, userId, compressMd5, unCompressMd5, filePath);
                return null;
            }

            //解析分类
            XkrClass xkrClass = xkrClassAgent.getClassById(classId);
            if (Objects.isNull(xkrClass)) {
                logger.error("ResourceService saveNewResource classId not found, classId:{}", classId);
                return null;
            }

            XkrResource resource = xkrResourceAgent.saveNewResource(title, detail, cost, classId,
                    userId, compressMd5, unCompressMd5, fileInfoDTO.getSize());
            if (Objects.nonNull(resource)) {

                //todo 富文本过滤
                return resource;
            }
        } catch (IOException | UpException e) {
            logger.error("ResourceService saveNewResource failed : classId:{},userId:{},compressMd5:{},unCompressMd5:{},filePath:{}",
                    classId, userId, compressMd5, unCompressMd5, filePath);
        }
        return null;
    }


    /**
     * 获取资源目录列表
     *
     * @param resourceId
     * @param resUri
     * @return
     */
    public List<FolderItemDTO> getResourceMenuList(Long resourceId, String resUri) {
        List<FolderItemDTO> list = Lists.newArrayList();
        XkrResource resource = xkrResourceAgent.getResourceById(resourceId);
        if (Objects.isNull(resource)) {
            logger.error("ResourceService getResourceMenuList resource info is null : resId:{},resUri:{}", resourceId, resUri);
            return list;
        }
        String unCompressMd5 = JSONObject.parseObject(resource.getExt()).getString(EXT_MD5_UNCOMPRESS_FILE_KEY);
        if (StringUtils.isEmpty(resource)) {
            logger.error("ResourceService getResourceMenuList md5FileList is null : resource:{}", JSON.toJSONString(resource));
            return list;
        }
        String dicPath = String.format(UpLoadApiService.getUncompressFilePathFormat(), String.valueOf(resource.getUserId()), "/" + unCompressMd5 + resUri);
        return upLoadApiService.getDirInfo(dicPath);
    }

    /**
     * 获取资源详情
     *
     * @param resourceId
     * @return
     */
    public ResourceDetailDTO getResourceDetailById(Long resourceId) {
        if (Objects.isNull(resourceId)) {
            return null;
        }
        XkrResource xkrResource = xkrResourceAgent.getResourceById(resourceId);
        if (Objects.isNull(xkrResource)) {
            return null;
        }
        ResourceDetailDTO resourceDetailDTO = new ResourceDetailDTO();

        buildResourceDetailDTO(resourceDetailDTO, xkrResource);

        return resourceDetailDTO;
    }

    /**
     * 搜索资源关键词
     *
     * @param keyword
     * @param orderType
     * @param pageNum
     * @param size
     * @return
     */
    public ListResourceDTO getResourceBySearchKeyword(String keyword, int orderType,
                                                      int pageNum, int size) {
        // TODO: 2018/5/17 参数校验，日志
        ListResourceDTO result = new ListResourceDTO();

        //排序的索引键值
        String sortKey = orderType == ORDER_BY_DOWNLOAD_COUNT ? "downloadCount" : "updateTime";
        int offset = pageNum - 1 < 0 ? 0 : pageNum - 1;

        //搜索字段
        Map<String, Float> field = ImmutableMap.of(
                "title", 1F,
                "content", 1F
        );

        //高亮字段
        Set<String> highLight = field.keySet();

        //过滤条件
        Map<String, Object> filterMap = ImmutableMap.of(
                "status", XkrResourceAgent.STATUS_NORMAL
        );

        SearchResultListDTO<ResourceIndexDTO> searchResultListDTO = searchApiService.searchByKeyWordInField(
                ResourceIndexDTO.class, keyword, field, filterMap,
                null, null, sortKey, highLight, offset, size
        );

        result.setTotalCount((int) searchResultListDTO.getTotalCount());

        searchResultListDTO.getSearchResultDTO().forEach(resourceIndexDTO -> {
            ResourceDTO resourceDTO = new ResourceDTO();
            XkrClass xkrClass = xkrClassAgent.getClassById(resourceIndexDTO.getClassId());
            if(Objects.isNull(xkrClass)){
                throw new RuntimeException();
            }
            buildResourceDTO(resourceDTO,resourceIndexDTO,xkrClass);
            result.getResList().add(resourceDTO);
        });

        return result;

    }

    /**
     * 获取分类下所有资源
     *
     * @param rootClassId
     * @param orderType
     * @param pageNum
     * @param size
     * @param needSearch
     * @return
     */
    public ListResourceDTO getResourcesByClassIds(Long rootClassId, int orderType,
                                                  int pageNum, int size, boolean needSearch) {
        ListResourceDTO result = new ListResourceDTO();
        if (Objects.isNull(rootClassId)) {
            return result;
        }
        List<XkrClass> classList = xkrClassAgent.getAllChildClassByClassId(rootClassId);
        if (CollectionUtils.isEmpty(classList)) {
            return result;
        }

        if (needSearch) {
            buildResourceByClassIdsOnSearch(result, classList, orderType, pageNum, size);
        } else {
            buildResourceByClassIdsOnOffline(result, classList, orderType, pageNum, size);
        }
        return result;
    }

    /**
     * 获取用户下相关资源
     *
     * @param userId
     * @param userType
     * @param pageNum
     * @param size
     * @return
     */
    public ListResourceDTO getResourcesByUser(Long userId, int userType,
                                              int pageNum, int size) {
        ListResourceDTO result = new ListResourceDTO();
        if (Objects.isNull(userId) ||
                (userType != USER_TYPE_UPLOAD && userType != USER_TYPE_DOWNLOAD)) {
            return result;
        }
        List<XkrResource> list;
        Page page;

        //上传或者下载相关
        if (USER_TYPE_UPLOAD == userType) {
            page = PageHelper.startPage(pageNum, size, "update_time desc");
            list = xkrResourceAgent.getResourceByUserId(userId, XkrResourceAgent.STATUS_NORMAL);
        } else {
            List<XkrResourceUser> resourceUsers = xkrResourceUserAgent.getResourceByUserId(userId, XkrResourceUserAgent.STATUS_PAYED);
            if (CollectionUtils.isEmpty(resourceUsers)) {
                return result;
            }
            List<Long> resIds = resourceUsers.stream().map(XkrResourceUser::getResourceId).collect(Collectors.toList());
            page = PageHelper.startPage(pageNum, size, "update_time desc");
            list = xkrResourceAgent.getResourceListByIds(resIds);
        }
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }

        result.setTotalCount((int) page.getTotal());

        List<XkrClass> classList = xkrClassAgent.getClassByIds(list.stream().map(XkrResource::getClassId).collect(Collectors.toList()));

        list.forEach(xkrResource -> {
            ResourceDTO resourceDTO = new ResourceDTO();
            XkrClass classBean = classList.stream().
                    filter(xkrClass1 -> xkrClass1.getId().equals(xkrResource.getClassId())).findFirst().orElseThrow(RuntimeException::new);
            buildResourceDTO(resourceDTO, xkrResource,classBean);
            result.getResList().add(resourceDTO);
        });

        return result;
    }

    public boolean reportResource(Long resourceId){
        return xkrResourceAgent.reportIllegalResource(resourceId);
    }


    private void buildResourceByClassIdsOnSearch(ListResourceDTO result, List<XkrClass> classList, int orderType,
                                                 int pageNum, int size) {
        //排序的索引键值
        String sortKey = orderType == ORDER_BY_DOWNLOAD_COUNT ? "downloadCount" : "updateTime";
        int offset = pageNum - 1 < 0 ? 0 : pageNum - 1;

        List<Long> classIds = classList.stream().map(XkrClass::getId).collect(Collectors.toList());

        Map<String, Object> filterMap = ImmutableMap.of(
                "classId", classIds,
                "status", XkrResourceAgent.STATUS_NORMAL
        );

        SearchResultListDTO<ResourceIndexDTO> searchResultListDTO = searchApiService.searchByFilterField(ResourceIndexDTO.class,
                filterMap, null, null, sortKey, offset, size
        );

        if (Objects.isNull(searchResultListDTO)) {
            return;
        }


        searchResultListDTO.getSearchResultDTO().forEach(resourceIndexDTO -> {
            ResourceDTO resourceDTO = new ResourceDTO();
            XkrClass classBean = classList.stream().
                    filter(xkrClass1 -> xkrClass1.getId().equals(resourceIndexDTO.getClassId())).findFirst().orElseThrow(RuntimeException::new);
            buildResourceDTO(resourceDTO,resourceIndexDTO,classBean);
            result.getResList().add(resourceDTO);
        });

        result.setTotalCount((int) searchResultListDTO.getTotalCount());

    }

    private void buildResourceByClassIdsOnOffline(ListResourceDTO result, List<XkrClass> classList, int orderType,
                                                  int pageNum, int size) {
        //排序的索引键值
        String sortKey = orderType == ORDER_BY_DOWNLOAD_COUNT ? "update_time" : "download_count";
        Page page = PageHelper.startPage(pageNum, size, sortKey + " desc");

        List<Long> classIds = classList.stream().map(XkrClass::getId).collect(Collectors.toList());

        //获取分类下所有资源
        List<XkrResource> resources = xkrResourceAgent.getResourceListByClassIds(classIds);

        result.setTotalCount((int) page.getTotal());

        resources.forEach(xkrResource -> {
            ResourceDTO resourceDTO = new ResourceDTO();
            XkrClass classBean = classList.stream().
                    filter(xkrClass1 -> xkrClass1.getId().equals(xkrResource.getClassId())).findFirst().orElseThrow(RuntimeException::new);
            buildResourceDTO(resourceDTO, xkrResource, classBean);
            result.getResList().add(resourceDTO);
        });


    }

    private void buildResourceDTO(ResourceDTO resourceDTO,ResourceIndexDTO resourceIndexDTO,XkrClass currentClass){
        String[] paths = currentClass.getPath().split("-");
        XkrClass rootClass = paths.length > 1 ?
                xkrClassAgent.getClassById(Long.valueOf(paths[1])) : currentClass;
        rootClass = Objects.isNull(rootClass) ? currentClass : rootClass;
        BeanUtils.copyProperties(resourceIndexDTO,resourceDTO);
        resourceDTO.setClassId(currentClass.getId());
        resourceDTO.setClassName(currentClass.getClassName());
        resourceDTO.setRootClassId(rootClass.getId());
        resourceDTO.setRootClassName(rootClass.getClassName());

    }

    private void buildResourceDTO(ResourceDTO resourceDTO,XkrResource resource,XkrClass currentClass){
        String[] paths = currentClass.getPath().split("-");
        XkrClass rootClass = paths.length > 1 ?
                xkrClassAgent.getClassById(Long.valueOf(paths[1])) : currentClass;
        rootClass = Objects.isNull(rootClass) ? currentClass : rootClass;
        XkrUser user = xkrUserAgent.getUserById(resource.getUserId());
        if (Objects.isNull(user)) {
            resourceDTO.setUserName("未知账号");
        } else {
            resourceDTO.setUserName(user.getUserName());
        }
        resourceDTO.setUserId(resource.getUserId());
        resourceDTO.setClassId(currentClass.getId());
        resourceDTO.setClassName(currentClass.getClassName());
        resourceDTO.setRootClassId(rootClass.getId());
        resourceDTO.setRootClassName(rootClass.getClassName());
        resourceDTO.setResourceId(resource.getId());
        resourceDTO.setReport(Integer.valueOf(resource.getReport()));
        resourceDTO.setDownloadCount(resource.getDownloadCount());
        resourceDTO.setCost(resource.getCost());
        resourceDTO.setContent(resource.getDetail());
        resourceDTO.setStatus(Integer.valueOf(resource.getStatus()));
        resourceDTO.setTitle(resource.getTitle());
        resourceDTO.setUpdateTime(resource.getUpdateTime());

    }



    private void buildResourceDetailDTO(ResourceDetailDTO detailDTO, XkrResource resource) {
        BeanUtils.copyProperties(resource, detailDTO);

        XkrClass currentClass = xkrClassAgent.getClassById(resource.getClassId());
        if (Objects.nonNull(currentClass)) {
            List<Long> parentClassIds = Arrays.stream(currentClass.getPath().split("-")).
                    map(Long::valueOf).collect(Collectors.toList());
            List<XkrClass> parentClasses = xkrClassAgent.getClassByIds(parentClassIds);

            detailDTO.buildParentClass(currentClass, parentClasses);
        }


        XkrUser user = xkrUserAgent.getUserById(resource.getUserId());
        if (Objects.isNull(user)) {
            detailDTO.setUserName("未知账号");
        } else {
            detailDTO.setUserName(user.getUserName());
        }
    }


}
