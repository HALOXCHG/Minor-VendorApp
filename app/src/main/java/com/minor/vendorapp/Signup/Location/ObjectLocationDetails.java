package com.minor.vendorapp.Signup.Location;

public class ObjectLocationDetails {

    public String locality, country, state, pincode, address_line, user_given_address;
    public Double latitude, longitude;


    public ObjectLocationDetails(Double latitude, Double longitude, String locality, String country, String state, String pincode, String address_line, String user_given_address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.country = country;
        this.state = state;
        this.pincode = pincode;
        this.address_line = address_line;
        this.user_given_address = user_given_address;
    }
}
