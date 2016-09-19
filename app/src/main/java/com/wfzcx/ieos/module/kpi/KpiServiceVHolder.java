package com.wfzcx.ieos.module.kpi;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.module.settings.MyFuncsActivity;
import com.wfzcx.ieos.utils.ResUtil;

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

    @BindView(R.id.iv_menu_ok)
    ImageView menuOk;

    @BindView(R.id.tv_menu_name)
    TextView menuName;

    private Map menu;

    public KpiServiceVHolder(ViewGroup parent, Activity target) {
        super(parent, R.layout.item_kpi_service);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(view -> {
            if (Boolean.valueOf(String.valueOf(menu.get("myFunc"))) && target instanceof MyFuncsActivity) {

                if (view.getTag() == null) {
                    menuOk.setVisibility(View.VISIBLE);
                    view.setTag(true);
                } else {
                    menuOk.setVisibility(View.GONE);
                    view.setTag(null);
                }

                ((MyFuncsActivity) target).renderFuncData(menu);

            } else {

                try {
                    Intent i = new Intent(getContext(), Class.forName("com.wfzcx.ieos.module." + menu.get("pkg") + "." + menu.get("code") + "_activity"));
                    getContext().startActivity(i);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void setData(Map data) {
        menu = data;

        if (Boolean.valueOf(String.valueOf(menu.get("myFunc")))) {
            if (Boolean.valueOf(String.valueOf(data.get("isFunc")))) {
                menuOk.setVisibility(View.VISIBLE);
                itemView.setTag(true);
            } else {
                menuOk.setVisibility(View.GONE);
                itemView.setTag(null);
            }
        }

        String iconStr = data.get("img").toString().replaceAll("img/", "").replaceAll(".png", "");
        ResUtil.setImageRes(menuIcon, iconStr);
        menuName.setText((String) menu.get("name"));
    }

    public static void setImageRes(ImageView imgView, String imageName) {
        try {
            Field field = Class.forName("com.wfzcx.ieos.R$drawable").getField(imageName);
            imgView.setImageResource(field.getInt(field));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
