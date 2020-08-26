package com.studiomusic.musicplayer.os10music;

import android.app.Application;

import com.beolla.ZergitasSDK;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class MyApplication extends Application {
    private String[] STAR = {"*"};
    private DatabaseHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHelper(getApplicationContext());
        ZergitasSDK.initApplication(this, this.getApplicationContext().getPackageName());
    }

    public DatabaseHelper getDatabase() {
        return db;
    }
}
