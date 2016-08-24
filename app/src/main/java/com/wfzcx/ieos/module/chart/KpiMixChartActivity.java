package com.wfzcx.ieos.module.chart;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.beam.expansion.BeamBaseActivity;
import com.wfzcx.ieos.R;
import com.wfzcx.ieos.ui.FontIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

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
    ComboLineColumnChartView chartView;

    private ComboLineColumnChartData data;

    private JSONObject jsonObj;
    private JSONArray paramArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpi_mix_chart);
        ButterKnife.bind(this);


        try {
            jsonObj = new JSONObject(getIntent().getStringExtra("jsonStr"));
            initToolbar();

//            Map<String, RequestBody> params = new HashMap<>();
//            paramArr = jsonObj.getJSONArray("params");
//            int len = paramArr.length();
//            if (len > 0) {
//                recurseQryData(len, paramArr.getJSONObject(0));
//            }
//
//
//            if (!jsonObj.getBoolean("multiSql")) {
//                if (jsonObj.get("params") instanceof JSONArray) {
//                    JSONArray paramObjs = jsonObj.getJSONArray("params");
//                    int len = paramObjs.length();
//
//                    JSONObject paramObj = paramObjs.getJSONObject(0);
//                    Iterator<String> keys = paramObj.keys();
//                    while (keys.hasNext()) {
//                        String key = keys.next();
//                        String val = paramObj.getString(key);
//                        if (val.indexOf("$") != -1) {
//                            val = val.replaceAll("$", "'");
//                        }
//
//                        params.put(key, RequestBody.create(MediaType.parse("text/plain"), val));
//                    }
//
//                    KpiDataModel.getInstance().getKpiData(params)
//                            .compose(new ProgressDialogTransform<>(KpiMixChartActivity.this, "正在请求数据"))
//                            .subscribe(list -> {
//
//                            });
//
//
//                } else if (jsonObj.get("params") instanceof JSONObject) {
//
//                }
//            } else {
//
//            }
//
//
////            params.put("type", jsonObj.getJSONArray(""));
//
//            KpiDataModel.getInstance().getKpiData(params);


            initChart();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void recurseQryData(int len, JSONObject params) {
        // 没有参数，可以传递查询
        if (params == null) {
            return;
        }


    }

    private void initToolbar() throws JSONException {
        tbTitle.setText(jsonObj.getString("menuName"));
        ficActionLeft.setOnClickListener(v -> finish());
    }

    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 12;

    private void initChart() {
        chartView.setOnValueTouchListener(new ValueTouchListener());

        generateValues();
        generateData();
    }

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    private void generateValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 50f + 5;
            }
        }
    }

    private void generateData() {
        // Chart looks the best when line data and column data have similar maximum viewports.
        data = new ComboLineColumnChartData(generateColumnData(), generateLineData());

        if (true) {
            Axis axisX = new Axis();
            List<AxisValue> axisValues = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                AxisValue av = new AxisValue(i);
                av.setLabel("黄山" + i);
                axisValues.add(av);
            }
            axisX.setValues(axisValues);

            Axis axisY = new Axis().setHasLines(true);
            Axis axisZ = new Axis();

            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
            data.setAxisYRight(axisZ);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chartView.setComboLineColumnChartData(data);
    }

    private LineChartData generateLineData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setCubic(true);
            line.setHasLabels(false);
            line.setHasLines(true);
            line.setHasPoints(true);
            lines.add(line);
        }

        LineChartData lineChartData = new LineChartData(lines);

        return lineChartData;

    }

    private ColumnChartData generateColumnData() {
        int numSubcolumns = 1;
        int numColumns = 12;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue((float) Math.random() * 50 + 5, ChartUtils.COLORS[j]));
            }

            columns.add(new Column(values));
        }

        ColumnChartData columnChartData = new ColumnChartData(columns);
        return columnChartData;
    }

    private class ValueTouchListener implements ComboLineColumnChartOnValueSelectListener {

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(KpiMixChartActivity.this, "Selected column: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(KpiMixChartActivity.this, "Selected line point: " + value, Toast.LENGTH_SHORT).show();
        }

    }

}
