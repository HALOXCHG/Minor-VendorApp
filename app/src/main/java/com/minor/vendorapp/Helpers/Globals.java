package com.minor.vendorapp.Helpers;

import android.content.SharedPreferences;

public class Globals {
    public static final String prefName = "pref_name";
    //Error Messages
    public static final String error_unknown = "Something went wrong! Please try again later.";
    public static final String dexter_error = "Dexter Error Occurred";
    //Volley Request Headers
    protected static final String authKey = "auth_key";

    //Firebase Token
    protected static final String firebase_token = "firebase_Token";
    public static SharedPreferences sharedPreferences;

}
