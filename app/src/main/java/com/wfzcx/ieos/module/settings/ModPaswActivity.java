package com.wfzcx.ieos.module.settings;

import android.os.Bundle;

import com.jude.beam.expansion.BeamBaseActivity;
import com.wfzcx.ieos.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_pasw);
        ButterKnife.bind(this);

    }
}
