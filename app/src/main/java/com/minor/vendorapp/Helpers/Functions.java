package com.minor.vendorapp.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.minor.vendorapp.PermissionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Functions {

    public static void requestPermissions(Context context, PermissionCallback permissionCallback, String... permissions) {
        Dexter.withContext(context)
                .withPermissions(
                        permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (permissionCallback != null) {
                            permissionCallback.onPermissionsChecked(report);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        if (permissionCallback != null) {
                            permissionCallback.errorListener(error);
                        }
                    }
                })
                .onSameThread()
                .check();
    }

    public static JSONObject signupJsonObject(final String a, final String b) {
        JSONObject object = new JSONObject();
        try {
            object.put("test", a);
            object.put("test2", b);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static void storeSignupData(JSONObject jsonObject) {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();

        editor.putString("avc", jsonObject.optString("avc"));

        editor.apply();
    }

    public static void logout() {
        Log.i("TAG", "Logout user");
    }

    //Redirect to browser
    public static void redirect_to_browser(Context context, String url) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        context.startActivity(intent);
    }

    //Capitalize First Letter Of Every Word In A String
    public static String capitalizeFirstCharacterOfWord(String str) {
        if (!str.isEmpty() && !str.equals(" ")) {
            StringBuilder strbuf = new StringBuilder();
            char ch = ' ';
            for (int i = 0; i < str.length(); i++) {
                if (ch == ' ' && str.charAt(i) != ' ') {
                    strbuf.append(Character.toUpperCase(str.charAt(i)));
                } else {
                    strbuf.append(Character.toLowerCase(str.charAt(i)));
                }
                ch = str.charAt(i);
            }
            return strbuf.toString();
        } else {
            return str;
        }
    }

    public static void show_volley_errors(VolleyError error, final Context context) {
        if (error instanceof NoConnectionError) {
            Toast.makeText(context, "No internet connection detected.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof NetworkError) {
            Toast.makeText(context, "Please check your internet connection & try again.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            Toast.makeText(context, "It seems to be a problem on our side. We're working on fixing it.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError) {
            Toast.makeText(context, "It seems to be a problem on our side. We're working on fixing it.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof TimeoutError) {
            Toast.makeText(context, "Connection timed out. Try again later.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, Globals.error_unknown, Toast.LENGTH_SHORT).show();
        }
    }
}
