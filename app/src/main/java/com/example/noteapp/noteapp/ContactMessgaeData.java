package com.example.noteapp.noteapp;

/**
 * Created by admin on 31/07/2015.
 */
public class ContactMessgaeData {


    /**user screen name of tweeter*/
    private String Message;

    public ContactMessgaeData(String reminder_Message) {
        //instantiate variables

     Message=reminder_Message;
    }

    /**
     * Get the user screen name for the tweet
     * @return tweetUser as a String
     */
    public String getMessage() {return Message;}
}
