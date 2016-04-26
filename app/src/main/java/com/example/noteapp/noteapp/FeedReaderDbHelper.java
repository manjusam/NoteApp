package com.example.noteapp.noteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by admin on 21/09/2015.
 */
public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MyDB.db";
    static String tableName = "remainder";
    Context context;
    final String SQL_CREATE_ENTRIES = "create table "+ tableName+"(_id integer primary key autoincrement,name text not null," +
            "number integer,message text,created_at datetime default current_timestamp,modified_at datetime default current_timestamp )";
    /*"CREATE TABLE mytable (" +  "id" + "integer primary key autoincrement," +
            "name" + "messageText" + TEXT_TYPE+" )";*/
    final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + tableName;
    SQLiteDatabase contactsDB=null;
    Cursor contactsCursor;
    public FeedReaderDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static ContentValues getValues(String contactName,String phoneNumber,String reminder_notes)
    {

        ContentValues contactValues = new ContentValues();
        try{
            //contactValues.put("","");
            contactValues.put("name",contactName);
            contactValues.put("number",phoneNumber);
            contactValues.put("message",reminder_notes);
            contactValues.put("created_at",getDateTime());
            contactValues.put("modified_at",getDateTime());
        }
        catch(Exception te){
            Log.e("NoteApp", te.getMessage()); }
      return contactValues;
    }
    public int updateContact(String name_contact,String message_from_edit) {

            FeedReaderDbHelper feed = new FeedReaderDbHelper(context);
            int high=0;
            SQLiteDatabase db = feed.getWritableDatabase();
        try {
            db.isOpen();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("message", message_from_edit);
            //  values.put(KEY_PH_NO, contact.getPhoneNumber());
           high  = db.update("remainder", values, "name= '" + name_contact+"'",null);
                 ;
            System.out.print(high+"ooooo");
            db.setTransactionSuccessful();
        }
        catch(Exception ed){
            Toast.makeText(context,"update not",Toast.LENGTH_LONG).show();}
           finally{db.endTransaction();}


        return high;
        // updating row


    }
/*
public static ContentValues getValues(Status status) {

        //prepare ContentValues to return
        ContentValues h omeValues = new ContentValues();

        //get the values
        try {
            //get each value from the table
           // homeValues.put(HOME_COL,"");
            homeValues.put(TWIT_ID, status.getId());
            Log.e("tt","ti"+ time.toString());
            //status.getUser().getProfileImageURL(); //returns a string which is profile image url
           // homeValues.put(USER_IMG, status.getUser().getProfileImageURL().toString()); is redundant
            homeValues.put(USER_IMG, status.getUser().getProfileImageURL());
        }
        catch(Exception te)
        { Log.e("NiceDataHelper", te.getMessage()); }
        //return the values
        return homeValues;
    }public Boolean ContainsTweets(long entryId)
    {
     database.rawQuery("select name from sqlite_master where type = 'table' and name = 'dict_tbl'", null);

     NiceDataHelper niceData= new NiceDataHelper(context);
     timelineDB = niceData.getReadableDatabase();
       twitCursor = timelineDB.rawQuery("SELECT * FROM twit WHERE twitter_id = '" + entryId + "'", null);
        if (twitCursor.getCount() > 0) { // This will get the number of rows
            return false;
        }
       // count++;
        return true;
    }*/
    public Boolean containsContactDetails(String contactName,String contactNumber){

        FeedReaderDbHelper DbHelper = new FeedReaderDbHelper(context);
        contactsDB = DbHelper.getReadableDatabase();
        contactsCursor=contactsDB.rawQuery("select * from remainder where name = '" + contactName + "' and number= '" + contactNumber + "'", null);
       // contactsCursor = contactsDB.rawQuery("SELECT * FROM reminder WHERE name = '" + contactName + "' and number = '" + contactNumber + "' , null);
        //String name= contactsCursor.getString(contactsCursor.getColumnIndex("name"));
        //String append_reminder= contactsCursor.getString(contactsCursor.getColumnIndex("message"));
       // contactsCursor.moveToFirst();
       // System.out.println(name + " "+ append_reminder);
        if(contactsCursor.getCount()>0)
        {
            return false;
        }
        contactsCursor.moveToFirst();
        contactsCursor.close();
        return true;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.e("table","created");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
