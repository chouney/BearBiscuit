package com.xkr.web.controller;

import com.google.common.collect.Maps;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.valid.UserCheck;
import com.xkr.domain.dto.file.FileUploadResponseDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.FileUploadResponseVO;
import main.java.com.upyun.Base64Coder;
import main.java.com.upyun.UpException;
import main.java.com.upyun.UpYunUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.websocket.server.UpgradeUtil;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/30
 */
@Controller
@RequestMapping("/api/common")
public class CommonController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String TMP_DIR_PATH = System.getProperty("java.io.tmpdir");

    @Autowired
    private UpLoadApiService upLoadApiService;

    @Value("${upyun.opt.user}")
    private String userName;

    @Value("${upyun.opt.password}")
    private String password;

    @Value("${upyun.bucket.file}")
    private String fileBucket;

    @Value("${upyun.bucket.image}")
    private String imageBucket;




    @CSRFGen
    @UserCheck
    @RequestMapping(value = "/file_upload", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult fileUpload(
            @RequestParam(name = "fileName") String fileName,
            @RequestParam(name = "contentLength") String contentLength,
            @ContainsInt({0, 1})
            @RequestParam(name = "type") Integer type,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }

        try {
            XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();

            String bucket = "";
            LocalDateTime date = LocalDateTime.now();
            String fileUri = "";
            if(UpLoadApiService.COMPRESS_FILE_TYPE == type) {
                bucket = fileBucket;
                fileUri = String.format(UpLoadApiService.getDirPathFormat(), user.getId(),date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                        date.getHour(), date.getMinute(), date.getSecond(), fileName);
            }else if(UpLoadApiService.IMAGE_FILE_TYPE == type){
                bucket = imageBucket;
                fileUri = String.format(UpLoadApiService.getImageFilePathFormat(), date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                        date.getHour(), date.getMinute(), date.getSecond(), fileName);
            }
            String policy = genPolicy(imageBucket, fileUri, 60, contentLength);
            FileUploadResponseVO responseVO = new FileUploadResponseVO();
            responseVO.setAuthorization(sign(fileUri,policy,bucket));
            responseVO.setDirUri(fileUri);
            responseVO.setPolicy(policy);
//        String filePath = String.join("/",TMP_DIR_PATH,String.valueOf(reqFile.getOriginalFilename()));
//        File file = new File(filePath);
//        try (FileOutputStream outputStream = new FileOutputStream(file)) {
//            outputStream.write(reqFile.getBytes());
//            FileUploadResponseDTO responseDTO = upLoadApiService.upload(type, file);
//            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
//                return new BasicResult<>(responseDTO.getStatus());
//            }
//            FileUploadResponseVO responseVO = new FileUploadResponseVO();
//
//            buildFileUploadResponseVO(responseVO,responseDTO);
//
            return new BasicResult<>(responseVO);
        } catch (UpException e) {
            logger.error("文件上传异常 fileName:{}", fileName, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    private String genPolicy(String bucket,String saveKeyPath,Integer expiration,String contentLength){
        Map<String,Object> params = Maps.newHashMap();
        params.put("expiration", Long.valueOf(System.currentTimeMillis() / 1000L + (long)expiration));
        params.put("bucket", bucket);
        params.put("content-length", contentLength);
        params.put("save-key", saveKeyPath);
        return UpYunUtils.getPolicy(params);

    }

    /**
     * 签名算法
     * @param fileUri
     * @param policy
     * @return
     * @throws UpException
     */
    private String sign(String fileUri,
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
            hmac = UpYunUtils.calculateRFC2104HMACRaw(getPassword(), raw);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("generate HMACRAW failure fileUri, raw：{}",fileUri,raw,e);
            throw new UpException("生成加密hmac异常");
        }
        if(hmac != null) {
            signature = Base64Coder.encodeLines(hmac);
        }

        logger.debug("fileUri, sign：{}",fileUri,signature);


        return "UPYUN " + getUserName() + ":" + signature;
    }


    private String getUserName(){
        return this.userName;
    }

    private String getPassword(){
        return this.password;
    }
//    private void buildFileUploadResponseVO(FileUploadResponseVO responseVO, FileUploadResponseDTO responseDTO) {
//        responseVO.setCompressMd5(responseDTO.getCompressMd5());
//        responseVO.setImageMd5(responseDTO.getImageMd5());
//        responseVO.setFileName(responseDTO.getFileName());
//    }
}
