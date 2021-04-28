package com.minor.vendorapp.ui.products;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.minor.vendorapp.ActivityProductAdd;
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

public class ProductsFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    ExtendedFloatingActionButton addProductButton;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    List<DataTransfer> productList;
    AdapterProductsListing adapterProductsListing;

    public ProductsFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh_products);
        addProductButton = (ExtendedFloatingActionButton) view.findViewById(R.id.addProductButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_products);

        productList = new ArrayList<>();
        adapterProductsListing = new AdapterProductsListing(productList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterProductsListing);


        fetchProducts(view);
        swipeToRefreshNews(view);
        addProductButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ActivityProductAdd.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchProducts(View view) {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("shopId", Globals.sharedPreferences.getString(Globals.shopId, null));
            jsonObject.put("shopId", "shop1619525798968");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("JSONObject", "" + jsonObject);
        ApiRequest.callApi(getActivity(), HitURL.fetchProducts, jsonObject, new Callback() {
            @Override
            public void response(JSONObject resp) {
                Log.i("Resp", "" + resp);
                String status = resp.optString("status");
                if (status.equalsIgnoreCase("200")) {
                    try {
                        JSONArray array = resp.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jObj = array.getJSONObject(i);
                            JSONObject object = jObj.optJSONObject("price");

                            String image = jObj.optString("itemImage");
                            String name = jObj.optString("itemName");
                            String description = jObj.optString("itemDescription");
                            String quantity = jObj.optString("itemInStock");
                            String sellingPrice = object.optString("sellingPrice");
                            String mrp = object.optString("MRP");

                            Log.i("Data", "Name " + name);
                            Log.i("Data", "Description " + description);
                            Log.i("Data", "Quantity" + quantity);
                            Log.i("Data", "Sell P" + sellingPrice);
                            Log.i("Data", "MRP " + mrp);
                            Log.i("Data", "Image " + image);

//                            String image = Functions.bitmapToBase64(getActivity(), ((BitmapDrawable) ((ImageView) view.findViewById(R.id.sampleHomeImage)).getDrawable()).getBitmap());
//                            String name = "Coffee Sachet";
//                            String description = "NIce coffee for your lungs and heart";
//                            String quantity = "2 Units";
//                            String sellingPrice = "Rs.48";
//                            String mrp = "Rs.35";
//                            JSONObject j = new JSONObject();
//                            j.put("A", "Abc");
//                            j.put("B", "Bcd");
                            productList.add(new DataTransfer(image, name, description, quantity, sellingPrice, mrp, jObj));
                        }
                        adapterProductsListing.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void error(VolleyError error) {
                Functions.showVolleyErrors(error, getContext());
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }
        });

//        String image = Functions.bitmapToBase64(getActivity(), ((BitmapDrawable) ((ImageView) view.findViewById(R.id.sampleHomeImage)).getDrawable()).getBitmap());
//        String name = "Coffee Sachet Bru cofeee";
//        String description = "NIce coffee for your lungs and heart Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore Lorem ipsum dolor sit amet";
//        String quantity = "2 Units";
//        String sellingPrice = "Rs.43";
//        String mrp = "Rs. 35";
//
//        productList.add(new DataTransfer(image, name, description, quantity, sellingPrice, mrp));
//        productList.add(new DataTransfer(image, name, description, quantity, sellingPrice, mrp));
//        adapterProductsListing.notifyDataSetChanged();

//        String image = Functions.bitmapToBase64(getActivity(), ((BitmapDrawable) ((ImageView) view.findViewById(R.id.sampleHomeImage)).getDrawable()).getBitmap());
//        String name = "Coffee Sachet";
//        String description = "NIce coffee for your lungs and heart";
//        String quantity = "2 Units";
//        String sellingPrice = "Rs.48";
//        String mrp = "Rs.35";
//        JSONObject j = new JSONObject();
//        try {
//            j.put("A", "Abc");
//            j.put("B", "Bcd");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        productList.add(new DataTransfer(image, name, description, quantity, sellingPrice, mrp, j));
//        productList.add(new DataTransfer(image, "New Prod", "None", "4Litres", "Rs.250", "Rs.175", j));
//        productList.add(new DataTransfer(image, "New Prod 2", "None None None None None ", "40 Litres", "Rs.2650", "Rs.1675", j));
    }

    //Swipe to refresh new section
    private void swipeToRefreshNews(View v) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //Set setRefreshing(false);
            swipeRefreshLayout.setEnabled(false);
            //Clear adapter
            productList.clear();
            //Notify data changed
            adapterProductsListing.notifyDataSetChanged();
            //Fetch data
            fetchProducts(v);
        });
    }
}