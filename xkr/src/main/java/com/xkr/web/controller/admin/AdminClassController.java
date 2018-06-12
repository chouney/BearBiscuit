package com.xkr.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.xkr.common.ErrorStatus;
import com.xkr.common.annotation.valid.IsNumberic;
import com.xkr.domain.dto.ResponseDTO;
import com.xkr.domain.dto.clazz.ClassMenuDTO;
import com.xkr.service.ClassService;
import com.xkr.web.model.BasicResult;
import com.xkr.web.model.vo.clazz.ClassMenuVO;
import org.chris.redbud.validator.annotation.MethodValidate;
import org.chris.redbud.validator.result.ValidResult;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Controller
@RequestMapping("/api/admin/cls")
public class AdminClassController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClassService classService;

    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public BasicResult getClassList(@RequestParam(name = "type") int type) {
        try {
            ClassMenuDTO rootClass = classService.getAllChildClassByClassId((long) type);
            if(!ErrorStatus.OK.equals(rootClass.getStatus())){
                return new BasicResult(ErrorStatus.ERROR);
            }
            ClassMenuVO vo = new ClassMenuVO();

            buildClassMenuVO(vo,rootClass);

            return new BasicResult<>(vo);
        } catch (Exception e) {
            logger.error("ClassController getClassList error ,type:{}", type, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/update",method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult updateClassName(
            @IsNumberic
            @RequestParam(name = "classId") String classId,
            @NotBlank
            @RequestParam(name = "className") String className,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {
            ResponseDTO<Boolean> responseDTO = classService.updateClassNameById(Long.valueOf(classId),className);
            if(!ErrorStatus.OK.equals(responseDTO.getStatus()) || Boolean.FALSE.equals(responseDTO.getData())){
                return new BasicResult(ErrorStatus.ERROR);
            }
            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("ClassController updateClassName error ,classId:{},className:{}", classId,className, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/del",method = {RequestMethod.POST})
    @ResponseBody
    public BasicResult batchDeleteClassName(
            @RequestParam(name = "classIds[]") String[] classIds) {
        try {
            List<Long> ids = Arrays.stream(classIds).map(Long::valueOf).collect(Collectors.toList());
            ResponseDTO<Boolean> responseDTO = classService.batchDeleteClassByClassId(ids);
            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult(responseDTO.getStatus());
            }
            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("ClassController batchDeleteClassName error ,classIds:{}", JSON.toJSONString(classIds), e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    @RequestMapping(value = "/add",method = {RequestMethod.POST})
    @ResponseBody
    @MethodValidate
    public BasicResult saveNewClass(
            @NotBlank
            @RequestParam(name = "className") String className,
            @IsNumberic
            @RequestParam(name = "parentClassId") String parentClassId,
            ValidResult result) {
        if(result.hasErrors()){
            return new BasicResult(result);
        }
        try {

            ResponseDTO<Boolean> responseDTO = classService.saveNewClass(className,Long.valueOf(parentClassId));
            if(!ErrorStatus.OK.equals(responseDTO.getStatus())){
                return new BasicResult(responseDTO.getStatus());
            }
            return new BasicResult<>(responseDTO.getData());
        } catch (Exception e) {
            logger.error("ClassController saveNewClass error ,className:{},parentClassId:{}", className, parentClassId, e);
        }
        return new BasicResult(ErrorStatus.ERROR);
    }

    private void buildClassMenuVO(ClassMenuVO vo,ClassMenuDTO classMenuDTOs){
        vo.setClassId(classMenuDTOs.getClassId());
        vo.setClassName(classMenuDTOs.getClassName());
        classMenuDTOs.getChild().forEach(classMenuDTO -> {
            ClassMenuVO menuVO = new ClassMenuVO();
            buildClassMenuVO(menuVO,classMenuDTO);
            vo.getChild().add(menuVO);
        });
    }

}
