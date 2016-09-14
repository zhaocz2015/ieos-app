package com.wfzcx.ieos.module.kpi;

import android.support.annotation.NonNull;

import com.jude.beam.expansion.list.BeamListFragmentPresenter;

import java.util.ArrayList;
import java.util.Map;

import rx.Observable;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
public class KpiServicePresenter extends BeamListFragmentPresenter<KpiServiceFragment, Map> {


    @Override
    protected void onCreateView(@NonNull KpiServiceFragment view) {
        super.onCreateView(view);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        ArrayList<Map> subMenus = (ArrayList<Map>) getView().getArguments().getSerializable("subMenus");
        Observable.just(subMenus)
                .map(maps -> {
                    for (Map m : maps) {
                        m.put("pkg", getView().getArguments().getString("pkg"));
                    }

                    return maps;
                })
                .unsafeSubscribe(getRefreshSubscriber());
    }
}
