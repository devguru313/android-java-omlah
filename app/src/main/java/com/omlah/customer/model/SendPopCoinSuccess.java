package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 07-10-2017.
 */

public class SendPopCoinSuccess implements Serializable{


    @SerializedName("success")
    public int success;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("id")
        public int id;
        @SerializedName("sender_id")
        public int sender_id;
        @SerializedName("receiver_id")
        public int receiver_id;
        @SerializedName("transaction_no")
        public String transaction_no;
        @SerializedName("transaction_type")
        public String transaction_type;
        @SerializedName("send_currency")
        public String send_currency;
        @SerializedName("receive_currency")
        public String receive_currency;
        @SerializedName("sender_currency")
        public String sender_currency;
        @SerializedName("pop_coin")
        public String pop_coin;
        @SerializedName("sender_balance_before_transaction")
        public int sender_balance_before_transaction;
        @SerializedName("sender_balance_after_transaction")
        public int sender_balance_after_transaction;
        @SerializedName("receiver_balance_before_transaction")
        public int receiver_balance_before_transaction;
        @SerializedName("receiver_balance_after_transaction")
        public int receiver_balance_after_transaction;
        @SerializedName("description")
        public String description;
        @SerializedName("transfer_type")
        public String transfer_type;
    }
}
