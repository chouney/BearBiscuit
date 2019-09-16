package com.xkr.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xkr.common.ErrorStatus;
import com.xkr.common.FileTypeEnum;
import com.xkr.core.compress.UnCompressProcessorFacade;
import com.xkr.dao.cache.BaseRedisService;
import com.xkr.domain.dto.file.FileInfoDTO;
import com.xkr.domain.dto.file.FileUploadResponseDTO;
import com.xkr.domain.dto.file.FolderItemDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.exception.UpFileExistException;
import com.xkr.util.FileUtil;
import main.java.com.UpYun;
import main.java.com.upyun.*;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
@Service
public class UpLoadApiService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${upyun.opt.user}")
    private String userName;

    @Value("${upyun.opt.password}")
    private String password;


    @Resource(name = "fileYun")
    private UpYun upYun;

    @Resource(name = "imageYun")
    private UpYun imageYun;

    @Resource(name = "mediaHandler")
    private MediaHandler mediaHandler;

    @Value("${upyun.bucket.file}")
    private String fileBucket;

    @Value("${upyun.bucket.image}")
    private String imageBucket;

    @Value("${front.domain}")
    private String frontDomain;

    @Value("${server.port}")
    private String port;

    @Autowired
    private BaseRedisService baseRedisService;

    @Autowired
    private UnCompressProcessorFacade compressProcessorFacade;

    public static final int COMPRESS_FILE_TYPE = 0;

    public static final int IMAGE_FILE_TYPE = 1;

    public static final int UNCOMPRE_FILE_TYPE = 2;

    private static final int DEFAULT_BLOCK_SIZE = 512;

    @Value("${upyun.root.path}")
    private String rootPath;

    //userId:fileName
    private static String DIR_PATH_FORMAT;
    //userId:md5FileName:fileName+postFix
    private static String COMPRESS_FILE_PATH_FORMAT;
    //userId:md5FileName:filePrefixName
    private static String UNCOMPRESS_FILE_PATH_FORMAT;
    //年月日时分秒:md5ImageName
    private static String IMAGE_FILE_PATH_FORMAT;
    //filename
    private static String EXT_FILE_PATH_FORMAT;

    @PostConstruct
    public void init() {
        DIR_PATH_FORMAT = rootPath + "/%s/%d%d%d%d%d%d-%s";
        EXT_FILE_PATH_FORMAT = rootPath + "/ext/%s";
        COMPRESS_FILE_PATH_FORMAT = rootPath + "/%s/%s/%s";
        UNCOMPRESS_FILE_PATH_FORMAT = rootPath + "/%s/%s/%s";
        IMAGE_FILE_PATH_FORMAT = rootPath + "/image/%d%d%d%d%d%d-%s";
    }

    /**
     * 下载文件
     *
     * @param upyunFilePath
     * @param targetFile
     * @return
     * @throws IOException
     * @throws UpException
     */
    public boolean downLoadFile(String upyunFilePath, File targetFile) throws IOException, UpException {
        logger.info("云盘下载文件,upyunPath:{},fileName:{}", upyunFilePath, targetFile.getName());
        return upYun.readFile(upyunFilePath, targetFile);
    }

    /**
     * 删除文件
     *
     * @param upyunFilePath
     * @return
     * @throws IOException
     * @throws UpException
     */
    public boolean deleteFile(String upyunFilePath) throws IOException, UpException {
        logger.info("云盘下载文件,upyunPath:{}", upyunFilePath);
        return upYun.deleteFile(upyunFilePath);
    }

    /**
     * 返回文件信息
     *
     * @param filePath
     * @return
     * @throws IOException
     * @throws UpException
     */
    public FileInfoDTO getFileInfo(String filePath) throws IOException, UpException {
        Map<String, String> map;
        try {
            map = upYun.getFileInfo(filePath);
            if (CollectionUtils.isEmpty(map)) {
                return null;
            }
        } catch (FileNotFoundException e) {
//            logger.error("云盘服务未找到该文件,filePath:{},error:", filePath, e);
            return null;
        }
        return new FileInfoDTO(map);
    }

    /**
     * 返回目录信息
     *
     * @param dicPath
     * @return
     */
    public List<FolderItemDTO> getDirInfo(String dicPath) {
        if (Objects.isNull(dicPath)) {
            return Lists.newArrayList();
        }
        try {
            List<UpYun.FolderItem> folderItems = upYun.readDir(dicPath);
            if(CollectionUtils.isEmpty(folderItems)){
                return Lists.newArrayList();
            }
            return folderItems.stream().map(folderItem -> new FolderItemDTO(folderItem.name, "Folder".equals(folderItem.type),
                    folderItem.size, folderItem.date)).collect(Collectors.toList());
        } catch (IOException | UpException e) {
            logger.error("云盘服务返回目录信息异常，dicPath:{}", dicPath, e);
        }
        return Lists.newArrayList();
    }


    /**
     * 内部文件上传服务
     *
     * @param filePath
     * @param file
     * @return 云盘文件路径
     * @throws UpException
     * @throws IOException
     */
    public String extFileUpload(String filePath, File file) throws UpException, IOException {
        if (Objects.nonNull(getFileInfo(filePath))) {
            throw new UpFileExistException("file already exist");
        }
        if (upYun.writeFile(filePath, file, true)) {
            logger.info("云盘服务上传内部文件成功,filePath:{},fileName:{}", filePath, file);
            return filePath;
        }
        return null;
    }


    /**
     * 文件上传
     *
     * @param fileType
     * @return
     */
    public FileUploadResponseDTO upload(int fileType, File uploadFile) throws IOException, UpException {
        if (COMPRESS_FILE_TYPE == fileType) {
            FileTypeEnum fileTypeEnum = FileUtil.getFileType(uploadFile);
            if (fileTypeEnum == null || fileTypeEnum.getProcessorClazz() == null) {
                logger.info("云盘服务未找到上传文件的文件格式,fileType:{},uploadFile:{}", fileType, uploadFile.getName());
                return new FileUploadResponseDTO(ErrorStatus.PARAM_ERROR);
            }
            //上传压缩文件
            XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();
            if (Objects.isNull(user)) {
                return new FileUploadResponseDTO(ErrorStatus.UNLOGIN_REDIRECT);
            }
            boolean isSuccess;
            File unCompressDic = compressProcessorFacade.unCompressFile(uploadFile, fileTypeEnum.getProcessorClazz());
            if (Objects.isNull(unCompressDic)) {
                logger.info("云盘服务解压缩文件失败,fileType:{},uploadFile:{}", fileType, uploadFile.getName());
                return new FileUploadResponseDTO(ErrorStatus.ERROR);
            }
            String compressMd5 = UpYun.md5(uploadFile);
            String filePrefixName = uploadFile.getName().split("\\.")[0];
            isSuccess = uploadUnCompressDic(String.valueOf(user.getId()), compressMd5, filePrefixName ,unCompressDic);
            if (!isSuccess) {
                logger.info("云盘服务上传解压缩文件失败,unCompressDic:{},fileType:{},uploadFile:{}", unCompressDic.getName(), fileType, uploadFile.getName());
                return new FileUploadResponseDTO(ErrorStatus.ERROR);
            }
            isSuccess = uploadCompressFile(String.valueOf(user.getId()), compressMd5, uploadFile, true);
            if (!isSuccess) {
                //若失败则删除解压缩上传文件
                logger.info("云盘服务上传压缩文件失败,回滚删除解压缩文件,unCompressDic:{},fileType:{},uploadFile:{}", unCompressDic.getName(), fileType, uploadFile.getName());
                String unComDicPath = String.format(UNCOMPRESS_FILE_PATH_FORMAT, String.valueOf(user.getId()), compressMd5, filePrefixName);
                deleteFile(unComDicPath, true);
            }
            return new FileUploadResponseDTO(compressMd5, uploadFile.getName());
        } else if (IMAGE_FILE_TYPE == fileType) {
            String imageMd5 = UpYun.md5(uploadFile);
            String picUri = uploadImageFile(imageMd5, uploadFile);
            if (!StringUtils.isEmpty(picUri)) {
                return new FileUploadResponseDTO(picUri);
            }
        }
        return new FileUploadResponseDTO(ErrorStatus.ERROR);

    }


    /**
     * 图片上传
     *
     * @param imageFile
     * @return
     * @throws IOException
     * @throws UpException
     */
    private String uploadImageFile(String md5FileName, File imageFile) throws IOException, UpException {
        LocalDateTime date = LocalDateTime.now();
        String picPath = String.format(IMAGE_FILE_PATH_FORMAT, date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                date.getHour(), date.getMinute(), date.getSecond(), md5FileName);
        try {
            imageYun.writeFile(picPath, imageFile, true);
        } catch (Exception e){
            logger.info("云盘服务上传图片失败,回滚删除图片,md5FileName:{},imageFile:{}", md5FileName, imageFile.getName());
            deleteFile(picPath, false);
            return null;
        }
        return picPath;
    }

    /**
     * 压缩文件上传
     *
     * @param userId
     * @param file
     * @param auto
     * @return
     * @throws IOException
     * @throws UpException
     */
    private boolean uploadCompressFile(String userId, String md5FileName, File file, boolean auto) throws IOException, UpException {
        String filePath = String.format(COMPRESS_FILE_PATH_FORMAT, userId, md5FileName, file.getName());
        if (Objects.nonNull(getFileInfo(filePath))) {
            throw new UpFileExistException("file already exist,filePath:"+filePath);
        }
        upYun.setContentMD5(md5FileName);
        try {
            upYun.writeFile(filePath, file, auto);
        }catch (Exception e){
            logger.info("云盘服务上传压缩文件失败,回滚删除压缩文件,userId:{},md5FileName:{},file:{}", userId, md5FileName, file.getName());
            deleteFile(filePath, false);
            return false;
        }
        return true;
    }

    /**
     * 文件夹上传
     *
     * @param userId
     * @param dictionary
     * @return
     * @throws UpException
     * @throws IOException
     */
    public boolean uploadUnCompressDic(String userId, String md5FileName, String filePrefixName, File dictionary) throws UpException, IOException {
        if (Objects.isNull(dictionary) || !dictionary.isDirectory()) {
            throw new UpException("file is not dictionary");
        }
        String dicPath = String.format(UNCOMPRESS_FILE_PATH_FORMAT, userId, md5FileName, filePrefixName);
        if (Objects.nonNull(getFileInfo(dicPath))) {
            throw new UpFileExistException("dictionary already exist");
        }
        boolean isSuccess = true;
        try {
            isSuccess = doUploadUnCompressDic(dicPath, dictionary);
        } catch (Exception e) {
            logger.error("云盘服务上传解压缩文件异常,dicPath:{},dictionary:{}", dicPath, dictionary.getName(), e);
            isSuccess = false;
        }
        if (!isSuccess) {
            logger.info("云盘服务上传解压缩文件失败,回滚删除解压缩文件,userId:{},md5FileName:{},file:{}", userId, md5FileName, dictionary.getName());
            deleteFile(dicPath, true);
        }
        return isSuccess;
    }


    /**
     * 同上
     *
     * @param dicPath
     * @param dictionary
     * @return
     * @throws UpException
     * @throws IOException
     */
    private boolean doUploadUnCompressDic(String dicPath, File dictionary) throws UpException, IOException {
        if (Objects.isNull(dictionary) || Objects.isNull(dictionary.listFiles())) {
            throw new UpException("file is not dictionary");
        }
        upYun.mkDir(dicPath, true);
        final boolean[] isSuccess = {true};
        File[] listFiles = dictionary.listFiles();
        Arrays.stream(listFiles).parallel().forEach(file -> {
            String subPath = dicPath + "/" + file.getName();
            try {
                if (file.isDirectory()) {
                    upYun.mkDir(subPath, true);
                    doUploadUnCompressDic(subPath, file);
                } else {
                    upYun.writeFile(subPath, file, true);
                }
            } catch (IOException | UpException e) {
                logger.error("云盘服务上传解压缩恩建异常,dicPath:{},dictionary:{}", dicPath, dictionary.getName(), e);
                isSuccess[0] = false;
            }
        });
        return isSuccess[0];
    }

    /**
     * 同上
     *
     * @param sourcePath
     * @param tarPath
     * @return
     * @throws UpException
     * @throws IOException
     */
    public FileUploadResponseDTO unCompressDirSDK(String sourcePath,String tarPath) throws UpException, IOException {

        Map<String,Object> paramsMap = Maps.newHashMap();
        //空间名
        paramsMap.put("service", fileBucket);
        //回调地址
        paramsMap.put(CompressHandler.Params.NOTIFY_URL, "http://"+frontDomain+":"+port+"/api/common/return_url");
//        paramsMap.put(CompressHandler.Params.NOTIFY_URL, "");
        //选择处理任务
        paramsMap.put(CompressHandler.Params.APP_NAME, "depress");

        //已json格式生成任务信息
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();

        //添加处理参数
        json.put(CompressHandler.Params.SOURCES, sourcePath);
        json.put(CompressHandler.Params.SAVE_AS, tarPath);

        array.add(json);

        //添加任务信息
        paramsMap.put(CompressHandler.Params.TASKS, array);

        try {
            //先创建一个文件夹
            if(!upYun.mkDir(tarPath,true)){
                return new FileUploadResponseDTO(ErrorStatus.RESOURCE_UPLOAD_ERROR);
            }

            Result result = mediaHandler.process(paramsMap);
            logger.debug("UploadApiService unCompressDirSDK ,sourcePath:{},targetPath:{},解压缩结果:{}",sourcePath,tarPath, JSON.toJSONString(result));
            if (result.isSucceed()) {
//                String[] ids = mediaHandler.getTaskId(result.getMsg());
//                Arrays.stream(ids).forEach(str -> baseRedisService.set(str, "0", 3600));
                return new FileUploadResponseDTO(ErrorStatus.OK);
            }
        } catch (IOException | UpException e) {
            logger.error("ploadApiService unCompressDirSDK 解压缩失败:sourcePath:{},targetPath:{},error:",sourcePath,tarPath,e);
        }
        return new FileUploadResponseDTO(ErrorStatus.RESOURCE_FAILD_UNCOMPRESSING);
    }

    /**
     * 文件删除
     *
     * @param path
     * @param isDictionary
     */
    @Async
    public void deleteFile(String path, boolean isDictionary) throws IOException, UpException {
        if (Objects.isNull(getFileInfo(path))) {
            logger.info("云盘服务未找到该文件,path:{}", path);
            return;
        }
        if (isDictionary) {
            List<FolderItemDTO> folderItemDTOS = getDirInfo(path);
            folderItemDTOS.forEach(folderItemDTO -> {
                String subPath = path + "/" + folderItemDTO.getName();
                try {
                    deleteFile(subPath, folderItemDTO.isFolder());
                } catch (IOException | UpException e) {
                    e.printStackTrace();
                }
            });
            upYun.rmDir(path);
        }
        upYun.deleteFile(path);
    }

    /**
     * 签名算法
     * @param fileUri
     * @param policy
     * @return
     * @throws UpException
     */
    public String sign(String fileUri,
                        String policy,
                        String bucket) throws UpException {

        String signature = null;
        StringBuilder sb = new StringBuilder();
        String sp = "&";
        sb.append("POST");
        sb.append(sp);
        sb.append("/" + bucket);
        sb.append(sp);
        sb.append(policy);

        String raw = sb.toString().trim();
        byte[] hmac = new byte[0];
        try {
            hmac = UpYunUtils.calculateRFC2104HMACRaw(UpYunUtils.md5(getPassword()), raw);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("generate HMACRAW failure fileUri, raw：{}",fileUri,raw,e);
            throw new UpException("生成加密hmac异常");
        }
        if(hmac != null) {
            signature = Base64Coder.encodeLines(hmac).trim();
        }

        logger.debug("fileUri, sign：{}",fileUri,signature);


        return "UPYUN " + getUserName() + ":" + signature;
    }


    public String genPolicy(String bucket,String saveKeyPath,Integer expiration,String contentLength){
        Map<String,Object> params = Maps.newHashMap();
        params.put("expiration", Long.valueOf(System.currentTimeMillis() / 1000L + (long)expiration));
        params.put("bucket", bucket);
        params.put("content-length", contentLength);
        params.put("save-key", saveKeyPath);
        return UpYunUtils.getPolicy(params);

    }

    public String getUserName(){
        return this.userName;
    }

    public String getPassword(){
        return this.password;
    }

    public static String getExtFilePathFormat() {
        return EXT_FILE_PATH_FORMAT;
    }

    public static String getDirPathFormat() {
        return DIR_PATH_FORMAT;
    }


    public static String getCompressFilePathFormat() {
        return COMPRESS_FILE_PATH_FORMAT;
    }

    public static String getUncompressFilePathFormat() {
        return UNCOMPRESS_FILE_PATH_FORMAT;
    }

    public static String getImageFilePathFormat() {
        return IMAGE_FILE_PATH_FORMAT;
    }

}
