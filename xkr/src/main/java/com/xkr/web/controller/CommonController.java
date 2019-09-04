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

import java.net.URLEncoder;
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
            fileName = URLEncoder.encode(fileName,"UTF-8");
            String policy = "";
            if(UpLoadApiService.COMPRESS_FILE_TYPE == type) {
                bucket = fileBucket;
                fileUri = String.format(UpLoadApiService.getDirPathFormat(), user.getId(),date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                        date.getHour(), date.getMinute(), date.getSecond(), fileName);
                policy = upLoadApiService.genPolicy(fileBucket, fileUri, 60, contentLength);
            }else if(UpLoadApiService.IMAGE_FILE_TYPE == type){
                bucket = imageBucket;
                fileUri = String.format(UpLoadApiService.getImageFilePathFormat(), date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                        date.getHour(), date.getMinute(), date.getSecond(), fileName);
                policy = upLoadApiService.genPolicy(imageBucket, fileUri, 60, contentLength);
            }
            FileUploadResponseVO responseVO = new FileUploadResponseVO();
            responseVO.setAuthorization(upLoadApiService.sign(fileUri,policy,bucket));
            responseVO.setDirUri(fileUri);
            responseVO.setPolicy(policy);
            return new BasicResult<>(responseVO);
        } catch (Exception e) {
            logger.error("文件上传异常 fileName:{}", fileName, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }



}
