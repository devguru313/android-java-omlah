package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MyVoucherCode implements Serializable {


    @SerializedName("data")
    public ArrayList<Data> data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("promotion")
        public Promotion promotion=null;
        @SerializedName("status")
        public String status;
        @SerializedName("promo_code")
        public String promo_code;
        @SerializedName("promotion_id")
        public String promotion_id;
        @SerializedName("id")
        public String id;
    }

    public static class Promotion {
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
}
