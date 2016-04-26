package com.example.noteapp.noteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 3/09/2015.
 */
public class LoginActivity extends Activity {
    SharedPreferences Note_pref;
    EditText email,password;
    String Get_email,Get_pass;
    public static Activity loginactivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        FloatingActivity.floatactiviy.finish();
        loginactivity=this;
        email = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.password);
        Note_pref = getSharedPreferences("NoteAppPrefs", Context.MODE_PRIVATE);
        Get_email = email.getText().toString();
        Get_pass = password.getText().toString();
        if (Get_email.isEmpty() && Get_pass.isEmpty()) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Login please.");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
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

        }



    }
    public void click_Register(View v)
    {
        Get_email = email.getText().toString();
        if (!isValidEmail(Get_email)) {
            email.setError("Invalid Email");
        }
        else{
            Get_email = email.getText().toString();
            SharedPreferences.Editor edit = Note_pref.edit();
            edit.putString("EMAIL", Get_email);
            Log.e("click",Get_email);
            edit.commit();}

        Get_pass  = password.getText().toString();
        if (!isValidPassword(Get_pass)) {
            password.setError("Invalid Password");
        }
        else{
            Get_pass = password.getText().toString();
            SharedPreferences.Editor edit = Note_pref.edit();
            edit.putString("PASSWORD", Get_pass);
            Log.e("click",Get_pass);
            edit.commit();}

          //  Intent log_float = new Intent(this, FloatingBubbleService.class);
            //startActivity(log_float);
        if(Note_pref.getString("EMAIL","").equals(Get_email)&& Note_pref.getString("PASSWORD","").equals(Get_pass))
        {
            Log.e("check click",Get_email+" "+Get_pass);
             Intent log_float = new Intent(this, FloatingBubbleService.class);
             startService(log_float);

        }
        else{Toast.makeText(this,"Give valid credentials",Toast.LENGTH_LONG ).show();}
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;}
        return false;
    }




    }

