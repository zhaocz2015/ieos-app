package com.wfzcx.ieos.module.main;

import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.model.AccountModel;
import com.wfzcx.ieos.module.kpi.KpiServiceFragment;
import com.wfzcx.ieos.module.login.LoginActivity;

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

    private long mLastBackPressTime = 0;

    private Map accMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        accMap = AccountModel.getInstance().getAccount();
        initNavigationView();
        initViewPager();

    }

    private void initViewPager() {
        List<Map> appMenus = (List<Map>) accMap.get("appMenus");

        mViewPager.setOffscreenPageLimit(appMenus.size());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Bundle b = new Bundle();
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

    private void initNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mod_pwd:
                        JUtils.Toast("修改密码");
                        break;
                    case R.id.update:
                        JUtils.Toast("更新版本");
                        break;
                    case R.id.about:
                        JUtils.Toast("关于APP");
                        break;
                    case R.id.logout:
                        AccountModel.getInstance().logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
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
