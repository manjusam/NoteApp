package com.example.noteapp.noteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by admin on 16/09/2015.
 */
public class RemainderNote extends Activity {
    Button load_contacts,see_all,cancel;
    EditText reminder_Text;
    String reminder_message;
    String remain;
    public static Activity reminder;
    SharedPreferences Note_pref;
  Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remainder_dialog);
        context=this;
        reminder=this;
        Note_pref = getSharedPreferences("NoteAppPrefs", Context.MODE_PRIVATE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = 600;
        params.width = 480;
        params.y = -10;
        this.setFinishOnTouchOutside(false);
        this.getWindow().setAttributes(params);
        reminder_Text = (EditText)findViewById(R.id.reminder_edittext);


        load_contacts= (Button) findViewById(R.id.load_contacts_button);
        see_all= (Button) findViewById(R.id.see_button);
        cancel= (Button) findViewById(R.id.cancel_button);

        see_all.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SeeAllActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder_message = reminder_Text.getText().toString();
                SharedPreferences.Editor edit = Note_pref.edit();
                edit.putString("REMAINDER MESSAGE",reminder_message);
                edit.commit();
                final AlertDialog.Builder builder = new AlertDialog.Builder(RemainderNote.this);
                builder.setTitle("Buddy!!!!");
                builder.setMessage("You wanna close NoteApp");
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //implement your logic for YES
                        RemainderNote.reminder.finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //implement your logic for NO


                    }
                });
                builder.setOnCancelListener(null);
                builder.show();
            }

        });
    }
    public void retrieve(View v)
    {   System.out.print("PPP"+reminder_message+"ooo");
        reminder_message = reminder_Text.getText().toString();
        if(reminder_message!= null && !reminder_message.isEmpty())
        {
            pickContact();

        }
        else
        {
            Toast.makeText(this,"PLS GIVE VALID MESSAGE",Toast.LENGTH_LONG).show();
        }
    }


    private void pickContact() {

        Intent pickContactIntent = new Intent(this,LoadingContactsActivity.class);
        Bundle reminder_bundle= new Bundle();

        reminder_bundle.putString("reminder_message", reminder_message);
        pickContactIntent.putExtras(reminder_bundle);
        //pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivity(pickContactIntent);


    }
    @Override
    public void onBackPressed() {
        reminder_message = reminder_Text.getText().toString();
        SharedPreferences.Editor edit = Note_pref.edit();
        edit.putString("REMAINDER MESSAGE",reminder_message);
        edit.commit();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buddy!!!!");
        builder.setMessage("You wanna close NoteApp");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //implement your logic for YES
                RemainderNote.reminder.finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //implement your logic for NO


            }
        });
        builder.setOnCancelListener(null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //remain = Note_pref.getString("REMAINDER MESSAGE","Edit Message");
        //reminder_Text.setText(remain);

    }
}
