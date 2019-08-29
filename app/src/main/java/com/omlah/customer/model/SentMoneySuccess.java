package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 04-10-2017.
 */

public class SentMoneySuccess implements Serializable {


    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("created")
        public String created;
        @SerializedName("description")
        public String description;
        @SerializedName("tax_amount")
        public String tax_amount;
        @SerializedName("tax_percentage")
        public String tax_percentage;
        @SerializedName("receiver_balance_after_transaction")
        public String receiver_balance_after_transaction;
        @SerializedName("receiver_balance_before_transaction")
        public String receiver_balance_before_transaction;
        @SerializedName("sender_balance_after_transaction")
        public String sender_balance_after_transaction;
        @SerializedName("sender_balance_before_transaction")
        public String sender_balance_before_transaction;
        @SerializedName("receiver_currency_difference")
        public String receiver_currency_difference;
        @SerializedName("sender_currency_difference")
        public String sender_currency_difference;
        @SerializedName("receive_amount")
        public String receive_amount;
        @SerializedName("receive_currency")
        public String receive_currency;
        @SerializedName("send_amount")
        public String send_amount;
        @SerializedName("send_currency")
        public String send_currency;
        @SerializedName("sender_amount")
        public String sender_amount;
        @SerializedName("sender_currency")
        public String sender_currency;
        @SerializedName("transaction_type")
        public String transaction_type;
        @SerializedName("transaction_no")
        public String transaction_no;
        @SerializedName("receiver_id")
        public String receiver_id;
        @SerializedName("sender_id")
        public String sender_id;
        @SerializedName("id")
        public String id;
    }
}
