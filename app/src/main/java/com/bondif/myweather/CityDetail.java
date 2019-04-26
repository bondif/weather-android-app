package com.bondif.myweather;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CityDetail extends AppCompatActivity {
    private long referenceTimestamp;
    private TextView tvCityName;
    private TextView tvDay;
    private TextView tvDate;
    private TextView tvDesc;
    private TextView tvWindVal;
    private TextView tvHumidityVal;
    private TextView tvPrecipitationVal;
    private LineChart chartTemp;
    private LineChart chartPrecipitation;
    private LineChart chartWind;
    private Button btnTemp;
    private Button btnPrecipitation;
    private Button btnWind;
    private List<ChartWeatherItem> data = new LinkedList<>();
    private LinearLayout layoutChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_detail);

        setResourceReferences();
        init();

        Intent intent = getIntent();
        String city = intent.getStringExtra(MainActivity.CITY_NAME);
        tvCityName.setText(city);

        /* send request */
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String appId = "6dcbc9f4f580d0b82ea6b353e7887715";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + appId;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("MyLog response", response);
                try {
                    JSONObject today = new JSONObject(response);

                    /* get date */
                    Date date = new Date(today.getLong("dt") * 1000);
                    SimpleDateFormat dayNameSdf = new SimpleDateFormat("EE");
                    SimpleDateFormat dateSdf = new SimpleDateFormat("dd/MM/yyyy");
                    tvDay.setText(dayNameSdf.format(date));
                    tvDate.setText(dateSdf.format(date));

                    /* get desc */
                    JSONObject weather = today.getJSONArray("weather").getJSONObject(0);

                    /* get humidity */
                    String desc = weather.getString("main");
                    JSONObject main = today.getJSONObject("main");
                    int humidity = main.getInt("humidity");

                    /* get wind speed */
                    JSONObject wind = today.getJSONObject("wind");
                    double windVal = wind.getDouble("speed");

                    JSONObject rain = today.has("rain") ? today.getJSONObject("rain") : null;
                    String precipitation;
                    if(rain != null) {
                        if(rain.has("1h"))
                            precipitation = String.valueOf(rain.getDouble("1h"));
                        else
                            precipitation = String.valueOf(rain.getDouble("3h"));
                    } else {
                        precipitation = "0.0";
                    }

                    tvDesc.setText(desc);
                    tvHumidityVal.setText(String.valueOf(humidity));
                    tvWindVal.setText(String.valueOf(windVal));
                    tvPrecipitationVal.setText(precipitation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MyLog", error.getMessage());
            }
        });

        String url1 = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&units=metric&appid=" + appId;
        StringRequest request1 = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("MyLog response", response);
                try {
                    JSONObject root = new JSONObject(response);
                    JSONArray jsonArray = root.getJSONArray("list");

                    for (int i = 0; i < root.getInt("cnt"); i++) {
                        ChartWeatherItem item = new ChartWeatherItem();

                        JSONObject fragment3h = jsonArray.getJSONObject(i);
                        long timestamp = fragment3h.getLong("dt");
                        item.setTimestamp(timestamp);

                        JSONObject main = fragment3h.getJSONObject("main");
                        int temp  = (int) (main.getDouble("temp"));
                        item.setTemp(temp);

                        JSONObject wind = fragment3h.getJSONObject("wind");
                        float windSpeed = (float)wind.getDouble("speed");
                        item.setWind(windSpeed);

                        JSONObject rain = fragment3h.has("rain") ? fragment3h.getJSONObject("rain") : null;
                        float precipitation = 0.0F;
                        if(rain != null && rain.length() != 0) {
                            if(rain.has("1h"))
                                precipitation = (float)rain.getDouble("1h");
                            else if(rain.has("3h"))
                                precipitation = (float)rain.getDouble("3h");
                        }
                        item.setPrecipitation(precipitation);

                        data.add(item);
                    }

                    /* temperature data set */
                    List<Entry> tempEntries = new ArrayList<>();
                    referenceTimestamp = data.get(0).getTimestamp();
                    for (int i = 0; i < 8; i++) {
                        tempEntries.add(new Entry(data.get(i).getTimestamp(), data.get(i).getTemp()));
                    }

                    LineDataSet tempDataSet = new LineDataSet(tempEntries, "Temperature");
                    LineData tempLineData = new LineData(tempDataSet);
                    chartTemp.setData(tempLineData);
                    chartTemp.invalidate();

                    /* precipitation data set */
                    List<Entry> precipitationEntries = new ArrayList<>();
                    referenceTimestamp = data.get(0).getTimestamp();
                    for (int i = 0; i < 8; i++) {
                        precipitationEntries.add(new Entry(data.get(i).getTimestamp(), data.get(i).getPrecipitation()));
                    }

                    LineDataSet precipitationDataSet = new LineDataSet(precipitationEntries, "Precipitation");
                    LineData precipitationLineData = new LineData(precipitationDataSet);
                    chartPrecipitation.setData(precipitationLineData);
                    chartPrecipitation.invalidate();

                    /* wind data set */
                    List<Entry> windEntries = new ArrayList<>();
                    referenceTimestamp = data.get(0).getTimestamp();
                    for (int i = 0; i < 8; i++) {
                        windEntries.add(new Entry(data.get(i).getTimestamp(), data.get(i).getWind()));
                    }

                    LineDataSet windDataSet = new LineDataSet(windEntries, "Wind");
                    LineData windLineData = new LineData(windDataSet);
                    chartWind.setData(windLineData);
                    chartWind.invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MyLog", error.getMessage());
            }
        });

        queue.add(request);
        queue.add(request1);

        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutChart.removeAllViews();
                layoutChart.addView(chartTemp);

                btnTemp.setTextColor(getResources().getColor(R.color.colorAccent));
                btnPrecipitation.setTextColor(getResources().getColor(R.color.black));
                btnWind.setTextColor(getResources().getColor(R.color.black));
            }
        });

        btnPrecipitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutChart.removeAllViews();
                layoutChart.addView(chartPrecipitation);

                btnTemp.setTextColor(getResources().getColor(R.color.black));
                btnPrecipitation.setTextColor(getResources().getColor(R.color.colorAccent));
                btnWind.setTextColor(getResources().getColor(R.color.black));
            }
        });

        btnWind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutChart.removeAllViews();
                layoutChart.addView(chartWind);

                btnTemp.setTextColor(getResources().getColor(R.color.black));
                btnPrecipitation.setTextColor(getResources().getColor(R.color.black));
                btnWind.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        configureChart();
    }

    private void init() {
        chartTemp = new LineChart(this);
        chartTemp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        chartPrecipitation = new LineChart(this);
        chartPrecipitation.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        chartWind = new LineChart(this);
        chartWind.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        layoutChart = findViewById(R.id.layoutChart);
        layoutChart.addView(chartTemp);

        btnTemp.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void configureChart() {
        /* temp chart */
        chartTemp.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartTemp.getAxisRight().setEnabled(false);
        ValueFormatter tempXAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis tempXAxis = chartTemp.getXAxis();
        tempXAxis.setValueFormatter(tempXAxisFormatter);

        /* precipitation chart */
        chartPrecipitation.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartPrecipitation.getAxisRight().setEnabled(false);
        ValueFormatter precipitationXAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis precipitationXAxis = chartPrecipitation.getXAxis();
        precipitationXAxis.setValueFormatter(precipitationXAxisFormatter);

        /* wind chart */
        chartWind.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chartWind.getAxisRight().setEnabled(false);
        ValueFormatter windXAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis windXAxis = chartWind.getXAxis();
        windXAxis.setValueFormatter(windXAxisFormatter);
    }

    private void setResourceReferences() {
        tvCityName         = findViewById(R.id.tvCityName);
        tvDay              = findViewById(R.id.tvDay);
        tvDate             = findViewById(R.id.tvDate);
        tvDesc             = findViewById(R.id.tvDesc);
        tvWindVal          = findViewById(R.id.tvWindVal);
        tvHumidityVal      = findViewById(R.id.tvHumidityVal);
        tvPrecipitationVal = findViewById(R.id.tvPrecipitationVal);
        btnTemp            = findViewById(R.id.btnTemp);
        btnPrecipitation   = findViewById(R.id.btnPrecipitation);
        btnWind            = findViewById(R.id.btnWind);
    }
}
