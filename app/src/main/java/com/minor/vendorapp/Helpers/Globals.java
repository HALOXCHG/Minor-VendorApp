package com.minor.vendorapp.Helpers;

import android.content.SharedPreferences;

public class Globals {
    //Default ShopTimings JsonObjects & Arrays
    public static final String[] dayOfWeek = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    public static final String shopClosed = "{\"status\":\"Closed\"}";
    public static final String shopOpen24hours = "{\"status\":\"Open 24 hours\"}";
    public static final String[] shopTypeList = {"Electronics", "Clothes", "Footwear", "Kirana"};
    public static final String[][] shopTypeSubList = {{"Gadgets", "Kitchen Appliances", "Home Appliances", "Mobiles", "Mobile Accessories", "Computers and Laptops", "Computer Peripherals", "Computer and Laptop Accessories", "Speakers", "Washing Machines", "Refrigerators", "Air Conditioners", "Health Care Appliances", "Wearable Tech", "Camera", "Camera Accessories", "Gaming Accessories"},
            {"Kid's wear", "Men's wear", "Women's wear", "Summer wear", "Winter wear", "Ethnic wear", "Inner wear", "Formal", "Casual", "Party Dress", "Top wear", "Bottom Wear", "Watches", "Accessories", "Sports", "Raincoats and WindCheaters", "Ties,Socks and Caps", "Night Wear", "Garments"},
            {"Men's", "Women's", "Sports", "Casual", "Formal", "Sandals & Floaters", "Flip-Flops", "Boots", "Running Shoes", "Sneakers", "School Shoes", "Ballerinas", "Heels",},
            {"Fruits & Vegetables", "Grocery", "Household", "Personal Care", "Toiletries", "Namkeen, Biscuits & Cookies", "Dairy Products", "Bakes And Shakes", "Paneer & Tofu", "Handwash Liquid", "Soaps & Shampoo", "Hair Conditioners", "Deo's & Perfumes", "Tea & Coffee", "Fruit Drinks & Juices", "Pet care", "Puja Items", "Floor Cleaners", "Spices", "Flours", "Pulses",}};
    public static final String[][] productUnitsList = {{"Unit(s)", "Item(s)", "Pieces", "Set"},
            {"Unit(s)", "Item(s)", "Pieces", "Set", "ft.", "meter(s)", "sq. ft.", "sq. meter"},
            {"Pair", "Set"},
            {"kg(s)", "gm(s)", "litre(s)", "ml", "dozen", "packet(s)", "Unit(s)", "Item(s)", "Pieces"}};

    //Error Messages
    public static final String errorUnknown = "Something went wrong! Please try again later.";
    public static final String dexterError = "Dexter Error Occurred";
    public static final String prefName = "pref_name";
    //User data
    public static final String shopTypeSelected = "shopTypeSelected";
    public static final String shopId = "shopId";
    //Volley Request Headers
    protected static final String authKey = "asdfgh";
    //Firebase Token
    protected static final String firebaseToken = "firebaseToken";
    public static CharSequence fieldRequiredError = "This field is required.";
    //Shared Prefs.
    public static SharedPreferences sharedPreferences;
}
