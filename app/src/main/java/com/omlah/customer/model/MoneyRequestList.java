package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 20-09-2017.
 */

public class MoneyRequestList implements Serializable{


    @SerializedName("success")
    public int success;
    @SerializedName("message")
    public String message="";
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Sender_detail {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("phone_number")
        public String phone_number;
    }

    public static class Requester {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("phone_number")
        public String phone_number;
    }

    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("request_no")
        public String request_no;
        @SerializedName("transaction_id")
        public String transaction_id;
        @SerializedName("requester_id")
        public String requester_id;
        @SerializedName("sender_id")
        public String sender_id;
        @SerializedName("request_currency")
        public String request_currency;
        @SerializedName("request_amount")
        public String request_amount;
        @SerializedName("send_currency")
        public String send_currency;
        @SerializedName("send_amount")
        public String send_amount;
        @SerializedName("description")
        public String description;
        @SerializedName("status")
        public String status;
        @SerializedName("created")
        public String created;
        @SerializedName("sender_detail")
        public Sender_detail sender_detail;
        @SerializedName("requester")
        public Requester requester;
    }
}
