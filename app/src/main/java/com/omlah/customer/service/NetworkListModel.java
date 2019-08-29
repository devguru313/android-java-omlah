package com.omlah.customer.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class NetworkListModel implements Serializable {


    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("data")
    public Data data;
    @Expose
    @SerializedName("success")
    public int success;

    public static class Data {
        @Expose
        @SerializedName("slk")
        public ArrayList<Slk> slk;
        @Expose
        @SerializedName("in")
        public ArrayList<In> in;
        @Expose
        @SerializedName("vo")
        public ArrayList<Vo> vo;
        @Expose
        @SerializedName("et")
        public ArrayList<Et> et;
        @Expose
        @SerializedName("du")
        public ArrayList<Du> du;
        @Expose
        @SerializedName("others")
        public ArrayList<Others> others;
    }

    public static class Slk {
        @Expose
        @SerializedName("active")
        public boolean active;
        @Expose
        @SerializedName("amount_max")
        public String amount_max;
        @Expose
        @SerializedName("amount_min")
        public String amount_min;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class In {
        @Expose
        @SerializedName("active")
        public boolean active;
        @Expose
        @SerializedName("amount_max")
        public String amount_max;
        @Expose
        @SerializedName("amount_min")
        public String amount_min;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class Vo {
        @Expose
        @SerializedName("active")
        public boolean active;
        @Expose
        @SerializedName("amount_max")
        public String amount_max;
        @Expose
        @SerializedName("amount_min")
        public String amount_min;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class Et {
        @Expose
        @SerializedName("active")
        public boolean active;
        @Expose
        @SerializedName("amount_max")
        public String amount_max;
        @Expose
        @SerializedName("amount_min")
        public String amount_min;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class Du {
        @Expose
        @SerializedName("active")
        public boolean active;
        @Expose
        @SerializedName("amount_max")
        public String amount_max;
        @Expose
        @SerializedName("amount_min")
        public String amount_min;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;
    }

    public static class Others {
        @Expose
        @SerializedName("active")
        public boolean active;
        @Expose
        @SerializedName("amount_max")
        public String amount_max;
        @Expose
        @SerializedName("amount_min")
        public String amount_min;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public String id;
    }
}
