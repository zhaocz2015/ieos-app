package com.wfzcx.ieos.module.dz_product;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.beam.bijection.BeamFragment;
import com.jude.utils.JUtils;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.ui.tablefixheader.TableFixHeaders;
import com.wfzcx.ieos.ui.tablefixheader.adapters.SampleTableAdapter;

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
 * @date: 2016-09-13
 */
public class dz_material_enter_table extends BeamFragment {

    private View rootView;

    @BindView(R.id.table)
    TableFixHeaders table;

    private String[] headerTitles = {"指标名称", "上涨数", "持平数", "下降数"};
    private String[] columnLabels = {"ROWSNAME", "UpNum", "CpNum", "DownNum"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_table_grid, container, false);
            ButterKnife.bind(this, rootView);

            ArrayList<Map> rsList = (ArrayList<Map>) getArguments().getSerializable("rsList");
            table.setAdapter(new MyAdapter(getContext(), rsList));
            table.setOnCellClickLisenter((row, column) -> {
                ((dz_material_enter_activity) getActivity()).renderChartData(row);
            });
        }
        return rootView;
    }

    public class MyAdapter extends SampleTableAdapter {

        private final int width;
        private final int height;

        private List<Map> rsList;

        public MyAdapter(Context context, List<Map> rsList) {
            super(context);

            this.rsList = rsList;

            width = JUtils.dip2px(100);
            height = JUtils.dip2px(40);
        }

        @Override
        public int getRowCount() {
            return rsList == null ? 0 : rsList.size();
        }

        @Override
        public int getColumnCount() {
            return rsList == null ? 0 : headerTitles.length - 1;
        }

        @Override
        public int getWidth(int column) {
            if (column == -1) {
                return JUtils.dip2px(60);
            }

            return width;
        }

        @Override
        public int getHeight(int row) {
            return height;
        }

        @Override
        public String getCellString(int row, int column) {
            if (row == -1) {
                return rsList == null ? "" : headerTitles[column + 1];
            }

            if (column == -1 || column == 0) {
                if (rsList.get(row).get(columnLabels[column + 1]) == null) {
                    return "";
                }
                return String.valueOf(rsList.get(row).get(columnLabels[column + 1]));
            } else {
                return Double.valueOf(String.valueOf(rsList.get(row).get(columnLabels[column + 1]))).toString();
            }
        }

        @Override
        public int getLayoutResource(int row, int column) {
            final int layoutResource;
            switch (getItemViewType(row, column)) {
                case 0:
                    layoutResource = R.layout.item_table1_header;
                    break;
                case 1:
                    layoutResource = R.layout.item_table1;
                    break;
                default:
                    throw new RuntimeException("wtf?");
            }


            return layoutResource;
        }

        @Override
        public int getItemViewType(int row, int column) {
            if (row < 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
    }

}
