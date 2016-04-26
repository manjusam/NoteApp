package com.example.noteapp.noteapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin on 29/09/2015.
 */
public class EditRemainder extends Activity implements View.OnClickListener {
    Button edit_button_ok;
    EditText edit_remainder;
    Button edit_clear;
    String edit_remainder_set;
   FeedReaderDbHelper feedEdit;
    SQLiteDatabase feedEditDB = null;
    Cursor editReminderCursor;
    ArrayList<String> edited_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_reminder);
        feedEdit = new FeedReaderDbHelper(this);
        feedEditDB = feedEdit.getWritableDatabase();
        edit_button_ok = (Button) findViewById(R.id.edit_remainder_ok_button);
        edit_clear = (Button) findViewById(R.id.edit_remainder_clear_button);
        edit_remainder = (EditText) findViewById(R.id.edit_remainder_edittext);
        Intent editintent = getIntent();
        Bundle bundle = editintent.getExtras();
        edited_contacts = bundle.getStringArrayList("contacts_list_edit");
        edit_remainder_set = bundle.getString("messageToEdit");
        edit_remainder.setText(edit_remainder_set);
        edit_button_ok.setOnClickListener(this);
        edit_clear.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.edit_remainder_ok_button:
                try {
                    String new_string =  edit_remainder.getText().toString();
                    for(int i=0;i<edited_contacts.size();i++) {
                        System.out.println("already its there........." + edited_contacts.get(i) + "oooooooopppppppsssss" + new_string);
                         feedEditDB.isOpen();
                        String query_update ="update remainder SET message = '" + new_string + "' where name = '" + edited_contacts.get(i)+"'";
                        editReminderCursor = feedEditDB.rawQuery(query_update,null);
                        editReminderCursor.moveToFirst();
                        editReminderCursor.close();
                        System.out.print(editReminderCursor.toString());
                       // editReminderCursor.moveToFirst();
                        //editReminderCursor.close();

                    }
                  feedEditDB.close();
                      // Toast.makeText(this, "Edited and updated succesfully ", Toast.LENGTH_LONG).show();
                  //editReminderCursor.close();
                }

                catch (Exception te) {
                    Toast.makeText(this, "catch" + te.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.edit_remainder_clear_button:
                edit_remainder.setText(" ");
                break;

        }
    }
}
