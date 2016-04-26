package com.example.noteapp.noteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin on 22/09/2015.
 */
public class SelectedContacts extends Activity implements AdapterView.OnItemClickListener {
    ListView list_contacts;
    ArrayList<String> final_contacts = new ArrayList<String>();
    ArrayList<String> final_phone_number = new ArrayList<String>();
    String remainder;
    public static Activity selected_activity;
    /**
     * database helper object
     */
    FeedReaderDbHelper contactDBHelper;
    /**
     * timeline database
     */
    SQLiteDatabase contactDB = null;
    Cursor updatecursor;
    MyFinalAdapter ma;
    Boolean checkContact_Details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_loading_contacts);
        list_contacts = (ListView) findViewById(R.id.contacts_listView);
        ma = new MyFinalAdapter();
        //get database helper
        contactDBHelper = new FeedReaderDbHelper(this);
        //get the database
        contactDB = contactDBHelper.getWritableDatabase();
        selected_activity=this;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = 600;
        params.width = 480;
        params.y = -10;
        this.setFinishOnTouchOutside(false);
        this.getWindow().setAttributes(params);
        Intent intent = getIntent();
        //  Bundle bundle = new Bundle();
        Bundle bundle = intent.getExtras();
        final_contacts = bundle.getStringArrayList("contacts");
        final_phone_number = bundle.getStringArrayList("contacts_number");
        remainder = bundle.getString("reminder");
        list_contacts.setAdapter(ma);
        list_contacts.setOnItemClickListener(this);
        list_contacts.setItemsCanFocus(false);
        list_contacts.setTextFilterEnabled(true);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ma.toggle(position);
    }

    public void cancelButton(View v)
    {
        Intent intent= new Intent(SelectedContacts.this,ThanksCountDown.class);
        startActivity(intent);
    }
    public void intentResult(View v)
    {
        try {
            for (int i = 0; i < final_contacts.size(); i++) {
                //need to name and ph noe from string name.
                if (ma.mCheckStates.get(i) == true) {

                    ContentValues contactValues = FeedReaderDbHelper.getValues(final_contacts.get(i), final_phone_number.get(i), remainder);
                    System.out.println("erere........." + remainder);
                    checkContact_Details = contactDBHelper.containsContactDetails(final_contacts.get(i), final_phone_number.get(i));
                    if (checkContact_Details) {
                        //Log.e("yes contains", "yes????");
                       // System.out.println("check details...................." + ma.mCheckStates.size() + final_contacts.get(i));
                        contactDB.insertOrThrow("remainder", null, contactValues);
                        Toast.makeText(this, "your remainder is saved", Toast.LENGTH_LONG).show();


                    } else {
                        System.out.println("already its there........." + ma.mCheckStates.size() + final_contacts.get(i));
                        // ContentValues content = new ContentValues();
                        //content.put("message",);
                        String qu= "update remainder SET message = '" + remainder + "' where number = '" + final_phone_number.get(i)+"'";
                        updatecursor=contactDB.rawQuery(qu,null);
                        updatecursor.moveToFirst();
                        updatecursor.close();
                       // updatecursor.moveToNext();
                        //updatecursor.close();
                        contactDB= contactDBHelper.getReadableDatabase();
                        updatecursor = contactDB.rawQuery("SELECT * FROM remainder WHERE name = '" + final_contacts.get(i)+ "' and number = '" + final_phone_number.get(i) + "'" , null);
                        updatecursor.moveToFirst();
                        String name= updatecursor.getString(updatecursor.getColumnIndex("name"));
                        String append_reminder=updatecursor.getString(updatecursor.getColumnIndex("message"));
                        String date = updatecursor.getString(updatecursor.getColumnIndex("created_at"));
                        // contactsCursor.moveToFirst();
                        System.out.println(name + " "+ append_reminder+""+date);
                        // System.out.println(name + " "+ append_reminder);
                       // updatecursor.moveToFirst();
                        updatecursor.close();

                        Toast.makeText(this, "your remainder is saved", Toast.LENGTH_LONG).show();

                    }

                }
            }
            Intent intent= new Intent(SelectedContacts.this,ThanksCountDown.class);
            startActivity(intent);

        }

        catch (Exception te)
        {Toast.makeText(this,"catch"+te.getMessage(),Toast.LENGTH_LONG).show();}


    }

    class MyFinalAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{
        private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView contact_name,contact_number;
        CheckBox cb;
        MyFinalAdapter()
        {
            mCheckStates = new SparseBooleanArray(final_contacts.size());
            mInflater = (LayoutInflater)SelectedContacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return final_contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View selected_view=convertView;
            if(convertView==null)
                selected_view = mInflater.inflate(R.layout.selected_contacts_checkbox, null);
            contact_name = (TextView) selected_view.findViewById(R.id.contact_name);
            contact_number= (TextView) selected_view.findViewById(R.id.contact_number);
            cb = (CheckBox) selected_view.findViewById(R.id.check_contact_final);

            contact_name.setText("Name :"+ final_contacts.get(position));
            contact_number.setText("Mobile :"+ final_phone_number.get(position));
            cb.setTag(position);
            //cb.setChecked(true);
            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);
            return selected_view;
        }
        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
            System.out.println("hello...........");
            notifyDataSetChanged();
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCheckStates.put((Integer) buttonView.getTag(), isChecked);
        }
    }

}
