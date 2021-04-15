package com.minor.vendorapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.DexterError;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Regex;

public class ActivitySignup extends AppCompatActivity {

    EditText shopName, ownerName, contactNumber, emailAddress, vendorAddress, shopType, shopTimings;
    ImageView shopImage;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        shopImage = (ImageView) findViewById(R.id.shopImage);

        shopName = (EditText) findViewById(R.id.shopName);
        ownerName = (EditText) findViewById(R.id.ownerName);
        contactNumber = (EditText) findViewById(R.id.contactNumber);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        vendorAddress = (EditText) findViewById(R.id.vendorAddress);
        shopType = (EditText) findViewById(R.id.shopType);
        shopTimings = (EditText) findViewById(R.id.shopTimings);

        signup = (Button) findViewById(R.id.signup);

        signup.setOnClickListener(view -> signup());
        vendorAddress.setOnClickListener(view -> triggerAddressPicker());

    }

    private void signup() {
        String inputShopName, inputOwnerName, inputContactNumber, inputEmailAddress, inputVendorAddress, inputShopType, inputShopTimings;

        inputShopName = getInputText(shopName);
        inputOwnerName = getInputText(ownerName);
        inputContactNumber = getInputText(contactNumber);
        inputEmailAddress = getInputText(emailAddress);
        inputVendorAddress = getInputText(vendorAddress);
        inputShopType = getInputText(shopType);
        inputShopTimings = getInputText(shopTimings);

        if (notEmpty(inputShopName) && inputShopName.matches(Regex.validNamesRegex)
                && notEmpty(inputOwnerName) && inputOwnerName.matches(Regex.validNamesRegex)
                && notEmpty(inputContactNumber) && inputContactNumber.matches(Regex.validPhoneNumberRegex)
                && notEmpty(inputEmailAddress) && inputEmailAddress.matches(Regex.validEmailIDRegex)
                && notEmpty(inputVendorAddress) && inputVendorAddress.matches(Regex.validAddressRegex)
                && notEmpty(inputShopType) && notEmpty(inputShopTimings)) {
            //Create JSON Obj.
            //Send API Request
            //Handle Response
            //Intent to HomeScreen
        } else {
            //Handle Regex errors
        }

    }

    private String getInputText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private Boolean notEmpty(String str) {
        return !(str.isEmpty() || str.equalsIgnoreCase(""));
    }

    private void triggerAddressPicker() {
        if (ActivityCompat.checkSelfPermission(ActivitySignup.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivitySignup.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getBaseContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        createDialog();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog();
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getApplicationContext(), "Dexter Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            createDialog();
        }
    }

    private void createDialog() {
        FragmentDialogAddressPicker fragmentDialogAddressPicker = new FragmentDialogAddressPicker();
        fragmentDialogAddressPicker.show(getSupportFragmentManager(), "AddressPicker");
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

    // navigating user to app settings
    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}