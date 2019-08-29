package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 16-10-2017.
 */

public class EventModelList implements Serializable{

    @SerializedName("data")
    public ArrayList<Data> data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("user")
        public User user;
        @SerializedName("event_categories")
        public ArrayList<Event_categories> event_categories;
        @SerializedName("status")
        public String status;
        @SerializedName("event_image")
        public String event_image;
        @SerializedName("description")
        public String description;
        @SerializedName("end_date")
        public String end_date;
        @SerializedName("start_date")
        public String start_date;
        @SerializedName("event_longitude")
        public String event_longitude;
        @SerializedName("event_latitude")
        public String event_latitude;
        @SerializedName("event_address")
        public String event_address;
        @SerializedName("event_place")
        public String event_place;
        @SerializedName("event_name")
        public String event_name;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }

    public static class User {
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public String id;
    }

    public static class Event_categories {
        @SerializedName("business_category")
        public Business_category business_category;
        @SerializedName("category_id")
        public String category_id;
        @SerializedName("event_id")
        public String event_id;
        @SerializedName("id")
        public String id;
    }

    public static class Business_category {
        @SerializedName("category_name")
        public String category_name;
    }
}
