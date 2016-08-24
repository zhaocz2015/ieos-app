package com.wfzcx.ieos.data.service;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
public class DyncUrlInterceptor implements Interceptor {

    public static String HOST;
    public static int PORT;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (!TextUtils.isEmpty(HOST)) {
            HttpUrl newUrl = originalRequest.url().newBuilder()
                    .host(HOST)
                    .port(PORT)
                    .build();
            originalRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build();
        }

        return chain.proceed(originalRequest);
    }
}