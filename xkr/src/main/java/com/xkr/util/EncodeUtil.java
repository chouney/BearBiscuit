package com.xkr.util;

import main.java.com.upyun.UpException;
import main.java.com.upyun.UpYunUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.tomcat.util.security.MD5Encoder;
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

    public static String md5(Object obj){
        return new Md5Hash(obj).toString();
    }

    public static String shaEnc(Object source){
        return new SimpleHash("sha-1",source).toHex();
    }


    //admintest 66d4aaa5ea177ac32c69946de3731ec0
    //admin 21232f297a57a5a743894a0e4a801fc3
    //19921221 1929756b328478455b81d249f68d41aa
    public static void main(String[] args) throws UpException {
//        System.out.println(md5("19921221"));
        String date = DateUtil.getGMTRFCUSDate();
        String uri = "/sharecoder/xkr/dev/6545627560447840256/f83975f363dd296366da248d6fc61d67/%E6%96%B0%E5%BB%BA%E6%96%87%E4%BB%B6%E5%A4%B9.zip";
        System.out.println(UpYunUtils.sign("GET",date,uri,"zhangqixiang",UpYunUtils.md5("zhangqixiang"),null));
        System.out.println(date);
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


}
