package com.minor.vendorapp.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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

    public static void storeSignupData(String shopName, String ownerName, String shopImage, String email, String contactNo) {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();

        editor.putString(Globals.shopName, shopName);
        editor.putString(Globals.ownerName, ownerName);
        editor.putString(Globals.shopImage, shopImage);
        editor.putString(Globals.email, email);
        editor.putString(Globals.contactNo, contactNo);
        editor.putBoolean(Globals.isLogin, true);

        editor.apply();
    }

    public static void storeShopId(String id) {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putString(Globals.shopId, id);
        editor.apply();
    }

    public static void storeShopPosition(int i) {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putInt(Globals.shopTypePosition, i);
        editor.apply();
    }

    public static void storeShopType(String shopType) {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putString(Globals.shopTypeSelected, shopType);
        editor.apply();
    }

    public static void storeShopTimingsObject(JSONObject jsonObject) {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putString(Globals.shopTimingsObject, jsonObject.toString());
        editor.apply();
    }

    public static void storeAddressObject(JSONObject jsonObject) {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putString(Globals.addressObject, jsonObject.toString());
        editor.apply();
    }

    public static String bitmapToBase64(Activity activity, Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }

    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//        byte[] dec = Base64.decode(b64, Base64.DEFAULT);
//        Bitmap decByte = BitmapFactory.decodeByteArray(dec, 0, dec.length);
    }

    //Redirect to browser
    public static void redirectToBrowser(Context context, String url) {
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

    public static String getInputText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static Boolean notEmpty(String str) {
        return !(str.isEmpty() || str.equalsIgnoreCase(""));
    }

    public static void showVolleyErrors(VolleyError error, final Context context) {
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
            Toast.makeText(context, Globals.errorUnknown, Toast.LENGTH_SHORT).show();
        }
    }
}
