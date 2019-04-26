package com.bondif.myweather;

public class ChartWeatherItem {
    private long timestamp;
    private float temp;
    private float wind;
    private float precipitation;

    public ChartWeatherItem(long timestamp, float temp, float wind, float precipitation) {
        this.timestamp = timestamp;
        this.temp = temp;
        this.wind = wind;
        this.precipitation = precipitation;
    }

    public ChartWeatherItem() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getWind() {
        return wind;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    public float getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(float precipitation) {
        this.precipitation = precipitation;
    }
}
