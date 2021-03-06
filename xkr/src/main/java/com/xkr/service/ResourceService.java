package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.SqlUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.common.ErrorStatus;
import com.xkr.common.LoginEnum;
import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;
import com.xkr.common.annotation.OptLog;
import com.xkr.dao.cache.AdminIndexRedisService;
import com.xkr.dao.mapper.XkrResourceMapper;
import com.xkr.domain.*;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.file.FileDownloadResponseDTO;
import com.xkr.domain.dto.file.FileInfoDTO;
import com.xkr.domain.dto.file.FolderItemDTO;
import com.xkr.domain.dto.resource.*;
import com.xkr.domain.dto.search.ResourceIndexDTO;
import com.xkr.domain.dto.search.SearchResultListDTO;
import com.xkr.domain.dto.user.UserStatusEnum;
import com.xkr.domain.entity.*;
import com.xkr.service.api.SearchApiService;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.util.DateUtil;
import main.java.com.upyun.UpException;
import main.java.com.upyun.UpYunUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
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
    private XkrResourceMapper xkrResourceMapper;

    @Autowired
    private XkrClassAgent xkrClassAgent;

    @Autowired
    private XkrUserAgent xkrUserAgent;

    @Autowired
    private XkrResourceAgent xkrResourceAgent;

    @Autowired
    private XkrAdminRecycleAgent xkrAdminRecycleAgent;

    @Autowired
    private XkrResourceUserAgent xkrResourceUserAgent;

    @Autowired
    private SearchApiService searchApiService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UpLoadApiService upLoadApiService;

    @Autowired
    private AdminIndexRedisService adminIndexRedisService;

    @Value("${upyun.opt.user}")
    private String optUser;

    @Value("${upyun.opt.password}")
    private String optPassword;

    @Value("${upyun.bucket.file}")
    private String fileBucket;

    public static final int ORDER_BY_UPDATE_TIME = 1;

    public static final int ORDER_BY_DOWNLOAD_COUNT = 2;

    public static final int USER_TYPE_UPLOAD = 1;

    public static final int USER_TYPE_DOWNLOAD = 2;

    public static final String EXT_MD5_UNCOMPRESS_FILE_KEY = "unCompressMd5";

    public static final String EXT_FILE_NAME_KEY = "fileName";
    public static final String EXT_FILE_MENU_KEY = "fileMenu";


    /**
     * ------------------- 管理员服务 ----------------------
     */
    /**
     * 资源搜索
     *
     * @param keyword
     * @param startDate
     * @param endDate
     * @param resType
     * @param status
     * @param report
     * @param pageNum
     * @param size
     * @return
     */
    public ListResourceDTO getResourceSearchByAdmin(String keyword, Date startDate,
                                                    Date endDate, Integer resType,
                                                    String userName, ResourceStatusEnum status,
                                                    Integer report, int pageNum, int size) {
        ListResourceDTO result = new ListResourceDTO();
        if (StringUtils.isEmpty(keyword) && Objects.isNull(startDate) && Objects.isNull(endDate) &&
                Objects.isNull(resType) && Objects.isNull(report) && Objects.isNull(status) && StringUtils.isEmpty(userName)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        size = size <= 0 ? 10 : size;
        int offset = pageNum - 1 < 0 ? 0 : (pageNum - 1) * size;

        List<XkrResource> resultList = null;
        Integer classType = XkrClassAgent.ROOT_DESIGN_CLASS_ID == resType ?
                XkrClassAgent.ROOT_DESIGN_ACTUAL_CLASS_ID :
                XkrClassAgent.ROOT_RESOURCE_ACTUAL_CLASS_ID;


        List<XkrClass> classList = xkrClassAgent.getAllChildClassByClassId(Long.valueOf(classType));
        List<Long> classIds = classList.stream().map(XkrClass::getId).collect(Collectors.toList());

        //排序的索引键值
        String sortKey = "update_time";
        Page page = PageHelper.startPage(pageNum, size, sortKey + " desc");
        //全部走sql接口
        resultList = xkrResourceAgent.searchByFilter(startDate, keyword, status.getCode(), report, classIds, userName);

        result.setTotalCount((int) page.getTotal());

        SqlUtil.clearLocalPage();

        buildResourceByFilterOnOffline(result, resultList);


        /** ea search逻辑 **/
//        Map<String, Object> filterMap = Maps.newHashMap();
//        filterMap.put("type", resType);
//        filterMap.put("report", report);
//        filterMap.put("status", status.getCode());
//        SearchResultListDTO<ResourceIndexDTO> resultListDTO = null;
//        if (StringUtils.isEmpty(keyword)) {
//            resultListDTO = searchApiService.searchByFilterField(ResourceIndexDTO.class, filterMap, Pair.of(startDate, endDate),
//                    "updateTime", null, offset, size);
//        } else {
//            resultListDTO = searchApiService.searchByKeyWordInField(ResourceIndexDTO.class,
//                    keyword, ImmutableMap.of("title", 0.5F, "content", 1.5F, "userName", 0.5F),
//                    filterMap, Pair.of(startDate, endDate), "updateTime", null, null, offset, size);
//        }
//        List<Long> classIds = resultListDTO.getSearchResultDTO().stream().map(ResourceIndexDTO::getClassId).collect(Collectors.toList());

//        List<XkrClass> xkrClasses = xkrClassAgent.getClassByIds(classIds);

//        buildListResourceDTO(result, xkrClasses, resultListDTO);
        return result;
    }

    /**
     * 批量操作资源
     *
     * @param resourceIds
     * @param resourceStatusEnum
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.RESOURCE, optEnum = OptEnum.UPDATE)
    public ResponseDTO<Boolean> batchUpdateResourceStatus(List<Long> resourceIds, ResourceStatusEnum resourceStatusEnum) {
        if (CollectionUtils.isEmpty(resourceIds) || Objects.isNull(resourceStatusEnum)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrAdminAccount adminAccount = (XkrAdminAccount) SecurityUtils.getSubject().getPrincipal();
        List<XkrResource> resources = xkrResourceAgent.getResourceListByIds(resourceIds, ResourceStatusEnum.ALL_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));

        Boolean success = xkrResourceAgent.batchUpdateResourceByIds(resourceIds, resourceStatusEnum);
        if (success) {
            try {
                //给用户发送消息
                Map<Long, String> userContentMapper = Maps.newHashMap();
                resources.forEach(resource ->
                        userContentMapper.put(resource.getUserId(),
                                String.format(MessageService.RESOURCE_TEMPLATE, resource.getTitle(), resourceStatusEnum.getDesc())));
                messageService.batchSaveMessageToUser(LoginEnum.ADMIN, adminAccount.getId(), userContentMapper);
            } catch (Exception e) {
                logger.error("发送消息异常,已忽略,resourceIds:{}", JSON.toJSONString(resourceIds), e);
            }
        }
        return new ResponseDTO<>(success);
    }


    /**
     * 批量删除资源
     *
     * @param resourceIds
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.RESOURCE, optEnum = OptEnum.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> batchDeleteResource(List<Long> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrAdminAccount adminAccount = (XkrAdminAccount) SecurityUtils.getSubject().getPrincipal();
        List<XkrResource> resources = xkrResourceAgent.getResourceListByIds(resourceIds);

        Boolean success = xkrResourceAgent.batchUpdateResourceByIds(resourceIds, ResourceStatusEnum.STATUS_DELETED);

        if (success) {
            try {
                //给用户发送消息
                Map<Long, String> userContentMapper = Maps.newHashMap();
                resources.forEach(resource -> {
                    XkrClass xkrClass = xkrClassAgent.getClassById(resource.getClassId());
                    XkrUser user = xkrUserAgent.getUserById(resource.getUserId());
                    xkrAdminRecycleAgent.saveNewResourceRecycle(resource.getId(), resource.getTitle(),
                            xkrClass.getClassName(), user.getUserName(), adminAccount.getAccountName());
                    userContentMapper.put(resource.getUserId(),
                            String.format(MessageService.RESOURCE_TEMPLATE, resource.getTitle(), ResourceStatusEnum.STATUS_DELETED.getDesc()));
                });
                messageService.batchSaveMessageToUser(LoginEnum.ADMIN, adminAccount.getId(), userContentMapper);
            } catch (Exception e) {
                logger.error("发送消息异常,已忽略,resourceIds:{}", JSON.toJSONString(resourceIds), e);
            }
        }
        return new ResponseDTO<>(success);
    }

    /**
     * 获取回收站资源
     *
     * @param pageNum
     * @param size
     * @return
     */
    public ListResourceRecycleDTO getRecycleResourceList(int pageNum, int size) {
        ListResourceRecycleDTO listResourceRecycleDTO = new ListResourceRecycleDTO();
        pageNum = pageNum < 1 ? 1 : pageNum;
        size = size < 1 ? 10 : size;
        Page page = PageHelper.startPage(pageNum, size, "update_time desc");
        List<XkrResourceRecycle> xkrResourceRecycles = xkrAdminRecycleAgent.getAllListByPage();
        xkrResourceRecycles.forEach(xkrResourceRecycle -> {
            ResourceRecycleDTO resourceRecycleDTO = new ResourceRecycleDTO();
            buildResourceRecycleDTO(resourceRecycleDTO, xkrResourceRecycle);
            listResourceRecycleDTO.getList().add(resourceRecycleDTO);
        });
        listResourceRecycleDTO.setTotalCount((int) page.getTotal());
        return listResourceRecycleDTO;
    }

    /**
     * 批量还原回收站资源
     *
     * @param resourceIds
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.RESOURCE, optEnum = OptEnum.RENEW)
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> batchRenewRecycle(List<Long> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        XkrAdminAccount adminAccount = (XkrAdminAccount) SecurityUtils.getSubject().getPrincipal();
        List<XkrResource> resources = xkrResourceAgent.getResourceListByIds(resourceIds, ResourceStatusEnum.ALL_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));

        Boolean success = xkrResourceAgent.batchUpdateResourceByIds(resourceIds, ResourceStatusEnum.STATUS_NORMAL);
        if (success) {
            try {
                //给用户发送消息

                Map<Long, String> userContentMapper = Maps.newHashMap();
                resources.forEach(resource ->
                        userContentMapper.put(resource.getUserId(),
                                String.format(MessageService.RESOURCE_TEMPLATE, resource.getTitle(), ResourceStatusEnum.STATUS_NORMAL.getDesc())));
                messageService.batchSaveMessageToUser(LoginEnum.ADMIN, adminAccount.getId(), userContentMapper);
                //清除回收站资源
                success = xkrAdminRecycleAgent.batchDeleteResourceRecycleByIds(resourceIds);
            } catch (Exception e) {
                logger.error("发送消息异常,已忽略,resourceIds:{}", JSON.toJSONString(resourceIds), e);
            }
        }
        return new ResponseDTO<>(success);
    }

    /**
     * 批量清除回收站资源
     *
     * @param resourceIds
     * @return
     */
    @OptLog(moduleEnum = OptLogModuleEnum.RESOURCE, optEnum = OptEnum.CLEAR)
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> batchClearRecycle(List<Long> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return new ResponseDTO<>(ErrorStatus.PARAM_ERROR);
        }
        //清除回收站资源
        boolean success = xkrAdminRecycleAgent.batchDeleteResourceRecycleByIds(resourceIds);
        if (success) {

            //删除又拍云资源
            deleteUpyunResource(resourceIds);

            //清除资源
            xkrResourceAgent.batchPhysicDeleteResourceByIds(resourceIds);
        }

        return new ResponseDTO<>(success);
    }

    /**
     * ------------------- 管理员服务 ----------------------
     */
    /**
     * 生成签名，放在Authorization头部
     *
     * @param downloadResourceAdmin
     * @param resourceId
     */
    @Transactional(rollbackFor = Exception.class)
    public FileDownloadResponseDTO downloadResourceAdmin(Long resourceId) {
        //资源检查1.是否存在,2是否异常
        XkrResource resource = xkrResourceAgent.getResourceById(resourceId);
        if (Objects.isNull(resource)) {
            logger.error("ResourceService downloadResouce resource not found,resouceId:{}", resourceId);
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_NOT_FOUND);
        }

        //未审核的可以下载供管理员审查
        if (ResourceStatusEnum.STATUS_FREEZED.getCode() == resource.getStatus().intValue() ||
                ResourceStatusEnum.STATUS_DELETED.getCode() == resource.getStatus().intValue()) {
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_FREEZED);
        }

        //生成下载token
        try {
            JSONObject ext = JSON.parseObject(resource.getExt());
            String downloadUrl = resource.getResourceUrl();
            String date = DateUtil.getGMTRFCUSDate();
            if (!StringUtils.isEmpty(downloadUrl)) {
                String fileUri = downloadUrl.substring(0, downloadUrl.lastIndexOf("/"));
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                downloadUrl = fileUri + "/" + fileName;
            }
            return new FileDownloadResponseDTO(
//                    "deprecated",
                    UpYunUtils.sign("GET", date, downloadUrl, fileBucket, optUser, UpYunUtils.md5(optPassword), null),
                    "/" + fileBucket + downloadUrl, date);
        } catch (Exception e) {
            logger.error("ResourceService build response token failed", e);
            throw new RuntimeException("generate download token failed", e);
        }
    }


    /**
     * ------------------- 用户服务 ----------------------
     */
    /**
     * 生成签名，放在Authorization头部
     *
     * @param downloadUser
     * @param resourceId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public FileDownloadResponseDTO downloadResource(XkrUser downloadUser, Long resourceId) {
        //资源检查1.是否存在,2是否异常
        XkrResource resource = xkrResourceAgent.getResourceById(resourceId);
        if (Objects.isNull(resource)) {
            logger.error("ResourceService downloadResouce resource not found,resouceId:{}", resourceId);
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_NOT_FOUND);
        }

        //未审核的可以下载供管理员审查
        if (ResourceStatusEnum.STATUS_FREEZED.getCode() == resource.getStatus().intValue() ||
                ResourceStatusEnum.STATUS_DELETED.getCode() == resource.getStatus().intValue()) {
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_FREEZED);
        }

        //用户检查1.账户异常,
        if (UserStatusEnum.USER_STATUS_NORMAL.getCode() != downloadUser.getStatus().intValue()) {
            return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_USER_FREEZED);
        }
        //下载鉴权,已购买直接生成token
        List<XkrResourceUser> payedList = xkrResourceUserAgent.getResourceByResAndUserId(downloadUser.getId(), resourceId, XkrResourceUserAgent.STATUS_PAYED);
        boolean alreadyPayed = !CollectionUtils.isEmpty(payedList);

        if (!alreadyPayed) {
            //用户检查2积分充足
            if (downloadUser.getWealth() - resource.getCost() < 0) {
                return new FileDownloadResponseDTO(ErrorStatus.RESOURCE_PAY_FAILED);
            }

            if (!xkrUserAgent.dealUserPurchase(downloadUser, resource.getCost())) {
                throw new RuntimeException("dealUserPruchase failed");
            }

            if (!xkrResourceUserAgent.saveNewPayRecord(downloadUser.getId(), resourceId)) {
                throw new RuntimeException("saveNew userRelationShip failed");
            }

            //上传加相应积分,如果作者还在的话
            XkrUser xkrUser = xkrUserAgent.getUserById(resource.getUserId());
            if (!Objects.isNull(xkrUser)) {
                xkrUser.setWealth(xkrUser.getWealth() + resource.getCost());
                xkrUserAgent.updateUserByPKSelective(xkrUser);
            }

        }

        //3更新资源库以及索引的下载数
        xkrResourceAgent.updateDownloadCountById(resourceId);


        //生成下载token
        try {
            XkrClass xkrClass = xkrClassAgent.getClassById(resource.getClassId());
            if (Objects.nonNull(xkrClass)) {
                String[] paths = xkrClass.getPath().split("-");
                Integer classType = paths.length > 1 ? Integer.valueOf(paths[1]) : XkrClassAgent.ROOT_CLASS_ID;

                //增加下载数
                adminIndexRedisService.incDownLoadCount(classType);
            }


            JSONObject ext = JSON.parseObject(resource.getExt());
            String downloadUrl = resource.getResourceUrl();
            String date = DateUtil.getGMTRFCUSDate();
            if (!StringUtils.isEmpty(downloadUrl)) {
                String fileUri = downloadUrl.substring(0, downloadUrl.lastIndexOf("/"));
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                downloadUrl = fileUri + "/" + fileName;
            }
            return new FileDownloadResponseDTO(
//                    "deprecated",
                    UpYunUtils.sign("GET", date, downloadUrl, fileBucket, optUser, UpYunUtils.md5(optPassword), null),
                    "/" + fileBucket + downloadUrl, date);
        } catch (Exception e) {
            logger.error("ResourceService build response token failed", e);
            throw new RuntimeException("generate download token failed", e);
        }

    }

    /**
     * 资源上传保存
     *
     * @param title
     * @param detail
     * @param cost
     * @param clsId
     * @param userId
     * @param cp
     * @return
     */
    public ResponseDTO<Long> saveNewResource(String title, String detail, Integer cost, Long classId, Long userId,
                                             String cp, String up) {
//        String filePath = String.format(UpLoadApiService.getCompressFilePathFormat(), userId, cp, up);
        try {
            FileInfoDTO fileInfoDTO = upLoadApiService.getFileInfo(cp);
            if (Objects.isNull(fileInfoDTO)) {
                logger.error("ResourceService saveNewResource failed : classId:{},userId:{},cp:{},up:{}",
                        classId, userId, cp, up);
                return new ResponseDTO<>(ErrorStatus.RESOURCE_NOT_FOUND);
            }

            //解析分类
            XkrClass xkrClass = xkrClassAgent.getClassById(classId);
            if (Objects.isNull(xkrClass)) {
                logger.error("ResourceService saveNewResource classId not found, classId:{}", classId);
                return new ResponseDTO<>(ErrorStatus.ERROR);
            }

            XkrResource resource = xkrResourceAgent.saveNewResource(title, detail, cost, xkrClass,
                    userId, cp, fileInfoDTO.getSize(), up);
            if (Objects.nonNull(resource)) {

                String[] paths = xkrClass.getPath().split("-");
                Integer classType = paths.length > 1 ? Integer.valueOf(paths[1]) : XkrClassAgent.ROOT_CLASS_ID;

                //上传数+1
                adminIndexRedisService.incrUploadCount(classType);

                //上传者可免费下载
                xkrResourceUserAgent.saveNewPayRecord(userId, resource.getId());


                return new ResponseDTO<>(resource.getId());

            }
        } catch (IOException | UpException e) {
            logger.error("ResourceService saveNewResource failed : classId:{},userId:{},cp:{},up:{}",
                    classId, userId, cp, up);
        }
        return new ResponseDTO<>(ErrorStatus.ERROR);

    }


    //    @Async
    public void unCompressFile(String resId, String fileName) {
        //上传成功调用解压缩方法
        String sourcePath = fileName;
        String tarPath = sourcePath;
        try {
            int lastInd;
            if ((lastInd = sourcePath.lastIndexOf("/")) != -1) {
                sourcePath = sourcePath.substring(0, lastInd) + "/" + URLDecoder.decode(sourcePath.substring(lastInd + 1), "UTF-8");
            }
            int ind;
            if ((ind = sourcePath.lastIndexOf(".")) != -1) {
                tarPath = sourcePath.substring(0, ind);
            }

            upLoadApiService.unCompressDirSDK(resId, sourcePath, tarPath);
        } catch (UpException | IOException e) {
            logger.error("解压缩文件失败，需要重试,resId:{},fileName:{},error:", resId, fileName, e);
        }
    }

    /**
     * 保存目录
     *
     * @param resId
     */
    public ListResourceFolderDTO saveNewFileMenu(String resId) {
        ListResourceFolderDTO listResourceFolderDTO = getResourceMenuList(Long.valueOf(resId));
        if (!ErrorStatus.OK.equals(listResourceFolderDTO.getStatus())) {
            return listResourceFolderDTO;
        }
        XkrResource resource = xkrResourceAgent.getResourceById(Long.valueOf(resId));
        if (Objects.isNull(resource)) {
            listResourceFolderDTO.setStatus(ErrorStatus.RESOURCE_NOT_FOUND);
            return listResourceFolderDTO;
        }
        String ext = resource.getExt();
        JSONObject extJson = JSON.parseObject(ext);
        if (Objects.nonNull(extJson)) {
            String fileDir = extJson.getString(ResourceService.EXT_FILE_NAME_KEY);
            extJson.put(ResourceService.EXT_FILE_MENU_KEY, listResourceFolderDTO);
            resource.setExt(extJson.toJSONString());
            xkrResourceMapper.updateByPrimaryKeySelective(resource);
            try {
                //删除目录
                upLoadApiService.deleteFile(fileDir, true);
            } catch (IOException | UpException e) {
                logger.error("删除目录失败", e);
            }

        }

        return listResourceFolderDTO;
    }

    /**
     * 获取资源目录列表
     *
     * @param resUri
     * @return
     */
    private List<FolderItemDTO> getResourceMenuList(String resUri) {
        logger.info("ResourceService getResourceMenuList info resUri: {}", resUri);
        return upLoadApiService.getDirInfo(resUri);
    }

    /**
     * 获取资源目录全拓扑（只返回正常状态的）
     *
     * @param resourceId
     * @return
     */
    public ListResourceFolderDTO getResourceMenuList(Long resourceId) {
        ListResourceFolderDTO list = new ListResourceFolderDTO();
        XkrResource resource = xkrResourceAgent.getResourceById(resourceId, ResourceStatusEnum.NON_DELETE_STATUSED.stream().
                map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        if (Objects.isNull(resource)) {
            logger.error("ResourceService getResourceMenuList resource info is null : resId:{}", resourceId);
            list.setStatus(ErrorStatus.RESOURCE_NOT_FOUND);
            return list;
        }
        String sourcePath = resource.getResourceUrl();
        int ind;
        if (Objects.isNull(sourcePath) || (ind = sourcePath.lastIndexOf(".")) == -1) {
            logger.error("ResourceService getResourceMenuList resource info is null : resId:{}", resourceId);
            list.setStatus(ErrorStatus.RESOURCE_NOT_FOUND);
            return list;
        }
        final String rootUri = sourcePath.substring(0, ind);

//        String rootUri = JSONObject.parseObject(resource.getExt()).getString(EXT_FILE_NAME_KEY);
        List<FolderItemDTO> currentMenuList = getResourceMenuList(rootUri);
        currentMenuList.forEach(folderItemDTO -> {
            ResourceFolderDTO resourceFolderDTO = new ResourceFolderDTO();
            resourceFolderDTO.setName(folderItemDTO.getName());
            resourceFolderDTO.setDate(folderItemDTO.getDate());
            resourceFolderDTO.setSize(folderItemDTO.getSize());
            if (folderItemDTO.isFolder()) {
                String uri = rootUri + "/" + folderItemDTO.getName();
                resourceFolderDTO.setFileType("d");
                buildResourceSubFolder(resourceFolderDTO, uri);
            } else {
                resourceFolderDTO.setFileType("-");
            }
            list.getList().add(resourceFolderDTO);
        });
        return list;
    }

    private void buildResourceSubFolder(ResourceFolderDTO resourceFolderDTO, String currentUri) {
        if (Objects.nonNull(resourceFolderDTO) && "d".equals(resourceFolderDTO.getFileType())) {
            resourceFolderDTO.setSubFolders(Lists.newArrayList());
            List<FolderItemDTO> subList = getResourceMenuList(currentUri);
            subList.forEach(folderItemDTO -> {
                ResourceFolderDTO subFolder = new ResourceFolderDTO();
                subFolder.setName(folderItemDTO.getName());
                subFolder.setDate(folderItemDTO.getDate());
                subFolder.setSize(folderItemDTO.getSize());
                if (folderItemDTO.isFolder()) {
                    String uri = currentUri + "/" + folderItemDTO.getName();
                    subFolder.setFileType("d");
                    buildResourceSubFolder(subFolder, uri);
                } else {
                    subFolder.setFileType("-");
                }
                resourceFolderDTO.getSubFolders().add(subFolder);
            });
        }

    }

    /**
     * 获取资源详情(只返回正常状态的)
     *
     * @param resourceId
     * @return
     */
    public ResourceDetailDTO getResourceDetailById(Long resourceId) {
        ResourceDetailDTO result = new ResourceDetailDTO();
        if (Objects.isNull(resourceId)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        XkrResource xkrResource = xkrResourceAgent.getResourceById(resourceId,
                ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        if (Objects.isNull(xkrResource)) {
            result.setStatus(ErrorStatus.ERROR);
            return result;
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
        if (StringUtils.isEmpty(keyword) || (
                ORDER_BY_DOWNLOAD_COUNT != orderType &&
                        ORDER_BY_UPDATE_TIME != orderType)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }

        //排序的索引键值
        List<Pair<String, SortOrder>> sortKeys = Lists.newArrayList();
        if (orderType == ORDER_BY_DOWNLOAD_COUNT) {
            sortKeys.add(Pair.of("downloadCount", SortOrder.DESC));
            sortKeys.add(Pair.of("updateTime", SortOrder.DESC));
        } else {
            sortKeys.add(Pair.of("updateTime", SortOrder.DESC));
            sortKeys.add(Pair.of("downloadCount", SortOrder.DESC));
        }
        size = size <= 0 ? 10 : size;
        int offset = pageNum - 1 < 0 ? 0 : (pageNum - 1) * size;

        //搜索字段
        Map<String, Float> field = ImmutableMap.of(
                "title", 1F,
                "content", 1F
        );

        //高亮字段
        Set<String> highLight = field.keySet();

        //过滤条件
        Map<String, Object> filterMap = ImmutableMap.of(
                "status", Arrays.asList(ResourceStatusEnum.STATUS_NORMAL.getCode(), ResourceStatusEnum.STATUS_FREEZED.getCode())
        );

        SearchResultListDTO<ResourceIndexDTO> searchResultListDTO = searchApiService.searchByKeyWordInField(
                ResourceIndexDTO.class, keyword, field, filterMap,
                null, null, sortKeys, highLight, offset, size
        );

        List<Long> classIds = searchResultListDTO.getSearchResultDTO().stream().map(ResourceIndexDTO::getClassId).collect(Collectors.toList());

        List<XkrClass> classList = xkrClassAgent.getClassByIds(classIds);

        buildListResourceDTO(result, classList, searchResultListDTO);

        return result;

    }

    /**
     * 获取分类下所有资源(只返回正常资源)
     *
     * @param rootClassId
     * @param orderType
     * @param pageNum
     * @param size
     * @return
     */
    public ListResourceDTO getResourcesByClassId(Long rootClassId, int orderType,
                                                 int pageNum, int size) {
        ListResourceDTO result = new ListResourceDTO();
        if (Objects.isNull(rootClassId)) {
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        List<XkrClass> classList = xkrClassAgent.getAllChildClassByClassId(rootClassId);
        if (CollectionUtils.isEmpty(classList)) {
            return result;
        }

        try {
            buildResourceByClassIdsOnSearch(result, classList, orderType, pageNum, size);
        } catch (Exception e) {
            logger.error("ResourceService getResourcesByClassId buildResourceByClassIdsOnSearch failed searchEngine error,use offline", e);
            buildResourceByClassIdsOnOffline(result, classList, orderType, pageNum, size);
        }
        return result;
    }

    /**
     * 获取用户下相关资源(上传返回所有状态,下载只返回正常资源)
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
            result.setStatus(ErrorStatus.PARAM_ERROR);
            return result;
        }
        List<XkrResource> list;
        Page page;

        //上传或者下载相关
        if (USER_TYPE_UPLOAD == userType) {
            page = PageHelper.startPage(pageNum, size, "create_time desc");
            list = xkrResourceAgent.getResourceByUserId(userId, ResourceStatusEnum.NON_DELETE_STATUSED.stream().map(ResourceStatusEnum::getCode).collect(Collectors.toList()));
        } else {
            List<XkrResourceUser> resourceUsers = xkrResourceUserAgent.getResourceByUserId(userId, XkrResourceUserAgent.STATUS_PAYED);
            if (CollectionUtils.isEmpty(resourceUsers)) {
                return result;
            }
            List<Long> resIds = resourceUsers.stream().map(XkrResourceUser::getResourceId).collect(Collectors.toList());
            page = PageHelper.startPage(pageNum, size, "create_time desc");
            list = xkrResourceAgent.getResourceListByIds(resIds, ImmutableList.of(
                    ResourceStatusEnum.STATUS_NORMAL.getCode()
            ));
        }
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }

        result.setTotalCount((int) page.getTotal());

        List<XkrClass> classList = xkrClassAgent.getClassByIds(list.stream().map(XkrResource::getClassId).collect(Collectors.toList()));

        XkrUser user = xkrUserAgent.getUserById(userId);

        buildListResourceDTO(result, list, classList, user);

        return result;
    }

    public ResponseDTO reportResource(Long resourceId) {
        XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();
        //用户无权限
        if (UserStatusEnum.USER_STATUS_NORMAL.getCode() != user.getStatus()) {
            return new ResponseDTO<>(ErrorStatus.COMMENT_USER_NOT_PRILIVEGED);
        }
        //查询用户是否下载过该评论
        List<XkrResourceUser> xkrResourceUsers = xkrResourceUserAgent.getResourceByResAndUserId(user.getId(), resourceId, XkrResourceUserAgent.STATUS_PAYED);
        if (CollectionUtils.isEmpty(xkrResourceUsers)) {
            return new ResponseDTO<>(ErrorStatus.COMMENT_USER_NOT_PRILIVEGED);
        }
        return xkrResourceAgent.reportIllegalResource(resourceId) ? new ResponseDTO() : new ResponseDTO(ErrorStatus.ERROR);
    }


    private void buildResourceByClassIdsOnSearch(ListResourceDTO result, List<XkrClass> classList, int orderType,
                                                 int pageNum, int size) {
        //排序的索引键值
        String sortKey = orderType == ORDER_BY_DOWNLOAD_COUNT ? "downloadCount" : "updateTime";
        size = size <= 0 ? 10 : size;
        int offset = pageNum - 1 < 0 ? 0 : (pageNum - 1) * size;

        List<Long> classIds = classList.stream().map(XkrClass::getId).collect(Collectors.toList());

        Map<String, Object> filterMap = ImmutableMap.of(
                "classId", classIds,
                "status", Arrays.asList(ResourceStatusEnum.STATUS_NORMAL.getCode(), ResourceStatusEnum.STATUS_FREEZED.getCode())
        );

        SearchResultListDTO<ResourceIndexDTO> searchResultListDTO = searchApiService.searchByFilterField(ResourceIndexDTO.class,
                filterMap, null, null, sortKey, offset, size
        );

        if (Objects.isNull(searchResultListDTO)) {
            return;
        }

        result.setTotalCount((int) searchResultListDTO.getTotalCount());

        buildListResourceDTO(result, classList, searchResultListDTO);
    }

    private void buildResourceByFilterOnOffline(ListResourceDTO resourceDTOList, List<XkrResource> xkrResources) {
        if (CollectionUtils.isEmpty(xkrResources)) {
            return;
        }


        List<Long> classIds = xkrResources.stream().map(XkrResource::getClassId).collect(Collectors.toList());

        //获取所有分配信息
        List<XkrClass> classList = xkrClassAgent.getClassByIds(classIds);


        List<Long> userIds = xkrResources.stream().map(XkrResource::getUserId).collect(Collectors.toList());

        List<XkrUser> users = xkrUserAgent.getUserByIds(userIds);


        buildListResourceDTO(resourceDTOList, xkrResources, classList, users);


    }

    private void buildResourceByClassIdsOnOffline(ListResourceDTO result, List<XkrClass> classList, int orderType,
                                                  int pageNum, int size) {
        //排序的索引键值
        String sortKey = orderType == ORDER_BY_DOWNLOAD_COUNT ? "update_time" : "download_count";
        Page page = PageHelper.startPage(pageNum, size, sortKey + " desc");

        List<Long> classIds = classList.stream().map(XkrClass::getId).collect(Collectors.toList());

        //获取分类下所有资源
        List<XkrResource> resources = xkrResourceAgent.getResourceListByClassIds(classIds, ImmutableList.of(
                ResourceStatusEnum.STATUS_NORMAL.getCode(),
                ResourceStatusEnum.STATUS_FREEZED.getCode()
        ));

        result.setTotalCount((int) page.getTotal());

        List<Long> userIds = resources.stream().map(XkrResource::getUserId).collect(Collectors.toList());

        List<XkrUser> users = xkrUserAgent.getUserByIds(userIds);


        buildListResourceDTO(result, resources, classList, users);


    }

    private void buildListResourceDTO(ListResourceDTO listResourceDTO, List<XkrResource> list, List<XkrClass> xkrClassList, List<XkrUser> users) {
        list.forEach(xkrResource -> {
            ResourceDTO resourceDTO = new ResourceDTO();
            XkrUser user = users.stream().filter(user1 -> xkrResource.getUserId().equals(user1.getId())).findFirst().orElse(null);
            XkrClass classBean = xkrClassList.stream().
                    filter(xkrClass1 -> xkrClass1.getId().equals(xkrResource.getClassId())).findFirst().orElse(null);
            buildResourceDTO(resourceDTO, xkrResource, classBean, user);
            listResourceDTO.getResList().add(resourceDTO);
        });
    }

    private void buildListResourceDTO(ListResourceDTO listResourceDTO, List<XkrResource> list, List<XkrClass> xkrClassList, XkrUser user) {
        list.forEach(xkrResource -> {
            ResourceDTO resourceDTO = new ResourceDTO();
            XkrClass classBean = xkrClassList.stream().
                    filter(xkrClass1 -> xkrClass1.getId().equals(xkrResource.getClassId())).findFirst().orElse(null);

            buildResourceDTO(resourceDTO, xkrResource, classBean, user);

            listResourceDTO.getResList().add(resourceDTO);
        });
    }

    private void buildListResourceDTO(ListResourceDTO listResourceDTO, List<XkrClass> xkrClassList, SearchResultListDTO<ResourceIndexDTO> searchResultListDTO) {
        listResourceDTO.setTotalCount((int) searchResultListDTO.getTotalCount());


        if (CollectionUtils.isEmpty(xkrClassList)) {
            return;
        }

        searchResultListDTO.getSearchResultDTO().forEach(resourceIndexDTO -> {
            ResourceDTO resourceDTO = new ResourceDTO();

            XkrClass xkrClass = xkrClassList.stream().filter(xkrClass1 -> resourceIndexDTO.getClassId().equals(xkrClass1.getId())).findAny().orElse(null);

            XkrClass rootClass = null;
            if (Objects.nonNull(xkrClass)) {
                String[] paths = xkrClass.getPath().split("-");
                rootClass = paths.length > 1 ?
                        xkrClassAgent.getClassById(Long.valueOf(paths[1])) : xkrClass;
            }
            buildResourceDTO(resourceDTO, resourceIndexDTO, xkrClass, rootClass);

            listResourceDTO.getResList().add(resourceDTO);
        });
    }

    private void buildResourceDTO(ResourceDTO resourceDTO, ResourceIndexDTO resourceIndexDTO, XkrClass currentClass, XkrClass rootClass) {
        resourceDTO.setContent(resourceIndexDTO.getContent());
        resourceDTO.setCost(resourceIndexDTO.getCost());
        resourceDTO.setDownloadCount(resourceIndexDTO.getDownloadCount());
        resourceDTO.setReport(resourceIndexDTO.getReport());
        resourceDTO.setResourceId(resourceIndexDTO.getResourceId());
        resourceDTO.setStatus(resourceIndexDTO.getStatus());
        resourceDTO.setTitle(resourceIndexDTO.getTitle());
        resourceDTO.setUpdateTime(resourceIndexDTO.getUpdateTime());
        resourceDTO.setUserId(resourceIndexDTO.getUserId());
        resourceDTO.setUserName(resourceIndexDTO.getUserName());
        if (Objects.nonNull(currentClass)) {
            resourceDTO.setClassId(currentClass.getId());
            resourceDTO.setClassName(currentClass.getClassName());
            resourceDTO.setRootClassId(rootClass.getId());
            resourceDTO.setRootClassName(rootClass.getClassName());
        } else {
//            resourceDTO.setClassId(currentClass.getId());
            resourceDTO.setClassName("分类不存在或被删除");
//            resourceDTO.setRootClassId(rootClass.getId());
            resourceDTO.setRootClassName("未知分类");
        }


    }

    private void buildResourceDTO(ResourceDTO resourceDTO, XkrResource resource, XkrClass currentClass, XkrUser user) {

        if (Objects.isNull(user)) {
            resourceDTO.setUserName("未知账号");
        } else {
            resourceDTO.setUserName(user.getUserName());
        }
        if (Objects.nonNull(currentClass)) {
            String[] paths = currentClass.getPath().split("-");
            XkrClass rootClass = paths.length > 1 ?
                    xkrClassAgent.getClassById(Long.valueOf(paths[1])) : currentClass;
            rootClass = Objects.isNull(rootClass) ? currentClass : rootClass;
            resourceDTO.setRootClassId(rootClass.getId());
            resourceDTO.setRootClassName(rootClass.getClassName());
            resourceDTO.setClassId(currentClass.getId());
            resourceDTO.setClassName(currentClass.getClassName());
        } else {
            resourceDTO.setClassName("分类不存在或被删除");
            resourceDTO.setRootClassName("分类不存在或被删除");

        }
        resourceDTO.setUserId(resource.getUserId());
        resourceDTO.setResourceId(resource.getId());
        resourceDTO.setReport(Integer.valueOf(resource.getReport()));
        resourceDTO.setDownloadCount(resource.getDownloadCount());
        resourceDTO.setCost(resource.getCost());
        resourceDTO.setContent(resource.getDetail());
        resourceDTO.setStatus(Integer.valueOf(resource.getStatus()));
        resourceDTO.setTitle(resource.getTitle());
        resourceDTO.setUpdateTime(resource.getUpdateTime());

    }

    private void buildResourceRecycleDTO(ResourceRecycleDTO resourceRecycleDTO, XkrResourceRecycle xkrResourceRecycle) {
        resourceRecycleDTO.setClassName(xkrResourceRecycle.getClassName());
        resourceRecycleDTO.setOptName(xkrResourceRecycle.getOptName());
        resourceRecycleDTO.setResourceId(xkrResourceRecycle.getResourceId());
        resourceRecycleDTO.setResourceTitle(xkrResourceRecycle.getResourceTitle());
        resourceRecycleDTO.setUpdateTime(xkrResourceRecycle.getUpdateTime());
        resourceRecycleDTO.setUserName(xkrResourceRecycle.getUserName());
    }

    private void buildResourceDetailDTO(ResourceDetailDTO detailDTO, XkrResource resource) {
        detailDTO.setDetail(resource.getDetail());
        detailDTO.setCost(resource.getCost());
        detailDTO.setDownloadCount(String.valueOf(resource.getDownloadCount()));
        detailDTO.setFileSize(resource.getFileSize());
        detailDTO.setResourceId(resource.getId());
        detailDTO.setTitle(resource.getTitle());
        detailDTO.setUserId(resource.getUserId());
        detailDTO.setUpdateTime(resource.getCreateTime());
        detailDTO.setResStatus(resource.getStatus());
        XkrUser user = xkrUserAgent.getUserById(resource.getUserId());
        if (Objects.isNull(user)) {
            detailDTO.setUserName("未知账号");
        } else {
            detailDTO.setUserName(user.getUserName());
        }

        XkrClass currentClass = xkrClassAgent.getClassById(resource.getClassId());
        if (Objects.nonNull(currentClass)) {
            List<Long> parentClassIds = Arrays.stream(currentClass.getPath().split("-")).
                    map(Long::valueOf).collect(Collectors.toList());
            List<XkrClass> parentClasses = xkrClassAgent.getClassByIds(parentClassIds);

            detailDTO.buildParentClass(currentClass, parentClasses);
        }


    }

    private void deleteUpyunResource(List<Long> resourceIds) {
        //又拍云删除文件
        List<XkrResource> xkrResources = xkrResourceAgent.getResourceListByIds(resourceIds,
                ImmutableList.of(ResourceStatusEnum.STATUS_DELETED.getCode()));
        if (!CollectionUtils.isEmpty(xkrResources)) {
            xkrResources.forEach(resource -> {
                String dirPath = "";
                try {
                    dirPath = resource.getResourceUrl();
                    logger.info("删除云资源，资源id:{},文件夹路径:{}", resource.getId(), dirPath);
                    if (!StringUtils.isEmpty(dirPath)) {
                        upLoadApiService.deleteFile(dirPath, false);
                    }
                } catch (Exception e) {
                    logger.error("删除云资源失败，资源id:{},文件夹路径:{}", resource.getId(), dirPath, e);
                }
            });
        }
    }


}
