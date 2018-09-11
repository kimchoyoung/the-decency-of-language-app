package com.beens;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class FragmentB extends Fragment {
    private Context context;
    LineChart lineChart;
    MyMarkerView myMarkerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.linechart_view, container, false);
        this.context = getContext();
        myMarkerView = new MyMarkerView(this.context,R.layout.markerviewlayout);
        lineChart = (LineChart) view.findViewById(R.id.line_chart);
        lineChart.setNoDataText("");
        setLineData();

        return view;
    }

    private void setLineData() {

        Cursor c = MainActivity.sqLiteDatabase.query(Queries.getUserRecTableName(userID), new String[]{"word", "count(time)"}, null, null, "word", null, "count(time)", null);
        List<Entry> entries = new ArrayList<>();

        if (c.moveToFirst()) {
            float i = 0f;
            do {
                entries.add(new Entry(i, Float.valueOf(c.getString(1))));
                i++;
            } while (c.moveToNext());
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Data set");
        lineDataSet.setLineWidth(2f);
        lineDataSet.setColor(Color.parseColor("#87CEEB"));
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(Color.parseColor("#4169E1"));
        lineDataSet.setCircleRadius(5f);


        List<String> words = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                words.add(c.getString(0));
            } while (c.moveToNext());
        }

        final String[] ylabels = words.toArray(new String[words.size()]);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ylabels[(int) value];
            }

        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setEnabled(false);
        yRAxis.setDrawGridLines(false);


        YAxis yLAxis = lineChart.getAxisLeft();
        //    yRAxis.setAxisMinimum(0f);
        yLAxis.setGranularity(1f);
        yLAxis.setDrawGridLines(true);

        LineData lineData = new LineData(lineDataSet);
        lineData.setDrawValues(false);


        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDrawGridBackground(false);

        myMarkerView.setChartView(lineChart);
        lineChart.setMarker(myMarkerView);
    }
}

