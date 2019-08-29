package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 06-10-2017.
 */

public class QRScanSuccess implements Serializable {
    
    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("merchant")
        public String merchant;
        @SerializedName("created")
        public String created;
        @SerializedName("transfer_type")
        public String transfer_type;
        @SerializedName("description")
        public String description;
        @SerializedName("sender_balance_after_transaction")
        public String sender_balance_after_transaction;
        @SerializedName("sender_balance_before_transaction")
        public String sender_balance_before_transaction;
        @SerializedName("reward_balance")
        public String reward_balance;
        @SerializedName("reward_balance_after_transaction")
        public String reward_balance_after_transaction;
        @SerializedName("reward_balance_before_transaction")
        public String reward_balance_before_transaction;
        @SerializedName("reward_offer_amount")
        public String reward_offer_amount;
        @SerializedName("reward_offer_percentage")
        public String reward_offer_percentage;
        @SerializedName("voucher_amount")
        public String voucher_amount;
        @SerializedName("voucher_percentage")
        public String voucher_percentage;
        @SerializedName("voucher_title")
        public String voucher_title;
        @SerializedName("offer_amount")
        public String offer_amount;
        @SerializedName("offer_percentage")
        public String offer_percentage;
        @SerializedName("offer_name")
        public String offer_name;
        @SerializedName("receiver_currency_difference")
        public String receiver_currency_difference;
        @SerializedName("sender_currency_difference")
        public String sender_currency_difference;
        @SerializedName("tip_amount")
        public String tip_amount;
        @SerializedName("poppay_fee")
        public String poppay_fee;
        @SerializedName("sender_amount")
        public String sender_amount;
        @SerializedName("sender_currency")
        public String sender_currency;
        @SerializedName("receive_amount")
        public String receive_amount;
        @SerializedName("receive_currency")
        public String receive_currency;
        @SerializedName("send_amount")
        public String send_amount;
        @SerializedName("send_currency")
        public String send_currency;
        @SerializedName("transaction_type")
        public String transaction_type;
        @SerializedName("customer_card_id")
        public String customer_card_id;
        @SerializedName("transaction_no")
        public String transaction_no;
        @SerializedName("receiver_id")
        public String receiver_id;
        @SerializedName("sender_id")
        public String sender_id;
        @SerializedName("subadmin_id")
        public String subadmin_id;
        @SerializedName("id")
        public String id;
    }
}
