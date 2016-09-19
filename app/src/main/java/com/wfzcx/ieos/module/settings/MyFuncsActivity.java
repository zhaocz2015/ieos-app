package com.wfzcx.ieos.module.settings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jude.beam.expansion.BeamBaseActivity;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.data.model.AccountModel;
import com.wfzcx.ieos.module.kpi.KpiServiceFragment;
import com.wfzcx.ieos.utils.ResUtil;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

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
 * @date: 2016-09-19
 */
public class MyFuncsActivity extends BeamBaseActivity implements SlideAndDragListView.OnDragListener, SlideAndDragListView.OnListItemLongClickListener {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.tabs_kpi)
    TabLayout mTabLayout;

    @BindView(R.id.vp_kpi)
    ViewPager mViewPager;

    @BindView(R.id.sdlv_my_funcs)
    SlideAndDragListView funcListView;

    private Map accMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_func);
        ButterKnife.bind(this);

        accMap = AccountModel.getInstance().getAccount();
        initToolbar();
        initViewPager();
        initFuncList();
    }

    private void initToolbar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText("请选择你的常用功能");
    }

    private void initViewPager() {
        List<Map> appMenus = (List<Map>) accMap.get("appMenus");

        mViewPager.setOffscreenPageLimit(appMenus.size());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Bundle b = new Bundle();
                b.putBoolean("isFunc", true);
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


    private FuncAdapter funcAdapter;

    private void initFuncList() {
        Menu funcMenu = new Menu(true, true, 0);
        funcMenu.addItem(new MenuItem.Builder()
                .setText("移除")
                .setTextColor(Color.parseColor("#ffffff"))
                .setBackground(new ColorDrawable(Color.parseColor("#ff0000")))
                .setTextSize(14)
                .setWidth(JUtils.dip2px(60))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());
        funcListView.setMenu(funcMenu);

        funcListView.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
            @Override
            public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
                switch (direction) {
                    case MenuItem.DIRECTION_RIGHT:
                        if (buttonPosition == 0) {
                            return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                        }
                    default:
                        return Menu.ITEM_NOTHING;
                }
            }
        });

        funcListView.setOnItemDeleteListener(new SlideAndDragListView.OnItemDeleteListener() {
            @Override
            public void onItemDelete(View view, int position) {
                renderFuncData(funcAdapter.getItem(position));
            }
        });

        funcAdapter = new FuncAdapter(getApplicationContext(), accMap.get("funcs") == null ? new ArrayList<Map>() : (List<Map>) accMap.get("funcs"));
        funcListView.setAdapter(funcAdapter);
        funcListView.setOnListItemLongClickListener(this);
        funcListView.setOnDragListener(MyFuncsActivity.this, funcAdapter.getAllItems());

    }

    public void renderFuncData(Map menu) {
        if (funcAdapter.indexOf(menu) == -1) {
            funcAdapter.addItem(menu);
        } else {
            funcAdapter.removeItem(funcAdapter.indexOf(menu));
        }
        funcListView.setOnDragListener(MyFuncsActivity.this, funcAdapter.getAllItems());

        // 保存数据信息
        accMap.put("funcs", funcAdapter.getAllItems());
        AccountModel.getInstance().setAccount(accMap);
    }

    @Override
    public void onDragViewStart(int position) {

    }

    @Override
    public void onDragViewMoving(int position) {

    }

    @Override
    public void onDragViewDown(int position) {
        // 保存数据信息
        accMap.put("funcs", funcAdapter.getAllItems());
        AccountModel.getInstance().setAccount(accMap);
    }

    @Override
    public void onListItemLongClick(View view, int position) {

    }

    class FuncAdapter extends BaseAdapter {

        private Context mContext;

        private List<Map> menuList;

        public FuncAdapter(Context mContext, List<Map> menuList) {
            this.mContext = mContext;
            this.menuList = menuList;
        }

        public int indexOf(Map menu) {
            int index = -1;
            for (Map tmp : menuList) {
                index++;
                if (tmp.get("code").equals(menu.get("code"))) {
                    return index;
                }
            }

            return -1;
        }

        public void addItem(Map menu) {
            menuList.add(menu);
            notifyDataSetChanged();
        }

        public void removeItem(int menuIndex) {
            menuList.remove(menuIndex);
            notifyDataSetChanged();
        }

        public List<Map> getAllItems() {
            return this.menuList;
        }

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public Map getItem(int i) {
            return menuList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            FuncVHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_quick_func, null);
                holder = new FuncVHolder(view);
                view.setTag(holder);
            } else {
                holder = (FuncVHolder) view.getTag();
            }

            String iconStr = menuList.get(i).get("img").toString().replaceAll("img/", "").replaceAll(".png", "");
            ResUtil.setImageRes(holder.menuIcon, iconStr);
            holder.menuText.setText((String) menuList.get(i).get("name"));

            return view;
        }

        class FuncVHolder {
            RoundedImageView menuIcon;
            TextView menuText;

            public FuncVHolder(View view) {
                this.menuIcon = (RoundedImageView) view.findViewById(R.id.iv_menu_icon);
                this.menuText = (TextView) view.findViewById(R.id.tv_menu_name);
            }

        }
    }

}
