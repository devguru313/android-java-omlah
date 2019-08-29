package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 27-10-2017.
 */

public class TicketBookingDetails implements Serializable {

    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("ascii")
        public String ascii;
        @SerializedName("booking_details")
        public Booking_details booking_details;
    }

    public static class Booking_details {
        @SerializedName("event_image_url")
        public String event_image_url;
        @SerializedName("booking_carts")
        public ArrayList<Booking_carts> booking_carts;
        @SerializedName("transaction")
        public Transaction transaction;
        @SerializedName("verified")
        public String verified;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("total_coin")
        public String total_coin;
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
        @SerializedName("booking_no")
        public String booking_no;
        @SerializedName("id")
        public String id;
    }

    public static class Booking_carts {
        @SerializedName("modified")
        public String modified;
        @SerializedName("created")
        public String created;
        @SerializedName("total_coin")
        public String total_coin;
        @SerializedName("ticket_coin")
        public String ticket_coin;
        @SerializedName("total_price")
        public String total_price;
        @SerializedName("ticket_price")
        public String ticket_price;
        @SerializedName("ticket_quantity")
        public String ticket_quantity;
        @SerializedName("ticket_title")
        public String ticket_title;
        @SerializedName("event_details_id")
        public String event_details_id;
        @SerializedName("booking_id")
        public String booking_id;
        @SerializedName("id")
        public String id;
    }

    public static class Transaction {
        @SerializedName("poppay_fee")
        public String poppay_fee;
        @SerializedName("sender_currency_difference")
        public String sender_currency_difference;
        @SerializedName("receiver_currency_difference")
        public String receiver_currency_difference;
    }
}
