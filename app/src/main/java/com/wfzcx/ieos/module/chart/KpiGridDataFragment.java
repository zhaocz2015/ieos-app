package com.wfzcx.ieos.module.chart;

import android.view.ViewGroup;

import com.jude.beam.bijection.RequiresPresenter;
import com.jude.beam.expansion.list.BeamListFragment;
import com.jude.beam.expansion.list.ListConfig;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.util.Map;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-24
 */
@RequiresPresenter(KpiGridDataPresenter.class)
public class KpiGridDataFragment extends BeamListFragment<KpiGridDataPresenter, Map> {

    @Override
    public BaseViewHolder<Map> getViewHolder(ViewGroup parent, int viewType) {
        return new KpiGridDataVHolder(parent);
    }

    @Override
    public ListConfig getConfig() {
        return super.getConfig().setNoMoreAble(false);
    }
}
