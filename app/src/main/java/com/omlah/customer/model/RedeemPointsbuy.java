package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RedeemPointsbuy implements Serializable {


    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("available_points")
        public String available_points;
    }
}
