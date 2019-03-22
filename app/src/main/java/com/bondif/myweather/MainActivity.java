package com.bondif.myweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageButton btnSearch;
    private ListView lvWeatherEntries;

    private List<WeatherItem> data;
    private WeatherListModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setResourceReferences();
        lvWeatherEntries.setAdapter(model);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.clear();
                model.notifyDataSetChanged();
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String city = etSearch.getText().toString();
                String appId = "6dcbc9f4f580d0b82ea6b353e7887715";
                String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + appId;
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("MyLog response", response);
                        try {
                            JSONObject root = new JSONObject(response);
                            JSONArray jsonArray = root.getJSONArray("list");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonDate = jsonArray.getJSONObject(i);
                                Date date = new Date(jsonDate.getLong("dt") * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy 'T' HH:mm");
                                String dateStr = sdf.format(date);

                                JSONObject main = jsonDate.getJSONObject("main");
                                JSONArray weather = jsonDate.getJSONArray("weather");
                                int minTemp  = (int) (main.getDouble("temp_min") - 273.15);
                                int maxTemp  = (int) (main.getDouble("temp_max") - 273.15);
                                int pressure = main.getInt("pressure");
                                int humidity = main.getInt("humidity");
                                String image = weather.getJSONObject(0).getString("main");

                                WeatherItem weatherItem = new WeatherItem(maxTemp, minTemp, pressure, humidity, image, dateStr);
                                data.add(weatherItem);
                            }
                            Log.i("MyLog size data", String.valueOf(data.size()));
                            model.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MyLog", "Connection Problem");
                    }
                });

                queue.add(request);
            }
        });
    }

    private void init() {
        data = new LinkedList<>();
        model = new WeatherListModel(getApplicationContext(), R.layout.weather_entry, data);
    }

    private void setResourceReferences() {
        etSearch         = findViewById(R.id.etSeach);
        btnSearch        = findViewById(R.id.btnSearch);
        lvWeatherEntries = findViewById(R.id.lvWeatherEntries);
    }
}
