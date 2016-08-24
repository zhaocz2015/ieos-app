package com.wfzcx.ieos.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JFileManager;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.app.Const;
import com.wfzcx.ieos.app.Dir;
import com.wfzcx.ieos.data.bean.ServerAddr;
import com.wfzcx.ieos.data.model.AccountModel;
import com.wfzcx.ieos.data.service.DyncUrlInterceptor;
import com.wfzcx.ieos.data.service.ErrorTransform;
import com.wfzcx.ieos.module.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
public class LoginActivity extends BeamBaseActivity {


    private static final String IP_FORMULA = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_setting)
    Button btnSetting;

    private long mLastBackPressTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_login, R.id.btn_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login: //登录
                if (TextUtils.isEmpty(etUsername.getText())) {
                    etUsername.requestFocus();
                    JUtils.Toast("请输入用户名");
                    return;
                }

                if (TextUtils.isEmpty(etPassword.getText())) {
                    etPassword.requestFocus();
                    JUtils.Toast("请输入密码");
                    return;
                }

                // 登录
                btnLogin.setEnabled(false);
                getExpansion().showProgressDialog("正在登录");
                AccountModel.getInstance().login(etUsername.getText().toString(), etPassword.getText().toString())
                        .compose(new ErrorTransform<>())
                        .doOnTerminate(() -> {
                            btnLogin.setEnabled(true);
                            getExpansion().dismissProgressDialog();
                        })
                        .subscribe(rsMap -> {
                            if (Boolean.valueOf(String.valueOf(rsMap.get("success")))) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }else{
                                JUtils.Toast("登录失败，无效的用户名和密码");
                                btnLogin.setEnabled(true);
                                getExpansion().dismissProgressDialog();
                            }
                        });

                break;
            case R.id.btn_setting: //设置
                // 弹出IP配置窗口
                showIpcfgDialog();
                break;
        }
    }


    private void showIpcfgDialog() {
        MaterialDialog ipDlg = new MaterialDialog.Builder(this)
                .customView(R.layout.view_ip_settings, true)
                .backgroundColorRes(R.color.color_f7f7f9)
                .show();


        // 服务器IP
        EditText etIp = (EditText) ipDlg.getCustomView().findViewById(R.id.et_ip);
        etIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!etIp.getText().toString().matches(IP_FORMULA)) {
                    etIp.requestFocus();
                    JUtils.Toast("请输入正确格式的IP地址");
                }
            }
        });

        // 服务器端口
        EditText etPort = (EditText) ipDlg.getCustomView().findViewById(R.id.et_port);

        // 读取服务器配置信息
        ServerAddr ipConfig = (ServerAddr) JFileManager.getInstance().getFolder(Dir.Object).readObjectFromFile(Const.SERVER_CONFIG);
        if (ipConfig != null) {
            etIp.setText(ipConfig.getIpAddr());
            etPort.setText(String.valueOf(ipConfig.getIpPort()));
        }

        // 确定
        Button okBtn = (Button) ipDlg.getCustomView().findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etIp.getText())) {
                etIp.requestFocus();
                JUtils.Toast("请输入服务器IP地址");
                return;
            }

            if (!etIp.getText().toString().matches(IP_FORMULA)) {
                etIp.requestFocus();
                JUtils.Toast("请输入正确格式的IP地址");
                return;
            }

            if (TextUtils.isEmpty(etPort.getText())) {
                etPort.requestFocus();
                JUtils.Toast("请输入服务器端口号");
                return;
            }

            // 保存IP配置信息
            ServerAddr mServerAddr = new ServerAddr(etIp.getText().toString(), Integer.valueOf(etPort.getText().toString()));
            JFileManager.getInstance().getFolder(Dir.Object).writeObjectToFile(mServerAddr, Const.SERVER_CONFIG);

            // 设置HOST和PORT
            DyncUrlInterceptor.HOST = mServerAddr.getIpAddr();
            DyncUrlInterceptor.PORT = mServerAddr.getIpPort();

            ipDlg.dismiss();
        });

        //取消
        Button cancelBtn = (Button) ipDlg.getCustomView().findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(v -> ipDlg.dismiss());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long mNowBackPressTime = System.currentTimeMillis();
            if (mNowBackPressTime - mLastBackPressTime > 2000) {
                JUtils.Toast("再次点击退出月报通");
                mLastBackPressTime = mNowBackPressTime;
            } else {
                this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
