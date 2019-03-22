package com.bondif.myweather;

/**
 * Created by user on 15/03/2019.
 */

public class WeatherItem {
    private int maxTemp;
    private int minTemp;
    private int pression;
    private int humidity;
    private String image;
    private String date;

    public WeatherItem() {
    }

    public WeatherItem(int maxTemp, int minTemp, int pression, int humidity, String image, String date) {
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.pression = pression;
        this.humidity = humidity;
        this.image = image;
        this.date = date;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getPression() {
        return pression;
    }

    public void setPression(int pression) {
        this.pression = pression;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
