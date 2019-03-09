package com.xkr.web.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.xkr.common.ErrorStatus;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * 控制器层错误异常处理
 *
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/4
 */
@RestController
public class BasicExceptionHandler extends BasicErrorController {


    public BasicExceptionHandler() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    /**
     * JSON数据化
     * @param request
     * @return
     */
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.TEXT_HTML_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String, Object> body = ImmutableMap.of(
                "code", status.value(),
                "msg", status.getReasonPhrase(),
                "data", ImmutableMap.of(),
                "ext", Collections.EMPTY_MAP
        );
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.put("Access-Control-Allow-Origin", Lists.newArrayList("*"));

        return new ResponseEntity<Map<String, Object>>(body,map ,status);
    }

}
