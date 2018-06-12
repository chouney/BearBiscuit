package com.xkr.common.annotation;

import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptLog {

    OptLogModuleEnum moduleEnum();

    OptEnum optEnum();
}
