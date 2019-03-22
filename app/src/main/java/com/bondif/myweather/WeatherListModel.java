package com.bondif.myweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 22/03/2019.
 */

public class WeatherListModel extends ArrayAdapter<WeatherItem> {
    private List<WeatherItem> weatherItems;
    private int resource;
    public static Map<String, Integer> images = new HashMap<>();

    static {
        images.put("Clear", R.drawable.clear);
        images.put("Rain", R.drawable.rain);
        images.put("Clouds", R.drawable.clouds);
    }

    public WeatherListModel(@NonNull Context context, int resource, List<WeatherItem> data) {
        super(context, resource, data);
        Log.i("MyLog Size", String.valueOf(data.size()));
        this.weatherItems = data;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        ImageView ivStatus = listItem.findViewById(R.id.ivStatus);
        TextView tvMaxTempVal = listItem.findViewById(R.id.tvMaxTempVal);
        TextView tvMinTempVal = listItem.findViewById(R.id.tvMinTempVal);
        TextView tvPressureVal = listItem.findViewById(R.id.tvPressionVal);
        TextView tvHumidityVal = listItem.findViewById(R.id.tvHumidityVal);
        TextView tvDate = listItem.findViewById(R.id.tvDate);
        String key = weatherItems.get(position).getImage();

        if(key != null) {
            ivStatus.setImageResource(images.get(key));
        }

        tvMaxTempVal.setText(String.valueOf(weatherItems.get(position).getMaxTemp()) + " °C");
        tvMinTempVal.setText(String.valueOf(weatherItems.get(position).getMinTemp()) + " °C");
        tvPressureVal.setText(String.valueOf(weatherItems.get(position).getPression()) + " hPa");
        tvHumidityVal.setText(String.valueOf(weatherItems.get(position).getHumidity()) + " %");
        tvDate.setText(String.valueOf(weatherItems.get(position).getDate()));

        return listItem;
    }
}
