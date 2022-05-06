package com.minor.vendorapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.Nav.ActivityHomeScreen;
import com.minor.vendorapp.UserAuth.ActivityUserAuth;

public class ActivityRoot extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        Globals.sharedPreferences = getSharedPreferences(Globals.prefName, MODE_PRIVATE);

        if (Globals.sharedPreferences.getBoolean(Globals.isLogin, false)) {
            Intent i = new Intent(getApplicationContext(), ActivityHomeScreen.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(getApplicationContext(), ActivityUserAuth.class);
            startActivity(i);
            finish();
        }
    }
}
