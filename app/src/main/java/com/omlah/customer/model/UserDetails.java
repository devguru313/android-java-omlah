package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 26-09-2017.
 */

public class UserDetails implements Serializable{

    @SerializedName("data")
    public Data data=null;
    @SerializedName("message")
    public String message="";

    public static class Data {
        @SerializedName("pass_code_verified")
        public String pass_code_verified="";
        @SerializedName("otp_option")
        public String otp_option="";
        @SerializedName("otp_verified")
        public String otp_verified="";
        @SerializedName("platform")
        public String platform="";
        @SerializedName("authentication")
        public String authentication="";
        @SerializedName("auth_token")
        public String auth_token="";
        @SerializedName("customer_id")
        public String customer_id="";
        @SerializedName("clearSession")
        public String clearSession="";
    }
}
