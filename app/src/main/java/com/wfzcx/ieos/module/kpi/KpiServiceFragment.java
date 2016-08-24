package com.wfzcx.ieos.module.kpi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.beam.bijection.RequiresPresenter;
import com.jude.beam.expansion.list.BeamListFragment;
import com.jude.beam.expansion.list.ListConfig;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.bean.ResultMap;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
@RequiresPresenter(KpiServicePresenter.class)
public class KpiServiceFragment extends BeamListFragment<KpiServicePresenter, Map> {

    private View rootView;

    @BindView(R.id.recycler)
    EasyRecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootView);

            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
        return rootView;
    }

    @Override
    public BaseViewHolder<Map> getViewHolder(ViewGroup parent, int viewType) {
        return new KpiServiceVHolder(parent);
    }

    @Override
    public ListConfig getConfig() {
        return super.getConfig().setNoMoreAble(false).setLoadmoreAble(false);
    }
}
