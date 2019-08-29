package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 28-10-2017.
 */

public class EventList implements Serializable{

    @SerializedName("data")
    public Data data=null;

    public static class Data {
        @SerializedName("ascii")
        public String ascii;
        @SerializedName("count")
        public String count;
        @SerializedName("booking")
        public ArrayList<Booking> booking;
    }

    public static class Booking {
        @SerializedName("event_tab")
        public String event_tab;
        @SerializedName("merchant")
        public Merchant merchant;
        @SerializedName("created")
        public String created;
        @SerializedName("verified")
        public String verified;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("grand_total")
        public String grand_total;
        @SerializedName("service_tax_amount")
        public String service_tax_amount;
        @SerializedName("service_tax")
        public String service_tax;
        @SerializedName("sub_total")
        public String sub_total;
        @SerializedName("booking_date")
        public String booking_date;
        @SerializedName("event_time")
        public String event_time;
        @SerializedName("event_image")
        public String event_image;
        @SerializedName("event_address")
        public String event_address;
        @SerializedName("event_place")
        public String event_place;
        @SerializedName("event_name")
        public String event_name;
        @SerializedName("booking_no")
        public String booking_no;
        @SerializedName("transaction_id")
        public String transaction_id;
        @SerializedName("merchant_id")
        public String merchant_id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }

    public static class Merchant {
        @SerializedName("country")
        public Country country;
        @SerializedName("email")
        public String email;
        @SerializedName("phone_number")
        public String phone_number;
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public String id;
    }

    public static class Country {
        @SerializedName("time_zone")
        public String time_zone;
        @SerializedName("id")
        public String id;
    }
}
