package com.minor.vendorapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.minor.vendorapp.Signup.ActivitySignup;

public class ActivityRoot extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
        startActivity(i);
    }
}
