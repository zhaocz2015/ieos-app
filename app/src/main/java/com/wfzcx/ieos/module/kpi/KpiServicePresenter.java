package com.wfzcx.ieos.module.kpi;

import android.support.annotation.NonNull;

import com.jude.beam.expansion.list.BeamListFragmentPresenter;

import java.util.ArrayList;
import java.util.List;
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

    private List<Map> funcs;

    @Override
    protected void onCreateView(@NonNull KpiServiceFragment view) {
        super.onCreateView(view);

        funcs = (List<Map>) getView().getArguments().getSerializable("funcs");
        onRefresh();
    }

    @Override
    public void onRefresh() {
        ArrayList<Map> subMenus = (ArrayList<Map>) getView().getArguments().getSerializable("subMenus");
        for (Map menu : subMenus) {
            menu.put("isFunc", hasFunc(menu));
        }

        Observable.just(subMenus)
                .map(maps -> {
                    for (Map m : maps) {
                        m.put("pkg", getView().getArguments().getString("pkg"));
                        m.put("myFunc", getView().getArguments().getBoolean("myFunc", false));
                    }

                    return maps;
                })
                .unsafeSubscribe(getRefreshSubscriber());
    }

    private boolean hasFunc(Map tmp) {
        if (funcs != null) {

            for (Map func : funcs) {
                if (func.get("code").equals(tmp.get("code"))) {
                    return true;
                }
            }
        }

        return false;
    }
}
