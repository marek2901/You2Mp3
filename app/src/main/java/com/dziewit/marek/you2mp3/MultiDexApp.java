package com.dziewit.marek.you2mp3;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class MultiDexApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
