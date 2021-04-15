package com.minor.vendorapp.Apis;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.minor.vendorapp.Helpers.Globals;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequest extends Globals {

    public static void callApi(final Context context, final String url, final JSONObject jsonobject, final Callback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (callback != null) {
                    callback.response(response);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) {
                            callback.error(error);
                        }
                        Log.i("Api Request", "onResponse: " + "Called" + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authkey", Globals.authKey);
                return params;

            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 1500;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1500;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

}
