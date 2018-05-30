package com.xkr.util;

import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class ArgUtil {

    public static void assertEquals(Object a, Object b) {
        if ((Objects.isNull(a) && Objects.nonNull(b))
                || !a.equals(b)) {
            throw new IllegalArgumentException("assert equal error");
        }
    }

    public static void assertNotEquals(Object a, Object b) {
        if ((Objects.isNull(a) && Objects.isNull(b))
                || Objects.nonNull(a) && a.equals(b)) {
            throw new IllegalArgumentException("assert equal error");
        }
    }

}
