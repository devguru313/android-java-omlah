package com.omlah.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 03-04-2018.
 */

public class CardErrorModel implements Serializable {

    @Expose
    @SerializedName("payment_settings")
    public Payment_settings payment_settings;
    @Expose
    @SerializedName("logout")
    public int logout;
    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("success")
    public int success;

    public static class Payment_settings {
        @Expose
        @SerializedName("stripe_publisherkey_test")
        public String stripe_publisherkey_test;
        @Expose
        @SerializedName("stripe_secretkey_test")
        public String stripe_secretkey_test;
        @Expose
        @SerializedName("stripe_publisherkey_live")
        public String stripe_publisherkey_live;
        @Expose
        @SerializedName("stripe_secretkey_live")
        public String stripe_secretkey_live;
        @Expose
        @SerializedName("stripe_mode")
        public String stripe_mode;
    }
}
