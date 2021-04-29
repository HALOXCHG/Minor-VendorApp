package com.minor.vendorapp.Nav.Profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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

import static android.content.Context.MODE_PRIVATE;
import static com.minor.vendorapp.Helpers.Functions.getInputText;
import static com.minor.vendorapp.Helpers.Functions.notEmpty;
import static com.minor.vendorapp.Helpers.Functions.storeAddressObject;
import static com.minor.vendorapp.Helpers.Functions.storeShopTimingsObject;
import static com.minor.vendorapp.Helpers.Functions.storeShopType;
import static com.minor.vendorapp.Helpers.Functions.storeSignupData;
import static com.minor.vendorapp.Helpers.Regex.validEmailIDRegex;
import static com.minor.vendorapp.Helpers.Regex.validNamesRegex;
import static com.minor.vendorapp.Helpers.Regex.validPhoneNumberRegex;

public class ProfileFragment extends Fragment implements FragmentDialogAddressPicker.CustomLocationListener, FragmentDialogShopTimingsPicker.CustomTimingsObjectListener {

    EditText profileShopName, profileOwnerName, profileContactNumber, profileEmailAddress, profileVendorAddress, profileShopType, profileShopTimings;
    ImageView profileShopImage;
    Button profileSaveButton;

    ObjectLocationDetails objectLocationDetails = new ObjectLocationDetails();
    JSONObject[] jsonObject = {null, null, null, null, null, null, null};
//    int shopTypeChoice = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Globals.sharedPreferences = getContext().getSharedPreferences(Globals.prefName, MODE_PRIVATE);

        profileShopImage = (ImageView) view.findViewById(R.id.profileShopImage);
        profileShopName = (EditText) view.findViewById(R.id.profileShopName);
        profileOwnerName = (EditText) view.findViewById(R.id.profileOwnerName);
        profileContactNumber = (EditText) view.findViewById(R.id.profileContactNumber);
        profileEmailAddress = (EditText) view.findViewById(R.id.profileEmailAddress);
        profileVendorAddress = (EditText) view.findViewById(R.id.profileVendorAddress);
        profileShopType = (EditText) view.findViewById(R.id.profileShopType);
        profileShopTimings = (EditText) view.findViewById(R.id.profileShopTimings);
        profileSaveButton = (Button) view.findViewById(R.id.profileSaveButton);

        //Set user profile data
        setProfileData();

        profileShopImage.setOnClickListener(view1 -> triggerImageCapture());
//        profileShopType.setOnClickListener(view1 -> getShopType());
        profileVendorAddress.setOnClickListener(view1 -> triggerAddressPicker());
        profileShopTimings.setOnClickListener(view1 -> triggerShopTimingsPickerDialog());
        profileSaveButton.setOnClickListener(view1 -> saveProfileData());

