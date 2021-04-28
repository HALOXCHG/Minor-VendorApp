package com.minor.vendorapp.Signup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.DexterError;
import com.minor.vendorapp.ActivityHomeScreen;
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

import static com.minor.vendorapp.Helpers.Functions.getInputText;
import static com.minor.vendorapp.Helpers.Functions.notEmpty;
import static com.minor.vendorapp.Helpers.Globals.shopTypeList;
import static com.minor.vendorapp.Helpers.Regex.validEmailIDRegex;
import static com.minor.vendorapp.Helpers.Regex.validNamesRegex;
import static com.minor.vendorapp.Helpers.Regex.validPhoneNumberRegex;

public class ActivitySignup extends AppCompatActivity implements FragmentDialogAddressPicker.CustomLocationListener, FragmentDialogShopTimingsPicker.CustomTimingsObjectListener {

    EditText shopName, ownerName, contactNumber, emailAddress, vendorAddress, shopType, shopTimings;
    ImageView shopImage;
    Button signup;

    ObjectLocationDetails objectLocationDetails = new ObjectLocationDetails();
    JSONObject[] jsonObject = {null, null, null, null, null, null, null};
    int shopTypeChoice = -1;

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
        shopType.setOnClickListener(view -> getShopType());
        vendorAddress.setOnClickListener(view -> triggerAddressPicker());
        shopTimings.setOnClickListener(view -> triggerShopTimingsPickerDialog());
        signup.setOnClickListener(view -> signup());
        findViewById(R.id.tempHome).setOnClickListener(view -> startActivity(new Intent(ActivitySignup.this, ActivityHomeScreen.class)));

    }

    private void getShopType() {
        new MaterialAlertDialogBuilder(ActivitySignup.this)
                .setSingleChoiceItems(shopTypeList, shopTypeChoice, (dialogInterface, i) -> {
                    shopType.setText(shopTypeList[i]);
                    shopTypeChoice = i;

                    SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                    editor.putInt(Globals.shopTypeSelected, i);
                    editor.apply();

                    dialogInterface.dismiss();
                })
                // Single-choice items (initialized with checked item)
                .show();
    }

    private void triggerImageCapture() {
        if (ActivityCompat.checkSelfPermission(ActivitySignup.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getBaseContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        imageCapture();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog(ActivitySignup.this);
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getApplicationContext(), Globals.dexterError, Toast.LENGTH_SHORT).show();
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
                        showDexterCustomSettingsDialog(ActivitySignup.this);
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getApplicationContext(), Globals.dexterError, Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            createAddressPickerDialog();
        }
    }

    private void createAddressPickerDialog() {
        FragmentDialogAddressPicker fragmentDialogAddressPicker = new FragmentDialogAddressPicker();
//        FragmentDialogTest fragmentDialogTest = new FragmentDialogTest();
        fragmentDialogAddressPicker.show(getSupportFragmentManager(), "AddressPicker");
    }

    @Override
    public void setAddress(ObjectLocationDetails objectLocationDetails) {
        this.objectLocationDetails = objectLocationDetails;
        vendorAddress.setText(objectLocationDetails.userGivenAddress);
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

    public void showDexterCustomSettingsDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        inputShopType = getInputText(shopType); //shopType : String format


        JSONObject inputShopTimingsObject = getShopTimingsJsonObject(); //shopTimings : JSON Object
        inputShopImage = Functions.bitmapToBase64(ActivitySignup.this, ((BitmapDrawable) shopImage.getDrawable()).getBitmap()); //shopImage : String format

        if (notEmpty(inputShopName) && inputShopName.matches(validNamesRegex)
                && notEmpty(inputOwnerName) && inputOwnerName.matches(validNamesRegex)
                && notEmpty(inputContactNumber) && inputContactNumber.matches(validPhoneNumberRegex)
                && notEmpty(inputEmailAddress) && inputEmailAddress.matches(validEmailIDRegex)
                && notEmpty(inputShopType) && notEmpty(inputShopImage)
                && (objectLocationDetails != null) && (inputShopTimingsObject != null)) {

            //Gives full signup JSON Obj.
            JSONObject signupJsonObject = getSignupJsonObject(inputOwnerName, inputShopName, inputEmailAddress, "password", inputContactNumber, inputShopType, inputShopImage, inputShopTimingsObject);
            Log.i("SignupObj", "" + signupJsonObject);
            //Send API Request
            ApiRequest.callApi(getBaseContext(), HitURL.signup, signupJsonObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    String status = resp.optString("status");
                    String message = resp.optString("message");

                    if (status.equalsIgnoreCase("200")) {
                        //Store shopType array pos. to shared pref
                        //Store data to shared prefs
                        //Intent to home
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void error(VolleyError error) {
                    Functions.showVolleyErrors(error, getApplicationContext());
                }
            });

        } else {
            //=========================================Harshit Dawane===========================================//
            if (notEmpty(inputShopName)) {
                if (!inputShopName.matches(validNamesRegex)) {
                    shopName.setError("Invalid name");
                } else {
                    shopName.setError(null);
                }
            } else {
                shopName.setError("This field is required.");

            }
            if (notEmpty(inputOwnerName)) {
                if (!inputOwnerName.matches(validNamesRegex)) {
                    ownerName.setError("Invalid name.");
                } else {
                    ownerName.setError(null);
                }
            } else {
                ownerName.setError("This field is required.");
            }

            if (notEmpty(inputContactNumber)) {
                if (!inputContactNumber.matches(validPhoneNumberRegex)) {
                    contactNumber.setError("Invalid Contact number.");
                } else {
                    contactNumber.setError(null);
                }
            } else {
                contactNumber.setError("This field is required.");
            }

//            if (notEmpty(inputEmailAddress)) {
//                if (!inputEmailAddress.matches(validEmailIDRegex)) {
//                    emailAddress.setError("Invalid email");
//                } else {
//                    emailAddress.setError(null);
//                }
//            }

            if (notEmpty(String.valueOf(objectLocationDetails))) {
                vendorAddress.setError("Required address.");
            } else {
                vendorAddress.setError(null);
            }

            if (notEmpty(inputShopType)) {
                shopType.setError("This field is required.");
            } else {
                shopType.setError(null);
            }

            if (isImageUploaded()) {
                Log.i("Image", "Eq");
            } else {
                Log.i("Image", "Not Eq");
            }

//            if(inputShopTimingsObject == null){
//                Toast.makeText(getApplicationContext(),"please select the shop timing",Toast.LENGTH_SHORT).show();
//            }
//            if(objectLocationDetails == null){
//                Toast.makeText(getApplicationContext(),"please provide shop's location/address",Toast.LENGTH_SHORT).show();
//            }
        }

    }

    private boolean isImageUploaded() {
        return ((((BitmapDrawable) shopImage.getDrawable()).getBitmap()).equals(((BitmapDrawable) ((ImageView) findViewById(R.id.sampleShopImage)).getDrawable()).getBitmap()));
    }

    private JSONObject getSignupJsonObject(final String ownerName, final String shopName, final String email, final String password, final String contactNo, final String shopType, final String shopImage, final JSONObject shopTimings) {
        JSONObject fullObject = new JSONObject();
        try {
            fullObject.put("ownerName", ownerName);
            fullObject.put("shopName", shopName);
            fullObject.put("email", email);
            fullObject.put("password", "Asdfghjkkk");
            fullObject.put("contactNo", contactNo);
            fullObject.put("shopType", shopType);
            fullObject.put("shopTimings", shopTimings);
            fullObject.put("shopImage", shopImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addLocationDetails(fullObject); //Adds Vendor Address/Location Details & returns
    }

    private JSONObject addLocationDetails(JSONObject jsonObject) {
        try {
            JSONObject shopAddress, location;
            shopAddress = new JSONObject();
            location = new JSONObject();

            shopAddress.put("locality", objectLocationDetails.locality);
            shopAddress.put("city", objectLocationDetails.city);
            shopAddress.put("state", objectLocationDetails.state);
            shopAddress.put("pincode", objectLocationDetails.pincode);
            shopAddress.put("addressLine", objectLocationDetails.addressLine);
            shopAddress.put("userGivenAddress", objectLocationDetails.userGivenAddress);

            location.put("latitude", objectLocationDetails.latitude);
            location.put("longitude", objectLocationDetails.longitude);

            jsonObject.put("shopAddress", shopAddress);
            jsonObject.put("location", location);
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
