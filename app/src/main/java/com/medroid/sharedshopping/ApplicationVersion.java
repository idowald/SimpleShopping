package com.medroid.sharedshopping;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseObject;

/**
 * Created by ido on 19/02/2016.
 */
public class ApplicationVersion {
    String objectId= null;
    String version="";

    final static String VERSION = "version";

    final static String NAME_OF_TABLE = "ApplicationVersion";

    public ApplicationVersion(String version) {
        this.version = version;
    }
    public ApplicationVersion(ParseObject parseObject){
        objectId= parseObject.getString(VERSION);
    }


    static public ApplicationVersion  Applicationversion= null;


    static public ApplicationVersion getCurrentVersion(Context context){
        if (Applicationversion!= null)
            return Applicationversion;
        SharedPreferences sharedPref =  context.getSharedPreferences(
                context.getString(R.string.application_key), Context.MODE_PRIVATE);
        String version =sharedPref.getString(NAME_OF_TABLE + VERSION,null);
        String objectId = sharedPref.getString(NAME_OF_TABLE + "objectId",null);
        if (version == null)
            return null;
        Applicationversion = new ApplicationVersion(version);

        Applicationversion.objectId = objectId;
        return Applicationversion;
    }
    static public void setCurrentApplication(ApplicationVersion current_application, Context context){
        SharedPreferences sharedPref =  context.getSharedPreferences(
                context.getString(R.string.application_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME_OF_TABLE + VERSION, current_application.version );
        editor.putString(NAME_OF_TABLE + "objectId" , current_application.objectId);
        editor.commit();
        Applicationversion= current_application;
    }


}
