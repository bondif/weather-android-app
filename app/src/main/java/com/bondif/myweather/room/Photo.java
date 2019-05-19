package com.bondif.myweather.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "photos")
public class Photo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String latitude;
    public String longitude;
    public String placeName;
    public String data;
    public String name;

    @Override
    public String toString() {
        return "Image : " + id + "\n" +
                "Place name: " + placeName + "\n" +
                "Image name: " + name + "\n" +
                "Image path: " + data + "\n" +
                "Longitude: " + longitude + "\n" +
                "Latitude: " + latitude;
    }
}
