package com.xkr.service.api;

import com.google.common.collect.Lists;
import com.xkr.common.ErrorStatus;
import com.xkr.common.FileTypeEnum;
import com.xkr.core.compress.UnCompressProcessorFacade;
import com.xkr.domain.dto.file.FileInfoDTO;
import com.xkr.domain.dto.file.FileUploadResponseDTO;
import com.xkr.domain.dto.file.FolderItemDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.exception.UpFileExistException;
import com.xkr.util.FileUtil;
import main.java.com.UpYun;
import main.java.com.upyun.UpException;
import main.java.com.upyun.UpYunUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    @Resource(name = "fileYun")
    private UpYun upYun;

    @Resource(name = "imageYun")
    private UpYun imageYun;

    @Autowired
    private UnCompressProcessorFacade compressProcessorFacade;

    public static final int COMPRESS_FILE_TYPE = 0;

    public static final int IMAGE_FILE_TYPE = 1;

    @Value("${upyun.root.path}")
    private String rootPath;

    //userId:md5FileName:fileName+postFix
    private static String COMPRESS_FILE_PATH_FORMAT;
    //userId:md5FileName
    private static String UNCOMPRESS_FILE_PATH_FORMAT;
    //年月日时分秒:md5FileName
    private static String IMAGE_FILE_PATH_FORMAT;
    //filename
    private static String EXT_FILE_PATH_FORMAT;

    @PostConstruct
    public void init() {
        EXT_FILE_PATH_FORMAT = rootPath + "/ext/%s";
        COMPRESS_FILE_PATH_FORMAT = rootPath + "/%s/%s/%s";
        UNCOMPRESS_FILE_PATH_FORMAT = rootPath + "/%s/%s";
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
     * 返回文件信息
     *
     * @param filePath
     * @return
     * @throws IOException
     * @throws UpException
     */
    public FileInfoDTO getFileInto(String filePath) throws IOException, UpException {
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
        if (Objects.nonNull(getFileInto(filePath))) {
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
            String unCompressMd5 = UpYunUtils.md5(unCompressDic.getName());
            isSuccess = uploadUnCompressDic(String.valueOf(user.getId()), unCompressMd5, unCompressDic);
            if (!isSuccess) {
                logger.info("云盘服务上传解压缩文件失败,unCompressDic:{},fileType:{},uploadFile:{}", unCompressDic.getName(), fileType, uploadFile.getName());
                return new FileUploadResponseDTO(ErrorStatus.ERROR);
            }
            String compressMd5 = UpYunUtils.md5(uploadFile.getName());
            isSuccess = uploadCompressFile(String.valueOf(user.getId()), compressMd5, uploadFile, true);
            if (!isSuccess) {
                //若失败则删除解压缩上传文件
                logger.info("云盘服务上传压缩文件失败,回滚删除解压缩文件,unCompressDic:{},fileType:{},uploadFile:{}", unCompressDic.getName(), fileType, uploadFile.getName());
                String unComDicPath = String.format(UNCOMPRESS_FILE_PATH_FORMAT, String.valueOf(user.getId()), unCompressMd5);
                deleteFile(unComDicPath, true);
            }
            return new FileUploadResponseDTO(compressMd5, unCompressMd5, uploadFile.getName());
        } else if (IMAGE_FILE_TYPE == fileType) {
            String imageMd5 = UpYunUtils.md5(uploadFile.getName());
            if (uploadImageFile(imageMd5, uploadFile)) {
                return new FileUploadResponseDTO(imageMd5);
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
    private boolean uploadImageFile(String md5FileName, File imageFile) throws IOException, UpException {
        LocalDateTime date = LocalDateTime.now();
        String picPath = String.format(IMAGE_FILE_PATH_FORMAT, date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                date.getHour(), date.getMinute(), date.getSecond(), md5FileName);
        boolean isSuccess = imageYun.writeFile(picPath, imageFile, true);
        if (!isSuccess) {
            logger.info("云盘服务上传图片失败,回滚删除图片,md5FileName:{},imageFile:{}", md5FileName, imageFile.getName());
            deleteFile(picPath, false);
        }
        return isSuccess;
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
        if (Objects.nonNull(getFileInto(filePath))) {
            throw new UpFileExistException("file already exist,filePath:"+filePath);
        }
        upYun.setContentMD5(UpYun.md5(file));
        boolean isSuccess = upYun.writeFile(filePath, file, auto);
        if (!isSuccess) {
            logger.info("云盘服务上传压缩文件失败,回滚删除压缩文件,userId:{},md5FileName:{},file:{}", userId, md5FileName, file.getName());
            deleteFile(filePath, false);
        }
        return isSuccess;
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
    public boolean uploadUnCompressDic(String userId, String md5FileName, File dictionary) throws UpException, IOException {
        if (Objects.isNull(dictionary) || !dictionary.isDirectory()) {
            throw new UpException("file is not dictionary");
        }
        String dicPath = String.format(UNCOMPRESS_FILE_PATH_FORMAT, userId, md5FileName);
        if (Objects.nonNull(getFileInto(dicPath))) {
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
     * 文件删除
     *
     * @param path
     * @param isDictionary
     */
    @Async
    public void deleteFile(String path, boolean isDictionary) throws IOException, UpException {
        if (Objects.isNull(getFileInto(path))) {
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

    public static String getExtFilePathFormat() {
        return EXT_FILE_PATH_FORMAT;
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
