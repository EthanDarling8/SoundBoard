package com.example.soundboard.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Sound.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance != null) return instance;

        instance = Room.databaseBuilder(context, AppDatabase.class, "sound-database")
                .build();
        return instance;
    }

    public abstract SoundDAO soundDAO();
}
