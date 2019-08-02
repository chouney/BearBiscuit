package com.xkr.web.controller;

import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.valid.UserCheck;
import com.xkr.domain.dto.file.FileUploadResponseDTO;
import com.xkr.domain.entity.XkrUser;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.FileUploadResponseVO;
import main.java.com.upyun.UpException;
import main.java.com.upyun.UpYunUtils;
import org.apache.shiro.SecurityUtils;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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


    @CSRFGen
    @UserCheck
    @RequestMapping(value = "/file_upload", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult fileUpload(
            @RequestParam(name = "fileName") String fileName,
//            @ContainsInt({0, 1})
//            @RequestParam(name = "type") Integer type,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }

        try {
            XkrUser user = (XkrUser) SecurityUtils.getSubject().getPrincipal();

            SimpleDateFormat formater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            formater.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = formater.format(new Date());
            String fileUri = String.format(UpLoadApiService.getDirPathFormat(),user.getId(),UpYunUtils.md5(fileName));
            FileUploadResponseVO responseVO = new FileUploadResponseVO();
            responseVO.setDate(date);
            responseVO.setAuthorization(UpYunUtils.sign("PUT", date, fileUri, this.userName, this.password, null).trim());
            responseVO.setDirUri(fileUri);
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

//    private void buildFileUploadResponseVO(FileUploadResponseVO responseVO, FileUploadResponseDTO responseDTO) {
//        responseVO.setCompressMd5(responseDTO.getCompressMd5());
//        responseVO.setImageMd5(responseDTO.getImageMd5());
//        responseVO.setFileName(responseDTO.getFileName());
//    }
}
