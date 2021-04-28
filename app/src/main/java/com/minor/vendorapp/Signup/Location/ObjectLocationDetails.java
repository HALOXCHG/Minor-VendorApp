package com.minor.vendorapp.Signup.Location;

public class ObjectLocationDetails {

    public String locality, state, pincode, addressLine, userGivenAddress, city;
    public Double latitude, longitude;

    public ObjectLocationDetails() {
    }

    public ObjectLocationDetails(Double latitude, Double longitude, String locality, String city, String state, String pincode, String addressLine, String userGivenAddress) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.addressLine = addressLine;
        this.userGivenAddress = userGivenAddress;
    }
}

