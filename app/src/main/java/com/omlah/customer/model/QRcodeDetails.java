package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 06-10-2017.
 */

public class QRcodeDetails implements Serializable {


    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("merchantAdditionalFee")
        public ArrayList<MerchantAdditionalFee> merchantAdditionalFee;
        @SerializedName("sender_currency_difference")
        public String sender_currency_difference;
        @SerializedName("poppay_fee")
        public String poppay_fee;
        @SerializedName("subUser")
        public String subUser;
        @SerializedName("spent_reward")
        public String spent_reward;
        @SerializedName("future_reward")
        public String future_reward;
        @SerializedName("reward_offer_amount")
        public String reward_offer_amount;
        @SerializedName("reward_offer_percentage")
        public String reward_offer_percentage;
        @SerializedName("sender_reward_balance")
        public String sender_reward_balance;
        @SerializedName("balance")
        public String balance;
        @SerializedName("voucher_amount")
        public String voucher_amount;
        @SerializedName("voucher_description")
        public String voucher_description;
        @SerializedName("voucher_percentage")
        public String voucher_percentage;
        @SerializedName("voucher_id")
        public String voucher_id;
        @SerializedName("voucher_title")
        public String voucher_title;
        @SerializedName("description")
        public String description;
        @SerializedName("offer_name")
        public String offer_name;
        @SerializedName("offer_percentage")
        public String offer_percentage;
        @SerializedName("offer_amount")
        public String offer_amount;
        @SerializedName("receive_amount")
        public String receive_amount;
        @SerializedName("receive_currency_symbol")
        public String receive_currency_symbol;
        @SerializedName("receive_currency")
        public String receive_currency;
        @SerializedName("amount")
        public String amount;
        @SerializedName("send_amount")
        public String send_amount;
        @SerializedName("send_currency_symbol")
        public String send_currency_symbol;
        @SerializedName("send_currency")
        public String send_currency;
        @SerializedName("merchant_profile")
        public String merchant_profile;
        @SerializedName("merchant_name")
        public String merchant_name;
        @SerializedName("id")
        public String id;
    }

    public static class MerchantAdditionalFee {

        @SerializedName("fee_name")
        public String fee_name;
        @SerializedName("fees")
        public String fees;
    }
}
