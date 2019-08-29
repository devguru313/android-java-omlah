package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 06-09-2017.
 */

public class FeedModelList implements Serializable{


    @SerializedName("success")
    public int success;
    @SerializedName("data")
    public ArrayList<Data> data;
    @SerializedName("url")
    public String url;

    public static class User {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("business_name")
        public String business_name;
    }

    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("media")
        public String media;
        @SerializedName("media_type")
        public String media_type;
        @SerializedName("description")
        public String description;
        @SerializedName("status")
        public String status;
        @SerializedName("created")
        public String created;
        @SerializedName("user")
        public User user;
    }
}
