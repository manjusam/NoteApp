package com.example.noteapp.noteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by admin on 27/09/2015.
 */
public class SeeAllActivity extends Activity {
    ListView seeall_listview;
    FeedReaderDbHelper feedReader;
    SQLiteDatabase feedDB=null;
    /**cursor for handling data*/
    Cursor remainderCursor;
    MySeeAllAdapter seeallcursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_seeall_view);
        seeall_listview = (ListView) findViewById(R.id.seeall_listview);
        try {
        feedReader = new FeedReaderDbHelper(this);
        feedDB = feedReader.getReadableDatabase();
        remainderCursor = feedDB.query(true,"remainder",new String[] {"_id" ,"name", "message", "created_at"},null,null,"message",null,"created_at DESC",null);
        //db.query(true, YOUR_TABLE_NAME, new String[] { COLUMN_NAME_1 ,COLUMN_NAME_2, COLUMN_NAME_3 },
        // null, null, COLUMN_NAME_2, null, null, null);
        //remainderCursor = feedDB.query("remainder", null, null, null, null, null, "created_at DESC");

            if (remainderCursor != null){
               remainderCursor.moveToFirst();
            //instantiate adapter
            // have to see this line and familiar with cursor adpater
            seeallcursorAdapter = new MySeeAllAdapter(this, remainderCursor,0);
            //this will make the app populate the new update data in the timeline view
            seeall_listview.setAdapter(seeallcursorAdapter);}
            else{
                Toast.makeText(this,"No reminder have set yet",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception exc){
            Log.e("SeeAllActivity", "Failed here from Profile Fragment: " + exc.getMessage()); }
    }

    private static class MySeeAllAdapter extends CursorAdapter {

        LayoutInflater minflater;
        FeedReaderDbHelper feedDeleteHelper;
        SQLiteDatabase feedDeleteDB = null;



      Context context;
        public MySeeAllAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            this.context=context;
            minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            feedDeleteHelper = new FeedReaderDbHelper(context);
            feedDeleteDB = feedDeleteHelper.getWritableDatabase();
        }

        protected static class RowViewHolder {
            public TextView reminderMessage;
            public TextView reminderTimestamp;
            public Button show_Button, delete_Button;

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = minflater.inflate(R.layout.seeall, parent, false);

            return view;
        }



        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            RowViewHolder holder = new RowViewHolder();
            holder.reminderMessage = (TextView) view.findViewById(R.id.remainder_seeall);
            holder.reminderTimestamp = (TextView) view.findViewById(R.id.time_stamp_view);
            holder.show_Button = (Button) view.findViewById(R.id.sa_btn_show);
            holder.delete_Button = (Button) view.findViewById(R.id.sa_btn_delete);

            String reminder_message= cursor.getString(cursor.getColumnIndex("message"));
            ContactMessgaeData cnt = new ContactMessgaeData(reminder_message);
            view.findViewById(R.id.sa_btn_show).setTag(cnt);
            view.findViewById(R.id.sa_btn_delete).setTag(cnt);
            holder.reminderMessage.setText(cursor.getString(cursor.getColumnIndex("message")) );
            holder.reminderTimestamp.setText(cursor.getString(cursor.getColumnIndex("created_at")));
            holder.show_Button.setOnClickListener(mOnTitleClickListener);
            holder.delete_Button.setOnClickListener(mOnTitleClickListener);
            view.setTag(holder);

        }
        private View.OnClickListener mOnTitleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            switch(v.getId()) {
                case R.id.sa_btn_show:
                    Intent replyIntent = new Intent(v.getContext(), ViewEditContact.class);
                    //get the data from the tag within the button view
                    final ContactMessgaeData theData = (ContactMessgaeData)v.getTag();

                    //pass the user name
                    replyIntent.putExtra("messageUser", theData.getMessage());
                    //go to the tweet screen
                    v.getContext().startActivity(replyIntent);
                    Toast.makeText(context,"showing linked contact",Toast.LENGTH_LONG).show();
                    break;
                case R.id.sa_btn_delete:
                    final ContactMessgaeData deleteData = (ContactMessgaeData) v.getTag();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Delete Reminder!!!.");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        feedDeleteDB.execSQL("delete from remainder where message = '" + deleteData.getMessage() + "'");
                                        feedDeleteDB.close();
                                        Toast.makeText(context, "REMAINDER DELETED ", Toast.LENGTH_LONG).show();
                                       // notifyDataSetChanged();
                                        dialog.cancel();

                                    }catch (Exception exce){Toast.makeText(context,"No such Remainder"+ exce.getMessage(),Toast.LENGTH_LONG).show();}
                                }
                            });
                    builder1.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    break;
            }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        feedDB.close();
    }
}