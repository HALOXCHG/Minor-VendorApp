package com.minor.vendorapp.Signup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.VolleyError;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.DexterError;
import com.minor.vendorapp.Apis.ApiRequest;
import com.minor.vendorapp.Apis.Callback;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.Helpers.HitURL;
import com.minor.vendorapp.Helpers.PermissionCallback;
import com.minor.vendorapp.R;
import com.minor.vendorapp.Signup.Location.FragmentDialogAddressPicker;
import com.minor.vendorapp.Signup.Location.ObjectLocationDetails;
import com.minor.vendorapp.Signup.ShopTimings.FragmentDialogShopTimingsPicker;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivitySignup extends AppCompatActivity implements FragmentDialogAddressPicker.CustomLocationListener, FragmentDialogShopTimingsPicker.CustomTimingsObjectListener {

    EditText shopName, ownerName, contactNumber, emailAddress, vendorAddress, shopType, shopTimings;
    ImageView shopImage;
    Button signup;

    ObjectLocationDetails objectLocationDetails;
    JSONObject[] jsonObject = {null, null, null, null, null, null, null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        shopImage = findViewById(R.id.shopImage);

        shopName = findViewById(R.id.shopName);
        ownerName = findViewById(R.id.ownerName);
        contactNumber = findViewById(R.id.contactNumber);
        emailAddress = findViewById(R.id.emailAddress);
        vendorAddress = findViewById(R.id.vendorAddress);
        shopType = findViewById(R.id.shopType);
        shopTimings = findViewById(R.id.shopTimings);

        signup = findViewById(R.id.signup);

        shopImage.setOnClickListener(view -> triggerImageCapture());
        vendorAddress.setOnClickListener(view -> triggerAddressPicker());
        shopTimings.setOnClickListener(view -> triggerShopTimingsPickerDialog());
        signup.setOnClickListener(view -> signup());

    }

    private void triggerImageCapture() {
        if (ActivityCompat.checkSelfPermission(ActivitySignup.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getBaseContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        imageCapture();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog();
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getApplicationContext(), Globals.dexter_error, Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.CAMERA);
        } else {
            imageCapture();
        }
    }

    //Navigate User to Camera
    private void imageCapture() {
        Intent cameraIntent = new Intent();
        cameraIntent.setAction("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(Intent.createChooser(cameraIntent, "Capture Image"), 102);
        //Override method onActivityResult triggers on successful Image capture
    }

    private void triggerAddressPicker() {
        if (ActivityCompat.checkSelfPermission(ActivitySignup.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivitySignup.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getBaseContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        createAddressPickerDialog();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog();
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getApplicationContext(), Globals.dexter_error, Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            createAddressPickerDialog();
        }
    }

    private void createAddressPickerDialog() {
        FragmentDialogAddressPicker fragmentDialogAddressPicker = new FragmentDialogAddressPicker();
        fragmentDialogAddressPicker.show(getSupportFragmentManager(), "AddressPicker");
    }

    @Override
    public void setAddress(String userAddress, ObjectLocationDetails objectLocationDetails) {
        this.objectLocationDetails = objectLocationDetails;
        vendorAddress.setText(userAddress);
    }

    private void triggerShopTimingsPickerDialog() {
        FragmentDialogShopTimingsPicker fragmentDialogShopTimingsPicker = new FragmentDialogShopTimingsPicker();
        fragmentDialogShopTimingsPicker.show(getSupportFragmentManager(), "TimingsPicker");
    }

    @Override
    public void returnObject(@NonNull JSONObject[] jsonObject) {
        for (int i = 0; i < this.jsonObject.length; i++) {
            if (jsonObject[i] != null)
                this.jsonObject[i] = jsonObject[i];
        }
    }

    public void showDexterCustomSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySignup.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs these permissions to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("Settings", (dialog, which) -> {
            dialog.cancel();
            openAppSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Navigate user to app settings
    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void signup() {
        String inputShopImage, inputShopName, inputOwnerName, inputContactNumber, inputEmailAddress, inputShopType;

        inputShopName = getInputText(shopName); //shopName : String format
        inputOwnerName = getInputText(ownerName); //ownerName : String format
        inputContactNumber = getInputText(contactNumber); //contactNumber : String format
        inputEmailAddress = getInputText(emailAddress); //emailAddress : String format
        inputShopType = getInputText(shopType); //shopName : String format

        JSONObject inputShopTimingsObject = getShopTimingsJsonObject(); //shopTimings : JSON Object
        inputShopImage = Functions.bitmap_to_base64(ActivitySignup.this, ((BitmapDrawable) shopImage.getDrawable()).getBitmap()); //shopImage : String format

//        if (notEmpty(inputShopName) && inputShopName.matches(Regex.validNamesRegex)
//                && notEmpty(inputOwnerName) && inputOwnerName.matches(Regex.validNamesRegex)
//                && notEmpty(inputContactNumber) && inputContactNumber.matches(Regex.validPhoneNumberRegex)
//                && notEmpty(inputEmailAddress) && inputEmailAddress.matches(Regex.validEmailIDRegex)
//                && notEmpty(inputShopType) && notEmpty(inputShopImage)
//                && (objectLocationDetails != null) && (inputShopTimingsObject != null)) {

        //Gives full signup JSON Obj.
        JSONObject signupJsonObject = getSignupJsonObject(inputContactNumber, inputOwnerName, inputEmailAddress, inputShopName, inputShopType, inputShopImage, inputShopTimingsObject);
        //Send API Request
        ApiRequest.callApi(getBaseContext(), HitURL.signup, signupJsonObject, new Callback() {
            @Override
            public void response(JSONObject resp) {
                //Handle Response
                //Intent to HomeScreen
            }

            @Override
            public void error(VolleyError error) {
                Functions.show_volley_errors(error, getApplicationContext());
            }
        });
        Log.i("Signup", "If" + signupJsonObject);

//        } else {
//            Log.i("Signup", "Else");
//            //Handle Regex errors
//        }

    }

    private String getInputText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private Boolean notEmpty(String str) {
        return !(str.isEmpty() || str.equalsIgnoreCase(""));
    }

    private JSONObject getSignupJsonObject(final String contact_no, final String owner_name, final String email, final String shop_name, final String shop_type, final String image, final JSONObject shop_timings) {
        JSONObject fullObject = new JSONObject();
        try {
            fullObject.put("contact_no", contact_no);
            fullObject.put("owner_name", owner_name);
            fullObject.put("email", email);
            fullObject.put("shop_name", shop_name);
            fullObject.put("shop_type", shop_type);
            fullObject.put("image", image);
            fullObject.put("shop_timings", shop_timings);
//            fullObject.put("user_name", "user_name"); //NEEDED
//            fullObject.put("password", "password");   //NEEDED
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addLocationDetails(fullObject); //Adds Vendor Address/Location Details & returns
    }

    private JSONObject addLocationDetails(JSONObject jsonObject) {
        try {
            jsonObject.put("latitude", objectLocationDetails.latitude);
            jsonObject.put("longitude", objectLocationDetails.longitude);
            jsonObject.put("address_line", objectLocationDetails.address_line);
            jsonObject.put("user_given_address", objectLocationDetails.user_given_address);
            jsonObject.put("pincode", objectLocationDetails.pincode);
            jsonObject.put("locality", objectLocationDetails.locality);
            jsonObject.put("state", objectLocationDetails.state);
            jsonObject.put("country", objectLocationDetails.country);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @NonNull
    private JSONObject getShopTimingsJsonObject() {
        JSONObject shopTimingsObj = new JSONObject();
        try {
            for (int i = 0; i < jsonObject.length; i++) {
                if (jsonObject[i] == null)
                    jsonObject[i] = new JSONObject(Globals.shopClosed);
                shopTimingsObj.put(Globals.dayOfWeek[i], jsonObject[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shopTimingsObj;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == -1 && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            Log.i("onActivityResult", "Image:" + image.toString());
            shopImage.setImageBitmap(image);
            Log.i("onActivityResult", "Image Drawable:" + ((BitmapDrawable) shopImage.getDrawable()).getBitmap());
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}