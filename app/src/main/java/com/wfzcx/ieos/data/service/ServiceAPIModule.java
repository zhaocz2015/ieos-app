package com.wfzcx.ieos.data.service;

import com.jude.utils.JFileManager;
import com.wfzcx.ieos.app.Const;
import com.wfzcx.ieos.app.Dir;
import com.wfzcx.ieos.data.bean.ServerAddr;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
@Module
public class ServiceAPIModule {
    @Singleton
    @Provides
    ServiceAPI provideServiceAPI(String serverAddr, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverAddr)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(ServiceAPI.class);
    }


    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new DyncUrlInterceptor())
                .addInterceptor(interceptor)
                .build();
        return client;
    }

    @Singleton
    @Provides
    String provideServerAddr() {
        ServerAddr serverAddr = (ServerAddr) JFileManager.getInstance().getFolder(Dir.Object).readObjectFromFile(Const.SERVER_CONFIG);
        if (serverAddr == null) {
            return Const.HTTP_SUFFIX + Const.DEFAULT_SERVER_ADDR + ":" + Const.DEFAULT_SERVER_PORT + "/" + Const.DEFAULT_SERVER_WEB + "/";
        }
        return serverAddr.toString();
    }

}
