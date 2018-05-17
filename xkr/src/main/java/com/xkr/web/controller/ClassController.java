package com.xkr.web.controller;

import com.google.common.collect.ImmutableMap;
import com.xkr.common.ErrorStatus;
import com.xkr.domain.dto.ClassMenuDTO;
import com.xkr.service.ClassService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.ClassMenuVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/cls")
public class ClassController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ClassService classService;

    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult getUserMessage(@RequestParam(name = "type") int type) {
        try {
            ClassMenuDTO rootClass = classService.getAllChildClassByClassId((long) type);
            if(Objects.isNull(rootClass)){
                return new BasicResult(ErrorStatus.ERROR);
            }
            ClassMenuVO vo = new ClassMenuVO();

            BeanUtils.copyProperties(rootClass,vo);

            return new BasicResult<>(vo);
        } catch (Exception e) {
            logger.error("ClassController getUserMessage error ,type:{}", type, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

}
