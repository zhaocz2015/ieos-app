package com.wfzcx.ieos.data.service;

import com.wfzcx.ieos.data.model.AccountModel;
import com.wfzcx.ieos.data.model.KpiDataModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
@Singleton
@Component(modules = {ServiceAPIModule.class})
public interface ServiceAPIModuleComponent {
    void inject(AccountModel model);
    void inject(KpiDataModel model);
}
