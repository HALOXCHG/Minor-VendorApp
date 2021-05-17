package com.minor.vendorapp.UserAuth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.minor.vendorapp.R;

public class ActivityUserAuth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        if (getSupportFragmentManager().getFragments().isEmpty())
            getSupportFragmentManager().beginTransaction().add(R.id.userAuthFragmentHolder, new FragmentLogin(), "fragmentLogin").commit();

    }
}