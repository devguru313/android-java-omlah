package com.omlah.customer.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LoginSession {

    Activity activity;

    SharedPreferences introPreferences;
    SharedPreferences.Editor introEditor;

    SharedPreferences userDetailsPreferences;
    SharedPreferences.Editor userDetailsEditor;

    SharedPreferences userProfilePreferences;
    SharedPreferences.Editor userProfileEditor;

    SharedPreferences screenNamePreferences;
    SharedPreferences.Editor screenNameEditor;

    SharedPreferences gcmPreferences;
    SharedPreferences.Editor gcmEditor;

    SharedPreferences deviceIdPreferences;
    SharedPreferences.Editor deviceIdEditor;

    SharedPreferences locationPreferences;
    SharedPreferences.Editor locationEditor;

    SharedPreferences popCoinPreferences;
    SharedPreferences.Editor popCoinEditor;

    SharedPreferences imagePreferences;
    SharedPreferences.Editor imageEditor;

    SharedPreferences notificationPreferences;
    SharedPreferences.Editor notificationEditor;

    SharedPreferences fingerPreferences;
    SharedPreferences.Editor fingerEditor;

    SharedPreferences countryCodePreferences;
    SharedPreferences.Editor countryCodeEditor;

    SharedPreferences OTPverificationPreferences;
    SharedPreferences.Editor OTPverificationEditor;



    public static LoginSession loginSession=null;

    //Constructor
    public static LoginSession getInstance(Activity activity)
    {
        if (loginSession==null) {
            loginSession = new LoginSession(activity);
        }
        return loginSession;
    }

    public LoginSession(Activity activity) {
        super();
        this.activity = activity;

        introPreferences = this.activity.getSharedPreferences("INTRO", Context.MODE_PRIVATE);
        introEditor = introPreferences.edit();

        screenNamePreferences = this.activity.getSharedPreferences("MENU", Context.MODE_PRIVATE);
        screenNameEditor = screenNamePreferences.edit();

        gcmPreferences= this.activity.getSharedPreferences("GCM", Context.MODE_PRIVATE);
        gcmEditor=gcmPreferences.edit();

        deviceIdPreferences= this.activity.getSharedPreferences("DEVICEID", Context.MODE_PRIVATE);
        deviceIdEditor=deviceIdPreferences.edit();

        userDetailsPreferences= this.activity.getSharedPreferences("USERDETAILS", Context.MODE_PRIVATE);
        userDetailsEditor=userDetailsPreferences.edit();

        userProfilePreferences= this.activity.getSharedPreferences("USERPROFILE", Context.MODE_PRIVATE);
        userProfileEditor=userProfilePreferences.edit();

        locationPreferences= this.activity.getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        locationEditor=locationPreferences.edit();

        popCoinPreferences = this.activity.getSharedPreferences("popCoin", Context.MODE_PRIVATE);
        popCoinEditor = popCoinPreferences.edit();

        imagePreferences = this.activity.getSharedPreferences("ProfileImage", Context.MODE_PRIVATE);
        imageEditor = imagePreferences.edit();

        notificationPreferences = this.activity.getSharedPreferences("Notification", Context.MODE_PRIVATE);
        notificationEditor = notificationPreferences.edit();

        fingerPreferences = this.activity.getSharedPreferences("FingerPrint", Context.MODE_PRIVATE);
        fingerEditor = fingerPreferences.edit();

        countryCodePreferences = this.activity.getSharedPreferences("CountryCode", Context.MODE_PRIVATE);
        countryCodeEditor = countryCodePreferences.edit();

        OTPverificationPreferences = this.activity.getSharedPreferences("CountryCode", Context.MODE_PRIVATE);
        OTPverificationEditor = OTPverificationPreferences.edit();


    }


    /////////////////////////////////////////GET / SAVE GCM ID //////////////////////////////////
    //GCM save register id
    public void saveGcmId(String gcmid)
    {
        gcmEditor.putString("gcmid",gcmid);
        gcmEditor.commit();
    }
    public String getGcmId()
    {
        String gcmid=gcmPreferences.getString("gcmid","");
        return gcmid;
    }

    /////////////////////////////////////////GET / SAVE DEVICE ID //////////////////////////////////

    //Save device id
    public void saveDeviceId (String deviceId,String macID) {
        deviceIdEditor.putString("deviceId", deviceId);
        deviceIdEditor.putString("macID", macID);
        deviceIdEditor.commit();
    }

    //get device id
    public String getDeviceId() {
        String deviceId = deviceIdPreferences.getString("deviceId", "");
        return deviceId;
    }

    public String getMACId() {
        String macID = deviceIdPreferences.getString("macID", "");
        return macID;
    }


    //////////////////////////////CHECK LOGIN DETAILS///////////////////////////////////////


    //logout
    public void logout() {

        userDetailsEditor.clear();
        userDetailsEditor.commit();
        userProfileEditor.clear();
        userProfileEditor.commit();
        imageEditor.clear();
        fingerEditor.clear();
        imageEditor.commit();
        fingerEditor.commit();
    }

    ///////////////////////////// SAVE MENU NAME OF COMPANY  ////////////////////////////////////////////////

    public void saveScreenName(String MenuName) {

        screenNameEditor.putString("MenuName", MenuName);
        screenNameEditor.commit();
    }

    public String getScreenName() {
        String MenuName = screenNamePreferences.getString("MenuName", "");
        return MenuName;
    }

    /////////////////////////////////////////SAVE USER LOGIN DETAILS //////////////////////

    public void saveUserDetails(boolean login,String user_id, String token,String authentication,boolean passCode,boolean otp) {

        userDetailsEditor.putBoolean("boolean", login);
        userDetailsEditor.putString("user_id",user_id);
        userDetailsEditor.putString("token",token);
        userDetailsEditor.putString("authentication",authentication);
        userDetailsEditor.putBoolean("passcodeSet", passCode);
        userDetailsEditor.putBoolean("otpVerified", otp);
        userDetailsEditor.commit();
    }

    public String getAuthentication() {
        String getAuthentication = userDetailsPreferences.getString("authentication", "");
        Log.e("getAuthentication",getAuthentication);
        return getAuthentication;
    }

    //login check
    public boolean isLoggedIn() {
        return userDetailsPreferences.getBoolean("boolean", false);
    }

    //check passcode activate
    public boolean isPassCodeSet() {
        return userDetailsPreferences.getBoolean("passcodeSet", false);
    }

    //check OTP activate
    public boolean isOTPVerified() {
        return userDetailsPreferences.getBoolean("otpVerified", false);
    }

    public void setLogin(boolean value){

        userDetailsEditor.putBoolean("boolean", value);
        userDetailsEditor.commit();
    }

    public void setOTPVerified(boolean value){

        userDetailsEditor.putBoolean("otpVerified", value);
        userDetailsEditor.commit();
    }

    public void setPassCodeVerified(boolean value){


        userDetailsEditor.putBoolean("passcodeSet", value);
        userDetailsEditor.commit();
    }

    public String getUser_id() {
        String getUser_id = userDetailsPreferences.getString("user_id","");
        return  getUser_id;
    }
    public String getToken() {
        String getToken = userDetailsPreferences.getString("token","");
        return  getToken;
    }

    //GET current location
    public void setLatLong(String lat, String lang) {

        locationEditor.putString("lat", lat);
        locationEditor.putString("lang", lang);
        locationEditor.commit();
    }

    public String getLat() {
        String getLat = locationPreferences.getString("lat", "");
        return getLat;
    }

    public String getLang() {
        String getLang = locationPreferences.getString("lang", "");
        return getLang;
    }


    //PopCoins

    public void setPopcoin(String coin) {
        popCoinEditor.putString("coin", coin);
        popCoinEditor.commit();
    }

    public String getPopCoin() {
        String getPopCoin = popCoinPreferences.getString("coin", "");
        return getPopCoin;
    }


    //Save device id
    public void saveUserProfile(String id, String name,String email,String phoneNumber,String balanceAmount,String currencyCode,String currencySymbol,String timeZone,String qr,String countrycode ) {
        userProfileEditor.putString("id", id);
        userProfileEditor.putString("name", name);
        userProfileEditor.putString("email", email);
        userProfileEditor.putString("phoneNumber", phoneNumber);
        userProfileEditor.putString("balanceAmount", balanceAmount);
        userProfileEditor.putString("currencyCode", currencyCode);
        userProfileEditor.putString("currencySymbol", currencySymbol);
        userProfileEditor.putString("timeZone", timeZone);
        userProfileEditor.putString("qr", qr);
        userProfileEditor.putString("countrycode", countrycode);
        userProfileEditor.commit();
    }

    public String getCustomerCountryCode() {
        String countrycode = userProfilePreferences.getString("countrycode", "");
        return countrycode;
    }

    public String getQRCODE() {
        String qr = userProfilePreferences.getString("qr", "");
        return qr;
    }

    public String getCustomerID() {
        String id = userProfilePreferences.getString("id", "");
        return id;
    }


    public void setBalanceAmount(String balanceAmount) {
        userProfileEditor.putString("balanceAmount", balanceAmount);
        userProfileEditor.commit();
    }

    public void setName(String name){
        userProfileEditor.putString("name", name);
        userProfileEditor.commit();
    }

    public String getname() {
        String name = userProfilePreferences.getString("name", "");
        return name;
    }

    public void setEmail(String email){
        userProfileEditor.putString("email", email);
        userProfileEditor.commit();
    }

    public String getemail() {
        String email = userProfilePreferences.getString("email", "");
        return email;
    }

    public void setPhoneNumber(String phoneNumber){
        userProfileEditor.putString("phoneNumber", phoneNumber);
        userProfileEditor.commit();
    }

    public String getphoneNumber() {
        String phoneNumber = userProfilePreferences.getString("phoneNumber", "");
        return phoneNumber;
    }

    public String getbalanceAmount() {
        String balanceAmount = userProfilePreferences.getString("balanceAmount", "");
        return balanceAmount;
    }

    public String getShowCurrency(){

        String cCode = userProfilePreferences.getString("currencyCode", "");
        String cSymbol = userProfilePreferences.getString("currencySymbol", "");

        if(cCode.isEmpty()){
            return cSymbol;
        }else if(cSymbol.isEmpty()){
            return cCode;
        }else{
            return cCode+" "+cSymbol;
        }

    }

    public String getcurrencyCodee() {
        String currencyCode = userProfilePreferences.getString("currencyCode", "");
        return currencyCode;
    }

    public String getcurrencySymbol() {
        String currencySymbol = userProfilePreferences.getString("currencySymbol", "");
        return currencySymbol;
    }

    public String gettimeZone() {
        String timeZone = userProfilePreferences.getString("timeZone", "");
        return timeZone;
    }

    public void setProfileImage(String profileImage) {
        imageEditor.putString("profileImage", profileImage);
        imageEditor.commit();
    }
    public String getProfileImage() {
        String profileImage = imagePreferences.getString("profileImage", "");
        return profileImage;
    }

    public String getProfileImageURL() {
        String profileImageURL = imagePreferences.getString("profileImageURL", "");
        return profileImageURL;
    }

    public void setNotificationSetting(boolean onoff) {
        notificationEditor.putBoolean("onoff", onoff);
        notificationEditor.commit();
    }

    public boolean isNotificationEnabled() {
        boolean onoff = notificationPreferences.getBoolean("onoff", true);
        return onoff;
    }

    //INTRO SECTION
    public void saveIntro(String intro)
    {
        introEditor.putBoolean("boolean", true);
        introEditor.putString("intro",intro);
        introEditor.commit();
    }

    //intro check
    public boolean isIntroIn() {
        return introPreferences.getBoolean("boolean", false);
    }


    public void setFingerOption(String enableOrDisable)
    {
        fingerEditor.putString("enableOrDisable",enableOrDisable);
        fingerEditor.commit();
    }


    public String getFingerOption()
    {
        String enableOrDisable=fingerPreferences.getString("enableOrDisable","ON");
        return enableOrDisable;
    }

    public void setCountryCode(String countryCode)
    {
        countryCodeEditor.putString("countryCode",countryCode);
        countryCodeEditor.commit();
    }

    public String getCountryCode()
    {
        String countryCode=countryCodePreferences.getString("countryCode","");
        return countryCode;
    }

    public void set_OTP_VERIFICATION(boolean value)
    {
        OTPverificationEditor.putBoolean("OTP_VERIFICATION",value);
        OTPverificationEditor.commit();
    }

    public boolean get_OTP_VERIFICATION()
    {
        Boolean otp_option = false;
        return otp_option;
    }



   /* public void setLanguage(String language)
    {
        languageEditor.putString("language",language);
        languageEditor.commit();
    }

    public String getLanguage()
    {
        return languagePreferences.getString("language","");
    }*/

}
