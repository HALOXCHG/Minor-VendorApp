package com.minor.vendorapp.Apis;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface Callback {

     void response(JSONObject resp);

     void error(VolleyError error);
}

