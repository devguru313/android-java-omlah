package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 26-09-2017.
 */

public class BeneficiaryList implements Serializable{

    @SerializedName("success")
    public int success;
    @SerializedName("data")
    public ArrayList<Data> data=null;

    public static class Beneficiary_user {
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("profile_image")
        public String profile_image;
        @SerializedName("phone_number")
        public String phone_number;
    }

    public static class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("bene_id")
        public String bene_id;
        @SerializedName("beneficiary_name")
        public String beneficiary_name;
        @SerializedName("user")
        public User user;
        @SerializedName("beneficiary_user")
        public Beneficiary_user beneficiary_user;
    }

    public static class User {
        @SerializedName("country")
        public Country country;
    }

    public static class Country {
        @SerializedName("country_code")
        public String country_code;
    }
}
