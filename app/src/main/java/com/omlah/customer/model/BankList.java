package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 09-11-2017.
 */

public class BankList implements Serializable{

    @SerializedName("success")
    public int success;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public ArrayList<Data> data;
    @SerializedName("withdrawal_fee")
    public String withdrawal_fee;
    @SerializedName("withdrawal_fee_option")
    public String withdrawal_fee_option;
    @SerializedName("withdrawal_extra_fee")
    public String withdrawal_extra_fee;

    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("account_holder_name")
        public String account_holder_name;
        @SerializedName("bank_name")
        public String bank_name;
        @SerializedName("account_no")
        public String account_no;
    }
}
