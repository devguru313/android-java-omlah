package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 06-04-2018.
 */

public class ReferHistory implements Serializable {

    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("referral_detail")
        public Referral_detail referral_detail;
        @SerializedName("referral_bonus")
        public String referral_bonus;
        @SerializedName("welcome_bonus")
        public String welcome_bonus;
        @SerializedName("refer_link")
        public String refer_link;
    }

    public static class Referral_detail {
        @SerializedName("referral_histories")
        public ArrayList<Referral_histories> referral_histories;
        @SerializedName("referred_by")
        public String referred_by;
        @SerializedName("referral_code")
        public String referral_code="";
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public String id;
    }

    public static class Referral_histories {
        @SerializedName("user")
        public User user;
        @SerializedName("created")
        public String created;
        @SerializedName("status")
        public String status;
        @SerializedName("referral_bonus")
        public String referral_bonus;
        @SerializedName("referral_user_id")
        public String referral_user_id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }

    public static class User {
        @SerializedName("name")
        public String name;
        @SerializedName("phone_number")
        public String phone_number;
        @SerializedName("id")
        public String id;
    }
}
