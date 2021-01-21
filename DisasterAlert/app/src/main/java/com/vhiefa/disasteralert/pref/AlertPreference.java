package com.vhiefa.disasteralert.pref;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Afifatul on 4/10/2017.
 */

public class AlertPreference {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AlertPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_CURRENT = "current_alert";

    public static final String KEY_HOME = "home_alert";

    public static final String KEY_COORDINAT = "home_coordinat";

    // Constructor
    public AlertPreference(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createAlertPref(String current_alert, String home_alert, String home_coordinat){
        // Storing login value as TRUE
    //    editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_CURRENT, current_alert);

        editor.putString(KEY_HOME, home_alert);

        editor.putString(KEY_COORDINAT, home_coordinat);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
 /*   public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    } */



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getAlertDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_CURRENT, pref.getString(KEY_CURRENT, "false"));
        user.put(KEY_HOME, pref.getString(KEY_HOME, "false"));
        user.put(KEY_COORDINAT, pref.getString(KEY_COORDINAT, "null"));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void clearAlertPref(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
  /*      Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
 /*   public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }*/
}
