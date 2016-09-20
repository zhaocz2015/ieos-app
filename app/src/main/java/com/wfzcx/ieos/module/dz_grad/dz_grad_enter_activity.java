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
public class dz_grad_enter_activity extends BeamBaseActivity {

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

    private String areaStr = "{" +
            "label: '区域'," +
            "keys:  ['15', '19','20', '21','22','23','24','25','26','27','28','29','30','31','32','166','167']," +
            "names: ['dzs','dcq','jkq','yhq','lcq','njx','jqx','lyx','qhx','pyx','xjx','wqx','lls','ycx']," +
            "values: ['德州市', '德城区', '经济开发区','运河开发区','陵城区','宁津县','庆云县','临邑县','齐河县','平原县','夏津县','武城县','乐陵市','禹城市']" +
            "}";

    private JSONObject jsonObj;
    private JSONObject areaObj;

    private String curNd;
    private String curYd;

    private String curKpiName = "主营业务收入";
    private String curAreaIndex = "";

    private List<Map> rsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dz_macro_pro_region);
        ButterKnife.bind(this);

        mChart.setVisibility(View.GONE);

        jsonObj = JSON.parseObject(jsonStr);
        areaObj = JSON.parseObject(areaStr);

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

                dz_grad_enter_table fragment = new dz_grad_enter_table();
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
        params.put("wheresql", RequestBody.create(MediaType.parse("text/plain"), " where datasource=2 "));
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
        toolbarTitle.setText(curNd + "年" + curYd + "月企业绩效评价");

        params.put("type", RequestBody.create(MediaType.parse("text/plain"), "dz_ent_rank_sql"));
        params.put("nd", RequestBody.create(MediaType.parse("text/plain"), curNd));
        params.put("yd", RequestBody.create(MediaType.parse("text/plain"), curYd));

        String whereSql = "";
        if (!AccountModel.getInstance().getUsername().equals("dzs")) {
            int areaIndex = areaObj.getJSONArray("names").indexOf(AccountModel.getInstance().getUsername());
            whereSql = " and qybh != 'average' and qybh in (select code from ieos.joc_ent t  where countyid = " + areaObj.getJSONArray("keys").get(areaIndex) + " )";
        } else {
            if (areaObj.getJSONArray("keys").getIntValue(0) == 15) {
                whereSql = " and qybh != 'average' ";
            } else {
                whereSql = " and qybh != 'average' and qybh in (select code from ieos.joc_ent t  where countyid = " + areaObj.getJSONArray("keys").getIntValue(0) + " )";
            }
        }

        params.put("whereSql", RequestBody.create(MediaType.parse("text/plain"), whereSql));

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
