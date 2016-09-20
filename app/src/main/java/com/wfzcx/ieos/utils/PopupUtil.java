package com.wfzcx.ieos.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.widgets.WheelMonthPicker;
import com.aigestudio.wheelpicker.widgets.WheelYearPicker;
import com.wfzcx.ieos.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-09-20
 */
public class PopupUtil {

    private static MaterialDialog.Builder dialogBuilder;

    private static Map<String, Integer> initData = new HashMap<>();

    public static void renderPopupView(Context mContext, List thrdWps, PopupViewListener positiveListener) {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.view_popup_dialog, null);

        final WheelYearPicker yearWp = (WheelYearPicker) popupView.findViewById(R.id.wp_year);
        yearWp.setYearStart(2010);
        yearWp.setYearEnd(Calendar.getInstance().get(Calendar.YEAR));
//        yearWp.setSelectedYear(Calendar.getInstance().get(Calendar.YEAR));

        final WheelMonthPicker monthWp = (WheelMonthPicker) popupView.findViewById(R.id.wp_month);
//        monthWp.setSelectedMonth(Calendar.getInstance().get(Calendar.MONTH));


        final WheelPicker thrdWp = (WheelPicker) popupView.findViewById(R.id.wp_thrd);

        if (thrdWps == null) {
            thrdWp.setVisibility(View.GONE);
        } else {
            thrdWp.setData(thrdWps);
        }

        dialogBuilder = new MaterialDialog.Builder(mContext)
                .title("选择查询条件")
                .customView(popupView, true)
                .showListener(dialogInterface -> {
                    if (initData != null) {
                        yearWp.setSelectedYear(initData.get("curNd"));
                        monthWp.setSelectedMonth(initData.get("curYd"));
                        if (thrdWps != null) {
                            thrdWp.setSelectedItemPosition(initData.get("thrdIndex"));
                        }
                    }
                })
                .positiveText("确定")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    Map<String, Integer> rsMap = new HashMap<String, Integer>();
                    rsMap.put("curNd", yearWp.getCurrentYear());
                    rsMap.put("curYd", monthWp.getCurrentMonth());

                    if (thrdWps != null) {
                        rsMap.put("thrdIndex", thrdWp.getCurrentItemPosition());
                    }

                    positiveListener.setOnPositiveListener(rsMap);
                })
                .negativeText("取消");

    }

    public static void setInitData(Map<String, Integer> rsMap) {
        initData = rsMap;
    }

    public static Map<String, Integer> getInitData() {
        return initData;
    }

    public static void showPopupView() {
        dialogBuilder.show();
    }


    public interface PopupViewListener {

        void setOnPositiveListener(Map<String, Integer> rsMap);

    }

}
