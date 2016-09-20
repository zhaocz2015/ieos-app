package com.wfzcx.ieos.module.dz_macro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.model.KpiDataModel;
import com.wfzcx.ieos.data.service.ErrorTransform;
import com.wfzcx.ieos.utils.PopupUtil;

import java.util.ArrayList;
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
public class dz_macro_pro_region_activity extends BeamBaseActivity {

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
            "keys:  ['500', '13','26', '550']," +
            "values: ['工业增加值', '主营业务收入', '利润总额','实现利税']" +
            "}";

    private JSONObject jsonObj;

    private String curNd;
    private String curYd;

    private String curKpiName = "工业增加值";

    private List<Map> rsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dz_macro_pro_region);
        ButterKnife.bind(this);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            PopupUtil.showPopupView();
        }
        return super.onOptionsItemSelected(item);
    }


    private void renderView() {
        renderPopView();
        renderChartView();
        renderViewPager();
    }

    private void renderPopView() {

        List<String> kpis = jsonObj.getJSONArray("values").toJavaObject(List.class);
        PopupUtil.renderPopupView(this, kpis, new PopupUtil.PopupViewListener() {
            @Override
            public void setOnPositiveListener(Map<String, Integer> rsMap) {
                curNd = String.valueOf(rsMap.get("curNd"));
                curYd = String.valueOf(rsMap.get("curYd") < 9 ? "0" + rsMap.get("curYd") : rsMap.get("curYd"));

                mViewPager.setCurrentItem(rsMap.get("thrdIndex"));
            }
        });

    }

    private void renderChartView() {

        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

//        MyMarkerView marker = new MyMarkerView(getApplicationContext(), R.layout.view_marker_dz_macro_pro_region);
//        mChart.setMarkerView(marker);

        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0f);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(true);


        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void renderViewPager() {

        mViewPager.setOffscreenPageLimit(jsonObj.getJSONArray("values").size());
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                Bundle b = new Bundle();
                b.putSerializable("rsList", (ArrayList) rsList);

                dz_macro_pro_region_table fragment = new dz_macro_pro_region_table();
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
            PopupUtil.getInitData().put("curNd", Integer.valueOf(curNd));
            PopupUtil.getInitData().put("curYd", Integer.valueOf(curYd));
            PopupUtil.getInitData().put("thrdIndex", pos);
            renderData((String) jsonObj.getJSONArray("keys").get(pos));
            return;
        }

        // 首次查询当前数据可查询的日期时间
        Map<String, RequestBody> params = new HashMap<>();
        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "dz_base_lastyearmonth"));
        params.put("wheresql", RequestBody.create(MediaType.parse("text/plain"), " b.dscode = 'REGION' "));
        KpiDataModel.getInstance().getKpiData(params)
                .compose(new ErrorTransform<>())
                .doOnTerminate(() -> getExpansion().dismissProgressDialog())
                .subscribe(rsDates -> {
                    if (rsDates.isEmpty()) {
                        JUtils.Toast("暂无数据");
                    } else {
                        Map rsMap = rsDates.get(0);
                        String date = rsMap.get("yd").toString();
                        curNd = date.substring(0, 4);
                        curYd = date.substring(5, 7);

                        PopupUtil.getInitData().put("curNd", Integer.valueOf(curNd));
                        PopupUtil.getInitData().put("curYd", Integer.valueOf(curYd));
                        PopupUtil.getInitData().put("thrdIndex", pos);

                        renderData((String) jsonObj.getJSONArray("keys").get(pos));

                    }
                });


    }

    private void renderData(String kpi) {

        Map<String, RequestBody> params = new HashMap<>();
        toolbarTitle.setText("山东省" + curNd + "年" + curYd + "月" + "各地市经济指标分析(亿元、%)");

        params.put("month_id", RequestBody.create(MediaType.parse("text/plain"), curNd + "-" + curYd));
        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "pro_macro_city_region_sql"));
        params.put("tatgetId", RequestBody.create(MediaType.parse("text/plain"), kpi));

        KpiDataModel.getInstance().getKpiData(params)
                .doOnTerminate(() -> getExpansion().dismissProgressDialog())
                .subscribe(rsList -> {
                    if (rsList.isEmpty()) {
                        JUtils.Toast("暂无数据");
                    } else {
                        this.rsList = rsList;
                        renderChartData(rsList);
                        renderPagerData();
                    }

                    getExpansion().dismissProgressDialog();
                });
    }

    private void renderChartData(List<Map> rsList) {
        // 重新绘制X轴
        mChart.getXAxis().setLabelCount(rsList.size());
        mChart.getXAxis().setAxisMaxValue(rsList.size());
        mChart.getXAxis().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value % rsList.size();
                if (index == 0 || index == rsList.size()) {
                    return "";
                }

                return rsList.get(index).get("name").toString();
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateBarData(rsList));
        data.setData(generateLineData(rsList));

        mChart.setData(data);
        mChart.invalidate();
    }

    private void renderPagerData() {
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private BarData generateBarData(List<Map> rsList) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int index = 0; index < rsList.size() - 1; index++) {
            entries.add(new BarEntry(index + 1, Float.valueOf(String.valueOf(rsList.get(index + 1).get("valAcc")))));
        }

        BarDataSet set = new BarDataSet(entries, curKpiName + "-累计(亿元)");
        set.setColor(Color.rgb(46, 199, 201));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.9f;

        BarData d = new BarData(set);
        d.setBarWidth(barWidth);

        return d;
    }

    private LineData generateLineData(List<Map> rsList) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < rsList.size() - 1; index++)
            entries.add(new Entry(index + 1f, Float.valueOf(String.valueOf(rsList.get(index + 1).get("valAccPy")))));

        LineDataSet set = new LineDataSet(entries, curKpiName + "-增幅(%)");
        set.setColor(Color.rgb(255, 0, 0));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(74, 135, 238));
        set.setCircleRadius(5f);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(255, 0, 0));

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        d.addDataSet(set);

        return d;
    }


}
