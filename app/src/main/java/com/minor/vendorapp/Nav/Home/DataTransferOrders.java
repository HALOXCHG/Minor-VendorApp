package com.minor.vendorapp.Nav.Home;

import org.json.JSONObject;

class DataTransferOrders {

    String orderId, name, address, time, date, billAmount, orderStatus;
    JSONObject jsonObject;

    public DataTransferOrders() {
    }

    public DataTransferOrders(String orderId, String name, String address, String time, String date, String billAmount, String orderStatus, JSONObject jsonObject) {
        this.orderId = orderId;
        this.name = name;
        this.address = address;
        this.time = time;
        this.date = date;
        this.billAmount = billAmount;
        this.orderStatus = orderStatus;
        this.jsonObject = jsonObject;
    }

    public DataTransferOrders clone() {

        DataTransferOrders clone = new DataTransferOrders();
        clone.orderId = this.getOrderId();
        clone.name = this.getName();
        clone.address = this.getAddress();
        clone.time = this.getTime();
        clone.date = this.getDate();
        clone.billAmount = this.getBillAmount();
        clone.orderStatus = this.getOrderStatus();
        clone.jsonObject = this.getJsonObject();

        return clone;

    }

    public String getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject
    ) {
        this.jsonObject = jsonObject;
    }
}

