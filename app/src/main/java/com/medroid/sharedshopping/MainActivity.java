package com.medroid.sharedshopping;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listview = null;
    ItemsAdapter itemsAdapter = null;
    ParseUser user = null;
    LayoutInflater inflater = null;
    AppCompatActivity activity = null;
    AlertDialog alert= null;
    ProgressDialog progress= null;
    Toolbar toolbar = null;


    AlertDialog errordialog = null;

    private AnalyticsTrackers analyticsTrackers;

    private Tracker mTracker;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        inflater= getLayoutInflater();
        activity= this;
        progress= new ProgressDialog(this);
        progress.setTitle("טוען");
        if (isNetworkAvailable() == false )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
            builder.setIcon(R.mipmap.ic_error);
            builder.setTitle("warning");
            builder.setMessage(getResources().getString(R.string.error_no_connection))
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (isNetworkAvailable())
                                errordialog.dismiss();
                            else
                                errordialog.show();
                        }
                    });

            errordialog = builder.create();
            errordialog.show();


        }

        ParseQuery<ParseObject> geturl= ParseQuery.getQuery("ApplicationUrl");
        geturl.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e== null){
                    MyApplication.MyUrl= object.getString("url");
                }else{
                    Toast.makeText(getApplicationContext(),"אין חיבור לאינטרנט",Toast.LENGTH_LONG).show();
                }
            }
        });

        listview = (ListView) findViewById(R.id.listView);
        itemsAdapter = new ItemsAdapter(this);
        listview.setAdapter(itemsAdapter);






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                inflater= getLayoutInflater();

                final  AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
                View newItemView = inflater.inflate(R.layout.new_item_alert,null);
                EditText name = (EditText)newItemView.findViewById(R.id.item_name);
                EditText  quantity= (EditText)newItemView.findViewById(R.id.quantity);

                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setTitle(getResources().getString(R.string.app_name))
                        .setMessage(getResources().getString(R.string.new_item))
                        .setView(newItemView)
                        .setPositiveButton("שמור", new saveNewItem(quantity,name,getApplicationContext()))
                        .setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.dismiss();
                            }
                        });
                alert= builder.create();
                alert.show ();






            }
        });
        FloatingActionButton refresh_btn = (FloatingActionButton) findViewById(R.id.refresh_btn);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: Main Activity");
        mTracker.setScreenName("Image~" + "Main Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        user = ParseUser.getCurrentUser();

        if (!isNetworkAvailable())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
            builder.setIcon(R.mipmap.ic_error);
            builder.setTitle("warning");
            builder.setMessage(getResources().getString(R.string.error_no_connection))
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (isNetworkAvailable())
                                errordialog.dismiss();
                            else
                                errordialog.show();
                        }
                    });

            errordialog = builder.create();
            errordialog.show();

            return;
        }
        else{
            try{
                //TODO why its empty
            }catch(Exception e){
                errordialog.dismiss();
            }
        }
        if (user == null) //no signed
        {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        toolbar.setTitle(user.getUsername());


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);

                builder.setTitle(getResources().getString(R.string.app_name))
                        .setMessage(getResources().getString(R.string.prompt_email)+":"+user.getUsername()+"\n"+
                                getResources().getString(R.string.prompt_password)+":"+user.getString("pass"));
                builder.create().show ();
            }
        });
        setSupportActionBar(toolbar);

        itemsAdapter.clear();
        progress.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.NAME_OF_TABLE);
        query.whereEqualTo("user", user);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        Item item = new Item(object);
                        itemsAdapter.add(item);
                    }
                    progress.dismiss();
                } else {

                    Toast.makeText(getApplicationContext(), "error, not connected", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.tell_friend_button){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.tell_friend_text)+": "+ MyApplication.MyUrl);
            sendIntent.setType("text/plain");

            startActivity(sendIntent);

        }
        if(id == R.id.share_list_button || id == R.id.share_list_button2){

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Share List")
                    .build());

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_list_text)+"\n"+
                    user.getUsername()+"\n"+
                    getResources().getString(R.string.share_list_text2)+"\n"+
                    user.get("pass")
                    +"\n"+
                    getResources().getString(R.string.share_list_text3)+"\n"+
                    MyApplication.MyUrl);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);


        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.send_im_shopping){
            final  String channel_name =ParseUser.getCurrentUser().getUsername();
            //problem -> unsc->
            //problem-> can't send to my own channel
            ParsePush.unsubscribeInBackground(channel_name, new SaveCallback() { //unsubscribe myself
                @Override
                public void done(ParseException e) {
                    if (e != null)
                        e.printStackTrace();
                    else {
                        ParsePush push = new ParsePush();
                        push.setChannel(channel_name);
                        push.setMessage(getResources().getString(R.string.im_shopping));
                        push.sendInBackground(new SendCallback() { //when finished sending subscribe myself
                            @Override
                            public void done(ParseException e) {
                                if (e != null)
                                    e.printStackTrace();
                                else {


                                    Toast.makeText(getApplicationContext(), "נשלחה ההתראה :)", Toast.LENGTH_SHORT).show();
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                sleep(1300);
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                            ParsePush.subscribeInBackground(channel_name); //save user name as push user


                                        }
                                    }.run();
                                }

                            }
                        });

                    }
                }
            }); //save user name as push user

        }

        if (id == R.id.loggoff_butn){

            AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
            builder.setIcon(R.mipmap.ic_error);
            builder.setMessage(getResources().getString(R.string.confirm_logoff));
            builder.setPositiveButton(getResources().getString(R.string.logoff_btn), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e== null){
                                Toast.makeText(getApplication(),"Logout successfull", Toast.LENGTH_LONG).show();
                                onResume();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show ();

        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class saveNewItem implements DialogInterface.OnClickListener{
        Context context = null;
        Item item= new Item("",0);
        EditText name = null;
        EditText  quantity= null;

        public saveNewItem(EditText quantity, EditText name, Context context) {
            this.quantity = quantity;
            this.name = name;
            this.context = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String item_name =name.getText().toString();
            Integer item_quantity = Integer.parseInt(quantity.getText().toString());
            item.setName(item_name);
            item.setQuantity(item_quantity);
            item.saveItem();
            itemsAdapter.add(0,item);
            Log.v("new item", item.toString());
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
