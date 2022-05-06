package com.minor.vendorapp.Helpers;

public class HitURL {

    //URL Path
    protected static final String urlPath = "http://34.214.121.145:5000/";

    //URL's
    public static final String signup = urlPath + "shopRegister";
    public static final String login = urlPath + "vendorLogin";
    public static final String addProduct = urlPath + "addItem";
    public static final String fetchProducts = urlPath + "itemDetail";
    public static final String updateProduct = urlPath + "updateItem";
    public static final String updateProfile = urlPath + "updateShop";
    public static final String shopVisits = urlPath + "profileVisits";
    public static final String fetchOrders = urlPath + "orderList";
    public static final String orderDetails = urlPath + "orderDetails";
    public static final String acceptOrder = urlPath + "processOrder";
    public static final String rejectOrder = urlPath + "processOrder";
    public static final String deliverOrder = urlPath + "processOrder";

}
