package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 26-09-2017.
 */

public class LoggedUserDetails implements Serializable {

    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("logoUrl")
        public String logoUrl;
        @SerializedName("tradings")
        public Tradings tradings;
        @SerializedName("qr")
        public String qr;
        @SerializedName("user")
        public User user;
    }

    public static class Tradings {
        @SerializedName("created")
        public String created;
        @SerializedName("status")
        public String status;
        @SerializedName("redeem_points")
        public String redeem_points;
        @SerializedName("img_public_id")
        public String img_public_id;
        @SerializedName("description")
        public String description;
        @SerializedName("trade_banner")
        public String trade_banner;
        @SerializedName("title")
        public String title;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("trading_no")
        public String trading_no;
        @SerializedName("id")
        public String id;
    }

    public static class User {
        @SerializedName("country")
        public Country country;
        @SerializedName("fee_list")
        public FeeList fee_list;
        @SerializedName("deduction_type")
        public FeeList deduction_type;
        @SerializedName("customer_smart_cards")
        public ArrayList<String> customer_smart_cards;
        @SerializedName("status")
        public String status;
        @SerializedName("popcoin_balance")
        public String popcoin_balance;
        @SerializedName("account_balance")
        public String account_balance;
        @SerializedName("account_type")
        public String account_type;
        @SerializedName("country_id")
        public String country_id;
        @SerializedName("address")
        public String address;
        @SerializedName("phone_number")
        public String phone_number;
        @SerializedName("email")
        public String email;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("device_id")
        public String device_id;
        @SerializedName("name")
        public String name;
        @SerializedName("role_id")
        public String role_id;
        @SerializedName("id")
        public String id;
    }

    public static class DeductionType{

        @SerializedName("customer_EVENT")
        public String customer_EVENT;

    }

    public static class FeeList{

        @SerializedName("merchant_SCAN")
        public String merchant_SCAN;
        @SerializedName("merchant_EVENT")
        public String merchant_EVENT;
        @SerializedName("customer_SCAN")
        public String customer_SCAN;
        @SerializedName("customer_EVENT")
        public String customer_EVENT;
        @SerializedName("customer_RECHARGE")
        public String customer_RECHARGE;
        @SerializedName("customer_BILL")
        public String customer_BILL;
        @SerializedName("merchant_API")
        public String merchant_API;
    }

    public static class Country {
        @SerializedName("poppay_limit")
        public Poppay_limit poppay_limit;
        @SerializedName("poppay_fee")
        public PoppayFee poppay_fee;
        @SerializedName("wallet_transfer_fee")
        public WalletTransferFee wallet_transfer_fee;
        @SerializedName("status")
        public String status;
        @SerializedName("time_zone")
        public String time_zone;
        @SerializedName("currency_symbol")
        public String currency_symbol;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("currency_name")
        public String currency_name;
        @SerializedName("phone_code")
        public String phone_code;
        @SerializedName("country_flag")
        public String country_flag;
        @SerializedName("country_code")
        public String country_code;
        @SerializedName("country_name")
        public String country_name;
        @SerializedName("id")
        public String id;
    }

    public static class Poppay_limit {
        @SerializedName("c_m_per_month")
        public String c_m_per_month;
        @SerializedName("c_m_per_transaction")
        public String c_m_per_transaction;
        @SerializedName("c_c_per_month")
        public String c_c_per_month;
        @SerializedName("c_c_per_transaction")
        public String c_c_per_transaction;
        @SerializedName("topup_limit_per_month")
        public String topup_limit_per_month;
        @SerializedName("country_id")
        public String country_id;
        @SerializedName("id")
        public String id;
    }

    public static class PoppayFee {
        @SerializedName("fee_amount")
        public String fee_amount="0";

    }

    public static class WalletTransferFee {
        @SerializedName("fee_option")
        public String fee_option="";
        @SerializedName("fee_amount")
        public String fee_amount="0";
        @SerializedName("extra_fees")
        public String extra_fees="0";
    }
}
