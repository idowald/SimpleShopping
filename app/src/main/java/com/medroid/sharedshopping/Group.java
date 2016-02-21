package com.medroid.sharedshopping;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by ido on 19/02/2016.
 */
public class Group extends Parseable {
    String Group_name= "";
    String objectId= null;
    String password= "";

    static  final String GROUP_NAME ="Group_name"; //field
    static  final String PASSWORD ="password"; //field

    static final String NAME_OF_TABLE = "Groups";

    public Group(String group_name, String password) {
        Group_name = group_name;
        this.password = password;
    }
    public Group(ParseObject parseObject, CallbackParse callbackParse){ //item call him
        parseObject.fetchIfNeededInBackground( new getParseable(this,callbackParse));

    }

    @Override
    public void generateFromParseObject(ParseObject parseObject) {
        Group_name = parseObject.getString(GROUP_NAME);
        objectId = parseObject.getObjectId();
        password= parseObject.getString(PASSWORD);
    }

    @Override
    public ParseObject toParseObject() {
        ParseObject object = null;
        if (objectId == null){
            object= new ParseObject(NAME_OF_TABLE);
        } else {
            object = ParseObject.createWithoutData(NAME_OF_TABLE, objectId);
        }
            object.put(PASSWORD, password);
            object.put(GROUP_NAME, Group_name);

        return object;
    }

    public String getGroup_name() {
        return Group_name;
    }

    public void setGroup_name(String group_name) {
        Group_name = group_name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void saveinstance(final CallbackParse callbackParse){
        final ParseObject object = this.toParseObject();
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                setObjectId(object.getObjectId());
                if (callbackParse!= null)
                    callbackParse.callback(null);

            }
        });
    }

    @Override
    public void InformWaiters() {
        //do nothing- have no relations
    }

    private static Group group =null;
    static public Group getCurrentGroup(Context context){
        if (group!= null)
            return group;
        SharedPreferences sharedPref =  context.getSharedPreferences(
                context.getString(R.string.application_key), Context.MODE_PRIVATE);

        String group_name = sharedPref.getString(NAME_OF_TABLE+ Group.GROUP_NAME,null);
        String pass= sharedPref.getString(NAME_OF_TABLE+Group.PASSWORD, null);
        String objectId = sharedPref.getString(NAME_OF_TABLE+"objectId", null);

        if (group_name == null)
            return null;
        group = new Group(group_name,pass);
        group.setObjectId(objectId);
        return group;
    }
    static public void setCurrentGroup(Group currentGroup, Context context){
        SharedPreferences sharedPref =  context.getSharedPreferences(
                context.getString(R.string.application_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
    if (currentGroup== null)
        editor.clear();
        else {
        editor.putString(NAME_OF_TABLE + Group.GROUP_NAME, currentGroup.getGroup_name());
        editor.putString(NAME_OF_TABLE + Group.PASSWORD, currentGroup.getPassword());
        editor.putString(NAME_OF_TABLE + "objectId", currentGroup.getObjectId());
        editor.commit();
    }
        group = currentGroup;
    }


    @Override
    public String getTableName() {
        return NAME_OF_TABLE;
    }
}
