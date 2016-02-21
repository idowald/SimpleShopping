package com.medroid.sharedshopping;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by ido on 12/02/2016.
 */
public class Item extends Parseable {
    ArrayList<CallbackParse> waiters_creators = new ArrayList<>();
    ArrayList<CallbackParse> waiters_groups = new ArrayList<>();

    String objectId = null;
    String name = "";
    Integer quantity = 0;
    User creator_user = null; //which group it is
    boolean deleted = false;
    Group group = null; //who created the object

    static final String NAME = "name"; //field
    static final String QUANTITY = "quantity"; //field
    static final String DELETED = "deleted"; //field
    static final String CREATOR_USER = "creator_user"; //field
    static final String GROUP = "group"; //field

    static final String NAME_OF_TABLE = "Items2";

    public Item(String name, Integer quantity, User creator_user, boolean deleted, Group group) {
        this.name = name;
        this.quantity = quantity;
        this.creator_user = creator_user;
        this.deleted = deleted;
        this.group = group;
    }


    public Item(ParseObject object) { //nobody points on item so it doesn't relational
        generateFromParseObject(object);


    }

    public ParseObject toParseObject() {
        ParseObject object = null;
        if (getObjectId() == null)
            object = new ParseObject(NAME_OF_TABLE);
        else {
            object = ParseObject.createWithoutData(NAME_OF_TABLE, getObjectId());
        }
        object.put(NAME, getName());
        object.put(QUANTITY, getQuantity());
        object.put(CREATOR_USER, creator_user.toParseObject());
        object.put(GROUP, group.toParseObject());
        object.put(DELETED, isDeleted());


        return object;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void deleteItem() {
        setDeleted(true);
        saveItem();

    }

    public void saveItem() {
        new Thread() {
            @Override
            public void run() {
                toParseObject().saveEventually();
            }
        }.start();
    }


    @Override
    public void generateFromParseObject(ParseObject parseObject) {
        CallbackParse getGroup = new CallbackParse() {
            @Override
            public void callback(Parseable parseable) {
                group = (Group) parseable;
            }
        };
        CallbackParse getCreator = new CallbackParse() {
            @Override
            public void callback(Parseable parseable) {
                creator_user = (User) parseable;
                InformWaiters();

            }
        };
        setObjectId(parseObject.getObjectId());
        setName(parseObject.getString(NAME));
        setQuantity(parseObject.getInt(QUANTITY));
        new User(parseObject.getParseObject(CREATOR_USER), getCreator);
        new Group(parseObject.getParseObject(GROUP), getGroup);
        setDeleted(parseObject.getBoolean(DELETED));
    }

    @Override
    public void InformWaiters() {
        if (group != null) {
            for (CallbackParse waiter : waiters_groups) {
                waiter.callback(group);
            }
            waiters_groups.clear();
        }
        if (creator_user != null) {
            for (CallbackParse waiter : waiters_creators) {
                waiter.callback(creator_user);
            }
            waiters_creators.clear();
        }
    }

    public void getGroup(CallbackParse callbackParse) {
        if (group!= null) {
            if (callbackParse != null)
                 callbackParse.callback(group);
        }
        else
        waiters_groups.add(callbackParse);

    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public synchronized void getCreator_user (CallbackParse callbackParse) {
        if (creator_user!= null) {
            if (callbackParse != null)
                callbackParse.callback(creator_user);
        }
        else
            waiters_creators.add(callbackParse);

    }

    public void setCreator_user(User creator_user) {
        this.creator_user = creator_user;
    }

    @Override
    public String getTableName() {
        return NAME_OF_TABLE;
    }
}
