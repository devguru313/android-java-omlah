package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 09-11-2017.
 */

public class SignUpDetail implements Serializable {


    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;
    @SerializedName("success")
    public String success=null;

    public static class Data {
        @SerializedName("otp")
        public String otp;
        @SerializedName("platform")
        public String platform;
        @SerializedName("authentication")
        public String authentication;
        @SerializedName("auth_token")
        public String auth_token;
        @SerializedName("customer_id")
        public String customer_id;
    }
}
