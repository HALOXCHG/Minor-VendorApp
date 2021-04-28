package com.minor.vendorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityLogin extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView Phone_number = (TextView)findViewById(R.id.contact_no);
        TextView Password_field = (TextView)findViewById(R.id.Password);
        Button Login = (Button)findViewById(R.id.Submit_button);
        TextView Signup_redirect = (TextView)findViewById(R.id.signup_textview);

        Signup_redirect.setOnClickListener(v -> redirectToSignup());
    }

    private void redirectToSignup() {
        Intent intent = new Intent(getApplicationContext(), ActivityPreSignUp.class);
        startActivity(intent);
    }
}

