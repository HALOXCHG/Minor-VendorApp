package com.minor.vendorapp;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.VolleyError;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.DexterError;
import com.minor.vendorapp.Apis.ApiRequest;
import com.minor.vendorapp.Apis.Callback;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.Helpers.HitURL;
import com.minor.vendorapp.Helpers.PermissionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static com.minor.vendorapp.Helpers.Functions.bitmapToBase64;
import static com.minor.vendorapp.Helpers.Functions.getInputText;
import static com.minor.vendorapp.Helpers.Functions.notEmpty;
import static com.minor.vendorapp.Helpers.Regex.validDigitsOnlyRegex;
import static com.minor.vendorapp.Helpers.Regex.validNamesRegex;

public class ActivityProductAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText productName, mrp, sellingPrice, quantity, availableStock, productDescription;
    Spinner productType, units;
    ImageView productImage;
    Button addProduct;

    String typeOfProduct, unit;
    ArrayAdapter<String> productTypeAdapter, unitsAdapter;
    Boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        b = getIntent().hasExtra("Object");

        Intent intent = getIntent();
        Globals.sharedPreferences = this.getSharedPreferences(Globals.prefName, MODE_PRIVATE);

        productName = (EditText) findViewById(R.id.productName);
        productType = (Spinner) findViewById(R.id.productType);
        mrp = (EditText) findViewById(R.id.mrp);
        sellingPrice = (EditText) findViewById(R.id.sellingPrice);
        quantity = (EditText) findViewById(R.id.quantity);
        units = (Spinner) findViewById(R.id.units);
        availableStock = (EditText) findViewById(R.id.availableStock);
        productDescription = (EditText) findViewById(R.id.productDescription);

        productImage = (ImageView) findViewById(R.id.productImage);

        addProduct = (Button) findViewById(R.id.addProduct);

        setProductTypeSpinner();
        setUnitsSpinner();
        productImage.setOnClickListener(view -> triggerImageCapture());
        addProduct.setOnClickListener(view -> addProduct());

        if (intent.hasExtra("Object")) {
            TextView t = (TextView) findViewById(R.id.addProductHeading);
            t.setText("Edit Product");
            try {
                setProductInfo(new JSONObject(intent.getStringExtra("Object")));

//                Toast.makeText(getApplicationContext(), "Obj. " + new JSONObject(getIntent().getStringExtra("Object")), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void setProductInfo(JSONObject jObj) {

        JSONObject object = jObj.optJSONObject("price");

        productName.setText(jObj.optString("itemName"));
        availableStock.setText(jObj.optString("itemInStock"));
        productDescription.setText(jObj.optString("itemDescription"));
        quantity.setText(jObj.optString("itemInStock"));
        sellingPrice.setText(object.optString("sellingPrice"));
        mrp.setText(object.optString("MRP"));

        productType.setSelection(productTypeAdapter.getPosition(jObj.optString("itemType")));
        units.setSelection(unitsAdapter.getPosition(jObj.optString("itemUnit")));

        String image = jObj.optString("itemImage");

        //If user CANNOT edit these
//        productName.setEnabled(false);
//        productType.setEnabled(false);
    }

    private void triggerImageCapture() {
        if (ActivityCompat.checkSelfPermission(ActivityProductAdd.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getBaseContext(), new PermissionCallback() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                        imageCapture();
                    if (report.isAnyPermissionPermanentlyDenied())
                        showDexterCustomSettingsDialog(ActivityProductAdd.this);
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getApplicationContext(), Globals.dexterError, Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.CAMERA);
        } else {
            imageCapture();
        }
    }

    private void imageCapture() {
        Intent cameraIntent = new Intent();
        cameraIntent.setAction("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(Intent.createChooser(cameraIntent, "Capture Image"), 102);
        //Override method onActivityResult triggers on successful Image capture
    }

    private void addProduct() {
        String inputProductName, inputProductType, inputShopImage, inputMrp, inputSellingPrice, inputQuantity, inputUnits, inputAvailableStock, inputProductDescription;

        inputProductName = getInputText(productName);
        inputProductType = typeOfProduct;
        inputMrp = getInputText(mrp);
        inputSellingPrice = getInputText(sellingPrice);
        inputQuantity = getInputText(quantity);
        inputUnits = unit;
        inputAvailableStock = getInputText(availableStock);
        inputProductDescription = getInputText(productDescription);

        inputShopImage = isImageUploaded() ? bitmapToBase64(ActivityProductAdd.this, ((BitmapDrawable) productImage.getDrawable()).getBitmap()) : null;

        if (notEmpty(inputProductName) && inputProductName.matches(validNamesRegex) &&
                notEmpty(inputProductType) && notEmpty(inputMrp) && inputMrp.matches(validDigitsOnlyRegex) &&
                notEmpty(inputSellingPrice) && inputSellingPrice.matches(validDigitsOnlyRegex) &&
                notEmpty(inputQuantity) && inputQuantity.matches(validDigitsOnlyRegex) &&
                notEmpty(inputAvailableStock) && inputAvailableStock.matches(validDigitsOnlyRegex) &&
                inputShopImage != null && inputUnits != null && inputProductType != null) {

            JSONObject jsonObject = getProductObject(inputProductName, inputAvailableStock, inputMrp, inputSellingPrice, inputShopImage, inputProductType, inputUnits, inputQuantity, inputProductDescription);
            Log.i("AddProduct", "If Passed " + jsonObject);
            finish();

            ApiRequest.callApi(getApplicationContext(), HitURL.addProduct, jsonObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    //Finish Activity
                }

                @Override
                public void error(VolleyError error) {
                    Functions.showVolleyErrors(error, getApplicationContext());
                }
            });
        } else {
            if (!isImageUploaded())
                productImage.setImageResource(R.drawable.image_error);

            productName.setError(notEmpty(inputProductName) ? inputProductName.matches(validNamesRegex) ? null : "Invalid Product Name" : Globals.fieldRequiredError);

            mrp.setError(notEmpty(inputMrp) ? inputMrp.matches(validDigitsOnlyRegex) ? null : "Invalid MRP" : Globals.fieldRequiredError);

            sellingPrice.setError(notEmpty(inputSellingPrice) ? inputSellingPrice.matches(validDigitsOnlyRegex) ? (notEmpty(inputMrp) && inputMrp.matches(validDigitsOnlyRegex) && Integer.parseInt(inputSellingPrice) > Integer.parseInt(inputMrp)) ? "Price cannot be greater than MRP" : null : "Invalid Price" : Globals.fieldRequiredError);

            quantity.setError(notEmpty(inputQuantity) ? inputQuantity.matches(validDigitsOnlyRegex) ? null : "Invalid Quantity" : Globals.fieldRequiredError);

            availableStock.setError(notEmpty(inputAvailableStock) ? inputAvailableStock.matches(validDigitsOnlyRegex) ? null : "Invalid Stock" : Globals.fieldRequiredError);
        }
    }

    private JSONObject getProductObject(final String itemName, final String itemInStock, final String MRP, final String sellingPrice, final String itemImage, final String itemType, final String itemUnit, final String sellingQuantity, final String itemDescription) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject price = new JSONObject();
            price.put("sellingPrice", sellingPrice);
            price.put("MRP", MRP);

            jsonObject.put("shopId", Globals.sharedPreferences.getString(Globals.shopId, null));
            jsonObject.put("itemName", itemName);
            jsonObject.put("itemInStock", itemInStock);
            jsonObject.put("price", price);
            jsonObject.put("itemType", itemType);
            jsonObject.put("itemUnit", itemUnit);
            jsonObject.put("sellingQuantity", sellingQuantity);
            jsonObject.put("itemDescription", itemDescription);
            jsonObject.put("itemImage", itemImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void setUnitsSpinner() {
        units.setOnItemSelectedListener(this);
        unitsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Globals.productUnitsList[Globals.sharedPreferences.getInt(Globals.shopTypeSelected, 0)]);
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        units.setAdapter(unitsAdapter);
    }

    private void setProductTypeSpinner() {
        productType.setOnItemSelectedListener(this);
        productTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Globals.shopTypeSubList[Globals.sharedPreferences.getInt(Globals.shopTypeSelected, 0)]);
        productTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productType.setAdapter(productTypeAdapter);
    }

    private boolean isImageUploaded() {
        return !((((BitmapDrawable) productImage.getDrawable()).getBitmap()).equals(((BitmapDrawable) ((ImageView) findViewById(R.id.sampleProductImage)).getDrawable()).getBitmap()) || (((BitmapDrawable) productImage.getDrawable()).getBitmap()).equals(((BitmapDrawable) ((ImageView) findViewById(R.id.sampleErrorImage)).getDrawable()).getBitmap()));
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
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == -1 && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            Log.i("onActivityResult", "Image:" + image.toString());
            productImage.setImageBitmap(image);
            productImage.setPadding(0, 0, 0, 0);
            productImage.setBackground(null);
            Log.i("onActivityResult", "Image Drawable:" + ((BitmapDrawable) productImage.getDrawable()).getBitmap());
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.productType)
            typeOfProduct = adapterView.getItemAtPosition(i).toString();
        if (adapterView.getId() == R.id.units)
            unit = adapterView.getItemAtPosition(i).toString();
//        Toast.makeText(getApplicationContext(), adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}