package com.xkr.dao.cache;

import com.xkr.domain.XkrClassAgent;
import com.xkr.domain.XkrResourceAgent;
import com.xkr.util.DateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/16
 */
@Service
public class AdminIndexRedisService extends BaseRedisService{

    private final static String CACHE_LOGINCOUNT_KEY = "xkr_loginCount";
    private final static String CACHE_REGCOUNT_KEY = "xkr_regCount";
    private final static String CACHE_RES_UPLOADCOUNT_KEY = "xkr_resource_uploadCount";
    private final static String CACHE_RES_DOWNLOADCOUNT_KEY = "xkr_resource_downloadCount";
    private final static String CACHE_DES_UPLOADCOUNT_KEY = "xkr_design_uploadCount";
    private final static String CACHE_DES_DOWNLOADCOUNT_KEY = "xkr_design_downloadCount";

    public void incrLoginCount(){
        long remainDaySecond = DateUtil.getTodayRemainSecond();
        incrByCount(CACHE_LOGINCOUNT_KEY,1L, (int) remainDaySecond);
    }

    public void incrRegCount(){
        long remainDaySecond = DateUtil.getTodayRemainSecond();
        incrByCount(CACHE_REGCOUNT_KEY,1L, (int) remainDaySecond);
    }

    public void incrUploadCount(int classId){
        long remainDaySecond = DateUtil.getTodayRemainSecond();
        if(XkrClassAgent.ROOT_DESIGN_CLASS_ID == classId) {
            incrByCount(CACHE_DES_UPLOADCOUNT_KEY, 1L, (int) remainDaySecond);
        }else if(XkrClassAgent.ROOT_RESOURCE_CLASS_ID == classId){
            incrByCount(CACHE_RES_UPLOADCOUNT_KEY, 1L, (int) remainDaySecond);
        }
    }

    public void incDownLoadCount(int classId){
        long remainDaySecond = DateUtil.getTodayRemainSecond();
        if(XkrClassAgent.ROOT_DESIGN_CLASS_ID == classId) {
            incrByCount(CACHE_DES_DOWNLOADCOUNT_KEY, 1L, (int) remainDaySecond);
        }else if(XkrClassAgent.ROOT_RESOURCE_CLASS_ID == classId){
            incrByCount(CACHE_RES_DOWNLOADCOUNT_KEY, 1L, (int) remainDaySecond);
        }
    }

    public Integer getLoginCount(){
        String value = get(CACHE_LOGINCOUNT_KEY);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public Integer getUploadCount(int classId){
        String value = null;
        if(XkrClassAgent.ROOT_DESIGN_CLASS_ID == classId) {
            value = get(CACHE_DES_UPLOADCOUNT_KEY);
        }else if(XkrClassAgent.ROOT_RESOURCE_CLASS_ID == classId){
            value = get(CACHE_RES_UPLOADCOUNT_KEY);
        }
        return value == null ? 0 : Integer.valueOf(value);
    }

    public Integer getRegCount(){
        String value = get(CACHE_REGCOUNT_KEY);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public Integer getDownLoadCount(int classId){
        String value = null;
        if(XkrClassAgent.ROOT_DESIGN_CLASS_ID == classId) {
            value = get(CACHE_DES_DOWNLOADCOUNT_KEY);
        }else if(XkrClassAgent.ROOT_RESOURCE_CLASS_ID == classId){
            value = get(CACHE_RES_DOWNLOADCOUNT_KEY);
        }
        return value == null ? 0 : Integer.valueOf(value);
    }

//    public static void main(String[] args){
//        System.out.println(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond());
//        System.out.println(LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond());
//        System.out.println(
//                LocalDate.now().plusDays(1).atStartOfDay().
//                minus(
//                        LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond(),
//                        ChronoUnit.SECONDS
//                ).toInstant(ZoneOffset.UTC).
//                getEpochSecond());
//    }
}
