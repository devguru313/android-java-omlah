package com.omlah.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RechargeHistory implements Serializable {


    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;
    @Expose
    @SerializedName("message")
    public String message;

    public static class Data {
        @Expose
        @SerializedName("created")
        public String created;
        @Expose
        @SerializedName("amount")
        public String amount;
        @Expose
        @SerializedName("callingCodes")
        public String callingCodes;
        @Expose
        @SerializedName("flag")
        public String flag;
        @Expose
        @SerializedName("provider_image")
        public String provider_image;
        @Expose
        @SerializedName("provider_name")
        public String provider_name;
        @Expose
        @SerializedName("service_type")
        public String service_type;
        @Expose
        @SerializedName("mobile_no")
        public String mobile_no;
        @Expose
        @SerializedName("country_code")
        public String country_code;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;
    }
}
