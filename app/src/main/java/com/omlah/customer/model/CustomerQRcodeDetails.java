package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 03-01-2018.
 */

public class CustomerQRcodeDetails implements Serializable {


    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("merchantAdditionalFee")
        public ArrayList<MerchantAdditionalFee> merchantAdditionalFee=null;
        @SerializedName("poppay_fee")
        public Poppay_fee poppay_fee = null;
        @SerializedName("reward_settings")
        public Reward_settings reward_settings=null;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("name")
        public String name;
        @SerializedName("role")
        public String role;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("phone_number")
        public String phone_number;
        @SerializedName("sender_currency_difference")
        public String sender_currency_difference;
    }

    public static class Reward_settings {
        @SerializedName("reward_balance")
        public String reward_balance;
        @SerializedName("redeem_reward_percentage")
        public String redeem_reward_percentage;
        @SerializedName("offer_type")
        public String offer_type;
        @SerializedName("redeem_reward")
        public String redeem_reward;
        @SerializedName("rewards")
        public String rewards;
        @SerializedName("reward_option")
        public String reward_option;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }

    public static class Poppay_fee {
        @SerializedName("fee_amount")
        public String fee_amount="0";
        @SerializedName("fee_option")
        public String fee_option="";
        @SerializedName("extra_fees")
        public String extra_fees="0";
        @SerializedName("deduction_type")
        public String deduction_type="";
    }

    public static class MerchantAdditionalFee {

        @SerializedName("fee_name")
        public String fee_name;
        @SerializedName("fees")
        public String fees;
    }
}
