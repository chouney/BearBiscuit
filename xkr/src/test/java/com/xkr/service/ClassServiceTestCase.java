package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.xkr.BaseServiceTest;
import com.xkr.MockServiceTest;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.dto.clazz.ClassMenuDTO;
import com.xkr.domain.entity.XkrClass;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class ClassServiceTestCase extends BaseServiceTest {

    @Autowired
    private ClassService classService;

    @Test
    public void testGetAllChildClassByClassId() {

        ClassMenuDTO list1 = classService.getAllChildClassByClassId(1L);
        System.out.println(JSON.toJSONString(list1));
    }

}
