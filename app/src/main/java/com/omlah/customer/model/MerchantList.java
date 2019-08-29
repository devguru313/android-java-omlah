package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 27-09-2017.
 */

public class MerchantList implements Serializable {


    @SerializedName("success")
    public String success;
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Offers {
        @SerializedName("id")
        public String id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("offer_name")
        public String offer_name;
        @SerializedName("percentage")
        public String percentage;
        @SerializedName("description")
        public String description="";
    }

    public static class Business_category {
        @SerializedName("id")
        public String id;
        @SerializedName("category_name")
        public String category_name;
    }

    public static class Merchant_categories {
        @SerializedName("id")
        public String id;
        @SerializedName("merchant_id")
        public String merchant_id;
        @SerializedName("category_id")
        public String category_id;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("business_category")
        public Business_category business_category;
    }

    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("address")
        public String address;
        @SerializedName("latitude")
        public String latitude;
        @SerializedName("longitude")
        public String longitude;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("banner_image")
        public String banner_image;
        @SerializedName("distance")
        public String distance;
        @SerializedName("offers")
        public ArrayList<Offers> offers;
        @SerializedName("merchant_categories")
        public ArrayList<Merchant_categories> merchant_categories;
        @SerializedName("merchant_reward_settings")
        public ArrayList<Merchant_Reward_Settings> merchant_reward_settings=null;
    }

    public static class Merchant_Reward_Settings {

        @SerializedName("id")
        public String id;
        @SerializedName("reward_option")
        public String reward_option;
        @SerializedName("redeem_reward_percentage")
        public String redeem_reward_percentage;
    }
}
