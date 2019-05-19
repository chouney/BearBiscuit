/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.xkr.common;

/**
 * author xkr
 * date 2017/3/9 0009 下午 16:02
 */
public enum  LoginEnum {

    CUSTOMER("1"),ADMIN("2");

    private String type;

    LoginEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public  String toString(){
        return this.type;
    }
}
