package com.minor.vendorapp.Nav.Home;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.minor.vendorapp.Apis.ApiRequest;
import com.minor.vendorapp.Apis.Callback;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.Globals;
import com.minor.vendorapp.Helpers.HitURL;
import com.minor.vendorapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    Button allOrders, ongoingOrders, historyOrders;
    TextView todayCounter, totalCounter;

    RecyclerView recyclerView;
    ProgressBar loader;
    LinearLayoutManager linearLayoutManager;

    List<DataTransferOrders> ordersList = new ArrayList<>();
    List<DataTransferOrders> ordersFilterList = new ArrayList<>();
    AdapterOrdersListing adapterOrdersListing;

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchOrdersList(getView());
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Globals.sharedPreferences = getActivity().getSharedPreferences(Globals.prefName, MODE_PRIVATE);

        allOrders = (Button) view.findViewById(R.id.allOrders);
        ongoingOrders = (Button) view.findViewById(R.id.ongoingOrders);
        historyOrders = (Button) view.findViewById(R.id.historyOrders);

        todayCounter = (TextView) view.findViewById(R.id.todayCounter);
        totalCounter = (TextView) view.findViewById(R.id.totalCounter);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerOrders);
        loader = (ProgressBar) view.findViewById(R.id.loader);

//        ordersList = new ArrayList<>();
//        ordersFilterList = new ArrayList<>();
        adapterOrdersListing = new AdapterOrdersListing(ordersList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterOrdersListing);

        allOrders.setOnClickListener(view1 -> fetchOrdersList(view));
        ongoingOrders.setOnClickListener(view1 -> loadFilteredOrders("Ongoing"));
        historyOrders.setOnClickListener(view1 -> loadFilteredOrders("History"));

        setCounter();

        return view;
    }

    private void setCounter() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("shopId", Globals.sharedPreferences.getString(Globals.shopId, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Counter JSONObject", "" + jsonObject);
        ApiRequest.callApi(getActivity(), HitURL.shopVisits, jsonObject, new Callback() {
            @Override
            public void response(JSONObject resp) {

                Log.i("Counter Resp", "" + resp);

                String status = resp.optString("status");
                String message = resp.optString("message");

                if (status.equalsIgnoreCase("200")) {
                    try {
                        JSONArray data = resp.optJSONArray("data");
                        JSONObject obj = data.optJSONObject(0);
                        JSONObject totalVisits = obj.getJSONObject("totalCounter");
                        String counterTotal = totalVisits.getString("counter");
                        JSONArray todayVisits = obj.optJSONArray("todayCounter");
                        Integer counterToday = todayVisits.length() != 1 ? todayVisits.length() : 0;

                        todayCounter.setText(counterToday.toString());
                        totalCounter.setText(counterTotal);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error(VolleyError error) {

            }
        });
    }

    private void loadFilteredOrders(String status) {
        Log.i("Filter", "ordersList " + ordersList);
        Log.i("Filter", "FilterList1 " + ordersFilterList);
        List<DataTransferOrders> tempList = new ArrayList<>();
        for (DataTransferOrders obj : ordersFilterList) {
            tempList.add(obj.clone());
        }
        ordersList.clear();
        adapterOrdersListing.notifyDataSetChanged();

        Log.i("Filter", "FilterList2 " + ordersFilterList);
        Log.i("Filter", "TempList " + tempList);

        for (DataTransferOrders obj : tempList) {
            boolean condn = status.equalsIgnoreCase("Ongoing") ? (obj.orderStatus.equalsIgnoreCase("Active") || obj.orderStatus.equalsIgnoreCase("Pending") || obj.orderStatus.equalsIgnoreCase("Delivered")) : (obj.orderStatus.equalsIgnoreCase("Completed") || obj.orderStatus.equalsIgnoreCase("Rejected") || obj.orderStatus.equalsIgnoreCase("Cancelled"));
            if (condn) {
                ordersList.add(obj);
            }
        }
        adapterOrdersListing.notifyDataSetChanged();
    }

    private void fetchOrdersList(View view) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("shopId", Globals.sharedPreferences.getString(Globals.shopId, null));
//            jsonObject.put("shopId", "shop1619525798968");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("JSONObject", "" + jsonObject);
        ApiRequest.callApi(getActivity(), HitURL.fetchOrders, jsonObject, new Callback() {
            @Override
            public void response(JSONObject resp) {
                Log.i("Resp", "" + resp);
                String status = resp.optString("status");
                String message = resp.optString("message");
                if (status.equalsIgnoreCase("200")) {
                    ordersList.clear();
                    adapterOrdersListing.notifyDataSetChanged();

//                    view.findViewById(R.id.productMessage).setVisibility(message.equalsIgnoreCase("No orders here yet.") ? View.VISIBLE : View.GONE);

                    try {
                        JSONArray array = resp.optJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jObj = array.getJSONObject(i);

                            String orderId = jObj.optString("orderId");
                            String custName = jObj.optString("custName");
                            String orderStatus = jObj.optString("status");
                            String billAmount = String.format("Rs. %s", jObj.optString("amount"));

                            String[] dateTime = new String[]{"date", "time"};
                            try {
                                dateTime = extractDateAndTime(jObj.optString("createdAt"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String orderDate = dateTime[0];
                            String orderTime = dateTime[1];

                            String custAddress = formatAddress(jObj.optJSONObject("Address"));

                            JSONObject orderDetailsObject = getIntentObject(jObj);

                            Log.i("DataTransferOrders", getIntentObject(jObj).toString());

                            ordersList.add(new DataTransferOrders(orderId, custName, custAddress, orderTime, orderDate, billAmount, orderStatus, orderDetailsObject));
                        }
                        Log.i("Filter", "RespList " + ordersList);
                        for (DataTransferOrders obj : ordersList) {
                            ordersFilterList.add(obj.clone());
                        }
                        Log.i("Filter", "RespFilterList " + ordersFilterList);
                        adapterOrdersListing.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
//                loader.setVisibility(View.GONE);
            }

            @Override
            public void error(VolleyError error) {
                Functions.showVolleyErrors(error, getContext());
//                loader.setVisibility(View.GONE);
            }
        });
    }

    private JSONObject getIntentObject(JSONObject jObj) {
        JSONObject returnObj = new JSONObject();
        try {
            JSONArray array = jObj.optJSONArray("itemDetails");
            JSONArray itemId = new JSONArray();
            JSONArray itemQuantity = new JSONArray();
            JSONArray itemTotal = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                itemId.put(i, item.optString("id"));
                itemQuantity.put(i, item.optString("quantity"));
                itemTotal.put(i, item.optString("itemTotal"));
            }
            returnObj.put("itemId", itemId);
            returnObj.put("userId", jObj.optString("userId"));

            returnObj.put("quantity", itemQuantity);
            returnObj.put("itemTotal", itemTotal);
            returnObj.put("amount", jObj.optString("amount"));
            returnObj.put("status", jObj.optString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObj;
    }

    private String formatAddress(JSONObject address) {
        return String.format("%1s,%2s,%3s", "2", "1", "4");
    }

    private String[] extractDateAndTime(String dateTime) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = format.parse(dateTime);
        Log.i("extractDateAndTime", "Date " + date);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Log.i("extractDateAndTime", sdf.format(date));

        SimpleDateFormat sdt = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        Log.i("extractDateAndTime", sdt.format(date));
        return new String[]{sdf.format(date), sdt.format(date)};
    }
}
