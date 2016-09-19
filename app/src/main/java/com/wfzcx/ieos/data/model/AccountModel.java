package com.wfzcx.ieos.data.model;


import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jude.beam.model.AbsModel;
import com.jude.utils.JFileManager;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.app.Const;
import com.wfzcx.ieos.app.Dir;
import com.wfzcx.ieos.data.bean.ServerAddr;
import com.wfzcx.ieos.data.service.DaggerServiceAPIModuleComponent;
import com.wfzcx.ieos.data.service.DyncUrlInterceptor;
import com.wfzcx.ieos.data.service.ErrorTransform;
import com.wfzcx.ieos.data.service.SchedulerTransform;
import com.wfzcx.ieos.data.service.ServiceAPI;
import com.wfzcx.ieos.module.settings.UpdateService;

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

    public Observable<Map> modPasw(String username, String oldpasw, String newpasw) {
        return mServiceAPI.modPasw(username, oldpasw, newpasw)
                .compose(new SchedulerTransform<>())
                .compose(new ErrorTransform<>());
    }

    public void logout() {
        mAccountSubject.onNext(null);
    }

    public void setAccount(Map account) {
        mAccountSubject.onNext(account);
    }

    public Map getAccount() {
        return mAccountSubject.getValue();
    }

    public String getUsername() {
        return (String) getAccount().get("username");
    }

    public String getUserCnname() {
        return (String) getAccount().get("userCnname");
    }

    public String getRegionname() {
        String areaStr = "{" +
                "label: '区域'," +
                "keys: ['15', '19','20', '21','22','23','24','25','26','27','28','29','30','31','32','166','167']," +
                "names: ['dzs','dcq','jkq','yhq','lcq','njx','jqx','lyx','qhx','pyx','xjx','wqx','lls','ycx']," +
                "values:  ['德州市', '德城区', '经济开发区','运河开发区','陵城区','宁津县','庆云县','临邑县','齐河县','平原县','夏津县','武城县','乐陵市','禹城市']" +
                "}";

        JSONObject areaJson = JSON.parseObject(areaStr);

        int areaIndex = areaJson.getJSONArray("names").indexOf(getUsername());
        return areaJson.getJSONArray("values").getString(areaIndex);
    }

    public void updateSoft(Context ctx) {
        mServiceAPI.updateSoft()
                .compose(new SchedulerTransform<>())
                .compose(new ErrorTransform<>())
                .subscribe(rsMap -> {
                    if (String.valueOf(rsMap.get("versionname")).equals(JUtils.getAppVersionName())) {
                        JUtils.Toast("已经是最新版本");
                    } else {
                        ServerAddr serverAddr = (ServerAddr) JFileManager.getInstance().getFolder(Dir.Object).readObjectFromFile(Const.SERVER_CONFIG);
                        String baseUrl = Const.HTTP_SUFFIX + serverAddr.getIpAddr() + ":" + serverAddr.getIpPort() + "/" + Const.DEFAULT_SERVER_WEB + "/";
                        String verUrl = baseUrl + rsMap.get("url");

                        String verName = String.valueOf(rsMap.get("versionname"));
                        String verInfo = String.valueOf(rsMap.get("description"));

                        showUpdateDialog(ctx, verName, verInfo, verUrl);
                    }
                });
    }

    private void showUpdateDialog(Context ctx, String versionName, String content, String url) {
        new MaterialDialog.Builder(ctx)
                .title("发现新版本 " + versionName)
                .content(content)
                .canceledOnTouchOutside(false)
                .positiveText("立即升级")
                .negativeText("稍候再说")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        JUtils.Log("Get Start");
                        Intent updateIntent = new Intent(ctx, UpdateService.class);
                        updateIntent.putExtra("title", "正在更新");
                        updateIntent.putExtra("url", url);
                        updateIntent.putExtra("path", findDownLoadDirectory());
                        updateIntent.putExtra("name", ctx.getString(R.string.app_name) + "v" + versionName + ".apk");
                        ctx.startService(updateIntent);
                    }
                })
                .show();

    }

    private String findDownLoadDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            JUtils.Log("找到SD卡");
            return Environment.getExternalStorageDirectory() + "/" + "download/";
        } else {
            JUtils.Log("没有SD卡");
            return Environment.getRootDirectory() + "/" + "download/";
        }
    }

}
