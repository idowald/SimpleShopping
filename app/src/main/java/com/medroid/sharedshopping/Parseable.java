package com.medroid.sharedshopping;

import com.parse.ParseObject;

/**
 * Created by ido on 19/02/2016.
 */
public abstract class Parseable    {

    abstract public void generateFromParseObject(ParseObject parseObject);

    abstract public ParseObject toParseObject();

    abstract public void InformWaiters();

    abstract public String getObjectId();

    abstract public String getTableName();

    public ParseObject generateWithoutData(){
        ParseObject parseObject = ParseObject.createWithoutData(getTableName(), getObjectId());
     return parseObject;
    }




}
