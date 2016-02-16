package com.example.ido.simpleshopping;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by ido on 12/02/2016.
 */
public class MyApplication extends Application {
    public static String MyUrl= null; // get it in the mainactivity. it's the application download page

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();


    }
}