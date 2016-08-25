package com.wfzcx.ieos.module.chart;

import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.wfzcx.ieos.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-25
 */
public class KpiGridDataVHolder extends BaseViewHolder<Map> {


    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_val_acc)
    TextView tvValAcc;
    @BindView(R.id.tv_val_acc_py)
    TextView tvValAccPy;

    public KpiGridDataVHolder(ViewGroup parent) {
        super(parent, R.layout.item_kpi_grid_data);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setData(Map data) {
        tvName.setText(data.get("name").toString());
        tvAmount.setText(data.get("amount").toString());
        tvValAcc.setText(data.get("valAcc").toString());
        tvValAccPy.setText(data.get("valAccPy").toString());
    }
}
