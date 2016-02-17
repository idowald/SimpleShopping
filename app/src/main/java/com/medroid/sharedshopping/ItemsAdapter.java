package com.medroid.sharedshopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
        name.setText(item.getName());
        quantity.setText(item.getQuantity()+"");


        ImageView delete= (ImageView) rowView.findViewById(R.id.delete_button);
        ImageView save= (ImageView) rowView.findViewById(R.id.save_button);

        delete.setOnClickListener(new listenDelete(item,position));
        save.setOnClickListener(new listenEdit(item,name,quantity));




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

    class listenEdit implements View.OnClickListener {
        Item item= null;
        EditText name = null;
        EditText quantity= null;

        public listenEdit(Item item, EditText name, EditText quantity) {
            this.item = item;
            this.name = name;
            this.quantity = quantity;
        }

        @Override
        public void onClick(View v) {
            item.setName(name.getText().toString());
            item.setQuantity(Integer.parseInt(quantity.getText().toString()));
            item.saveItem();
            Toast.makeText(context,"saved", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();



        }
    }
}
