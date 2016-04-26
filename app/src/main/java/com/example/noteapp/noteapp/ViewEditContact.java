package com.example.noteapp.noteapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 27/09/2015.
 */
public class ViewEditContact extends Activity {
    TextView contact_textview;
    FeedReaderDbHelper feedReader;
    SQLiteDatabase feedDB=null;
    /**cursor for handling data*/
    Cursor remainderCursor;
    String messageFromExtra;
    ArrayList<String> temp_contacts;
    Button edit_note_button,delete_note_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_edit_contact_layout);
        contact_textview = (TextView) findViewById(R.id.vec_contact_textview);
        edit_note_button = (Button) findViewById(R.id.vec_btn_edit);
        delete_note_button = (Button) findViewById(R.id.vec_btn_cancel);

        Bundle extras = getIntent().getExtras();
        messageFromExtra = extras.getString("messageUser");
        System.out.println(">>>>>>>>> msg from contact v e contact"+messageFromExtra);
        try {
            String data;
            feedReader = new FeedReaderDbHelper(this);
            feedDB = feedReader.getReadableDatabase();
            remainderCursor = feedDB.rawQuery("select name, number from remainder where message like '" + messageFromExtra + "'", null);
            if (remainderCursor.moveToFirst()) {
                String temp = "";
                temp_contacts = new ArrayList<>();
                do {
                    data = remainderCursor.getString(remainderCursor.getColumnIndex("name")) + "\n";
                    temp_contacts.add(data);
                    temp += data + "\n";
                } while (remainderCursor.moveToNext());
                contact_textview.setText(temp);
              // for(int i=0;i<temp_contacts.size();i++){
              //  contact_textview.setText(temp_contacts.get(i)+"\n");}
            }
            remainderCursor.close();

        }
        catch(Exception exce)
        {
            Log.e("ViewEditContact", "Failed here from ViewEditContact: " + exce.getMessage());
        }
   edit_note_button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent =new Intent(ViewEditContact.this,EditRemainder.class);
             Bundle bundle_edit = new Bundle();
             bundle_edit.putString("messageToEdit",messageFromExtra);
             bundle_edit.putStringArrayList("contacts_list_edit",temp_contacts);
             intent.putExtras(bundle_edit);
             startActivity(intent);
         }
     });

    }

    public void edit_message()
    {


    }
}
