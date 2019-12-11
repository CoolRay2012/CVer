package com.zui.cver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyChart chartV;
    private MyChart chartC;
    private MyChart chartW;
    private MyChart chartCc;

    private TextView tVChartCcAvg;

    private boolean onRecord = false;

    private class MyChart {
        private LineChart chart;
        private String name;
        private long data = 0;

        private MyChart(@IdRes int id, String s) {
            this.chart = findViewById(id);
            this.name = s;

            chartInit(chart);
        }

        private void chartInit(LineChart chart) {
            chart.setDrawGridBackground(false);
            // no description text
            chart.getDescription().setText("test");
            chart.getDescription().setTextColor(R.color.colorAccent);
            chart.getDescription().setEnabled(true);
        // enable touch gestures
            chart.setTouchEnabled(true);
            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            // if disabled, scaling can be done on x- and y-axis separately
            chart.setPinchZoom(false);
            chart.getAxisLeft().setDrawGridLines(true);
            chart.getAxisRight().setEnabled(true);
            chart.getXAxis().setDrawGridLines(true);
            chart.getXAxis().setDrawAxisLine(true);
            // set an alternative background color
            chart.setBackgroundColor(Color.argb(96,55,55,55));
            chart.setNoDataText("Waiting data observing...");
            chart.setMaxVisibleValueCount(10);
            chart.setVisibleXRangeMinimum(9);
            chart.setVisibleXRangeMaximum(10);
            //chart.setHighlightPerDragEnabled(true);  // display value when zoom
            //chart.setHighlightPerTapEnabled(true);

            LineData data = new LineData();
            data.setValueTextColor(Color.RED);
            // add empty data
            chart.setData(data);

            // get the legend (only possible after setting data)
            Legend l = chart.getLegend();
            // modify the legend ...
            l.setForm(Legend.LegendForm.LINE);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            //l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            //l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(true);
            //l.setTypeface(tfLight);
            l.setTextColor(Color.RED);

            XAxis xl = chart.getXAxis();
            //xl.setTypeface(tfLight);
            xl.setTextColor(Color.WHITE);
            xl.setDrawGridLines(false);
            xl.setAvoidFirstLastClipping(true);
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setEnabled(true);

            YAxis rightAxis = chart.getAxisRight();
            //leftAxis.setTypeface(tfLight);
            rightAxis.setTextColor(Color.WHITE);
            //rightAxis.setAxisMaximum(100f);
            //rightAxis.setAxisMinimum(0f);
            rightAxis.setDrawGridLines(true);

            YAxis leftAxis = chart.getAxisLeft();
            //leftAxis.setEnabled(false);
            leftAxis.setDrawLabels(false);

            // don't forget to refresh the drawing
            chart.invalidate();
        }

        public void addData(){
            ILineDataSet set = chart.getData().getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well
            if (set == null) {
                set = createSet();
                chart.getData().addDataSet(set);
            }
            //chart.getData().addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            chart.getData().addEntry(new Entry(set.getEntryCount(), getLatestData()), 0);
            chart.getData().notifyDataChanged();

            freshWidget();
        }

        public void freshWidget(){}

        public void clearData() {
            chart.getData().clearValues();
            chart.getData().notifyDataChanged();
        }

        public void updateData() {
            //to be override
        }

        public long getData() {
            //Log.d("CVer","data" + data);
            return data;
        }

        public float getLatestData() {
            updateData();
            return (float)data;
        }

        public void setData(long l) {
            data = l;
        }

        private LineDataSet createSet() {
            LineDataSet set = new LineDataSet(null, this.name);
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(ColorTemplate.rgb("ff0000"),125);
            set.setCircleColor(Color.RED);
            set.setLineWidth(2);
            set.setCircleRadius(1f);
            set.setFillAlpha(125);
            //set.setDrawFilled(true); // will impact performance
            set.setFillColor(ColorTemplate.rgb("ff0000"));
            set.setHighLightColor(Color.rgb(244, 17, 17));
            set.setValueTextColor(Color.RED);
            set.setValueTextSize(6f);
            set.setDrawValues(true);
            return set;
        }

        public LineChart getChart() {
            return chart;
        }

        public boolean isDataNull(){
            return null == chart.getData();
        }
    }

    private class MyChartV extends MyChart{

        //BatteryManager mBatteryManager;
        BroadcastReceiver batteryReceiver;

        private MyChartV (@IdRes int id, String s) {
            super(id, s);
            batteryReceiver = new BroadcastReceiver() {
                //int scale = -1;
                //int level = -1;
                int voltage = -1;
                int temp = -1;
                @Override
                public void onReceive(Context context, Intent intent) {
                    //level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    //scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                    voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                    setData(voltage);
                    //Log.d("BatteryManager", "level is "+level+"/"+scale+", temp is "+temp+", voltage is "+voltage);
                }
            };
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryReceiver, filter);
        }

        @Override
        public float getLatestData(){
            return getData();
        }
    }

    private class MyChartC extends MyChart{

        BatteryManager mBatteryManager;

        private MyChartC (@IdRes int id, String s) {
            super(id, s);
            mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        }

        @Override
        public void updateData() {
            setData(-(mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)));
        }
        @Override
        public float getLatestData(){
            updateData();
            return (float)(getData()>>10);
        }
    }

    private class MyChartW extends MyChart{
        private MyChart cv;
        private MyChart cc;
        private MyChartW (@IdRes int id, String s, MyChart cv, MyChart cc) {
            super(id, s);
            this.cv = cv;
            this.cc = cc;
        }

        @Override
        public float getLatestData() {
            setData(cv.getData()*cc.getData());
            return getData()>>20;
        }
    }

    private class MyChartCc extends MyChart{
        private MyChart cw;
        private MyChartCc (@IdRes int id, String s, MyChart cw) {
            super(id, s);
            this.cw = cw;
            getChart().setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                    freshWidget();
                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                    freshWidget();
                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {

                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                    freshWidget();
                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                    freshWidget();
                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {
                    freshWidget();
                }
            });
        }

        @Override
        public float getLatestData() {
            setData(cw.getData()/3800);
            return getData()>>10;
        }

        public int calAvg(){
            float sum = 0;
            float l = getChart().getLowestVisibleX();
            float h = getChart().getHighestVisibleX();
            //Log.d("Cver","l:"+l+" h:"+h);

            if (l<0) return 0;//bypass init case

            for(int i = (int)l; i <= (int)h;i++) {
                //Log.d("CVer","sum index:" + i + " y:" + getChart().getData().getDataSetByIndex(0).getEntryForIndex(i).getY());
                sum += getChart().getData().getDataSetByIndex(0).getEntryForIndex(i).getY();
            }
            return (int)(sum/(h-l+1));
        }

        @Override
        public void freshWidget() {
            tVChartCcAvg.setText(String.valueOf(calAvg()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecord) {
                    Snackbar.make(view, "Stop record", Snackbar.LENGTH_LONG)
                            .setAction("Stop", null).show();
                    onRecord = false;
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    //stopMonitor();
                } else {
                    Snackbar.make(view, "Start record", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    onRecord = true;
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                    startMonitor();
                }
            }
        });

        chartV = new MyChartV(R.id.chart1,"Voltage(mV)");
        chartC = new MyChartC(R.id.chart2,"Current(mA)");
        chartW = new MyChartW(R.id.chart3,"Power(mW)",chartV,chartC);
        chartCc = new MyChartCc(R.id.chart4,"Current@3.8v(mA)", chartW);

        tVChartCcAvg = findViewById(R.id.tAvgChartCc);

        //startMonitor();
    }

    private void addEntry(MyChart chart) {
        if (!chart.isDataNull()) {
            chart.addData();
            // let the chart know it's data has changed
            chart.getChart().notifyDataSetChanged();
            // move to the latest entry
            chart.getChart().moveViewToX(chart.getChart().getData().getEntryCount());
        }
    }

    private void clearEntry(MyChart chart) {
        chart.clearData();
        chart.getChart().notifyDataSetChanged();
    }

    private Thread thread;

    private void startMonitor() {

        if (thread != null)
            thread.interrupt();

        clearEntry(chartV);
        clearEntry(chartC);
        clearEntry(chartW);
        clearEntry(chartCc);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 720; i++) {
                    if (!onRecord) break;
                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry(chartV);
                            addEntry(chartC);
                            addEntry(chartW);
                            addEntry(chartCc);
                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

//    private void stopMonitor() {
//        if (thread != null) {
//            Log.d("CVer", "stop thread");
//            thread.interrupt();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
