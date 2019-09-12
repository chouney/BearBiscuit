package com.xkr.web.controller;

import com.alibaba.fastjson.JSON;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.NoBasicAuth;
import com.xkr.common.annotation.valid.UserCheck;
import com.xkr.dao.cache.BaseRedisService;
import com.xkr.domain.dto.file.FileUploadResponseDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.FileUploadResponseVO;
import com.xkr.web.model.vo.FileUploadReturnVO;
import main.java.com.upyun.UpYunUtils;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.chris.redbud.validator.validate.annotation.ContainsInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.time.LocalDateTime;

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

    @Autowired
    private BaseRedisService baseRedisService;

    private static final String HANDLING_STATUS = "0";

    private static final String SUCCESS_STATUS = "1";
    private static final String FAIL_STATUS = "2";

    @CSRFGen
    @UserCheck
    @RequestMapping(value = "/file_upload", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult fileUpload(
            @RequestParam(name = "fileName") String fileName,
            @RequestParam(name = "contentLength", required = false, defaultValue = "") String contentLength,
            @ContainsInt({0, 1, 2})
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
            String policy = "";
            FileUploadResponseVO responseVO = new FileUploadResponseVO();
            if (UpLoadApiService.COMPRESS_FILE_TYPE == type) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
                bucket = fileBucket;
                fileUri = String.format(UpLoadApiService.getDirPathFormat(), user.getId(), date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                        date.getHour(), date.getMinute(), date.getSecond(), fileName);
                policy = upLoadApiService.genPolicy(fileBucket, fileUri, 60, contentLength);
                responseVO.setAuthorization(upLoadApiService.sign(fileUri, policy, bucket));
            } else if (UpLoadApiService.IMAGE_FILE_TYPE == type) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
                bucket = imageBucket;
                fileUri = String.format(UpLoadApiService.getImageFilePathFormat(), date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                        date.getHour(), date.getMinute(), date.getSecond(), fileName);
                policy = upLoadApiService.genPolicy(imageBucket, fileUri, 60, contentLength);
                responseVO.setAuthorization(upLoadApiService.sign(fileUri, policy, bucket));
            } else if (UpLoadApiService.UNCOMPRE_FILE_TYPE == type) {
                //解压缩的fileName为fileUri
                String sourcePath = fileName;
                String tarPath = "";
                int ind;
                if ((ind = sourcePath.lastIndexOf(".")) != -1) {
                    tarPath = sourcePath.substring(0, ind);
                }
                FileUploadResponseDTO fileUploadResponseDTO = upLoadApiService.unCompressDirSDK(sourcePath, tarPath);
                if (!ErrorStatus.OK.equals(fileUploadResponseDTO.getStatus())) {
                    return new BasicResult(fileUploadResponseDTO.getStatus());
                }

            }
            responseVO.setDirUri(fileUri);
            responseVO.setPolicy(policy);
            return new BasicResult<>(responseVO);
        } catch (Exception e) {
            logger.error("文件上传异常 fileName:{}", fileName, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @NoBasicAuth
    @RequestMapping(value = "/return_url", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult returnUrl(
            @RequestBody String jsonStr,
            HttpServletRequest request) {
        try {
            //签名认证
            String auth = request.getHeader("Authorization");
            String date = request.getHeader("Date");
            String contentMd5 = request.getHeader("Content-MD5");
            String uri = request.getRequestURI();
            String comAuth = UpYunUtils.sign("POST", date, uri, upLoadApiService.getUserName(), upLoadApiService.getPassword(), contentMd5);
            if (StringUtils.isEmpty(auth) || !auth.equals(comAuth)) {
                return new BasicResult(ErrorStatus.BASIC_AUTH_ERROR);
            }

            //解析回调
//            FileUploadReturnVO returnVO = JSON.parseObject(jsonStr, FileUploadReturnVO.class);
//            if (returnVO != null) {
//                String taskId = returnVO.getTask_id();
//                if (200 == returnVO.getStatus_code()) {
//                    baseRedisService.set(taskId, SUCCESS_STATUS, 3600);
//                    return new BasicResult<>(ErrorStatus.OK);
//                }
//                baseRedisService.set(taskId, JSON.toJSONString(returnVO), 3600);
//                return new BasicResult<>(ErrorStatus.OK);
//            }
        } catch (Exception e) {
            logger.error("文件上传异常 jsonStr:{}", jsonStr, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/poll_up", method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult pollUp(
            @RequestParam(name = "fileUri") String fileUri) {
        try {
            String status = baseRedisService.get("UNCOMPRESS_" + fileBucket + fileUri);
            if (StringUtils.isEmpty(status)) {
                return new BasicResult(ErrorStatus.RESOURCE_UNCOMPRESSING);
            }
            if (SUCCESS_STATUS.equals(status)) {
                return new BasicResult<>(ErrorStatus.OK);
            }
            FileUploadReturnVO returnVO = JSON.parseObject(status, FileUploadReturnVO.class);
            return new BasicResult(returnVO.getStatus_code(), returnVO.getError());

        } catch (Exception e) {
            logger.error("文件上传异常 fileUri:{}", fileUri, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

}
