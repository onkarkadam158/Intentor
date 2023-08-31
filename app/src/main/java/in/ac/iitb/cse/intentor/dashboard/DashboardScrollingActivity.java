package in.ac.iitb.cse.intentor.dashboard;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import in.ac.iitb.cse.intentor.R;
import in.ac.iitb.cse.intentor.databinding.ActivityScrollingDashboardBinding;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
public class DashboardScrollingActivity extends AppCompatActivity {

    private ActivityScrollingDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;

        setContentView(R.layout.activity_scrolling_dashboard);
        BarChart barChartOfUsageTime = findViewById(R.id.barChartOfUsageTime);
        BarChart barChartOfVisitCount = findViewById(R.id.barChartOfVisitCount);

//        TextView appUsageTextView = findViewById(R.id.appUsageTextView);

        AppUsageStatistics appUsageStatistics = new AppUsageStatistics(this);
        List<AppUsageInfo> appUsageInfoList = appUsageStatistics.getUsageStatistics();

        StringBuilder stringBuilder = new StringBuilder();
        for (AppUsageInfo appUsageInfo : appUsageInfoList) {
            String appName = appUsageInfo.getPackageName();
            long totalUsageTime = appUsageInfo.getTotalUsageTime();
            long todaysTotalUsageTime = appUsageInfo.getTotalUsageTimeOfToday();
//            long totalAppVisits = appUsageInfo.getTotalVisitCounts();
            long todaysAppVisits = appUsageInfo.getTotalVisitCountsOfToday();
            stringBuilder.append("App: ").append(appName).append("\n");
            stringBuilder.append("Total Time: ").append(formatTime(totalUsageTime)).append("\n");
            stringBuilder.append("Todays Time: ").append(formatTime(todaysTotalUsageTime)).append("\n");
//            stringBuilder.append("Total App visits: ").append(totalAppVisits).append("\n");
            stringBuilder.append("Todays App visits: ").append(todaysAppVisits).append("\n\n");
        }
//        appUsageTextView.setText(stringBuilder.toString());
        createAndDisplayGraphs(barChartOfUsageTime,barChartOfVisitCount,appUsageInfoList);
    }
    public void createAndDisplayGraphs(BarChart barChartOfUsageTime,BarChart barChartOfVisitCount, List<AppUsageInfo> appUsageInfoList){

        setupBarChart(barChartOfUsageTime);
        setupBarChart(barChartOfVisitCount);

        // Populate the chart with data
        populateBarChartOfUsagetime(barChartOfUsageTime, appUsageInfoList);
        populateBarChartOfVisitCount(barChartOfVisitCount, appUsageInfoList);
    }

    private void setupBarChart(BarChart barChart) {
        // Customizing the appearance and behavior of the bar chart
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);

        //Setting TextViews
        TextView chartDescription = findViewById(R.id.chartDescriptionOfUsageTime);
        chartDescription.setText("App Usage (In minutes)");
        chartDescription.setVisibility(View.VISIBLE);

        TextView chartDescriptionOfVisitCount = findViewById(R.id.chartDescriptionOfVisitCount);
        chartDescriptionOfVisitCount.setText("App Visit Counts");
        chartDescriptionOfVisitCount.setVisibility(View.VISIBLE);
        ////done with textview

        //AXIS Prperties
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Display labels for each bar
//        xAxis.setLabelRotationAngle(90);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setEnabled(true); // Enable Y-axis
        yAxis.setDrawGridLines(false); // Remove Y-axis grid lines
        barChart.getAxisLeft().setEnabled(true); // Hide left axis
        barChart.getAxisRight().setEnabled(false); // Hide right axis

        barChart.animateXY(1000, 1000, Easing.EaseInOutQuad);
        barChart.setExtraBottomOffset(10f); // Additional bottom padding
        barChart.setFitBars(true); // Bars fit inside the chart's margins

        //About bars
        barChart.getLegend().setEnabled(false); // Hide legend
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
    }
    private void populateBarChartOfUsagetime(BarChart barChart, List<AppUsageInfo> appUsageInfoList) {
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < appUsageInfoList.size(); i++) {
            AppUsageInfo appUsageInfo = appUsageInfoList.get(i);
            float usageTime = appUsageInfo.getTotalUsageTimeOfToday()/(60000); // in minutes
            String appName = appUsageInfo.getAppName();
            System.out.println(appName+"--time--"+usageTime+"---Packagename--"+appUsageInfo.getPackageName());
            entries.add(new BarEntry(i, usageTime, appName)); // Add usage time and app name
        }

        BarDataSet dataSet = new BarDataSet(entries, "App Usage");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // Adjust bar width

        barChart.setData(barData);
        final String[] appNames = new String[appUsageInfoList.size()];
        for (int i = 0; i < appUsageInfoList.size(); i++) {
            appNames[i] = appUsageInfoList.get(i).getAppName();
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < appNames.length) {
                    return appNames[index];
                }
                return ""; // Return an empty label if the index is out of range
            }
        });
        barChart.invalidate(); // Refresh the chart
    }
    private void populateBarChartOfVisitCount(BarChart barChart, List<AppUsageInfo> appUsageInfoList) {
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < appUsageInfoList.size(); i++) {
            AppUsageInfo appUsageInfo = appUsageInfoList.get(i);
            float visitCountsOfToday = appUsageInfo.getTotalVisitCountsOfToday(); // in numbers
            String appName = appUsageInfo.getAppName();
            System.out.println(appName+"--visitCountsOfToday--"+visitCountsOfToday+"---Packagename--"+appUsageInfo.getPackageName());
            entries.add(new BarEntry(i, visitCountsOfToday, appName)); // Add usage time and app name
        }

        BarDataSet dataSet = new BarDataSet(entries, "App Usage");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // Adjust bar width

        barChart.setData(barData);
        final String[] appNames = new String[appUsageInfoList.size()];
        for (int i = 0; i < appUsageInfoList.size(); i++) {
            appNames[i] = appUsageInfoList.get(i).getAppName();
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < appNames.length) {
                    return appNames[index];
                }
                return ""; // Return an empty label if the index is out of range
            }
        });
        barChart.invalidate(); // Refresh the chart
    }
    private String formatTime(long totalTimeInForeground) {
        // Implemented a method to format the usage time in a user-friendly way.
        // For example, converting milliseconds to hours and minutes.
        // Converted the total time to a more human-readable format (e.g., hours, minutes, seconds).
        long totalSeconds = totalTimeInForeground / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
//        System.out.println("Total Time in Foreground: " + formattedTime);
        return formattedTime;
    }
}