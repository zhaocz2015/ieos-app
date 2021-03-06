package com.wfzcx.ieos.data.service;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
public interface ServiceAPI {

    @POST("permissions/LoginController/login.do")
    @FormUrlEncoded
    Observable<Map> login(@Field("usercode") String username, @Field("passwd") String password);

    @POST("permissions/LoginController/modify.do")
    @FormUrlEncoded
    Observable<Map> modPasw(@Field("usercode") String username, @Field("passwd") String oldpasw, @Field("newpasswd") String newpasw);

    @GET("softupdate/SoftUpdateController/getSoftVersionInfo.do")
    Observable<Map> updateSoft();

    @Multipart
    @POST("queryData/query.do")
    Observable<List<Map>> getData(@PartMap Map<String, RequestBody> params);

}
