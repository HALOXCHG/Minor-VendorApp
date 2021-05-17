package com.minor.vendorapp.UserAuth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.minor.vendorapp.Apis.ApiRequest;
import com.minor.vendorapp.Apis.Callback;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.Helpers.HitURL;
import com.minor.vendorapp.Nav.ActivityHomeScreen;
import com.minor.vendorapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.minor.vendorapp.Helpers.Functions.getInputText;
import static com.minor.vendorapp.Helpers.Functions.notEmpty;
import static com.minor.vendorapp.Helpers.Functions.storeAddressObject;
import static com.minor.vendorapp.Helpers.Functions.storeShopId;
import static com.minor.vendorapp.Helpers.Functions.storeShopPosition;
import static com.minor.vendorapp.Helpers.Functions.storeShopTimingsObject;
import static com.minor.vendorapp.Helpers.Functions.storeShopType;
import static com.minor.vendorapp.Helpers.Functions.storeSignupData;
import static com.minor.vendorapp.Helpers.Regex.validEmailIDRegex;
import static com.minor.vendorapp.Helpers.Regex.validPasswordRegex;
import static com.minor.vendorapp.Helpers.Regex.validPhoneNumberRegex;

public class FragmentLogin extends Fragment {

    TextView signupTextview;
    EditText loginParameter, loginPassword;
    Button loginButton;

    public FragmentLogin() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Globals.sharedPreferences = this.getActivity().getSharedPreferences(Globals.prefName, MODE_PRIVATE);

        loginParameter = (EditText) view.findViewById(R.id.loginParameter);
        loginPassword = (EditText) view.findViewById(R.id.loginPassword);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        signupTextview = (TextView) view.findViewById(R.id.signupTextview);

        loginButton.setOnClickListener(v -> login());
        signupTextview.setOnClickListener(v -> redirectToSignup());

        return view;
    }

    private void login() {
        String inputLoginParameter, inputLoginPassword;
        inputLoginParameter = getInputText(loginParameter);
        inputLoginPassword = getInputText(loginPassword);

        if (notEmpty(inputLoginParameter) && (inputLoginParameter.matches(validPhoneNumberRegex) || inputLoginParameter.matches(validEmailIDRegex)) &&
                notEmpty(inputLoginPassword) && inputLoginPassword.matches(validPasswordRegex)) {

            JSONObject jsonObject = getLoginJsonObject(inputLoginParameter, inputLoginPassword);

            ApiRequest.callApi(getContext(), HitURL.login, jsonObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    String status = resp.optString("status");
                    String message = resp.optString("message");

                    if (status.equalsIgnoreCase("200")) {

                        try {
                            JSONObject data = new JSONObject(resp.optJSONObject("data").toString());
                            Log.i("Resp", "" + resp);

                            storeShopId(data.optString("shopId"));
                            storeShopType(data.optString("shopType"));
                            storeShopPosition(getShopPosition(data.optString("shopType")));
                            storeShopTimingsObject(getShopTimingDetails(data));
                            storeAddressObject(getLocationDetails(data));
                            Log.i("Location", "" + getLocationDetails(data));
                            storeSignupData(data.optString("shopName"), data.optString("ownerName"), data.optString("shopImage"), data.optString("email"), data.optString("contactNo"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setIsLogin();
                        Intent intent = new Intent(getContext(), ActivityHomeScreen.class);
                        startActivity(intent);
                        getActivity().finish();
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
            if (notEmpty(inputLoginParameter)) {
                if (inputLoginParameter.matches(validPhoneNumberRegex) || inputLoginParameter.matches(validEmailIDRegex)) {
                    loginParameter.setError(null);
                } else {
                    loginParameter.setError("Invalid Contact No./Email");
                }
            } else {
                loginParameter.setError(Globals.fieldRequiredError);
            }

            if (notEmpty(inputLoginPassword)) {
                if (inputLoginParameter.matches(validPasswordRegex)) {
                    loginPassword.setError(null);
                } else {
                    loginPassword.setError("Invalid password");
                }
            } else {
                loginPassword.setError(Globals.fieldRequiredError);
            }
        }
    }

    private int getShopPosition(String shopType) {
        for (int i = 0; i < Globals.shopTypeList.length; i++)
            if (shopType.equals(Globals.shopTypeList[i]))
                return i;
        return 1;
    }

    private JSONObject getShopTimingDetails(JSONObject data) {
        try {
            return new JSONObject(data.optJSONObject("shopTimings").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject getLocationDetails(JSONObject jsonObject) {
        JSONObject returnObj = new JSONObject();
        try {
            JSONObject j = jsonObject.optJSONObject("location");
            JSONObject lat = j.optJSONObject("latitude");
            JSONObject lon = j.optJSONObject("longitude");
            JSONObject obj = new JSONObject();
            obj.put("latitude", lat.optString("$numberDecimal"));
            obj.put("longitude", lon.optString("$numberDecimal"));
            returnObj.put("location", obj);
            returnObj.put("shopAddress", jsonObject.optJSONObject("shopAddress"));
//            old.locality = shopAddress.optString("locality");
//            old.city = shopAddress.optString("city");
//            old.state = shopAddress.optString("state");
//            old.pincode = shopAddress.optString("pincode");
//            old.addressLine = shopAddress.optString("addressLine");
//            old.userGivenAddress = shopAddress.optString("userGivenAddress");
//            old.latitude = location.optDouble("latitude");
//            old.longitude = location.optDouble("longitude");

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
        return returnObj;
    }

    private void setIsLogin() {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putBoolean(Globals.isLogin, true);
        editor.apply();
    }

    private JSONObject getLoginJsonObject(String parameter, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("parameter", parameter);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void redirectToSignup() {
//        Intent intent = new Intent(getContext(), ActivityPreSignUp.class);
//        startActivity(intent);
        FragmentSignupUserCredentials fragmentSignupUserCredentials = new FragmentSignupUserCredentials();
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.userAuthFragmentHolder, fragmentSignupUserCredentials, "fragmentSignupUserCredentials").addToBackStack("fragmentSignupUserCredentials").commit();
    }
}