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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.CombinedChart;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            PopupUtil.showPopupView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void renderView() {
        renderPopView();
        renderViewPager();
    }

    private void renderPopView() {

        PopupUtil.renderPopupView(this, null, new PopupUtil.PopupViewListener() {
            @Override
            public void setOnPositiveListener(Map<String, Integer> rsMap) {
                curNd = String.valueOf(rsMap.get("curNd"));
                curYd = String.valueOf(rsMap.get("curYd") < 9 ? "0" + rsMap.get("curYd") : rsMap.get("curYd"));

                renderData(0);
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
            PopupUtil.getInitData().put("curNd", Integer.valueOf(curNd));
            PopupUtil.getInitData().put("curYd", Integer.valueOf(curYd));
            renderData((String) jsonObj.getJSONArray("keys").get(pos));
            return;
        }

        // 首次查询当前数据可查询的日期时间
        Map<String, RequestBody> params = new HashMap<>();
        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "dz_rank_lastyearmonth_imp"));
        params.put("wheresql", RequestBody.create(MediaType.parse("text/plain"), " where datasource=1 "));
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
                .doOnTerminate(() -> getExpansion().dismissProgressDialog())
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
