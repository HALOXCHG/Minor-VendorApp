package com.minor.vendorapp.Nav.Home;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.minor.vendorapp.Apis.ApiRequest;
import com.minor.vendorapp.Apis.Callback;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.HitURL;
import com.minor.vendorapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityViewOrderDetails extends AppCompatActivity {

    TextView custName, custMobile, custAddress, itemTotal, deliveryCharge, grandTotal;
    Button acceptOrder, rejectOrder;

    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayoutManager linearLayoutManager;

    List<DataTransferOrderDetailsItems> orderDetailsItemsList;
    AdapterOrderDetailsItems adapterOrderDetailsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_details);

        //Use getIntent() here

        custName = (TextView) findViewById(R.id.custName);
        custMobile = (TextView) findViewById(R.id.custMobile);
        custAddress = (TextView) findViewById(R.id.custAddress);
        itemTotal = (TextView) findViewById(R.id.itemTotal);
        deliveryCharge = (TextView) findViewById(R.id.deliveryCharge);
        grandTotal = (TextView) findViewById(R.id.grandTotal);

        acceptOrder = (Button) findViewById(R.id.acceptOrder);
        rejectOrder = (Button) findViewById(R.id.rejectOrder);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerOrderItems);
        loader = (ProgressBar) findViewById(R.id.loader);

        orderDetailsItemsList = new ArrayList<>();
        adapterOrderDetailsItems = new AdapterOrderDetailsItems(orderDetailsItemsList);
        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterOrderDetailsItems);

        acceptOrder.setOnClickListener(view -> acceptOrder());
        rejectOrder.setOnClickListener(view -> rejectOrder());

        setOrderDetails();

    }

    private void setOrderDetails() {
        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("Object"));
            jsonObject.remove("quantity");
            jsonObject.remove("itemTotal");
            jsonObject.remove("amount");
//            JSONArray quantityOrdered  = new JSONObject(getIntent().getStringExtra("Object")).optJSONArray("quantity");
            Log.i("Obj", "" + jsonObject);
//            Log.i("Quan", "" + quantityOrdered);
            ApiRequest.callApi(getBaseContext(), HitURL.orderDetails, jsonObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    Log.i("Resp", "" + resp);
                    JSONObject data = resp.optJSONObject("data");
                    JSONArray array = data.optJSONArray("userdetails");
                    setCustomerDetails(array);
                    setorderDetailsItemsList(data.optJSONArray("itemdetails"));
                    setBillAmountDetails();

                }

                @Override
                public void error(VolleyError error) {
                    Functions.showVolleyErrors(error, getBaseContext());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBillAmountDetails() {
        try {
            String amount = new JSONObject(getIntent().getStringExtra("Object")).optString("amount");
            JSONArray itemTotal = new JSONObject(getIntent().getStringExtra("Object")).optJSONArray("itemTotal");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setorderDetailsItemsList(JSONArray itemdetails) {
        try {
            JSONArray quantityOrdered = new JSONObject(getIntent().getStringExtra("Object")).optJSONArray("quantity");
            JSONArray itemTotal = new JSONObject(getIntent().getStringExtra("Object")).optJSONArray("itemTotal");
            JSONArray details = itemdetails.getJSONArray(0);

            for (int i = 0; i < details.length(); i++) {
                JSONObject obj = details.optJSONObject(i);
                String itemName = obj.optString("itemName");
                String offeredUnits = String.format("%1s %2s", obj.optInt("sellingQuantity"), obj.optString("itemUnits"));

                JSONObject price = obj.optJSONObject("price");
                String itemSellingPrice = price.optString("sellingPrice");
                String itemTotalCost = String.format("Rs. %1s", itemTotal.optInt(i));
                String itemImage = obj.optString("itemImage");

                orderDetailsItemsList.add(i, new DataTransferOrderDetailsItems(itemImage, itemName, offeredUnits, String.valueOf(quantityOrdered.optInt(i)), String.format(" x Rs.%1s", itemSellingPrice), itemTotalCost));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapterOrderDetailsItems.notifyDataSetChanged();
    }

    private void setCustomerDetails(JSONArray array) {

        try {
            JSONObject obj = array.getJSONObject(0);
            custName.setText(obj.optString("userName"));
            custMobile.setText(obj.optString("contactNo"));
            custAddress.setText(formatAddress(obj.optJSONObject("address")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String formatAddress(JSONObject address) {
        return String.format("%1s,%2s,%3s", "2", "1", "4");
    }

    private void rejectOrder() {
        //API Req.
    }

    private void acceptOrder() {
        //API Req.
    }
}