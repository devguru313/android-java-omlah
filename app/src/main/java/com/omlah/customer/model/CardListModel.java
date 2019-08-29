package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 03-04-2018.
 */

public class CardListModel implements Serializable {
    
    @SerializedName("payment_settings")
    public Payment_settings payment_settings;
    @SerializedName("data")
    public ArrayList<Data> data;
    @SerializedName("message")
    public String message;

    public static class Payment_settings {
        @SerializedName("stripe_publisherkey_test")
        public String stripe_publisherkey_test;
        @SerializedName("stripe_secretkey_test")
        public String stripe_secretkey_test;
        @SerializedName("stripe_publisherkey_live")
        public String stripe_publisherkey_live;
        @SerializedName("stripe_secretkey_live")
        public String stripe_secretkey_live;
        @SerializedName("stripe_mode")
        public String stripe_mode;
    }

    public static class Data {
        @SerializedName("status")
        public String status;
        @SerializedName("card_validity")
        public String card_validity;
        @SerializedName("payment_type")
        public String payment_type;
        @SerializedName("card_type")
        public String card_type;
        @SerializedName("card_no")
        public String card_no;
        @SerializedName("stripe_customer_id")
        public String stripe_customer_id;
        @SerializedName("stripe_token_id")
        public String stripe_token_id;
        @SerializedName("id")
        public String id;
    }
}
