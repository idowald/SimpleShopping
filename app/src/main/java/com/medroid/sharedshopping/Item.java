package com.medroid.sharedshopping;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ido on 12/02/2016.
 */
public class Item {
    String objectId=null;
    String name="";
    Integer quantity=0;
    ParseUser user= null;

    static final String NAME_OF_TABLE = "Items";

    public Item(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
        user= ParseUser.getCurrentUser();
    }
    public Item(ParseObject object){
        setObjectId(object.getObjectId());
        setName(object.getString("name"));
        setQuantity(object.getInt("quantity"));
        user= (ParseUser) object.getParseObject("user");
    }

    public ParseObject toParseObject(){
        ParseObject object = null ;
        if (getObjectId()== null)
            object = new ParseObject(NAME_OF_TABLE);
        else{
            object = ParseObject.createWithoutData(NAME_OF_TABLE,getObjectId());
        }
        object.put("name",getName());
        object.put("quantity",getQuantity());
        object.put("user", user);


        return object;
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void deleteItem(){
        if (getObjectId()!= null)
            new Thread(){
                @Override
                public void run() {
                    ParseObject.createWithoutData(NAME_OF_TABLE, getObjectId()).deleteEventually();

                }
            }.start();

    }

    public void saveItem(){
        new Thread(){
            @Override
            public void run() {
                toParseObject().saveEventually();
            }
        }.start();
    }
}