        return view;
    }

    private void saveProfileData() {
        String inputShopImage, inputShopName, inputOwnerName, inputContactNumber, inputEmailAddress, inputShopType, inputPassword;

        inputShopName = getInputText(profileShopName); //shopName : String format
        inputOwnerName = getInputText(profileOwnerName); //ownerName : String format
        inputContactNumber = getInputText(profileContactNumber); //contactNumber : String format
        inputEmailAddress = getInputText(profileEmailAddress); //emailAddress : String format
        inputShopType = getInputText(profileShopType); //shopType : String format


        JSONObject inputShopTimingsObject = getShopTimingsJsonObject(); //shopTimings : JSON Object
        inputShopImage = Functions.bitmapToBase64(getActivity(), ((BitmapDrawable) profileShopImage.getDrawable()).getBitmap()); //shopImage : String format

        if (notEmpty(inputShopName) && inputShopName.matches(validNamesRegex)
                && notEmpty(inputOwnerName) && inputOwnerName.matches(validNamesRegex)
                && notEmpty(inputContactNumber) && inputContactNumber.matches(validPhoneNumberRegex)
                && notEmpty(inputEmailAddress) && inputEmailAddress.matches(validEmailIDRegex)
                && notEmpty(inputShopType) && notEmpty(inputShopImage)
                && (objectLocationDetails != null) && (inputShopTimingsObject != null)) {

            //Gives full signup JSON Obj.
            JSONObject profileUpdateObject = getProfileUpdateObject(inputOwnerName, inputShopName, inputEmailAddress, inputContactNumber, inputShopType, inputShopImage, inputShopTimingsObject);
            Log.i("SignupObj", "" + profileUpdateObject);

            //Send API Request
            ApiRequest.callApi(getContext(), HitURL.updateProfile, profileUpdateObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    String status = resp.optString("status");
                    String message = resp.optString("message");

                    if (status.equalsIgnoreCase("200")) {
//                        storeShopId(message);
                        storeShopType(profileUpdateObject.optString("shopType"));
                        storeShopTimingsObject(inputShopTimingsObject);
                        storeAddressObject(addLocationDetails(new JSONObject()));
                        storeSignupData(profileUpdateObject.optString("shopName"), profileUpdateObject.optString("ownerName"), profileUpdateObject.optString("shopImage"), profileUpdateObject.optString("email"), profileUpdateObject.optString("contactNo"));

                        Toast.makeText(getContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void error(VolleyError error) {
                    Functions.showVolleyErrors(error, getContext());
                }
            });

        } else {
            //=========================================Harshit Dawane===========================================//
            if (notEmpty(inputShopName)) {
                if (!inputShopName.matches(validNamesRegex)) {
                    profileShopName.setError("Invalid name");
                } else {
                    profileShopName.setError(null);
                }
            } else {
                profileShopName.setError("This field is required.");

            }
            if (notEmpty(inputOwnerName)) {
                if (!inputOwnerName.matches(validNamesRegex)) {
                    profileOwnerName.setError("Invalid name.");
                } else {
                    profileOwnerName.setError(null);
                }
            } else {
                profileOwnerName.setError("This field is required.");
            }

            if (notEmpty(inputContactNumber)) {
                if (!inputContactNumber.matches(validPhoneNumberRegex)) {
                    profileContactNumber.setError("Invalid Contact number.");
                } else {
                    profileContactNumber.setError(null);
                }
            } else {
                profileContactNumber.setError("This field is required.");
            }

            if (notEmpty(inputEmailAddress)) {
                if (!inputEmailAddress.matches(validEmailIDRegex)) {
                    profileEmailAddress.setError("Invalid email");
                } else {
                    profileEmailAddress.setError(null);
                }
            } else {
                profileContactNumber.setError("This field is required.");
            }

            if (!notEmpty(String.valueOf(objectLocationDetails))) {
                profileVendorAddress.setError("Required address.");
            } else {
                profileVendorAddress.setError(null);
            }

            if (!notEmpty(inputShopType)) {
                profileShopType.setError("This field is required.");
            } else {
                profileShopType.setError(null);
            }

            if (!isImageUploaded())
                profileShopImage.setImageResource(R.drawable.image_error);

//            if (inputShopTimingsObject == null) {
//                Toast.makeText(getContext(), "Please select the Shop Timings.", Toast.LENGTH_SHORT).show();
//            }
//
//            if (objectLocationDetails == null) {
//                Toast.makeText(getContext(), "Please provide Shop's location/address.", Toast.LENGTH_SHORT).show();
//            }
        }
//        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
//
//        editor.putString(Globals.shopName, profileShopName.getText().toString().trim());
//        editor.putString(Globals.ownerName, profileOwnerName.getText().toString().trim());
//        editor.putString(Globals.contactNo, profileContactNumber.getText().toString().trim());
//        editor.putString(Globals.email, profileEmailAddress.getText().toString().trim());
//        editor.putString(Globals.shopTypeSelected, profileShopType.getText().toString().trim());
//        editor.putString(Globals.shopImage, Functions.bitmapToBase64(getActivity(), ((BitmapDrawable) profileShopImage.getDrawable()).getBitmap()));
//
//        JSONObject inputShopTimingsObject = getShopTimingsJsonObject();
//        editor.putString(Globals.shopTimingsObject, inputShopTimingsObject.toString());
//
//        editor.putString(Globals.addressObject, addLocationDetails(new JSONObject()).toString());
//
//        editor.apply();
//
//        Toast.makeText(getContext(), "Data Saved", Toast.LENGTH_SHORT).show();
    }

    private boolean isImageUploaded() {
        return !((((BitmapDrawable) profileShopImage.getDrawable()).getBitmap()).equals(((BitmapDrawable) ((ImageView) getView().findViewById(R.id.sampleProductImage)).getDrawable()).getBitmap()) || (((BitmapDrawable) profileShopImage.getDrawable()).getBitmap()).equals(((BitmapDrawable) ((ImageView) getView().findViewById(R.id.sampleErrorImage)).getDrawable()).getBitmap()));
    }

    private JSONObject getProfileUpdateObject(final String ownerName, final String shopName, final String email, final String contactNo, final String shopType, final String shopImage, final JSONObject shopTimings) {
        JSONObject fullObject = new JSONObject();
        try {
            fullObject.put("shopId", Globals.sharedPreferences.getString(Globals.shopId, null));
            fullObject.put("ownerName", ownerName);
            fullObject.put("shopName", shopName);
            fullObject.put("email", email);
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

    private void setProfileData() {
        //Set ShopImage
        if (!Globals.sharedPreferences.getString(Globals.shopImage, null).isEmpty())
            profileShopImage.setImageBitmap(Functions.base64ToBitmap(Globals.sharedPreferences.getString(Globals.shopImage, null)));

        //Set ShopName
        if (!Globals.sharedPreferences.getString(Globals.shopName, null).isEmpty())
            profileShopName.setText(Globals.sharedPreferences.getString(Globals.shopName, null));

        //Set OwnerName
        if (!Globals.sharedPreferences.getString(Globals.ownerName, null).isEmpty())
            profileOwnerName.setText(Globals.sharedPreferences.getString(Globals.ownerName, null));

        //Set contactNo
        if (!Globals.sharedPreferences.getString(Globals.contactNo, null).isEmpty())
            profileContactNumber.setText(Globals.sharedPreferences.getString(Globals.contactNo, null));

        //Set email
        if (!Globals.sharedPreferences.getString(Globals.email, null).isEmpty())
            profileEmailAddress.setText(Globals.sharedPreferences.getString(Globals.email, null));

        //Set ShopType
        if (!Globals.sharedPreferences.getString(Globals.shopTypeSelected, null).isEmpty())
            profileShopType.setText(Globals.sharedPreferences.getString(Globals.shopTypeSelected, null));

        profileShopType.setEnabled(false);

        //Set shopTimings
        if (!Globals.sharedPreferences.getString(Globals.shopTimingsObject, null).isEmpty()) {
            try {
                JSONObject currentShopTimings = new JSONObject(Globals.sharedPreferences.getString(Globals.shopTimingsObject, null));
                parseTimings(currentShopTimings);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (JSONObject object : jsonObject) {
                Log.i("Parse", "Obj. " + object);
            }
        }

        //Set address
        if (!Globals.sharedPreferences.getString(Globals.addressObject, null).isEmpty()) {
            try {
                objectLocationDetails = getLocationDetails(new JSONObject(Globals.sharedPreferences.getString(Globals.addressObject, null)));
                profileVendorAddress.setText(objectLocationDetails.userGivenAddress);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.i("Loc", objectLocationDetails.addressLine);
//            Log.i("Loc", objectLocationDetails.city);
//            Log.i("Loc", objectLocationDetails.locality);
//            Log.i("Loc", objectLocationDetails.pincode);
//            Log.i("Loc", objectLocationDetails.state);
//            Log.i("Loc", objectLocationDetails.userGivenAddress);
//            Log.i("Loc", objectLocationDetails.latitude.toString());
//            Log.i("Loc", objectLocationDetails.longitude.toString());
        }


    }

    private void parseTimings(JSONObject currentShopTimings) {
        for (int i = 0; i < Globals.dayOfWeek.length; i++) {
            jsonObject[i] = currentShopTimings.optJSONObject(Globals.dayOfWeek[i]);
        }
    }

    private ObjectLocationDetails getLocationDetails(JSONObject jsonObject) {
        ObjectLocationDetails old = new ObjectLocationDetails();
        try {
            JSONObject location = new JSONObject(jsonObject.optString("location"));
            JSONObject shopAddress = new JSONObject(jsonObject.optString("shopAddress"));

            old.locality = shopAddress.optString("locality");
            old.city = shopAddress.optString("city");
            old.state = shopAddress.optString("state");
            old.pincode = shopAddress.optString("pincode");
            old.addressLine = shopAddress.optString("addressLine");
            old.userGivenAddress = shopAddress.optString("userGivenAddress");
            old.latitude = location.optDouble("latitude");
            old.longitude = location.optDouble("longitude");

//            JSONObject shopAddress, location;
//            shopAddress = new JSONObject();
//            location = new JSONObject();
//
//            shopAddress.put("locality", objectLocationDetails.locality);
//            shopAddress.put("city", objectLocationDetails.city);
//            shopAddress.put("state", objectLocationDetails.state);
//            shopAddress.put("pincode", objectLocationDetails.pincode);
//            shopAddress.put("addressLine", objectLocationDetails.addressLine);
//            shopAddress.put("userGivenAddress", objectLocationDetails.userGivenAddress);
//
//            location.put("latitude", objectLocationDetails.latitude);
//            location.put("longitude", objectLocationDetails.longitude);
//
//            jsonObject.put("shopAddress", shopAddress);
//            jsonObject.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return old;
    }

    private void triggerImageCapture() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        imageCapture();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog(getActivity());
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getContext(), Globals.dexterError, Toast.LENGTH_SHORT).show();
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        createAddressPickerDialog();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog(getActivity());
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getContext(), Globals.dexterError, Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            createAddressPickerDialog();
        }
    }

    private void triggerShopTimingsPickerDialog() {
        FragmentDialogShopTimingsPicker fragmentDialogShopTimingsPicker = new FragmentDialogShopTimingsPicker();
        fragmentDialogShopTimingsPicker.setTargetFragment(ProfileFragment.this, 17);
        fragmentDialogShopTimingsPicker.show(getParentFragmentManager(), "TimingsPicker");
    }

    private void createAddressPickerDialog() {
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", objectLocationDetails.latitude);
        bundle.putDouble("longitude", objectLocationDetails.longitude);
        bundle.putString("userGivenAddress", objectLocationDetails.userGivenAddress);
        FragmentDialogAddressPicker fragmentDialogAddressPicker = new FragmentDialogAddressPicker();
        fragmentDialogAddressPicker.setTargetFragment(ProfileFragment.this, 15);
        fragmentDialogAddressPicker.setArguments(bundle);
        fragmentDialogAddressPicker.show(getParentFragmentManager(), "AddressPicker");
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
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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
    public void setAddress(ObjectLocationDetails objectLocationDetails) {
        this.objectLocationDetails = objectLocationDetails;
        profileVendorAddress.setText(objectLocationDetails.userGivenAddress);
    }

    @Override
    public void returnObject(@NonNull JSONObject[] jsonObject) {
        for (int i = 0; i < this.jsonObject.length; i++) {
            if (jsonObject[i] != null)
                this.jsonObject[i] = jsonObject[i];
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == -1 && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            Log.i("onActivityResult", "Image:" + image.toString());
            profileShopImage.setImageBitmap(image);
            profileShopImage.setPadding(0, 0, 0, 0);
            profileShopImage.setBackground(null);
            Log.i("onActivityResult", "Image Drawable:" + ((BitmapDrawable) profileShopImage.getDrawable()).getBitmap());
        }

    }
}
