package com.beens;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class FragmentA extends Fragment {

    HorizontalBarChart horizontalBarChart;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barchart_view, container, false);

        horizontalBarChart = (HorizontalBarChart) view.findViewById(R.id.hori_chart);
        horizontalBarChart.setNoDataText("");
        setBarData();

        return view;
    }

    private void setBarData() {
        Cursor c = MainActivity.sqLiteDatabase.query(Queries.getUserRecTableName(userID), new String[]{"word", "count(time)"}, null, null, "word", null, "count(time)", null);
        List<BarEntry> entries = new ArrayList<>();

        if (c.moveToFirst()) {
            float i = 0f;
            do {
                entries.add(new BarEntry(i, Float.valueOf(c.getString(1))));
                i++;
            } while (c.moveToNext());
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Data set");

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

        AxisBase axisBase = horizontalBarChart.getAxisRight();
        axisBase.setEnabled(false);

        YAxis yAxis = horizontalBarChart.getAxisLeft();
        yAxis.setGranularity(1f);

        XAxis xAxis = horizontalBarChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        Description description = new Description();
        description.setText("");

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);
        horizontalBarChart.setData(barData);
        horizontalBarChart.invalidate();
        horizontalBarChart.setDrawGridBackground(false);
        horizontalBarChart.setNoDataText("");
    }
}


