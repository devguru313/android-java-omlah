package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckPhoneNumber implements Serializable{

    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("user")
        public boolean user=true;
        @SerializedName("userDetails")
        public UserDetails userDetails;
    }

    public static class UserDetails {

        @SerializedName("name")
        public String name;
        @SerializedName("profile_image")
        public String profile_image="";
        @SerializedName("country")
        public Country country;
    }

    public static class Country {

        @SerializedName("phone_code")
        public String phone_code;
    }
}
