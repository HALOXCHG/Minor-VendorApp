package com.minor.vendorapp.Nav.Products;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.minor.vendorapp.Apis.ApiRequest;
import com.minor.vendorapp.Apis.Callback;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.Helpers.HitURL;
import com.minor.vendorapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProductsFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    ExtendedFloatingActionButton addProductButton;
    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayoutManager linearLayoutManager;

    List<DataTransfer> productList;
    AdapterProductsListing adapterProductsListing;

    Boolean callByRefresh = false;

    public ProductsFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProducts(getView());
        Log.i("onResume", "Run");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        Globals.sharedPreferences = getActivity().getSharedPreferences(Globals.prefName, MODE_PRIVATE);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh_products);
        addProductButton = (ExtendedFloatingActionButton) view.findViewById(R.id.addProductButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_products);
        loader = (ProgressBar) view.findViewById(R.id.loader);

        productList = new ArrayList<>();
        adapterProductsListing = new AdapterProductsListing(productList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterProductsListing);


//        fetchProducts(view);
        swipeToRefreshNews(view);
        addProductButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ActivityProductAdd.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchProducts(View view) {
        if (!callByRefresh) {
            loader.setVisibility(View.VISIBLE);
        }
//        swipeRefreshLayout.setEnabled(false);
//        productList.clear();
//        adapterProductsListing.notifyDataSetChanged();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("shopId", Globals.sharedPreferences.getString(Globals.shopId, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("JSONObject", "" + jsonObject);
        ApiRequest.callApi(getActivity(), HitURL.fetchProducts, jsonObject, new Callback() {
            @Override
            public void response(JSONObject resp) {
                Log.i("Resp", "" + resp);
                String status = resp.optString("status");
                String message = resp.optString("message");
                if (status.equalsIgnoreCase("200")) {
                    productList.clear();
                    adapterProductsListing.notifyDataSetChanged();

                    view.findViewById(R.id.productMessage).setVisibility(message.equalsIgnoreCase("No item added yet") ? View.VISIBLE : View.GONE);

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

//                            Log.i("Data", "Name " + name);
//                            Log.i("Data", "Description " + description);
//                            Log.i("Data", "Quantity" + quantity);
//                            Log.i("Data", "Sell P" + sellingPrice);
//                            Log.i("Data", "MRP " + mrp);
//                            Log.i("Data", "Image " + image);
                            productList.add(new DataTransfer(image, name, description, quantity, sellingPrice, mrp, jObj));
                        }
                        adapterProductsListing.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                loader.setVisibility(View.GONE);
                callByRefresh = false;
//                swipeRefreshLayout.setEnabled(true)
            }

            @Override
            public void error(VolleyError error) {
                Functions.showVolleyErrors(error, getContext());
                swipeRefreshLayout.setRefreshing(false);
                loader.setVisibility(View.GONE);
                callByRefresh = false;
//                swipeRefreshLayout.setEnabled(true);
            }
        });
    }

    //Swipe to refresh new section
    private void swipeToRefreshNews(View v) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!swipeRefreshLayout.canChildScrollUp() && !swipeRefreshLayout.isRefreshing()) {
//                swipeRefreshLayout.setRefreshing(true);
                callByRefresh = true;
                Log.i("Swipe", "" + swipeRefreshLayout.canChildScrollUp());
                Log.i("Swipe", "" + swipeRefreshLayout.isRefreshing());
                //Fetch data
                fetchProducts(v);
            }
        });
    }
}
