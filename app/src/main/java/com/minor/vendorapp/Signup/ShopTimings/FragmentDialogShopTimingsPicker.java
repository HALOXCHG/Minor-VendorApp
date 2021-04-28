package com.minor.vendorapp.Signup.ShopTimings;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class FragmentDialogShopTimingsPicker extends DialogFragment {

    CustomTimingsObjectListener customTimingsObjectListener;
    Dialog dialog;

    ToggleButton toggle0, toggle1, toggle2, toggle3, toggle4, toggle5, toggle6;
    CheckBox checkOpened, checkClosed;
    LinearLayout timePickerFields;
    TextInputEditText openedTime, closedTime;
    Button cancel, save;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //Passing User Location Data back to Signup Activity via Interface
        try {
            customTimingsObjectListener = (FragmentDialogShopTimingsPicker.CustomTimingsObjectListener) getActivity();
        } catch (ClassCastException e) {
            Log.i("TAG", "onAttach: ClassCastException " + e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_shop_timings_picker_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toggle0 = view.findViewById(R.id.toggle_0);
        toggle1 = view.findViewById(R.id.toggle_1);
        toggle2 = view.findViewById(R.id.toggle_2);
        toggle3 = view.findViewById(R.id.toggle_3);
        toggle4 = view.findViewById(R.id.toggle_4);
        toggle5 = view.findViewById(R.id.toggle_5);
        toggle6 = view.findViewById(R.id.toggle_6);

        checkOpened = view.findViewById(R.id.checkOpened);
        checkClosed = view.findViewById(R.id.checkClosed);

        timePickerFields = view.findViewById(R.id.timePickerFields);

        openedTime = view.findViewById(R.id.openedTimeTextField);
        closedTime = view.findViewById(R.id.closedTimeTextField);

        cancel = view.findViewById(R.id.cancelButton);
        save = view.findViewById(R.id.saveButton);

        toggle0.setOnClickListener(view1 -> toggleHandler());
        toggle1.setOnClickListener(view1 -> toggleHandler());
        toggle2.setOnClickListener(view1 -> toggleHandler());
        toggle3.setOnClickListener(view1 -> toggleHandler());
        toggle4.setOnClickListener(view1 -> toggleHandler());
        toggle5.setOnClickListener(view1 -> toggleHandler());
        toggle6.setOnClickListener(view1 -> toggleHandler());

        checkOpened.setOnClickListener(view1 -> checkHandler(checkOpened));
        checkClosed.setOnClickListener(view1 -> checkHandler(checkClosed));

        openedTime.setOnClickListener(view1 -> getTime(openedTime));
        closedTime.setOnClickListener(view1 -> getTime(closedTime));

        save.setOnClickListener(view1 -> saveObject());
        cancel.setOnClickListener(view1 -> dialog.dismiss());
    }

    private void saveObject() {
        if (saveValidations()) {
            Log.i("Save", "Pass" + Arrays.toString(getMainJsonObject()));
            customTimingsObjectListener.returnObject(getMainJsonObject());
        }
        dialog.dismiss();
    }

    private boolean saveValidations() {
        return checkOpened.isChecked() || checkClosed.isChecked() || (!Objects.requireNonNull(openedTime.getText()).toString().isEmpty() && !Objects.requireNonNull(closedTime.getText()).toString().isEmpty());
    }

    private JSONObject[] getMainJsonObject() {
        Boolean[] b = {toggle0.isChecked(), toggle1.isChecked(), toggle2.isChecked(), toggle3.isChecked(), toggle4.isChecked(), toggle5.isChecked(), toggle6.isChecked()};
        JSONObject[] obj = new JSONObject[7];
        JSONObject jsonObject = getPrimaryJsonObject();
        for (int i = 0; i < b.length; i++) {
            obj[i] = b[i] ? jsonObject : null;
        }
        Log.i("Save", "Obj: " + Arrays.toString(obj));
        return obj;
    }

    private JSONObject getPrimaryJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (checkOpened.isChecked()) {
                jsonObject = new JSONObject(Globals.shopOpen24hours);
            } else if (checkClosed.isChecked()) {
                jsonObject = new JSONObject(Globals.shopClosed);
            } else {
                jsonObject.put("status", "Open");
                jsonObject.put("shopOpeningTime", Objects.requireNonNull(openedTime.getText()).toString());
                jsonObject.put("shopClosingTime", Objects.requireNonNull(closedTime.getText()).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void checkHandler(CheckBox check) {
        timePickerFields.setVisibility(check.isChecked() ? View.GONE : View.VISIBLE);
        (check.getId() == checkOpened.getId() ? checkClosed : checkOpened).setChecked(false);
    }

    private void toggleHandler() {
        boolean b = toggle0.isChecked() || toggle1.isChecked() || toggle2.isChecked() || toggle3.isChecked() || toggle4.isChecked() || toggle5.isChecked() || toggle6.isChecked();
        save.setEnabled(b);
    }

    private void getTime(TextInputEditText textInputEditText) {
        TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(), (timePicker, selectedHour, selectedMinute) -> {
//            if (selectedHour > 12) {
//                selectedHour -= 12;
//                timeSet = "PM";
//            } else if (selectedHour == 0) {
//                selectedHour += 12;
//                timeSet = "AM";
//            } else if (selectedHour == 12) {
//                timeSet = "PM";
//            } else {
//                timeSet = "AM";
//            }
            selectedHour = (selectedHour > 12) ? selectedHour - 12 : ((selectedHour == 0) ? selectedHour + 12 : selectedHour);
            String minute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
            String timeSet = (selectedHour > 12 || selectedHour == 12) ? "PM" : "AM";
            textInputEditText.setText(String.format("%s:%s %s", selectedHour, minute, timeSet));

        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public interface CustomTimingsObjectListener {
        void returnObject(@NonNull JSONObject[] jsonObject);
    }
}

