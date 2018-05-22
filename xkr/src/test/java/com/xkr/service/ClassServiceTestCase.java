package com.xkr.service;

import com.alibaba.fastjson.JSON;
import com.xkr.MockServiceTest;
import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.dto.clazz.ClassMenuDTO;
import com.xkr.domain.entity.XkrClass;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
public class ClassServiceTestCase extends MockServiceTest {

    @InjectMocks
    private ClassService classService = new ClassService();

    @Mock
    private XkrClassAgent classAgent ;

    @Test
    public void testGetAllChildClassByClassId() {
        List<XkrClass> list = Lists.newArrayList();
        XkrClass xkrClass1 = new XkrClass();
        xkrClass1.setId(1L);
        xkrClass1.setClassName("毕设");
        xkrClass1.setStatus((byte)1);
        xkrClass1.setParentClassId(0L);
        xkrClass1.setPath("0-1");
        XkrClass xkrClass2 = new XkrClass();
        xkrClass2.setId(2L);
        xkrClass2.setClassName("资源");
        xkrClass2.setStatus((byte)1);
        xkrClass2.setParentClassId(0L);
        xkrClass2.setPath("0-2");
        XkrClass xkrClass3 = new XkrClass();
        xkrClass3.setId(4124124214124L);
        xkrClass3.setClassName("毕设1-1");
        xkrClass3.setStatus((byte)1);
        xkrClass3.setParentClassId(1L);
        xkrClass3.setPath("0-1-4124124214124");
        XkrClass xkrClass4 = new XkrClass();
        xkrClass4.setId(12312125125125L);
        xkrClass4.setClassName("毕设1-1-1");
        xkrClass4.setStatus((byte)1);
        xkrClass4.setParentClassId(4124124214124L);
        xkrClass4.setPath("0-1-4124124214124-12312125125125L");
        list.add(xkrClass1);
        list.add(xkrClass3);
        list.add(xkrClass4);

        when(classAgent.getAllChildClassByClassId(anyLong())).thenReturn(list);
        ClassMenuDTO list1 = classService.getAllChildClassByClassId(1L);
        System.out.println(JSON.toJSONString(list1));
    }

}
