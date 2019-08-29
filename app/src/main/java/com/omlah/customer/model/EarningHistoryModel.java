package com.omlah.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EarningHistoryModel implements Serializable {

    @Expose
    @SerializedName("data")
    public Data data;
    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("success")
    public int success;

    public static class Data {
        @Expose
        @SerializedName("orderList")
        public ArrayList<OrderList> orderList;
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
        @SerializedName("transaction_id")
        public String transaction_id;
        @Expose
        @SerializedName("merchant_id")
        public String merchant_id;
        @Expose
        @SerializedName("customer_id")
        public String customer_id;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class OrderList {
        @Expose
        @SerializedName("transaction")
        public Transaction transaction;
        @Expose
        @SerializedName("rewards_earned")
        public String rewards_earned;
        @Expose
        @SerializedName("transaction_id")
        public String transaction_id;
        @Expose
        @SerializedName("merchant_id")
        public String merchant_id;
        @Expose
        @SerializedName("customer_id")
        public String customer_id;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class Transaction {
        @Expose
        @SerializedName("created")
        public String created;
        @Expose
        @SerializedName("tip_amount")
        public String tip_amount;
        @Expose
        @SerializedName("offer_amount")
        public String offer_amount;
        @Expose
        @SerializedName("send_amount")
        public String send_amount;
        @Expose
        @SerializedName("sender_amount")
        public String sender_amount;
        @Expose
        @SerializedName("sender_currency")
        public String sender_currency;
        @Expose
        @SerializedName("transaction_no")
        public String transaction_no;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class Merchant_detail {
        @Expose
        @SerializedName("merchant_reward_settings")
        public ArrayList<Merchant_reward_settings> merchant_reward_settings;
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

    public static class Merchant_reward_settings {
        @Expose
        @SerializedName("redeem_reward_percentage")
        public String redeem_reward_percentage;
        @Expose
        @SerializedName("offer_type")
        public String offer_type;
        @Expose
        @SerializedName("redeem_reward")
        public String redeem_reward;
        @Expose
        @SerializedName("reward_option")
        public String reward_option;
        @Expose
        @SerializedName("user_id")
        public String user_id;
        @Expose
        @SerializedName("id")
        public String id;
    }
}
