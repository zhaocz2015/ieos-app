package com.wfzcx.ieos.module.kpi;

import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.wfzcx.ieos.R;

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
    RoundedImageView menuIcon;
    @BindView(R.id.tv_menu_name)
    TextView menuName;

    private Map menu;

    public KpiServiceVHolder(ViewGroup parent) {
        super(parent, R.layout.item_kpi_service);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(view -> {
            try {
                Intent i = new Intent(getContext(), Class.forName("com.wfzcx.ieos.module." + menu.get("pkg") + "." + menu.get("code") + "_activity"));
                getContext().startActivity(i);
            } catch (ClassNotFoundException e) {
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
