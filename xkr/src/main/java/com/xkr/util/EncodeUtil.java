package com.xkr.util;

import org.apache.shiro.codec.Base64;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
public class EncodeUtil {
    /**
     * base64加密
     * @param str
     * @return
     */
    public static String encBase64(String str){
        return Base64.encodeToString(str.getBytes());
    }

    /**
     * base64解密
     * @param str
     * @return
     */
    public static String decBase64(String str){
        return Base64.decodeToString(str);
    }

    /**
     * 生成邮箱验证码
     * @param param
     * @return
     */
    public static String createEmailValidateString(String ... param){
        if(param == null || param.length == 0){
            return "";
        }
        StringBuilder sb = new StringBuilder(param[0]);
        for(int i = 1 ; i<param.length;i++){
            sb.append("#").append(param[i]);
        }
        return encBase64(sb.toString());
    }

    /**
     * 解析邮箱验证码
     * @param validateString
     * @return
     */
    public static String[] getEmailValidateString(String validateString){
        if(StringUtils.isEmpty(validateString)){
            return new String[0];
        }
        return decBase64(validateString).split("#");
    }


    public static void main(String[] args){
        String code = createEmailValidateString(new Date().toString(),"4124124124");
        System.out.println(code);
        System.out.println(Arrays.toString(getEmailValidateString(code)));
    }
}
