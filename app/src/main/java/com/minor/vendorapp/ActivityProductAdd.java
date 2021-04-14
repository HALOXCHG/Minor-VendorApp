package com.minor.vendorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ActivityProductAdd extends AppCompatActivity {

    EditText productName, productType, mrp, sellingPrice, quantity, units, availableStock, productDescription;
    ImageView productImage;
    Button addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);


    }
}