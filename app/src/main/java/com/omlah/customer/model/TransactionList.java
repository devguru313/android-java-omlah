package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 28-09-2017.
 */

public class TransactionList implements Serializable{


    @SerializedName("success")
    public int success;
    @SerializedName("count")
    public int count;
    @SerializedName("data")
    public Data data;

    public class Data {
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
        public int status;
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
        public String account_balance;
    }

    public static class Transactions {
        @SerializedName("id")
        public String id;
        @SerializedName("status")
        public String status;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified="";
        @SerializedName("pop_coin")
        public String pop_coin;
        @SerializedName("sender_id")
        public String sender_id;
        @SerializedName("tax_amount")
        public String tax_amount;
        @SerializedName("description")
        public String description;
        @SerializedName("send_amount")
        public String send_amount;
        @SerializedName("receiver_id")
        public String receiver_id;
        @SerializedName("send_currency")
        public String send_currency;
        @SerializedName("sender_amount")
        public String sender_amount;
        @SerializedName("tax_percentage")
        public String tax_percentage;
        @SerializedName("transaction_no")
        public String transaction_no;
        @SerializedName("receive_amount")
        public String receive_amount;
        @SerializedName("sender_currency")
        public String sender_currency;
        @SerializedName("transaction_type")
        public String transaction_type;
        @SerializedName("refund_id")
        public String refund_id;
        @SerializedName("receive_currency")
        public String receive_currency;
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
        @SerializedName("receiver")
        public Receiver receiver;
        @SerializedName("sender")
        public Sender sender;
        @SerializedName("refund_transaction")
        public RefundTransaction refund_transaction = null;
    }

    public static class RefundTransaction {

        @SerializedName("refund_amount")
        public String refund_amount="";
        @SerializedName("refund_type")
        public String refund_type = "";
        @SerializedName("refund_status")
        public String refund_status = "";
        @SerializedName("refund_description")
        public String refund_description = "";

    }


}
