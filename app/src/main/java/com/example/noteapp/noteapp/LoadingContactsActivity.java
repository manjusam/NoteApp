package com.example.noteapp.noteapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class LoadingContactsActivity extends Activity implements AdapterView.OnItemClickListener {
    List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    MyAdapter ma ;
    Button select,cancel;
    String remainder;
    public static Activity loading_contacts;
   // SimpleCursorAdapter cursor_adapter;
    ListView list_contacts;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_contacts);
        getAllContacts(this.getContentResolver());
        list_contacts = (ListView) findViewById(R.id.contacts_listView);
        loading_contacts=this;
        ma = new MyAdapter();
        list_contacts.setAdapter(ma);
        list_contacts.setOnItemClickListener(this);
        list_contacts.setItemsCanFocus(false);
        list_contacts.setTextFilterEnabled(true);
        Intent intent = getIntent();
        //  Bundle bundle = new Bundle();
        Bundle bundle = intent.getExtras();
       remainder=bundle.getString("reminder_message");
        // Initialize Content Resolver object to work with content Provider
        select = (Button) findViewById(R.id.select_button);
        cancel= (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadingContactsActivity.this,RemainderNote.class);
                startActivity(intent);
            }
        });
        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> selected_names = new ArrayList<String>();
                ArrayList<String> selected_phone_no = new ArrayList<String>();
                StringBuilder checkedcontacts = new StringBuilder();
                System.out.println(".............." + ma.mCheckStates.size());
                for (int i = 0; i < name1.size(); i++)

                {
                    if (ma.mCheckStates.get(i) == true) {
                        selected_names.add(name1.get(i));
                        selected_phone_no.add(phno1.get(i));
                        checkedcontacts.append(name1.get(i));
                        checkedcontacts.append("\n");

                    } else {
                        System.out.println("Not Checked......" + name1.get(i));
                    }


                }
                if(selected_names.size()>0)
                {
                Toast.makeText(LoadingContactsActivity.this, checkedcontacts,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoadingContactsActivity.this,SelectedContacts.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("contacts",selected_names);
                bundle.putStringArrayList("contacts_number",selected_phone_no);
                bundle.putString("reminder",remainder);
                intent.putExtras(bundle);
                startActivity(intent);}

                else{
                    Intent intent= new Intent(LoadingContactsActivity.this,ThanksCountDown.class);
                    startActivity(intent);}
            }
        });

    }
    public  void getAllContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println(".................."+phoneNumber);
            name1.add(name);
            phno1.add(phoneNumber);
        }

        phones.close();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ma.toggle(position);
    }


    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{
        private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView contact_name,contact_number;
        CheckBox cb;
        MyAdapter()
        {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater)LoadingContactsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return name1.size();
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
            View vi=convertView;
            if(convertView==null)
                vi = mInflater.inflate(R.layout.layout_checkbox, null);
            contact_name = (TextView) vi.findViewById(R.id.contact_name);
            contact_number= (TextView) vi.findViewById(R.id.contact_number);
            cb = (CheckBox) vi.findViewById(R.id.check_contact);

            contact_name.setText("Name :"+ name1.get(position));
            contact_number.setText("Phone No :"+ phno1.get(position));
            cb.setTag(position);
            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);
            return vi;
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


