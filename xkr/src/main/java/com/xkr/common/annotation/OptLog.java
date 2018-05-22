package com.xkr.common.annotation;

import com.xkr.common.OptEnum;
import com.xkr.common.OptLogModuleEnum;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/25
 */
public @interface OptLog {

    OptLogModuleEnum moduleEnum();

    OptEnum optEnum();
}
