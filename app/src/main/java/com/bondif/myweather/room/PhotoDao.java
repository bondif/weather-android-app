package com.bondif.myweather.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM photos")
    public Photo[] findAll();

    @Insert
    public void insertPhoto(Photo photo);

    @Query("SELECT * from photos WHERE placeName like :q")
    public Photo[] findAllByPlaceName(String q);

    @Query("DELETE FROM photos")
    public void deleteAll();
}
