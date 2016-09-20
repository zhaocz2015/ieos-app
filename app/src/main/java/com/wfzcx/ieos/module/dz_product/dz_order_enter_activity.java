package com.wfzcx.ieos.module.dz_product;

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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.model.AccountModel;
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
public class dz_order_enter_activity extends BeamBaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.chart)
    PieChart mChart;

    @BindView(R.id.tabs_kpi)
    TabLayout mTabLayout;

    @BindView(R.id.vp_kpi)
    ViewPager mViewPager;

    private String jsonStr = "{" +
            "label: '类型'," +
            "keys:  ['hb', 'tb']," +
            "values: ['环比', '同比']" +
            "}";

    private JSONObject jsonObj;

    private String curNd;
    private String curYd;

    private String curKpiName = "工业增加值";

    private List<Map> rsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dz_product_enter);
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
        mChart.setUsePercentValues(true);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(false);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(true);

    }

    private void renderViewPager() {

        mViewPager.setOffscreenPageLimit(jsonObj.getJSONArray("values").size());
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                Bundle b = new Bundle();
                b.putSerializable("rsList", (ArrayList) rsList);

                dz_order_enter_table fragment = new dz_order_enter_table();
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

        String whereSql = " f.nd = e.year and f.fcode = e.code and f.fflag = 0";
        if (!AccountModel.getInstance().getUsername().equals("dzs")) {
            whereSql = " f.nd = e.year and f.fcode = e.code and f.fflag = 0 and e.countyid in ( select s.regionid from ieos.sys_user s where s.username='" + AccountModel.getInstance().getUsername() + "')";
        }

        // 首次查询当前数据可查询的日期时间
        Map<String, RequestBody> params = new HashMap<>();
        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "lastyearmonth3"));
        params.put("tablename", RequestBody.create(MediaType.parse("text/plain"), "ieos.job_deduction01 f,ieos.joc_ent e"));
        params.put("wheresql", RequestBody.create(MediaType.parse("text/plain"), whereSql));
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
        toolbarTitle.setText(AccountModel.getInstance().getRegionname() + "调度订单指数分析(单位：户)");

        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "dz_order_enter_sql"));
        params.put("month_id", RequestBody.create(MediaType.parse("text/plain"), curNd + "-" + curYd));
        params.put("type1", RequestBody.create(MediaType.parse("text/plain"), kpi));

        if (!AccountModel.getInstance().getUsername().equals("dzs")) {
            params.put("whereEnterSql", RequestBody.create(MediaType.parse("text/plain"), " and e.countyid in ( select s.regionid from ieos.sys_user s where s.username='" + AccountModel.getInstance().getUsername() + "')"));
        }

        KpiDataModel.getInstance().getKpiData(params)
                .doOnTerminate(() -> getExpansion().dismissProgressDialog())
                .subscribe(rsList -> {
                    if (rsList.isEmpty()) {
                        JUtils.Toast("暂无数据");
                    } else {
                        this.rsList = rsList;
                        renderChartData(0);
                        renderPagerData();
                    }

                    getExpansion().dismissProgressDialog();
                });
    }

    private String[] pieTitles = {"上涨数", "持平数", "下降数"};
    private String[] pieLabels = {"UpNum", "CpNum", "DownNum"};

    public void renderChartData(int index) {
        // 重新绘制X轴

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (int i = 0; i < pieLabels.length; i++) {
            entries.add(new PieEntry(Float.valueOf(String.valueOf(rsList.get(index).get(pieLabels[i]))), pieTitles[i]));
        }

        PieDataSet dataSet = new PieDataSet(entries, (String) rsList.get(index).get("ROWSNAME"));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);
        mChart.invalidate();
    }

    private void renderPagerData() {
        mViewPager.getAdapter().notifyDataSetChanged();
    }


}
