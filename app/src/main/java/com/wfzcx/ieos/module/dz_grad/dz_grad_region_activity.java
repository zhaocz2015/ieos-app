package com.wfzcx.ieos.module.dz_grad;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.CombinedChart;
import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.model.KpiDataModel;
import com.wfzcx.ieos.data.service.ErrorTransform;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-09-13
 */
public class dz_grad_region_activity extends BeamBaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.chart)
    CombinedChart mChart;

    @BindView(R.id.tabs_kpi)
    TabLayout mTabLayout;

    @BindView(R.id.vp_kpi)
    ViewPager mViewPager;

    private String jsonStr = "{" +
            "label: '指标'," +
            "keys:  ['33']," +
            "values: ['工业总产值']" +
            "}";

    private JSONObject jsonObj;

    private String curNd;
    private String curYd;

    private String curKpiName = "主营业务收入";

    private List<Map> rsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dz_macro_pro_region);
        ButterKnife.bind(this);

        mChart.setVisibility(View.GONE);

        jsonObj = JSON.parseObject(jsonStr);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        renderView();
        renderData(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private WheelView yearWheelView;
    private WheelView monthWheelView;
    private WheelView kpiWheelView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // 搜索
            View wheelView = View.inflate(this, R.layout.view_search_dz_macro_pro_region, null);
            initWheelView(wheelView);
            new MaterialDialog.Builder(this)
                    .title("查询条件")
                    .customView(wheelView, true)
                    .positiveText("确定")
                    .onPositive((dialog, which) -> {
                        getExpansion().showProgressDialog("正在加载数据");

                        curNd = (String) yearWheelView.getSelectionItem();
                        curYd = (String) monthWheelView.getSelectionItem();

                        mViewPager.setCurrentItem(kpiWheelView.getCurrentPosition());
                    })
                    .show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void initWheelView(View wheelView) {
        List<String> years = new ArrayList<>();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2010; i <= curYear; i++) {
            years.add(i + "");
        }

        yearWheelView = (WheelView) wheelView.findViewById(R.id.wv_year);
        yearWheelView.setSkin(WheelView.Skin.Common);
        yearWheelView.setWheelAdapter(new ArrayWheelAdapter(this));
        yearWheelView.setWheelData(years);
        yearWheelView.setSelection(curYear - 2010);

        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                months.add("0" + i);
                continue;
            }

            months.add(i + "");
        }
        monthWheelView = (WheelView) wheelView.findViewById(R.id.wv_month);
        monthWheelView.setSkin(WheelView.Skin.Common);
        monthWheelView.setWheelAdapter(new ArrayWheelAdapter(this));
        monthWheelView.setWheelData(months);
        monthWheelView.setSelection(Integer.valueOf(curYd) - 1);


        int curIndex = 0;
        List<String> kpis = jsonObj.getJSONArray("values").toJavaObject(List.class);
        for (int i = 0; i < kpis.size(); i++) {
            if (curKpiName.equals(kpis.get(i))) {
                curIndex = i;
                break;
            }
        }
        kpiWheelView = (WheelView) wheelView.findViewById(R.id.wv_kpi);
        kpiWheelView.setSkin(WheelView.Skin.Common);
        kpiWheelView.setWheelAdapter(new ArrayWheelAdapter(this));
        kpiWheelView.setWheelData(kpis);
        kpiWheelView.setSelection(curIndex);

    }

    private void renderView() {
        renderViewPager();
    }

    private void renderViewPager() {

        mViewPager.setOffscreenPageLimit(jsonObj.getJSONArray("values").size());
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                Bundle b = new Bundle();
                b.putSerializable("rsList", (ArrayList) rsList);
                b.putInt("index", position);

                dz_grad_region_table fragment = new dz_grad_region_table();
                fragment.setArguments(b);

                return fragment;
            }

            @Override
            public int getCount() {
                return jsonObj.getJSONArray("values").size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return (CharSequence) jsonObj.getJSONArray("values").get(position);
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setVisibility(View.GONE);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                renderData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void renderData(int pos) {
        curKpiName = (String) jsonObj.getJSONArray("values").get(pos);
        getExpansion().showProgressDialog("正在加载数据");

        if (curNd != null && curYd != null) {
            renderData((String) jsonObj.getJSONArray("keys").get(pos));
            return;
        }

        // 首次查询当前数据可查询的日期时间
        Map<String, RequestBody> params = new HashMap<>();
        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "dz_rank_lastyearmonth_imp"));
        params.put("wheresql", RequestBody.create(MediaType.parse("text/plain"), " where datasource=1 "));
        KpiDataModel.getInstance().getKpiData(params)
                .compose(new ErrorTransform<>())
                .subscribe(rsDates -> {
                    if (rsDates.isEmpty()) {
                        JUtils.Toast("暂无数据");
                    } else {
                        Map rsMap = rsDates.get(0);
                        String date = rsMap.get("yd").toString();
                        curNd = date.substring(0, 4);
                        curYd = date.substring(5, 7);

                        renderData((String) jsonObj.getJSONArray("keys").get(pos));

                    }
                });


    }

    private void renderData(String kpi) {
        Map<String, RequestBody> params = new HashMap<>();
        toolbarTitle.setText("德州市" + curNd + "年" + curYd + "月绩效评价");

        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "dz_rank_sql"));
        params.put("nd", RequestBody.create(MediaType.parse("text/plain"), curNd));
        params.put("yd", RequestBody.create(MediaType.parse("text/plain"), curYd));

        KpiDataModel.getInstance().getKpiData(params)
                .subscribe(rsList -> {
                    if (rsList.isEmpty()) {
                        JUtils.Toast("暂无数据");
                    } else {
                        this.rsList = rsList;
                        renderPagerData();
                    }

                    getExpansion().dismissProgressDialog();
                });
    }


    private void renderPagerData() {
        mViewPager.getAdapter().notifyDataSetChanged();
    }

}
