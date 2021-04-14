package com.minor.vendorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });


    }

    private void signup() {
//        Toast.makeText(ActivitySignup.this, "Signup ", Toast.LENGTH_SHORT).show();
        String inputShopName, inputOwnerName, inputContactNumber, inputEmailAddress, inputVendorAddress, inputShopType, inputShopTimings;

        inputShopName = getInputText(shopName);
        inputOwnerName = getInputText(ownerName);
        inputContactNumber = getInputText(contactNumber);
        inputEmailAddress = getInputText(emailAddress);
        inputVendorAddress = getInputText(vendorAddress);
        inputShopType = getInputText(shopType);
        inputShopTimings = getInputText(shopTimings);

        if(notEmpty(inputShopName) && inputShopName.matches(Regex.validNamesRegex)
            && notEmpty(inputOwnerName) && inputOwnerName.matches(Regex.validNamesRegex)
            && notEmpty(inputContactNumber) && inputContactNumber.matches(Regex.validPhoneNumberRegex)
            && notEmpty(inputEmailAddress) && inputEmailAddress.matches(Regex.validEmailIDRegex)
            && notEmpty(inputVendorAddress) && inputVendorAddress.matches(Regex.validAddressRegex)
            && notEmpty(inputShopType) && notEmpty(inputShopTimings)){
            //Create JSON Obj.
            //Send API Request
            //Handle Response
            //Intent to HomeScreen
        } else {
            //Handle Regex erros
        }

    }

    private String getInputText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private Boolean notEmpty(String str){
        return !(str.isEmpty() || str.equalsIgnoreCase(""));
    }

}