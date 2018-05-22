package com.xkr.dao.cache;

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
    private final static String CACHE_UPLOADCOUNT_KEY = "xkr_uploadCount";
    private final static String CACHE_DOWNLOADCOUNT_KEY = "xkr_downloadCount";

    public void incrLoginCount(){
        long remainDaySecond = LocalDate.now().plusDays(1).atStartOfDay().
                minus(
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond(),
                        ChronoUnit.SECONDS
                ).toInstant(ZoneOffset.UTC).
                getEpochSecond();
        incrByCount(CACHE_LOGINCOUNT_KEY,1L, (int) remainDaySecond);
    }

    public void incrRegCount(){
        long remainDaySecond = LocalDate.now().plusDays(1).atStartOfDay().
                minus(
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond(),
                        ChronoUnit.SECONDS
                ).toInstant(ZoneOffset.UTC).
                getEpochSecond();
        incrByCount(CACHE_REGCOUNT_KEY,1L, (int) remainDaySecond);
    }

    public void incrUploadCount(){
        long remainDaySecond = LocalDate.now().plusDays(1).atStartOfDay().
                minus(
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond(),
                        ChronoUnit.SECONDS
                ).toInstant(ZoneOffset.UTC).
                getEpochSecond();
        incrByCount(CACHE_UPLOADCOUNT_KEY,1L, (int) remainDaySecond);
    }

    public void incDownLoadCount(){
        long remainDaySecond = LocalDate.now().plusDays(1).atStartOfDay().
                minus(
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond(),
                        ChronoUnit.SECONDS
                ).toInstant(ZoneOffset.UTC).
                getEpochSecond();
        incrByCount(CACHE_DOWNLOADCOUNT_KEY,1L, (int) remainDaySecond);
    }

    public Integer getLoginCount(){
        String value = get(CACHE_LOGINCOUNT_KEY);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public Integer getUploadCount(){
        String value = get(CACHE_UPLOADCOUNT_KEY);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public Integer getRegCount(){
        String value = get(CACHE_REGCOUNT_KEY);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public Integer getDownLoadCount(){
        String value = get(CACHE_DOWNLOADCOUNT_KEY);
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
