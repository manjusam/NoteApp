package com.example.noteapp.noteapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by admin on 2/10/2015.
 */
public class SmsHelper {
    private Context contx;
    FeedReaderDbHelper mDbHelper;
    IncomingSms incomingSms;

    SQLiteDatabase db;

    ContentResolver contentResolver;
    SmsObserver outgoingSMS;
  //  MyContentObserver Observer;

    final Uri SMS_STATUS_URI = Uri.parse("content://sms");
  //  Uri S = Uri.parse("content://sms/sent");


    public class IncomingSms extends BroadcastReceiver {
        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();
        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderNum));
                        Cursor cursor = contx.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, senderNum, null, null);
                        if(cursor.moveToFirst()){
                            senderNum = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        }
                        cursor.close();
                        Cursor mCursor = db.query("remainder", new String[]{"name", "message"}, "name" + "='" + senderNum + "'", null,
                                null, null, null, null);

                        if (mCursor != null && mCursor.moveToFirst()) {
                            String msg = mCursor.getString(mCursor.getColumnIndex("message"));
                            for (int j=0;j<3;j++) {

                                LayoutInflater inflater = LayoutInflater.from(contx);
                                View layout = inflater.inflate(R.layout.toast_layout,null);

                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText(msg+"from incoming message");

                                Toast toast = new Toast(contx);
                                toast.setGravity(0, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        }
                        // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "ooooooooosenderNum: "+ senderNum + ", message: " + message, duration);
                        toast.show();

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);

            }
        }
    }

    public class SmsObserver extends ContentObserver {

        private Context mContext;
        private int initialPos;


        private String smsBodyStr = "", phoneNoStr = "";
        private long smsDatTime = System.currentTimeMillis();


        public SmsObserver(Handler handler,Context ctx) {
            super(handler);
            mContext = ctx;
            initialPos = getLastMsgId();
            Toast.makeText(mContext,"Inside the SMSobsever class contructor",Toast.LENGTH_LONG).show();
        }

        public boolean deliverSelfNotifications() {
            return true;
        }
        public int getLastMsgId() {

            Cursor cur =mContext.getContentResolver().query(SMS_STATUS_URI, null, null, null, null);
            cur.moveToFirst();
            int lastMsgId = cur.getInt(cur.getColumnIndex("_id"));
            Log.i("oooooooo", "Last sent message id: " + String.valueOf(lastMsgId));
            cur.close();
            return lastMsgId;
        }
        public void onChange(boolean selfChange) {

            super.onChange(selfChange);

            try {
                Log.e("Info", "Notification on SMS observer");
                String receiver="";
                Cursor sms_sent_cursor = mContext.getContentResolver().query(SMS_STATUS_URI, null, null, null, null);
                if (sms_sent_cursor != null) {
                    if (sms_sent_cursor.moveToFirst()) {


                            if (initialPos != getLastMsgId()) {
                                // Here you get the last sms. Do what you want.
                               receiver = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address"));
                               // String receiver_name = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex(""));
                                System.out.println(" Receiver Ph no :"+receiver);

                                Toast.makeText(mContext, "CHANGE CHANGE CHANGE CHANGE"+receiver, Toast.LENGTH_LONG).show();
                                // Then, set initialPos to the current position.
                                initialPos = getLastMsgId();
                            }

                        }
                    sms_sent_cursor.close();
                    }

                 Toast.makeText(mContext,"receiver"+ receiver,Toast.LENGTH_LONG).show();
                Cursor mCursor = db.query("remainder", new String[]{"name", "message"}, "number" + "='" + receiver + "'", null,
                        null, null, null, null);

                if (mCursor != null && mCursor.moveToFirst()) {
                    String msg = mCursor.getString(mCursor.getColumnIndex("message"));
                    for (int j=0;j<3;j++) {

                        LayoutInflater inflater = LayoutInflater.from(contx);
                        View layout = inflater.inflate(R.layout.toast_layout,null);

                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText(msg+" "+"from outgoing message");

                        Toast toast = new Toast(contx);
                        toast.setGravity(0, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                    mCursor.close();
                }


                else
                    Log.e("Info","Send Cursor is Empty");
            }
            catch(Exception sggh){
                Log.e("Error", "Error on onChange : "+sggh.toString());
            }

        }//fn onChange

    }//End of class SmsObserver


    public SmsHelper(Context ctx) {
        this.contx = ctx;
         Toast.makeText(contx,"SMS HELPER CONTRUCUTOR",Toast.LENGTH_LONG).show();
        incomingSms = new IncomingSms();
        outgoingSMS =new SmsObserver(new Handler(),contx);
        mDbHelper = new FeedReaderDbHelper(ctx);
        db = mDbHelper.getReadableDatabase();
        // outgoingReceiver = new OutgoingReceiver();
    }

   public void start(){
       System.out.println("SmsHelper start method");
       Toast.makeText(contx,"SMS HELPER START METHOD",Toast.LENGTH_LONG).show();

       contentResolver = contx.getContentResolver();
       contentResolver.registerContentObserver(SMS_STATUS_URI, true, outgoingSMS);

       IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
       contx.registerReceiver(incomingSms, intentFilter);
   }
    public void stop() {

        contx.getContentResolver().
                unregisterContentObserver(outgoingSMS);
        //list incoming sms
       contx.unregisterReceiver(incomingSms);
    }
}
