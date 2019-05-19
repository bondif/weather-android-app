package com.bondif.myweather.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Photo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PhotoDao photoDao();
}
