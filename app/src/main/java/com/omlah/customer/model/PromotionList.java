package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PromotionList implements Serializable {

    @SerializedName("data")
    public Data data=null;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("promotions")
        public List<Promotions> promotions;
        @SerializedName("total_count")
        public String total_count;

    }

    public static class Promotions {
        @SerializedName("transaction")
        public ArrayList<Transaction> transaction=null;
        @SerializedName("user")
        public User user;
        @SerializedName("modified")
        public String modified;
        @SerializedName("created")
        public String created;
        @SerializedName("offer_price")
        public String offer_price;
        @SerializedName("title")
        public String title;
        @SerializedName("status")
        public String status;
        @SerializedName("redeem_type")
        public String redeem_type;
        @SerializedName("description")
        public String description;
        @SerializedName("img_public_id")
        public String img_public_id;
        @SerializedName("promotion_banner")
        public String promotion_banner;
        @SerializedName("promotion_no")
        public String promotion_no;
        @SerializedName("id")
        public String id;
    }

    public static class Transaction {
        @SerializedName("status")
        public String status="";
        @SerializedName("promo_code")
        public String promo_code="";
    }

    public static class User {
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("id")
        public String id;
    }
}
