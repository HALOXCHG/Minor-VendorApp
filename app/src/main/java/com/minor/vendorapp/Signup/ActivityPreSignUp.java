package com.minor.vendorapp.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.R;

import static com.minor.vendorapp.Helpers.Regex.validPasswordRegex;
import static com.minor.vendorapp.Helpers.Regex.validPhoneNumberRegex;

public class ActivityPreSignUp extends AppCompatActivity {

    EditText phoneNo, password, confirmPassword;
    ImageView eyeIcon1, eyeIcon2;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presign_up);

        phoneNo = findViewById(R.id.contact_no);
        password = findViewById(R.id.Password);
        confirmPassword = findViewById(R.id.Confirm_password);
        submit = findViewById(R.id.Submit_button);
        eyeIcon1 = findViewById(R.id.imageView);
        eyeIcon2 = findViewById(R.id.imageView2);

        submit.setOnClickListener(view -> preSignUp());      //ann. Function or implementation
        eyeIcon1.setOnClickListener(view -> eye(password, eyeIcon1));
        eyeIcon2.setOnClickListener(view -> eye(confirmPassword, eyeIcon2));
    }

    private void eye(TextView password, ImageView icon) {
        if (password.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            icon.setImageResource(R.drawable.layout_eye_hidden);
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            icon.setImageResource(R.drawable.layout_eye_show);
        }
    }

    private void preSignUp() {
        String inputPhoneNo, inputPassword, inputConfirmPassword;

        inputPhoneNo = Functions.getInputText(phoneNo);
        inputPassword = Functions.getInputText(password);
        inputConfirmPassword = Functions.getInputText(confirmPassword);

        if (Functions.notEmpty(inputPhoneNo) && inputPhoneNo.matches(validPhoneNumberRegex)
                && Functions.notEmpty(inputPassword) && inputPassword.matches(validPasswordRegex)
                && Functions.notEmpty(inputConfirmPassword) && inputConfirmPassword.matches(validPasswordRegex)
                && inputPassword.equals(inputConfirmPassword)) {

            Intent intent = new Intent(getApplicationContext(), ActivitySignup.class);
            intent.putExtra("phoneNo", inputPhoneNo);
            intent.putExtra("password", inputPassword);
            startActivity(intent);

        } else {
            if (Functions.notEmpty(inputPhoneNo)) {
                if (!inputPhoneNo.matches(validPhoneNumberRegex)) {
                    phoneNo.setError("Invalid Input.");
                } else {
                    phoneNo.setError(null);
                }
            } else {
                phoneNo.setError(Globals.fieldRequiredError);
            }

            if (Functions.notEmpty(inputPassword)) {
                if (!inputPassword.matches(validPasswordRegex)) {
                    password.setError("Invalid Password.");
                } else {
                    if (inputPassword.length() < 8) {
                        password.setError("Password too Short.");
                    } else if (inputPassword.length() > 21) {
                        password.setError("Password Max Limit Reached.");
                    } else {
                        password.setError(null);
                    }
                }
            } else {
                password.setError(Globals.fieldRequiredError);
            }

            if (Functions.notEmpty(inputConfirmPassword)) {
                if (!inputConfirmPassword.matches(validPasswordRegex)) {
                    confirmPassword.setError("Invalid Password.");
                } else {
                    if (!inputConfirmPassword.equals(inputPassword)) {
                        confirmPassword.setError("Password Mismatch.");
                    } else {
                        confirmPassword.setError(null);
                    }
                }
            } else {
                confirmPassword.setError(Globals.fieldRequiredError);
            }
        }

    }
}

