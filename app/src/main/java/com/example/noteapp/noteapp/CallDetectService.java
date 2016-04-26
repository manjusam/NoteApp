package com.example.noteapp.noteapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by manju on 05-Apr-2015.
 */
public class CallDetectService extends Service {

    private CallHelper callHelper;
   private SmsHelper smsHelper;
   // private FloatingBubbleService  floatingBubbleService;

    public CallDetectService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("......onStartCommand/////......");
        Toast.makeText(this,"Onstart Command",Toast.LENGTH_LONG).show();
        callHelper = new CallHelper(this);
       smsHelper  = new SmsHelper(this);
        int res = super.onStartCommand(intent, flags, startId);
        callHelper.start();
        smsHelper.start();
       // smsHelper.startoutgoing();
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        callHelper.stop();
      smsHelper.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
