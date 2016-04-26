package com.example.noteapp.noteapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

/**
 * Created by admin on 30/09/2015.
 */
public class ThanksCountDown extends Activity {
    Button button;
    public static Activity thanks_activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown);
       thanks_activity=this;
        button= (Button) findViewById(R.id.start);
        final MyCounter timer = new MyCounter(5000,1000);
        timer.start();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ThanksCountDown.thanks_activity.finish();
                SelectedContacts.selected_activity.finish();
                LoadingContactsActivity.loading_contacts.finish();
            }
        });
    }
    public class MyCounter extends CountDownTimer {

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            System.out.println("Timer Completed.");
            ThanksCountDown.thanks_activity.finish();
            SelectedContacts.selected_activity.finish();
            LoadingContactsActivity.loading_contacts.finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            button.setText((millisUntilFinished/1000)+"");
            System.out.println("Timer  : " + (millisUntilFinished/1000));
        }
    }
}
