package com.omlah.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 03-04-2018.
 */

public class CardPayMentSuccess implements Serializable {


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
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("transfer_type")
        public String transfer_type;
        @Expose
        @SerializedName("sender_balance_after_transaction")
        public double sender_balance_after_transaction;
        @Expose
        @SerializedName("sender_balance_before_transaction")
        public double sender_balance_before_transaction;
        @Expose
        @SerializedName("offer_amount")
        public int offer_amount;
        @Expose
        @SerializedName("offer_percentage")
        public int offer_percentage;
        @Expose
        @SerializedName("offer_name")
        public String offer_name;
        @Expose
        @SerializedName("receiver_currency_difference")
        public int receiver_currency_difference;
        @Expose
        @SerializedName("sender_currency_difference")
        public int sender_currency_difference;
        @Expose
        @SerializedName("poppay_fee")
        public int poppay_fee;
        @Expose
        @SerializedName("sender_amount")
        public int sender_amount;
        @Expose
        @SerializedName("sender_currency")
        public String sender_currency;
        @Expose
        @SerializedName("receive_amount")
        public int receive_amount;
        @Expose
        @SerializedName("receive_currency")
        public String receive_currency;
        @Expose
        @SerializedName("send_amount")
        public int send_amount;
        @Expose
        @SerializedName("send_currency")
        public String send_currency;
        @Expose
        @SerializedName("transaction_type")
        public String transaction_type;
        @Expose
        @SerializedName("transaction_no")
        public String transaction_no;
        @Expose
        @SerializedName("receiver_id")
        public int receiver_id;
        @Expose
        @SerializedName("sender_id")
        public int sender_id;
        @Expose
        @SerializedName("subadmin_id")
        public int subadmin_id;
        @Expose
        @SerializedName("id")
        public String id;
    }
}
