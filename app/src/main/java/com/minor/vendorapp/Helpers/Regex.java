package com.minor.vendorapp.Helpers;

public class Regex {

    //Regex Patterns
    public static final String validPhoneNumberRegex = "^[6-9]\\d{9}$";
    public static final String validEmailIDRegex = "^[A-Za-z0-9._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
    public static final String validPasswordRegex = "^[a-zA-Z0-9@$!%*#?&]{8,21}$";
    public static final String validOTPRegex = "^[0-9]{6}$";
    public static final String validDigitsOnlyRegex = "^[0-9]{0,10}$";
    public static final String validNamesRegex = "^[a-zA-Z\\s]{1,50}$";
    public static final String validProfessionRegex = "^[a-zA-Z.\\s]{0,30}$";
    public static final String validCompanyRegex = "^[a-zA-Z0-9&.\\s]{0,50}$";
    public static final String validAddressRegex = "^[a-zA-Z0-9-,().\\s]{1,100}$";
}
