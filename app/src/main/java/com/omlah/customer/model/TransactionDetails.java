package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 28-09-2017.
 */

public class TransactionDetails implements Serializable{
    
    @SerializedName("success")
    public String success;
    @SerializedName("data")
    public Data data;

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
        public String account_balance;
    }

    public static class Data {
        @SerializedName("additional_fee_name")
        public String additional_fee_name;
        @SerializedName("additional_fees")
        public String additional_fees;
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
        @SerializedName("voucher_percentage")
        public String voucher_percentage="0";
        @SerializedName("voucher_amount")
        public String voucher_amount="0";
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
        @SerializedName("offer_name")
        public String offer_name;
        @SerializedName("offer_percentage")
        public String offer_percentage;
        @SerializedName("offer_amount")
        public String offer_amount;
        @SerializedName("reward_offer_percentage")
        public String reward_offer_percentage="0";
        @SerializedName("reward_offer_amount")
        public String reward_offer_amount="0";
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
        @SerializedName("poppay_fee")
        public String poppay_fee;
        @SerializedName("tip_amount")
        public String tip_amount=null;
        @SerializedName("description")
        public String description;
        @SerializedName("transfer_type")
        public String transfer_type;
        @SerializedName("status")
        public String status;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("receiver")
        public Receiver receiver;
        @SerializedName("sender")
        public Sender sender;
        @SerializedName("refund_transaction")
        public RefundTransaction refund_transaction = null;
        @SerializedName("reward_transactions")
        public ArrayList<RewardTransactions> reward_transactions=null;
    }

    public static class RewardTransactions {

        @SerializedName("rewards_earned")
        public String rewards_earned="0";
        @SerializedName("discount_amount")
        public String discount_amount="0";
    }

    public static class RefundTransaction {

        @SerializedName("refund_amount")
        public String refund_amount="";
        @SerializedName("refund_type")
        public String refund_type = "";
        @SerializedName("refund_status")
        public String refund_status = "";
    }
}
