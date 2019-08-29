package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 24-10-2017.
 */

public class EventModelDetails implements Serializable {

    @SerializedName("data")
    public Data data;
    @SerializedName("success")
    public String success;

    public static class Data {
        @SerializedName("customer")
        public Customer customer;
        @SerializedName("events")
        public Events events;
    }

    public static class Events {
        @SerializedName("event_categories")
        public ArrayList<Event_categories> event_categories;
        @SerializedName("event_terms")
        public ArrayList<Event_terms> event_terms;
        @SerializedName("event_ticket_dates")
        public ArrayList<Event_ticket_dates> event_ticket_dates;
        @SerializedName("additioanlFees")
        public ArrayList<AdditioanlFees> additioanlFees;
        @SerializedName("user")
        public User user;
        @SerializedName("status")
        public String status;
        @SerializedName("poppay_fee_apply")
        public String poppay_fee_apply;
        @SerializedName("event_image")
        public String event_image;
        @SerializedName("service_tax")
        public String service_tax;
        @SerializedName("description")
        public String description;
        @SerializedName("end_date")
        public String end_date;
        @SerializedName("start_date")
        public String start_date;
        @SerializedName("youtube_url")
        public String youtube_url;
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
        @SerializedName("sender_currency_difference")
        public String sender_currency_difference;
    }

    public static class Event_categories {
        @SerializedName("business_category")
        public Business_category business_category;
        @SerializedName("modified")
        public String modified;
        @SerializedName("created")
        public String created;
        @SerializedName("category_id")
        public String category_id;
        @SerializedName("event_id")
        public String event_id;
        @SerializedName("id")
        public String id;
    }

    public static class Business_category {
        @SerializedName("modified")
        public String modified;
        @SerializedName("created")
        public String created;
        @SerializedName("status")
        public String status;
        @SerializedName("category_name")
        public String category_name;
        @SerializedName("id")
        public String id;
    }

    public static class Event_terms {
        @SerializedName("terms")
        public String terms;
        @SerializedName("event_id")
        public String event_id;
    }

    public static class Event_ticket_dates {
        @SerializedName("event_ticket_details")
        public ArrayList<Event_ticket_details> event_ticket_details;
        @SerializedName("event_times")
        public ArrayList<Event_times> event_times;
        @SerializedName("modified")
        public String modified;
        @SerializedName("created")
        public String created;
        @SerializedName("status")
        public String status;
        @SerializedName("event_date")
        public String event_date;
        @SerializedName("event_id")
        public String event_id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }

    public static class Event_ticket_details {
        @SerializedName("status")
        public String status;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("ticket_price")
        public String ticket_price;
        @SerializedName("ticket_coin")
        public String ticket_coin;
        @SerializedName("ticket_booked")
        public String ticket_booked;
        @SerializedName("ticket_quantity")
        public String ticket_quantity;
        @SerializedName("ticket_description")
        public String ticket_description;
        @SerializedName("ticket_title")
        public String ticket_title;
        @SerializedName("date_id")
        public String date_id;
        @SerializedName("event_id")
        public String event_id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }

    public static class Event_times {
        @SerializedName("modified")
        public String modified;
        @SerializedName("created")
        public String created;
        @SerializedName("status")
        public String status;
        @SerializedName("event_time")
        public String event_time;
        @SerializedName("date_id")
        public String date_id;
        @SerializedName("event_id")
        public String event_id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }

    public static class User {
        @SerializedName("country")
        public Country country;
        @SerializedName("business_name")
        public String business_name;
        @SerializedName("name")
        public String name;
        @SerializedName("user_no")
        public String user_no;
    }

    public static class Country {
        @SerializedName("time_zone")
        public String time_zone;
        @SerializedName("currency_symbol")
        public String currency_symbol;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("id")
        public String id;
    }

    public static class Customer {

        @SerializedName("id")
        public String id;
        @SerializedName("country")
        public CustomerCountry country;
    }

    public static class CustomerCountry {

        @SerializedName("poppay_fee")
        public PoppayFee poppay_fee;
    }

    public static class PoppayFee {

        @SerializedName("fee_amount")
        public String fee_amount;
    }

    public static class AdditioanlFees {

        @SerializedName("fee_name")
        public String fee_name;
        @SerializedName("fees")
        public String fees;
    }
}
