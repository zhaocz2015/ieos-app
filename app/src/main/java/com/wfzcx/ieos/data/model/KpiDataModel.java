package com.wfzcx.ieos.data.model;

import android.content.Context;

import com.jude.beam.model.AbsModel;
import com.wfzcx.ieos.data.service.DaggerServiceAPIModuleComponent;
import com.wfzcx.ieos.data.service.ErrorTransform;
import com.wfzcx.ieos.data.service.SchedulerTransform;
import com.wfzcx.ieos.data.service.ServiceAPI;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.RequestBody;
import rx.Observable;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-24
 */
public class KpiDataModel extends AbsModel {

    @Inject
    ServiceAPI mServiceAPI;

    public static KpiDataModel getInstance() {
        return getInstance(KpiDataModel.class);
    }


    @Override
    protected void onAppCreate(Context ctx) {
        super.onAppCreate(ctx);
        DaggerServiceAPIModuleComponent.builder().build().inject(this);
    }

    public Observable<List<Map>> getKpiData(Map<String, RequestBody> params) {
        return mServiceAPI.getData(params)
                .compose(new SchedulerTransform<>())
                .compose(new ErrorTransform<>());
    }

}
