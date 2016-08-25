package com.wfzcx.ieos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wfzcx.ieos.data.model.AccountModel;
import com.wfzcx.ieos.module.login.LoginActivity;
import com.wfzcx.ieos.module.main.MainActivity;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-11
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class clazz = LoginActivity.class;
        if (AccountModel.getInstance().isLogin()) {
            clazz = MainActivity.class;
        }

        startActivity(new Intent(this, clazz));

        finish();
    }
}
