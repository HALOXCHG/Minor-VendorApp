package com.minor.vendorapp.UserAuth;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.R;

import static com.minor.vendorapp.Helpers.Regex.validPasswordRegex;
import static com.minor.vendorapp.Helpers.Regex.validPhoneNumberRegex;

public class FragmentSignupUserCredentials extends Fragment {

    EditText phoneNo, password, confirmPassword;
    ImageView eyeIcon1, eyeIcon2;
    Button submit;

    public FragmentSignupUserCredentials() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_user_credentials, container, false);

        phoneNo = view.findViewById(R.id.contact_no);
        password = view.findViewById(R.id.Password);
        confirmPassword = view.findViewById(R.id.Confirm_password);
        submit = view.findViewById(R.id.Submit_button);
        eyeIcon1 = view.findViewById(R.id.imageView);
        eyeIcon2 = view.findViewById(R.id.imageView2);

        submit.setOnClickListener(v -> preSignUp());      //ann. Function or implementation
        eyeIcon1.setOnClickListener(v -> eye(password, eyeIcon1));
        eyeIcon2.setOnClickListener(v -> eye(confirmPassword, eyeIcon2));

        return view;
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

//            Intent intent = new Intent(getContext(), ActivitySignup.class);
//            intent.putExtra("phoneNo", inputPhoneNo);
//            intent.putExtra("password", inputPassword);
//            startActivity(intent);

            FragmentSignupUserDetails fragmentSignupUserDetails = new FragmentSignupUserDetails();
            Bundle bundle = new Bundle();
            bundle.putString("phoneNo", inputPhoneNo);
            bundle.putString("password", inputPassword);
            fragmentSignupUserDetails.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.userAuthFragmentHolder, fragmentSignupUserDetails, "fragmentSignupUserDetails").addToBackStack("fragmentSignupUserDetails").commit();

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