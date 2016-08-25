package com.wfzcx.ieos.module.chart;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

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
import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.model.KpiDataModel;
import com.wfzcx.ieos.data.service.ProgressDialogTransform;
import com.wfzcx.ieos.ui.FontIcon;

import java.util.ArrayList;
import java.util.HashMap;
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
 * @date: 2016-08-24
 */
public class KpiMixChartActivity extends BeamBaseActivity {

    @BindView(R.id.fic_action_left)
    FontIcon ficActionLeft;
    @BindView(R.id.tb_title)
    TextView tbTitle;

    @BindView(R.id.chart_view)
    CombinedChart mChart;

    @BindView(R.id.tabs_kpi)
    TabLayout mTabLayout;

    @BindView(R.id.vp_kpi)
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpi_mix_chart);
        ButterKnife.bind(this);

        initToolbar();
        initMixData();

    }

    private void initToolbar() {
        tbTitle.setText("");
        ficActionLeft.setOnClickListener(v -> finish());
    }

    private void initMixData() {
        // 查询当前数据可查询日期
        Map<String, RequestBody> params = new HashMap<>();
        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "dz_base_lastyearmonth"));
        params.put("wheresql", RequestBody.create(MediaType.parse("text/plain"), " b.dscode = 'REGION' "));

        KpiDataModel.getInstance().getKpiData(params)
                .compose(new ProgressDialogTransform<>(KpiMixChartActivity.this, "正在请求数据"))
                .subscribe(rsList -> {
                    if (rsList.isEmpty()) {
                        JUtils.Toast("暂无数据");
                        return;
                    }

                    Map rsMap = rsList.get(0);
                    String date = rsMap.get("yd").toString();
                    String nd = date.substring(0, 4);
                    String yd = date.substring(5, 7);

                    params.put("month_id", RequestBody.create(MediaType.parse("text/plain"), nd + "-" + yd));

                    initChartView(params);
                    initViewPager(params);

                    // 请求图表和表格数据
//                    Map<String, RequestBody> nparams = new HashMap<>();
//                    KpiDataModel.getInstance().getKpiData(nparams)
//                            .compose(new ProgressDialogTransform<>(KpiMixChartActivity.this, "正在请求数据"))
//                            .subscribe(nrsList -> {
//                                initChartView(nrsList);
//
//                            });
                });
    }

    private void initChartView(Map<String, RequestBody> params) {

        String[] titles = {"工业增加值", "主营业务收入", "利润总额", "实现利税"};
        String[] kpiIds = {"500", "13", "26", "550"};

        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "pro_macro_city_region_sql"));
        params.put("tatgetId", RequestBody.create(MediaType.parse("text/plain"), kpiIds[0]));


        KpiDataModel.getInstance().getKpiData(params)
                .compose(new ProgressDialogTransform<>((BeamBaseActivity) this, "正在请求数据"))
                .subscribe(rsList -> {

                    Legend l = mChart.getLegend();
                    l.setWordWrapEnabled(true);
                    l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

                    YAxis rightAxis = mChart.getAxisRight();
                    rightAxis.setDrawGridLines(false);
                    rightAxis.setAxisMinValue(0f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.setDrawGridLines(false);
                    leftAxis.setAxisMinValue(0f);

                    XAxis xAxis = mChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                    xAxis.setAxisMinValue(0f);
                    xAxis.setGranularity(1f);
                    xAxis.setValueFormatter(new AxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            int index = (int) value % rsList.size();
                            return rsList.get(index).get("name").toString();
                        }

                        @Override
                        public int getDecimalDigits() {
                            return 0;
                        }
                    });

                    mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                            CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
                    });

                    CombinedData data = new CombinedData();

                    // 折线数据
                    LineData ld = new LineData();
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    for (int i = 0; i < rsList.size(); i++) {
                        entries.add(new Entry(i, Float.valueOf(String.valueOf(rsList.get(i).get("valAccPy")))));
                    }
                    LineDataSet set = new LineDataSet(entries, "累计增幅");
                    set.setAxisDependency(YAxis.AxisDependency.LEFT);
                    ld.addDataSet(set);
                    data.setData(ld);

                    // 柱状数据
                    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
                    for (int i = 0; i < rsList.size(); i++) {
                        entries1.add(new BarEntry(i, Float.valueOf(String.valueOf(rsList.get(i).get("valAcc")))));
                    }
                    BarDataSet set1 = new BarDataSet(entries1, "累计");
                    set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

                    BarData bd = new BarData(set1);
                    data.setData(bd);

                    mChart.setData(data);
                    mChart.invalidate();

                });


    }

    private void initViewPager(Map<String, RequestBody> params) {

        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            private String[] titles = {"工业增加值", "主营业务收入", "利润总额", "实现利税"};
            private String[] kpiIds = {"500", "13", "26", "550"};

            @Override
            public Fragment getItem(int position) {

                Bundle b = new Bundle();

                params.put("type", RequestBody.create(MediaType.parse("text/plain"), "pro_macro_city_region_sql"));
                params.put("tatgetId", RequestBody.create(MediaType.parse("text/plain"), kpiIds[position]));

                JUtils.Toast("current tabIndex === " + position + "---->" + kpiIds[position]);

                b.putSerializable("params", (HashMap) params);

                KpiGridDataFragment fragment = new KpiGridDataFragment();
                fragment.setArguments(b);

                return fragment;
            }

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);

    }

}
