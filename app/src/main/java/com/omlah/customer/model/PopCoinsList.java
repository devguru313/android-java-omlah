package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 20-09-2017.
 */

public class PopCoinsList implements Serializable {


    @SerializedName("data")
    public Data data;

    public class Data {

        @SerializedName("count")
        public String count;
        @SerializedName("transactions")
        public ArrayList<Transactions> transactions;

    }

    public static class Receiver {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("email")
        public String email;
        @SerializedName("status")
        public String status;
        @SerializedName("address")
        public String address;
        @SerializedName("role_id")
        public String role_id;
        @SerializedName("commission")
        public String commission;
        @SerializedName("country_id")
        public String country_id;
        @SerializedName("phone_number")
        public String phone_number;
        @SerializedName("account_type")
        public String account_type;
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("invoice_period")
        public String invoice_period;
        @SerializedName("deposit_amount")
        public String deposit_amount;
        @SerializedName("popcoin_balance")
        public String popcoin_balance;
        @SerializedName("account_balance")
        public String account_balance;
    }

    public static class Sender {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("email")
        public String email;
        @SerializedName("status")
        public String status;
        @SerializedName("address")
        public String address;
        @SerializedName("role_id")
        public String role_id;
        @SerializedName("commission")
        public String commission;
        @SerializedName("country_id")
        public String country_id;
        @SerializedName("phone_number")
        public String phone_number;
        @SerializedName("account_type")
        public String account_type;
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("invoice_period")
        public String invoice_period;
        @SerializedName("deposit_amount")
        public String deposit_amount;
        @SerializedName("popcoin_balance")
        public String popcoin_balance;
        @SerializedName("account_balance")
        public double account_balance;
    }

    public static class Transactions {
        @SerializedName("id")
        public String id;
        @SerializedName("sender_id")
        public String sender_id;
        @SerializedName("receiver_id")
        public String receiver_id;
        @SerializedName("transaction_no")
        public String transaction_no;
        @SerializedName("transaction_type")
        public String transaction_type;
        @SerializedName("send_currency")
        public String send_currency;
        @SerializedName("send_amount")
        public String send_amount;
        @SerializedName("receive_currency")
        public String receive_currency;
        @SerializedName("receive_amount")
        public String receive_amount;
        @SerializedName("sender_currency")
        public String sender_currency;
        @SerializedName("sender_amount")
        public String sender_amount;
        @SerializedName("sender_currency_difference")
        public String sender_currency_difference;
        @SerializedName("receiver_currency_difference")
        public String receiver_currency_difference;
        @SerializedName("sender_balance_before_transaction")
        public String sender_balance_before_transaction;
        @SerializedName("sender_balance_after_transaction")
        public String sender_balance_after_transaction;
        @SerializedName("receiver_balance_before_transaction")
        public String receiver_balance_before_transaction;
        @SerializedName("receiver_balance_after_transaction")
        public String receiver_balance_after_transaction;
        @SerializedName("pop_coin")
        public String pop_coin;
        @SerializedName("tax_percentage")
        public String tax_percentage;
        @SerializedName("tax_amount")
        public String tax_amount;
        @SerializedName("description")
        public String description;
        @SerializedName("transfer_type")
        public String transfer_type;
        @SerializedName("status")
        public String status;
        @SerializedName("created")
        public String created;
        @SerializedName("receiver")
        public Receiver receiver;
        @SerializedName("sender")
        public Sender sender;
    }


}
