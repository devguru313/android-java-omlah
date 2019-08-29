package com.omlah.customer.model.recharge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OperatorListResponse implements Serializable {


    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;
    @Expose
    @SerializedName("message")
    public String message;

    public static class Data {
        @Expose
        @SerializedName("logo")
        public String logo;
        @Expose
        @SerializedName("isoName")
        public String isoName;
        @Expose
        @SerializedName("mostPopularAmount")
        public String mostPopularAmount;
        @Expose
        @SerializedName("denominationType")
        public String denominationType;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("operatorId")
        public String operatorId;
    }
}
