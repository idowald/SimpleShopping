package com.medroid.sharedshopping;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * Created by ido on 19/02/2016.
 */
public class User  extends Parseable{
     String objectId= null;
    String first_name= "";
    Group group = null;
    ArrayList<CallbackParse> waiters= new ArrayList<>();

    static  final String FIRST_NAME ="first_name"; //field
    static  final String GROUP ="group"; //field

    static final String NAME_OF_TABLE = "User";

    public User(String first_name, Group group) {
        this.first_name = first_name;
        this.group = group;
    }
    public User(ParseObject parseObject, CallbackParse callbackParse){
        parseObject.fetchIfNeededInBackground( new getParseable(this,callbackParse));

    }

    @Override
    public void generateFromParseObject(ParseObject parseObject) {
        objectId = parseObject.getObjectId();
        first_name = parseObject.getString(FIRST_NAME);
        CallbackParse callbackParse= new CallbackParse() {
            @Override
            public void callback(Parseable parseable) {
                group = (Group) parseable;
            }
        };
        new Group(parseObject.getParseObject(GROUP),callbackParse );
    }

    @Override
    public ParseObject toParseObject() {
        ParseObject object = null;
        if (objectId == null){
            object= new ParseObject(NAME_OF_TABLE);
        } else {
            object = ParseObject.createWithoutData(NAME_OF_TABLE, objectId);
        }
        object.put(FIRST_NAME, first_name);
        object.put(GROUP, group.toParseObject());


        return object;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void getGroup(CallbackParse callback) {
        if (group != null){
            if (callback!= null)
            callback.callback(group);
        } else{
            waiters.add(callback);

        }

    }
    public void saveInstance(final CallbackParse callbackParse){

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

    public void setGroup(Group group) {
        this.group = group;
    }

    static public void getCurrentUser(final CallbackParse callback){
    ParseQuery<ParseObject> query = ParseQuery.getQuery(NAME_OF_TABLE);
    query.fromLocalDatastore();
    query.getFirstInBackground(new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject object, ParseException e) {
            if (e== null){
                new User(object,callback);
            }else{
                callback.callback(null);
            }
        }
    });

}

    @Override
    public void InformWaiters() {
        if (group!= null){
            for (CallbackParse waiter: waiters){
                waiter.callback(group);
            }
            waiters.clear();
        }
    }

    static private User user = null;
    static public User getCurrentUser(Context context){
        if (user!= null)
            return user;
        SharedPreferences sharedPref =  context.getSharedPreferences(
                context.getString(R.string.application_key), Context.MODE_PRIVATE);
        String first_name =sharedPref.getString(NAME_OF_TABLE + FIRST_NAME,null);
        String objectId = sharedPref.getString(NAME_OF_TABLE + "objectId",null);
        if (first_name == null)
            return null;
        user = new User(first_name,Group.getCurrentGroup(context));
        user.objectId = objectId;
        return user;
    }
    static public void setCurrentUser(User current_user, Context context){

        SharedPreferences sharedPref =  context.getSharedPreferences(
                context.getString(R.string.application_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (current_user == null)
            editor.clear();
        else {
            editor.putString(NAME_OF_TABLE + FIRST_NAME, current_user.getFirst_name());
            editor.putString(NAME_OF_TABLE + "objectId", current_user.getObjectId());
            editor.commit();
        }
        editor.commit();
        user= current_user;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public String getTableName() {
        return NAME_OF_TABLE;
    }
}
