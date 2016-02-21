package com.medroid.sharedshopping;


import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//not in use
public class NewItemActivity extends AppCompatActivity {
    EditText name = null;
    EditText quantity= null;
    Button savebutton= null;
    Button cancelbutton= null;

    User current_user =null;
    Item newItem = null;
    Group group= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        User.getCurrentUser(new CallbackParse() {
            @Override
            public void callback(Parseable parseable) {
                current_user= (User) parseable;
                current_user.getGroup(new CallbackParse() {
                    @Override
                    public void callback(Parseable parseable) {
                        group = (Group) parseable;
                        newItem =  new Item("",0,current_user,false,group);
                    }
                });

            }
        });



        name= (EditText)findViewById(R.id.item_name);
        quantity= (EditText)findViewById(R.id.quantity);

        savebutton= (Button) findViewById(R.id.save_button) ;
        cancelbutton= (Button)findViewById(R.id.cancel) ;


        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItem.setQuantity(Integer.parseInt(quantity.getText().toString()));
                newItem.setName(name.getText().toString());
                newItem.saveItem();

                Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_LONG).show();
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(1200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }
                }.start();

            }
        });
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }


}
