package com.wfzcx.ieos.module.chart;

import android.support.annotation.NonNull;

import com.jude.beam.expansion.list.BeamListFragmentPresenter;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.data.model.KpiDataModel;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-25
 */
public class KpiGridDataPresenter extends BeamListFragmentPresenter<KpiGridDataFragment, Map> {

    @Override
    protected void onCreateView(@NonNull KpiGridDataFragment view) {
        super.onCreateView(view);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        Map<String, RequestBody> params = (Map<String, RequestBody>) getView().getArguments().getSerializable("params");

        JUtils.Log("DEBUGGGGGG", params.get("tatgetId").toString());

        KpiDataModel.getInstance().getKpiData(params)
                .map(rsList -> {
                    Map tmp = new HashMap();
                    tmp.put("name", "区域");
                    tmp.put("amount", "企业户数");
                    tmp.put("valAcc", "累计");
                    tmp.put("valAccPy", "累计增幅");
                    rsList.add(0, tmp);

                    return rsList;
                })
                .unsafeSubscribe(getRefreshSubscriber());
    }
}
