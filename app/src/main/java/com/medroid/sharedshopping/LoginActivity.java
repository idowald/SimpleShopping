package com.medroid.sharedshopping;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */


    /**
     * A dummy authentication store containing known user names and passwords.
     *
     */

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mfirstNameView;

    AlertDialog dialog= null;

    private AnalyticsTrackers analyticsTrackers;

    private Tracker mTracker;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        ParseUser user= ParseUser.getCurrentUser();
        if (user!= null){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_present);
                builder.setTitle("עדכון!")
                        .setMessage("שיפרנו את המערכת! נא להתחבר מחדש!")
                        .setPositiveButton("תודה!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mEmailView.requestFocus();
                                ParseUser.logOutInBackground();
                            }
                        });

                dialog = builder.create();
                dialog.show();
            } else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_tip);
            builder.setTitle("Tip")
                    .setMessage(getResources().getString(R.string.tip))
                    .setPositiveButton("הבנתי!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mEmailView.requestFocus();
                        }
                    });

            dialog = builder.create();
            dialog.show();
        }

        // Set up the login form.

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mEmailView.setFocusable(true);
        mPasswordView = (EditText) findViewById(R.id.password);
        mfirstNameView = (EditText) findViewById(R.id.first_name);


        mPasswordView.setFocusable(true);
        mEmailView.setFocusable(true);
        Button log_into_list_btn = (Button) findViewById(R.id.log_into_list);
        Button sign_up_create_list_btn = (Button) findViewById(R.id.sign_up_new_list);
        log_into_list_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        sign_up_create_list_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });


    }








    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {
        /**
         * creating new list
         */

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString().toLowerCase().trim();
        final String password = mPasswordView.getText().toString();
        final String first_name = mfirstNameView.getText().toString().trim();



        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Status status = new Status() {
                @Override
                public void issuccess(boolean status) {
                    //sign to push notification service
                    if (status) {
                        ParsePush.subscribeInBackground(email.toLowerCase());
                        Toast.makeText(getApplicationContext(), "פתחנו רשימה חדשה!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        //list already taken try different name error
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_list_taken), Toast.LENGTH_LONG).show();
                    }
                }
            };
            new CreateNewList(email,password,first_name).checkIfExist(status);

        }
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString().toLowerCase().trim();
        final String password = mPasswordView.getText().toString();
        final String first_name = mfirstNameView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Status status= new Status() {
                @Override
                public void issuccess(boolean status) {
                    //sign to push notification service
                    if (status) {
                        ParsePush.subscribeInBackground(email.toLowerCase() );
                        Toast.makeText(getApplicationContext(), "נרשמנו!", Toast.LENGTH_LONG).show();
                        finish();
                    } else{
                        //wrong password to list
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_incorrect_password),Toast.LENGTH_LONG).show();
                    }
                }
            };
            new Login(email,password,first_name).checkIfListExist(status);


        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length()>4 && (!email.contains(" ")) ;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: Main Activity");
        mTracker.setScreenName("Image~" + "Main Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    class Login{
        /**
         *  go to parse-> check if group list name+ password is good.
         *  if yes. succeed,
         *  if not. failure
         */
        private String password= null;
        private String Group_name= null;
        private String user_first_name = null;

        Group group = null;
        User user = null;

        public Login(String group_name,String password  ,  String user_first_name ) {
            this.password = password;
            Group_name = group_name;
            this.user_first_name = user_first_name;

        }
        public void checkIfListExist(final Status status){
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Group.NAME_OF_TABLE);
           query.whereEqualTo(Group.PASSWORD,password);
            //query.whereEqualTo(Group.GROUP_NAME, Group_name);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e== null){
                        // success

                        CallbackParse GroupFinished= new CallbackParse() {
                            @Override
                            public void callback(Parseable parseable) {
                                group.saveinstance(new CallbackParse() {
                                    @Override
                                    public void callback(Parseable parseable) {
                                        Group.setCurrentGroup(group,getApplicationContext());

                                        user =new User(user_first_name, group);
                                        user.saveInstance(new CallbackParse() {
                                            @Override
                                            public void callback(Parseable parseable) {
                                                User.setCurrentUser(user,getApplicationContext());
                                                status.issuccess(true);
                                            }
                                        });

                                    }
                                });
                            }
                        };
                        group =new Group(object, GroupFinished);




                    }
                    else{//failure
                      status.issuccess(false);

                    }
                }
            });
        }
    }

    class CreateNewList{
        private String GroupList = null;
        private String password= null;
        private String user_first_name=  null;

        public CreateNewList(String groupList, String password, String user_first_name) {
            GroupList = groupList;
            this.password = password;
            this.user_first_name = user_first_name;
        }

        public void checkIfExist(final Status status){
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Group.NAME_OF_TABLE);
            query.whereEqualTo(Group.PASSWORD,password);
            query.whereEqualTo(Group.GROUP_NAME, GroupList);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e== null){
                     // failure
                    status.issuccess(false);

                    }
                    else{
                        //success
                        final Group group = new Group(GroupList,password);
                        group.saveinstance(new CallbackParse() {
                            @Override
                            public void callback(Parseable parseable) {
                                Group.setCurrentGroup(group,getApplicationContext());
                                 final User user =new User(user_first_name, group);
                                user.saveInstance(new CallbackParse() {
                                    @Override
                                    public void callback(Parseable parseable) {
                                        User.setCurrentUser(user,getApplicationContext());
                                        status.issuccess(true);
                                    }
                                });

                            }
                        });







                    }
                }
            });
        }


    }

    interface  Status{
         void issuccess(boolean status);
    }
}

