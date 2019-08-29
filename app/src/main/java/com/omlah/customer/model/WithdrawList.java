package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 09-11-2017.
 */

public class WithdrawList implements Serializable{


    @SerializedName("success")
    public int success;
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class User {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("email")
        public String email;
        @SerializedName("phone_number")
        public String phone_number;
    }

    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("reference_no")
        public String reference_no;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("withdrawal_amount")
        public String withdrawal_amount;
        @SerializedName("withdrawal_fee")
        public String withdrawal_fee;
        @SerializedName("total_amount")
        public String total_amount;
        @SerializedName("account_holder_name")
        public String account_holder_name;
        @SerializedName("bank_name")
        public String bank_name;
        @SerializedName("account_no")
        public String account_no;
        @SerializedName("status")
        public String status;
        @SerializedName("created")
        public String created;
        @SerializedName("user")
        public User user;
    }
}
