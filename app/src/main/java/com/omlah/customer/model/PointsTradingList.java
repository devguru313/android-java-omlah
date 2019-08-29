package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PointsTradingList implements Serializable {

    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("point_tradings")
        public ArrayList<Point_tradings> point_tradings;
    }

    public static class Point_tradings {
        @SerializedName("transaction")
        public ArrayList<Transaction> transaction=null;
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

    public static class Transaction {
        @SerializedName("status")
        public String status;
        @SerializedName("redeem_points")
        public String redeem_points;
        @SerializedName("point_trading_id")
        public String point_trading_id;
        @SerializedName("id")
        public String id;
    }
}
