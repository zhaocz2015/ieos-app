package com.wfzcx.ieos.utils;

import android.content.Context;

import com.wfzcx.ieos.app.APP;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-24
 */
public class ResUtil {

    private static Context mContext = APP.getInstance();

    public static int getLayoutId(String paramString) {
        return mContext.getResources().getIdentifier(paramString, "layout",
                mContext.getPackageName());
    }

    public static int getStringId(String paramString) {
        return mContext.getResources().getIdentifier(paramString, "string",
                mContext.getPackageName());
    }

    public static int getDrawableId(String paramString) {
        return mContext.getResources().getIdentifier(paramString,
                "drawable", mContext.getPackageName());
    }

    public static int getStyleId(String paramString) {
        return mContext.getResources().getIdentifier(paramString,
                "style", mContext.getPackageName());
    }

    public static int getId(String paramString) {
        return mContext.getResources().getIdentifier(paramString, "id", mContext.getPackageName());
    }

    public static int getColorId(String paramString) {
        return mContext.getResources().getIdentifier(paramString,
                "color", mContext.getPackageName());
    }

    public static int getArrayId(String paramString) {
        return mContext.getResources().getIdentifier(paramString,
                "array", mContext.getPackageName());
    }

}
