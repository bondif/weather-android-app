package com.bondif.myweather;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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
    public static final String CITY_NAME = "com.bondif.myweather.CITY_NAME";
    private EditText etSearch;
    private ImageButton btnSearch;
    private Button btnCityDetail;
    private ListView lvWeatherEntries;
    private ListView lvNavList;
    private ActionBarDrawerToggle barDrawerToggle;
    private DrawerLayout layoutDrawer;
    private List<WeatherItem> data;
    private WeatherListModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setResourceReferences();
        addDrawerItems();
        setupDrawer();
        lvWeatherEntries.setAdapter(model);

        /* activating sidebar menu icon */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

        btnCityDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etSearch.length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a city name!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), CityDetail.class);
                String city = etSearch.getText().toString();
                intent.putExtra(CITY_NAME, city);
                startActivity(intent);
            }
        });
    }

    private void addDrawerItems() {
        String[] navChoices = { "Home", "Image Search" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navChoices);
        lvNavList.setAdapter(adapter);

        /* handling side bar links */
        lvNavList.setOnItemClickListener((parent, view, position, id) -> {
                Toast.makeText(MainActivity.this, "Test " + position, Toast.LENGTH_SHORT).show();
            }
        );
    }

    private void setupDrawer() {
        barDrawerToggle = new ActionBarDrawerToggle(this, layoutDrawer, null, R.string.drawer_open, R.string.drawer_close);
        barDrawerToggle.setDrawerIndicatorEnabled(true);
        layoutDrawer.addDrawerListener(barDrawerToggle);

    }

    private void init() {
        data = new LinkedList<>();
        model = new WeatherListModel(getApplicationContext(), R.layout.weather_entry, data);
    }

    private void setResourceReferences() {
        etSearch         = findViewById(R.id.etSeach);
        btnSearch        = findViewById(R.id.btnSearch);
        btnCityDetail    = findViewById(R.id.btnCityDetail);
        lvWeatherEntries = findViewById(R.id.lvWeatherEntries);
        lvNavList        = findViewById(R.id.lvNavList);
        layoutDrawer     = findViewById(R.id.layoutDrawer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (barDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        barDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        barDrawerToggle.onConfigurationChanged(newConfig);
    }
}
