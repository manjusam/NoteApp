package com.example.noteapp.noteapp;

import android.app.Activity;
import android.app.Notification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

 /**
 * Created by admin on 3/09/2015.
 */
    public class FloatingBubbleService extends Service {
    //String current_address;
    private int SERVICE_NOTIFICATION = 1;
    private WindowManager windowManager;
    public ImageView chatHead;
    WindowManager.LayoutParams params;
    Context context;
    @Override

    public void onCreate() {
        super.onCreate();
        context=this;
        if(LoginActivity.loginactivity!=null)
            LoginActivity.loginactivity.finish();
        FloatingActivity.floatactiviy.finish();
        setupNotification();


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.float_button);
        chatHead.setImageAlpha(75);//setAlpha(.0f);
        params= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        //this code is for dragging the chat head
        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
            //touchStartTime = System.currentTimeMillis();
            initialX = params.x;
            initialY = params.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            break;
            case MotionEvent.ACTION_UP:
            if(Math.abs(event.getRawX()-initialTouchX )<=2){

            chatHead.performClick();
            return false;
            }
            break;
            case MotionEvent.ACTION_MOVE:
                params.x = initialX
                        + (int) (event.getRawX() - initialTouchX);
                params.y = initialY
                        + (int) (event.getRawY() - initialTouchY);
            windowManager.updateViewLayout(v, params);
            break;
            }
            return true;

            }

        });


        windowManager.addView(chatHead, params);
        chatHead.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"service",Toast.LENGTH_LONG).show();
                Intent remainder = new Intent(context, RemainderNote.class);
                //remainder.setAction(Intent.ACTION_MAIN);
                //remainder.addCategory(Intent.CATEGORY_LAUNCHER);
                remainder.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(remainder);
            }

        });
    }
    //its always remain in task bar.
    private void setupNotification()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            Notification serviceNotification = new NotificationCompat.Builder(this)
                    .setContentTitle("Your Service")
                    .setContentText("Your Service is running in the background")
                    .setSmallIcon(R.drawable.float_button)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setOngoing(true)
                    .build();
            startForeground(SERVICE_NOTIFICATION, serviceNotification);
        }
        else{
            Notification serviceNotification = new Notification();
            serviceNotification.flags = Notification.FLAG_ONGOING_EVENT;
            startForeground(SERVICE_NOTIFICATION, serviceNotification);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void clearNotification()
    {
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearNotification();
    }
}
