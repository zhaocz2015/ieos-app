package com.wfzcx.ieos.module.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.app.Const;
import com.wfzcx.ieos.data.model.AccountModel;
import com.wfzcx.ieos.module.kpi.KpiServiceFragment;
import com.wfzcx.ieos.module.login.LoginActivity;
import com.wfzcx.ieos.module.settings.ModPaswActivity;
import com.wfzcx.ieos.module.settings.MyFuncsActivity;
import com.wfzcx.ieos.utils.ResUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-11
 */
public class MainActivity extends BeamBaseActivity {

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.tabs_kpi)
    TabLayout mTabLayout;

    @BindView(R.id.vp_kpi)
    ViewPager mViewPager;

    @BindView(R.id.recycler)
    EasyRecyclerView funcRecylcer;
    RecyclerArrayAdapter funcAdapter;

    private long mLastBackPressTime = 0;

    private Map accMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 检查版本
//        AccountModel.getInstance().updateSoft(MainActivity.this);

        accMap = AccountModel.getInstance().getAccount();
        initNavigationView();
        initViewPager();
        initFuncRecycler();

    }

    private void initViewPager() {
        List<Map> appMenus = (List<Map>) accMap.get("appMenus");

        mViewPager.setOffscreenPageLimit(appMenus.size());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Bundle b = new Bundle();
                b.putBoolean("myFunc", false);
                b.putString("pkg", (String) appMenus.get(position).get("code"));
                b.putSerializable("subMenus", (ArrayList) appMenus.get(position).get("childrens"));

                KpiServiceFragment fragment = new KpiServiceFragment();
                fragment.setArguments(b);

                return fragment;
            }

            @Override
            public int getCount() {
                return appMenus.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return (String) appMenus.get(position).get("name");
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void initFuncRecycler() {
        DividerDecoration itemDecoration = new DividerDecoration(Color.parseColor("#c7c7c7"), JUtils.dip2px(0.5f), 0, 0);
        funcRecylcer.addItemDecoration(itemDecoration);

        funcAdapter = new RecyclerArrayAdapter(getApplicationContext()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new BaseViewHolder<Map>(parent, R.layout.item_quick_func) {
                    @Override
                    public void setData(Map data) {
                        String iconStr = data.get("img").toString().replaceAll("img/", "").replaceAll(".png", "");
                        ResUtil.setImageRes($(R.id.iv_menu_icon), iconStr);

                        TextView menuName = $(R.id.tv_menu_name);
                        menuName.setText((String) data.get("name"));
                    }
                };
            }
        };


        funcRecylcer.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        funcRecylcer.setAdapter(funcAdapter);

        funcAdapter.setOnItemClickListener(position -> {
            try {
                Map menu = (Map) funcAdapter.getItem(position);
                Intent i = new Intent(this, Class.forName("com.wfzcx.ieos.module." + menu.get("pkg") + "." + menu.get("code") + "_activity"));
                startActivity(i);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AccountModel.getInstance().isLogin()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        funcAdapter.clear();
        funcAdapter.addAll((List<Map>) accMap.get("funcs"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.REQ_MOD_PASW_CODE && resultCode == RESULT_OK) {
            // 修改密码，重新登录
            AccountModel.getInstance().logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mod_pwd:
                        startActivityForResult(new Intent(getApplicationContext(), ModPaswActivity.class), Const.REQ_MOD_PASW_CODE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.update_app:
                        AccountModel.getInstance().updateSoft(MainActivity.this);
                        break;
                    case R.id.my_func:
                        startActivity(new Intent(getApplicationContext(), MyFuncsActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        new MaterialDialog.Builder(MainActivity.this)
                                .content("是否注销用户")
                                .positiveText("确定")
                                .onPositive((dialog, which) -> {
                                    AccountModel.getInstance().logout();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                })
                                .negativeText("取消")
                                .show();

                }
                return false;
            }
        });

        TextView name = (TextView) navView.getHeaderView(0).findViewById(R.id.name);
        name.setText((String) accMap.get("username"));

        TextView cname = (TextView) navView.getHeaderView(0).findViewById(R.id.cname);
        cname.setText((String) accMap.get("userCnname"));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

            long mNowBackPressTime = System.currentTimeMillis();
            if (mNowBackPressTime - mLastBackPressTime > 2000) {
                JUtils.Toast("再次点击退出月报通");
                mLastBackPressTime = mNowBackPressTime;
            } else {
                this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
