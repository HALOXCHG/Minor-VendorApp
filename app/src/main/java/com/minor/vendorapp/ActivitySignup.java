package com.minor.vendorapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.DexterError;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.Helpers.Regex;

public class ActivitySignup extends AppCompatActivity implements FragmentDialogAddressPicker.CustomLocationListener {

    EditText shopName, ownerName, contactNumber, emailAddress, vendorAddress, shopType, shopTimings;
    ImageView shopImage;
    Button signup;

    ObjectLocationDetails objectLocationDetails;

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

        shopImage.setOnClickListener(view -> triggerImageCapture());
        vendorAddress.setOnClickListener(view -> triggerAddressPicker());
        signup.setOnClickListener(view -> signup());

    }

    private void triggerImageCapture() {
        if (ActivityCompat.checkSelfPermission(ActivitySignup.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getBaseContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        ImageCapture();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog();
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getApplicationContext(), Globals.dexter_error, Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.CAMERA);
        } else {
            ImageCapture();
        }
    }

    private void signup() {
        String inputShopImage, inputShopName, inputOwnerName, inputContactNumber, inputEmailAddress, inputVendorAddress, inputShopType, inputShopTimings;

        inputShopImage = Functions.bitmap_to_base64(ActivitySignup.this, ((BitmapDrawable) shopImage.getDrawable()).getBitmap());

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
                && notEmpty(inputShopType) && notEmpty(inputShopTimings) && notEmpty(inputShopImage)
                && objectLocationDetails != null) {
            //Create JSON Obj.

            //Send API Request
            //Handle Response
            //Intent to HomeScreen
            Log.i("Signup", "If");
        } else {
            //Handle Regex errors
            Log.i("Signup", "Else");
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
                    Toast.makeText(getApplicationContext(), Globals.dexter_error, Toast.LENGTH_SHORT).show();
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

    // Navigate user to app settings
    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    //Navigate User to Camera
    private void ImageCapture() {
        Intent cameraIntent = new Intent();
        cameraIntent.setAction("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(Intent.createChooser(cameraIntent, "Capture Image"), 102);
        //Override method onActivityResult triggers on successful Image capture
    }

//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }
//
//    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//        return inSampleSize;
//    }

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
    public void setAddress(String userAddress, ObjectLocationDetails objectLocationDetails) {
        this.objectLocationDetails = objectLocationDetails;
        vendorAddress.setText(userAddress);
    }
}