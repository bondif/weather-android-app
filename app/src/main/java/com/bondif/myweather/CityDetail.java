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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CityDetail extends AppCompatActivity {
    private TextView tvCityName;
    private TextView tvDay;
    private TextView tvDate;
    private TextView tvDesc;
    private TextView tvWindVal;
    private TextView tvHumidityVal;
    private TextView tvPrecipitationVal;

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
                    SimpleDateFormat dayNameSdf = new SimpleDateFormat("E");
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
                            precipitation = rain.getString("1h");
                        else
                            precipitation = rain.getString("3h");
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
        queue.add(request);
    }

    private void setResourceReferences() {
        tvCityName         = findViewById(R.id.tvCityName);
        tvDay              = findViewById(R.id.tvDay);
        tvDate             = findViewById(R.id.tvDate);
        tvDesc             = findViewById(R.id.tvDesc);
        tvWindVal          = findViewById(R.id.tvWindVal);
        tvHumidityVal      = findViewById(R.id.tvHumidityVal);
        tvPrecipitationVal = findViewById(R.id.tvPrecipitationVal);
    }
}
