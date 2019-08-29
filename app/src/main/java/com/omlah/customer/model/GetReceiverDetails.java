package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetReceiverDetails implements Serializable {

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("user")
        public User user;
    }

    public class User {
        @SerializedName("name")
        public String name;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("country")
        public Country country;
    }

    public class Country {
        @SerializedName("phone_code")
        public String phone_code;
    }
}
