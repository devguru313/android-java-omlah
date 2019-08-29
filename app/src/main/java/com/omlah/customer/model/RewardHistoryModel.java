package com.omlah.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RewardHistoryModel implements Serializable {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;
    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("success")
    public int success;

    public static class Data {
        @Expose
        @SerializedName("merchant_detail")
        public Merchant_detail merchant_detail;
        @Expose
        @SerializedName("logo_url")
        public String logo_url;
        @Expose
        @SerializedName("total_orders")
        public String total_orders;
        @Expose
        @SerializedName("reward_balance")
        public String total_rewards;
        @Expose
        @SerializedName("rewards_earned")
        public int rewards_earned;
        @Expose
        @SerializedName("merchant_id")
        public int merchant_id;
        @Expose
        @SerializedName("customer_id")
        public int customer_id;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Merchant_detail {
        @Expose
        @SerializedName("profile_image")
        public String profile_image;
        @Expose
        @SerializedName("phone_number")
        public String phone_number;
        @Expose
        @SerializedName("address")
        public String address;
        @Expose
        @SerializedName("email")
        public String email;
        @Expose
        @SerializedName("business_name")
        public String business_name;
        @Expose
        @SerializedName("id")
        public String id;
    }
}
