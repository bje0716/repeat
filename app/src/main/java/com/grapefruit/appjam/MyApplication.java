package com.grapefruit.appjam;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by GrapeFruit on 2017-08-14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
