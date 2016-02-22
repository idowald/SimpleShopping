package com.medroid.sharedshopping;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by ido on 19/02/2016.
 */

class getParseable implements GetCallback<ParseObject> {
    private Parseable parsecalli= null;
    private CallbackParse callback = null;

    public getParseable(Parseable parsecalli, CallbackParse callback) {
        /**
         *  parsecalli is the class we're inflating
         *  callback is the class that wants to instance in it
         *  1 user to * items
         *  items have user field. and it wants the user.
         *  the user is the parsecalli- (it want to be inflated and return to item callback)
         */
        this.parsecalli = parsecalli;
        this.callback= callback;
        //parsecalli.InformWaiters();

    }




    @Override
    public void done(ParseObject object, ParseException e) {
        if (e== null){
            parsecalli.generateFromParseObject(object);
            if (callback!= null)
                callback.callback(parsecalli); //hot and ready from the cloud
        }else {
            //no connection
        }
    }
}
