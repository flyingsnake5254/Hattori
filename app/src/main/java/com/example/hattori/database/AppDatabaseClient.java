package com.example.hattori.database;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseClient {
    private Context context;
    private static AppDatabaseClient instance;

    private AppDatabase appDatabase;

    private AppDatabaseClient(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "MyDatabase").build();
    }

    public static synchronized AppDatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}