package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 26-09-2017.
 */

public class CountryList implements Serializable {

    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("total_count")
        public String total_count;
        @SerializedName("countryList")
        public ArrayList<CountriesList> countryList;
    }

    public static class CountriesList {
        @SerializedName("status")
        public String status;
        @SerializedName("time_zone")
        public String time_zone;
        @SerializedName("currency_symbol")
        public String currency_symbol;
        @SerializedName("currency_code")
        public String currency_code;
        @SerializedName("currency_name")
        public String currency_name;
        @SerializedName("phone_code")
        public String phone_code;
        @SerializedName("country_flag")
        public String country_flag;
        @SerializedName("country_code")
        public String country_code;
        @SerializedName("country_name")
        public String country_name;
        @SerializedName("id")
        public String id;
    }
}
