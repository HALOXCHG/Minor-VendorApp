package com.minor.vendorapp.Nav.Products;

import org.json.JSONObject;

class DataTransfer {

    String image, name, description, quantity, sellingPrice, MRP;
    JSONObject jsonObject;

    public DataTransfer(String image, String name, String description, String quantity, String sellingPrice, String MRP, JSONObject jsonObject) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.MRP = MRP;
        this.jsonObject = jsonObject;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public String getMRP() {
        return MRP;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}

