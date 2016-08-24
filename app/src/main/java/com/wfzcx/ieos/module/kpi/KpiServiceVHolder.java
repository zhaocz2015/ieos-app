package com.wfzcx.ieos.module.kpi;

import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.module.chart.ChartType;
import com.wfzcx.ieos.module.chart.KpiGridChartActivity;
import com.wfzcx.ieos.module.chart.KpiMixChartActivity;
import com.wfzcx.ieos.module.chart.KpiPieChartActivity;
import com.wfzcx.ieos.utils.ResUtil;

import org.json.JSONObject;

import java.lang.reflect.Field;
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
public class KpiServiceVHolder extends BaseViewHolder<Map> {


    @BindView(R.id.iv_menu_icon)
    ImageView menuIcon;
    @BindView(R.id.tv_menu_name)
    TextView menuName;

    private Map menu;

    public KpiServiceVHolder(ViewGroup parent) {
        super(parent, R.layout.item_kpi_service);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(view -> {

            Class clazz = null;
            try {
                JSONObject jsonObj = new JSONObject(getContext().getResources().getString(ResUtil.getStringId((String) menu.get("func"))));
                jsonObj.put("menuCode", menu.get("func"));
                jsonObj.put("menuName", menu.get("name"));

                if (ChartType.mix.toString().equals(jsonObj.getString("chart"))) {
                    clazz = KpiMixChartActivity.class;
                } else if (ChartType.pie.toString().equals(jsonObj.getString("chart"))) {
                    clazz = KpiPieChartActivity.class;
                } else if (ChartType.grid.toString().equals(jsonObj.getString("chart"))) {
                    clazz = KpiGridChartActivity.class;
                }

                Intent i = new Intent(getContext(), clazz);
                i.putExtra("jsonStr", jsonObj.toString());
                getContext().startActivity(i);

            } catch (Exception e) {
                JUtils.Toast("即将开放，敬请期待");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setData(Map data) {
        menu = data;

        String iconStr = data.get("img").toString().replaceAll("img/", "").replaceAll(".png", "");
        setImageRes(iconStr);
        menuName.setText((String) menu.get("name"));
    }

    private void setImageRes(String imageName) {
        try {
            Field field = Class.forName("com.wfzcx.ieos.R$drawable").getField(imageName);
            menuIcon.setImageResource(field.getInt(field));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
