package com.xkr.util;

import java.math.BigDecimal;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class MoneyUtil {

    public static String fen2yuan(long fen) {
        return String.valueOf(new BigDecimal(fen).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100)).doubleValue());
    }

    public static long yuan2fen(String yuan){
        return new BigDecimal(yuan).setScale(2,BigDecimal.ROUND_FLOOR).multiply(new BigDecimal(100)).longValue();
    }

}
