package com.wfzcx.ieos.data.model;


import android.content.Context;

import com.jude.beam.model.AbsModel;
import com.jude.utils.JFileManager;
import com.wfzcx.ieos.app.Const;
import com.wfzcx.ieos.app.Dir;
import com.wfzcx.ieos.data.bean.ResultMap;
import com.wfzcx.ieos.data.bean.ServerAddr;
import com.wfzcx.ieos.data.service.DaggerServiceAPIModuleComponent;
import com.wfzcx.ieos.data.service.DyncUrlInterceptor;
import com.wfzcx.ieos.data.service.SchedulerTransform;
import com.wfzcx.ieos.data.service.ServiceAPI;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
public class AccountModel extends AbsModel {

    @Inject
    ServiceAPI mServiceAPI;

    private static final String FILE_ACCOUNT = "account";

    public static AccountModel getInstance() {
        return getInstance(AccountModel.class);
    }

    private BehaviorSubject<Map> mAccountSubject = BehaviorSubject.create();

    @Override
    protected void onAppCreate(Context ctx) {
        super.onAppCreate(ctx);
        DaggerServiceAPIModuleComponent.builder().build().inject(this);
        // 服务器地址初始化
        ServerAddr mServerAddr = (ServerAddr) JFileManager.getInstance().getFolder(Dir.Object).readObjectFromFile(Const.SERVER_CONFIG);
        if (mServerAddr == null) {
            mServerAddr = new ServerAddr(Const.DEFAULT_SERVER_ADDR, Const.DEFAULT_SERVER_PORT);
            JFileManager.getInstance().getFolder(Dir.Object).writeObjectToFile(mServerAddr, Const.SERVER_CONFIG);

            // 设置HOST和PORT
            DyncUrlInterceptor.HOST = mServerAddr.getIpAddr();
            DyncUrlInterceptor.PORT = mServerAddr.getIpPort();
        }


        //账号持久化
        mAccountSubject.subscribe(account -> {
            if (account == null)
                JFileManager.getInstance().getFolder(Dir.Object).deleteChild(FILE_ACCOUNT);
            else
                JFileManager.getInstance().getFolder(Dir.Object).writeObjectToFile(account, FILE_ACCOUNT);
        });

        //初始化账户
        Observable.just((Map) JFileManager.getInstance().getFolder(Dir.Object).readObjectFromFile(FILE_ACCOUNT))
                .doOnNext(account -> mAccountSubject.onNext(account))
                .subscribe();
    }

    public boolean isLogin() {
        return mAccountSubject.getValue() != null;
    }

    public Observable<Map> login(String username, String password) {
        return mServiceAPI.login(username, password)
                .compose(new SchedulerTransform<>())
                .doOnNext(account -> {
                    account.put("username", username);
                    account.put("password", password);
                    mAccountSubject.onNext(account);
                });
    }

    public void logout() {
        mAccountSubject.onNext(null);
    }

    public Map getAccount() {
        return mAccountSubject.getValue();
    }

}
