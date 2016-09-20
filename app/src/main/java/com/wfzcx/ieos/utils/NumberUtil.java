package com.wfzcx.ieos.utils;

import java.math.BigDecimal;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-09-20
 */
public class NumberUtil {

    public static Integer parseIntVal(Object numObj) {
        if (numObj == null) {
            return 0;
        }

        return new BigDecimal(String.valueOf(numObj)).intValue();
    }

}
