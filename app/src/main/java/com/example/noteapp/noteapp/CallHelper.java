package com.example.noteapp.noteapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by admin on 2/10/2015.
 */
public class CallHelper  {

    private Context ctx;
    private TelephonyManager tm;
    private CallStateListener callStateListener;
    private FloatingBubbleService floatingBubbleService;
    private OutgoingReceiver outgoingReceiver;
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: // called when someone is ringing to this phone

                    // Toast.makeText(ctx, "Testing!", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
                    Cursor cursor = ctx.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, incomingNumber, null, null );
                    if(cursor.moveToFirst()){
                        incomingNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    }
                    cursor.close();

                    Cursor mCursor = db.query("remainder", new String[] {"name","message"}, "name" + "='" + incomingNumber+"'", null,
                            null, null, null, null);

                    if (mCursor != null && mCursor.moveToFirst()) {
                        String msg = mCursor.getString(mCursor.getColumnIndex("message"));
                        for (int i=0;i<3;i++) {

                            LayoutInflater inflater = LayoutInflater.from(ctx);
                            View layout = inflater.inflate(R.layout.toast_layout,null);

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText(msg+"some1 s calling");

                            Toast toast = new Toast(ctx);
                            toast.setGravity(0, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        }
                    }

                    break;

            }
        }
    }


    private class OutgoingReceiver extends BroadcastReceiver {
        FloatingBubbleService bubble;
        public OutgoingReceiver() {
            bubble = new FloatingBubbleService();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

           Toast.makeText(context, "Outgoing Reciver Calls"+number, Toast.LENGTH_LONG).show();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = ctx.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, number, null, null );
            if(cursor.moveToFirst()){
                number = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            cursor.close();

            Cursor mCursor = db.query("remainder", new String[] {"name","message"}, "name" + "='" + number+"'", null,
                    null, null, null, null);

            if (mCursor != null && mCursor.moveToFirst()) {
                String msg = mCursor.getString(mCursor.getColumnIndex("message"));
                for (int i = 0; i < 2; i++) {
                    Toast.makeText(context, "Coming Toast in foor loop" + i, Toast.LENGTH_LONG).show();
                    LayoutInflater inflater = LayoutInflater.from(ctx);
                    View layout = inflater.inflate(R.layout.toast_layout, null);

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText(msg + "you calling");

                    Toast toast = new Toast(ctx);
                    toast.setGravity(0, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();


                }

                ColorFilter filter = new LightingColorFilter( Color.BLACK, Color.BLUE);
              //  drawable.setColorFilter(colorFilter);

               // ColorStateList myList = new ColorStateList(states, colors);
             // calling the float service with the extra message
       //bubble .chatHead.setColorFilter(filter);






            }
        }

    }

    public CallHelper(Context ctx) {
        this.ctx = ctx;

        callStateListener = new CallStateListener();
        floatingBubbleService = new FloatingBubbleService();
        mDbHelper = new FeedReaderDbHelper(ctx);
        db = mDbHelper.getReadableDatabase();
        outgoingReceiver = new OutgoingReceiver();
        Toast.makeText(ctx,"CallHelper Constructor",Toast.LENGTH_LONG).show();
    }




                public void start () {
                    Toast.makeText(ctx, "Call Helper start", Toast.LENGTH_LONG).show();
                    tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                    tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
                    ctx.registerReceiver(outgoingReceiver, intentFilter);


                }

                /**
                 * Stop calls detection.
                 */
                public void stop () {
                    tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
                    ctx.unregisterReceiver(outgoingReceiver);
                }

            }