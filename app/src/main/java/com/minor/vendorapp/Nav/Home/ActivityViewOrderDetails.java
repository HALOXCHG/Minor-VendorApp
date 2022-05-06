package com.minor.vendorapp.Nav.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    Button acceptOrder, rejectOrder, deliverOrder;
    LinearLayout linearButtons, linearButton, linearText;

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

        linearButtons = (LinearLayout) findViewById(R.id.linearButtons);
        linearButton = (LinearLayout) findViewById(R.id.linearButton);
        linearText = (LinearLayout) findViewById(R.id.linearText);

        acceptOrder = (Button) findViewById(R.id.acceptOrder);
        rejectOrder = (Button) findViewById(R.id.rejectOrder);
        deliverOrder = (Button) findViewById(R.id.deliverOrder);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerOrderItems);
        loader = (ProgressBar) findViewById(R.id.loader);

        orderDetailsItemsList = new ArrayList<>();
        adapterOrderDetailsItems = new AdapterOrderDetailsItems(orderDetailsItemsList);
        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterOrderDetailsItems);

        acceptOrder.setOnClickListener(view -> acceptOrder());
        rejectOrder.setOnClickListener(view -> rejectOrder());
        deliverOrder.setOnClickListener(view -> deliverOrder());

        setUI();
        setOrderDetails();

    }

    private void setUI() {
        try {
            String orderStatus = new JSONObject(getIntent().getStringExtra("Object")).optString("status");
            Log.i("Status", orderStatus);
            if (orderStatus.equalsIgnoreCase("Pending")) {
                linearButtons.setVisibility(View.VISIBLE);
                linearButton.setVisibility(View.GONE);
                linearText.setVisibility(View.GONE);
            } else if (orderStatus.equalsIgnoreCase("Accepted") || orderStatus.equalsIgnoreCase("Active")) {
                linearButtons.setVisibility(View.GONE);
                linearButton.setVisibility(View.VISIBLE);
                linearText.setVisibility(View.GONE);
            } else if (orderStatus.equalsIgnoreCase("Delivered") || orderStatus.equalsIgnoreCase("Cancelled")) {
                linearButtons.setVisibility(View.GONE);
                linearButton.setVisibility(View.GONE);
                linearText.setVisibility(View.GONE);
            } else if (orderStatus.equalsIgnoreCase("Completed")) {
                linearButtons.setVisibility(View.GONE);
                linearButton.setVisibility(View.GONE);
                linearText.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
//                    Log.i("Resp", "" + resp);
                    JSONObject data = resp.optJSONObject("data");
                    JSONArray array = data.optJSONArray("userdetails");
                    setCustomerDetails(array);
                    Log.i("Resp", "" + data.optJSONArray("itemdetails"));
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
            JSONArray itemTotalArray = new JSONObject(getIntent().getStringExtra("Object")).optJSONArray("itemTotal");
            Log.i("TotalArray", "" + itemTotalArray);
            int total = 0;
            for (int i = 0; i < itemTotalArray.length(); i++) {
                total += Integer.parseInt(itemTotalArray.getString(i));
            }

            itemTotal.setText(String.format("Rs.%1s", total));
            deliveryCharge.setText(String.format("Rs.%1s", "14"));
            grandTotal.setText(String.format("Rs.%1s", (Integer.parseInt(amount) + 14)));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setorderDetailsItemsList(JSONArray itemdetails) {
        try {
            JSONArray quantityOrdered = new JSONObject(getIntent().getStringExtra("Object")).optJSONArray("quantity");
            JSONArray itemTotal = new JSONObject(getIntent().getStringExtra("Object")).optJSONArray("itemTotal");

            Log.i("Dets", "" + quantityOrdered);
            for (int j = 0; j < itemdetails.length(); j++) {
                JSONArray details = itemdetails.getJSONArray(j);
                for (int i = 0; i < details.length(); i++) {
                    JSONObject obj = details.optJSONObject(i);
                    String itemName = obj.optString("itemName");
                    String offeredUnits = String.format("%1s %2s", obj.optInt("sellingQuantity"), obj.optString("itemUnit"));

                    JSONObject price = obj.optJSONObject("price");
                    String itemSellingPrice = price.optString("sellingPrice");
                    String itemTotalCost = String.format("Rs. %1s", itemTotal.optInt(i));
                    String itemImage = obj.optString("itemImage");

                    Log.i("Item Details ", j + " itemName: " + itemName + " offeredUnits: " + offeredUnits +
                            " price: " + price + " itemSellingPrice: " + itemSellingPrice + " itemTotalCost" + itemTotalCost);

                    orderDetailsItemsList.add(i, new DataTransferOrderDetailsItems(itemImage, itemName, offeredUnits, String.valueOf(quantityOrdered.optString(j)), String.format(" x Rs.%1s", itemSellingPrice), itemTotalCost));
                }
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
        String locality = address.optString("locality");
        String city = address.optString("city");
        String pincode = address.optString("pincode");
        String addressLine = address.optString("addressLine");

        return String.format("%1s,\n%2s, %3s - %4s.", addressLine, locality, city, pincode);
        //        return String.format("%1s,%2s,%3s", "2", "1", "4");
    }

    private void rejectOrder() {
        //API Req.
        String orderId = getIntent().getStringExtra("OrderId"); //SET OrderId
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderId);
            jsonObject.put("decision", "Rejected");
            Log.i("TotalArray", "" + jsonObject);
            ApiRequest.callApi(getBaseContext(), HitURL.rejectOrder, jsonObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    Toast.makeText(getApplicationContext(), "Order Rejected", Toast.LENGTH_SHORT).show();
                    finish();
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

    private void acceptOrder() {
        //API Req.
        String orderId = getIntent().getStringExtra("OrderId"); //SET OrderId
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderId);
            jsonObject.put("decision", "Accepted");
            Log.i("TotalArray", "" + jsonObject);
            ApiRequest.callApi(getBaseContext(), HitURL.acceptOrder, jsonObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    Toast.makeText(getApplicationContext(), "Order Accepted", Toast.LENGTH_SHORT).show();
                    finish();
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

    private void deliverOrder() {
        //API Req.
        String orderId = getIntent().getStringExtra("OrderId"); //SET OrderId
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderId);
            jsonObject.put("decision", "Delivered");
            Log.i("TotalArray", "" + jsonObject);
            ApiRequest.callApi(getBaseContext(), HitURL.deliverOrder, jsonObject, new Callback() {
                @Override
                public void response(JSONObject resp) {
                    Toast.makeText(getApplicationContext(), "Order Delivered", Toast.LENGTH_SHORT).show();
                    finish();
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
}