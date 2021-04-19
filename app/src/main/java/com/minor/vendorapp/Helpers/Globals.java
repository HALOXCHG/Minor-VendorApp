package com.minor.vendorapp.Helpers;

import android.content.SharedPreferences;

public class Globals {
    //Default ShopTimings JsonObjects & Arrays
    public static final String shopClosed = "{\"status\":\"Closed\"}";
    public static final String prefName = "pref_name";
    //Error Messages
    public static final String error_unknown = "Something went wrong! Please try again later.";
    public static final String dexter_error = "Dexter Error Occurred";
    public static final String shopOpen24hours = "{\"status\":\"Open 24 hours\"}";
    public static final String[] dayOfWeek = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static SharedPreferences sharedPreferences;

    //Volley Request Headers
    protected static final String authKey = "auth_key";

    //Firebase Token
    protected static final String firebase_token = "firebase_Token";

}
