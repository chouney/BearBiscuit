package com.xkr.web.controller;

import com.sun.nio.zipfs.ZipPath;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.CSRFGen;
import com.xkr.common.annotation.CSRFValid;
import com.xkr.service.api.UpLoadApiService;
import com.xkr.web.model.BasicResult;
import main.java.com.upyun.UpException;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.Min;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/3
 */
@Controller
@RequestMapping("/test")
public class TestController {
    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UpLoadApiService upLoadApiService;

    @RequestMapping(value = "/index", method = {RequestMethod.GET})
    @ResponseBody
    @MethodValidate
    public BasicResult index(
            @Length(min = 3,max=6)
            @RequestParam String name,
            @Min(3)
            @RequestParam Integer age,
            ValidResult validResult) {
        if(validResult.hasErrors()){
            return new BasicResult(validResult.getErrors());
        }
        return new BasicResult(ErrorStatus.OK);
    }

    @RequestMapping(value = "/test1", method = {RequestMethod.GET})
    @ResponseBody
    @CSRFGen
    public BasicResult index1(Model model) {
        return new BasicResult(ErrorStatus.OK);
    }

    @RequestMapping(value = "/test2", method = {RequestMethod.GET})
    @ResponseBody
    @CSRFValid
    public BasicResult index2(Model model) {
        return new BasicResult(ErrorStatus.OK);
    }

    @RequestMapping(value = "/upload", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult index3(Model model) throws URISyntaxException, IOException, UpException {
        File file = new File("/Users/chriszhang/外包/www-logic/xkr/src/main/resources/static");
//        upLoadApiService.uploadUnCompressDic("423123",file);
        return new BasicResult(ErrorStatus.OK);
    }

    @RequestMapping(value = "/test4", method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult index4(Model model) {
        return new BasicResult(ErrorStatus.OK);
    }



}
