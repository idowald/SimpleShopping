package com.medroid.sharedshopping;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ido on 12/02/2016.
 */
public class ItemsAdapter extends BaseAdapter {
    ArrayList<Item> values= new ArrayList<>();
    Context context= null;

    public ItemsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item,parent,false);
        Item item= values.get(position);
        EditText name= (EditText )rowView.findViewById(R.id.item_name);
        EditText quantity= (EditText )rowView.findViewById(R.id.item_quantity);
        TextView creator_text_view= (TextView) rowView.findViewById(R.id.creator_name);


        name.setText(item.getName());
        quantity.setText(item.getQuantity()+"");

        item.getCreator_user(new getCreator(creator_text_view));

        ImageView delete= (ImageView) rowView.findViewById(R.id.delete_button);


        delete.setOnClickListener(new listenDelete(item,position));
        listenEdit listen= new listenEdit(item,name,quantity);
        quantity.addTextChangedListener(listen);

        name.addTextChangedListener(listen);





        return rowView;
    }

    public void add(Item object) {

        values.add(object);
        notifyDataSetChanged();
    }

    public void add(int position , Item object ) {

        values.add(position, object);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        values.remove(index);
        notifyDataSetChanged();
    }

    public void clear() {
        values.clear();
        notifyDataSetChanged();
    }

    class listenDelete implements View.OnClickListener {
        Item item= null;
        int index =-1;
        public listenDelete(Item item, int index) {
            this.item = item;
            this.index= index;
        }

        @Override
        public void onClick(View v) {
            remove(index);
            if (item.getObjectId()!= null)
                item.deleteItem();
            Toast.makeText(context, "deleted", Toast.LENGTH_LONG).show();


        }
    }

    class listenEdit implements TextWatcher {
        Item item= null;
        EditText name = null;
        EditText quantity= null;


        long idle_min = 1500; // 4 seconds after user stops typing
        long last_text_edit = 0;
        Handler h = new Handler();
        boolean already_queried = false;

        private Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + idle_min - 500)) {
                    // user hasn't changed the EditText for longer than
                    // the min delay (with half second buffer window)
                    if (!already_queried) { // don't do this stuff twice.
                        already_queried = true;
                        saveChanges();  // your queries
                    }
                }
            }
        };

        public listenEdit(Item item, EditText name, EditText quantity) {
            this.item = item;
            this.name = name;
            this.quantity = quantity;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            last_text_edit = System.currentTimeMillis();
            h.postDelayed(input_finish_checker, idle_min);

        }


        public void saveChanges() {
            item.setName(name.getText().toString());
            item.setQuantity(Integer.parseInt(quantity.getText().toString()));
            item.saveItem();
            Toast.makeText(context,"נשמר", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();



        }
    }

    class getCreator implements CallbackParse{
        TextView textView= null;
        String text= null;

        public getCreator(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void callback(Parseable parseable) {
           String name =((User)parseable).getFirst_name();
            if (name.length()>=3)
             text =(name.substring(0,3));
            else
             text= name;
            textView.setText(text);


        }
    }
}
