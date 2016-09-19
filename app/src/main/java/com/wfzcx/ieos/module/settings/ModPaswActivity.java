package com.wfzcx.ieos.module.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.model.AccountModel;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-09-19
 */
public class ModPaswActivity extends BeamBaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.et_oldpasw)
    EditText oldpasw;

    @BindView(R.id.et_newpasw)
    EditText newpasw;

    @BindView(R.id.et_rptpasw)
    EditText rptpasw;

    @BindView(R.id.btn_ok)
    Button okBtn;

    private Map accMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_pasw);
        ButterKnife.bind(this);

        accMap = AccountModel.getInstance().getAccount();

        initToolbar();
        okBtn.setOnClickListener(v -> {
            modPasw();
        });
    }

    private void initToolbar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText("修改密码");
    }

    private void modPasw() {
        if (TextUtils.isEmpty(oldpasw.getText().toString())) {
            JUtils.Toast("请输入旧密码");
            return;
        }

        if (TextUtils.isEmpty(newpasw.getText().toString())) {
            JUtils.Toast("请输入新密码");
            return;
        }

        if (TextUtils.isEmpty(rptpasw.getText().toString())) {
            JUtils.Toast("请确认新密码");
            return;
        }

        if (!newpasw.getText().toString().equals(rptpasw.getText().toString())) {
            JUtils.Toast("确认密码不一致，请重新输入");
            return;
        }

        okBtn.setEnabled(false);
        getExpansion().showProgressDialog("正在提交数据");

        AccountModel.getInstance().modPasw(AccountModel.getInstance().getUsername(), oldpasw.getText().toString(), newpasw.getText().toString())
                .doOnTerminate(() -> {
                    okBtn.setEnabled(true);
                    getExpansion().dismissProgressDialog();
                })
                .subscribe(rsMap -> {
                    if (Boolean.valueOf(String.valueOf(rsMap.get("success")))) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        JUtils.Toast(String.valueOf(rsMap.get("msg")));
                        okBtn.setEnabled(true);
                        getExpansion().dismissProgressDialog();
                    }
                });

    }
}
