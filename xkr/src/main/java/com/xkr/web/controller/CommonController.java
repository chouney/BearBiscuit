package com.xkr.web.controller;

import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.domain.dto.file.FileUploadResponseDTO;
import com.xkr.exception.UpFileExistException;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.FileUploadResponseVO;
import main.java.com.upyun.UpException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    @CSRFGen
    @RequestMapping(value = "/file_upload", method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult fileUpload(
            @RequestParam(name = "file") MultipartFile reqFile,
            @ContainsInt({0, 1})
            @RequestParam(name = "type") Integer type,
            ValidResult result) {
        if (result.hasErrors()) {
            return new BasicResult(result);
        }
        String filePath = String.join("/",TMP_DIR_PATH,String.valueOf(reqFile.getOriginalFilename()));
        File file = new File(filePath);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(reqFile.getBytes());
            FileUploadResponseDTO responseDTO = upLoadApiService.upload(type, file);
            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult<>(responseDTO.getStatus());
            }
            FileUploadResponseVO responseVO = new FileUploadResponseVO();

            buildFileUploadResponseVO(responseVO,responseDTO);

            return new BasicResult<>(responseVO);
        } catch (UpFileExistException e){
            logger.info("文件已存在 reqFileName:{},type:{}", reqFile.getOriginalFilename(), type);
            return new BasicResult(ErrorStatus.RESOURCE_ALREADY_EXIST);
        } catch (IOException | UpException e) {
            logger.error("文件上传异常 reqFileName:{},type:{}", reqFile.getOriginalFilename(), type, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    private void buildFileUploadResponseVO(FileUploadResponseVO responseVO,FileUploadResponseDTO responseDTO){
        responseVO.setCompressMd5(responseDTO.getCompressMd5());
        responseVO.setImageMd5(responseDTO.getImageMd5());
        responseVO.setFileName(responseDTO.getFileName());
    }
}
