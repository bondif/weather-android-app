package com.bondif.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private LineChart chart;
    private List<ChartWeatherItem> data = new LinkedList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_detail);

        setResourceReferences();

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

                    List<Entry> entries = new ArrayList<>();
                    referenceTimestamp = data.get(0).getTimestamp();
                    for (int i = 0; i < 8; i++) {
                        entries.add(new Entry(data.get(i).getTimestamp(), data.get(i).getTemp()));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Temperature");
                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    chart.invalidate();
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

        configureChart();
    }

    private void configureChart() {
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        ValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
    }

    private void setResourceReferences() {
        tvCityName         = findViewById(R.id.tvCityName);
        tvDay              = findViewById(R.id.tvDay);
        tvDate             = findViewById(R.id.tvDate);
        tvDesc             = findViewById(R.id.tvDesc);
        tvWindVal          = findViewById(R.id.tvWindVal);
        tvHumidityVal      = findViewById(R.id.tvHumidityVal);
        tvPrecipitationVal = findViewById(R.id.tvPrecipitationVal);
        chart              = findViewById(R.id.chart);
    }
}
