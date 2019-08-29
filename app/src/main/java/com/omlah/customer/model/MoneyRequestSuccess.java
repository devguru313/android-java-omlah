package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 04-10-2017.
 */

public class MoneyRequestSuccess implements Serializable {


    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("created")
        public String created;
        @SerializedName("request_no")
        public String request_no;
        @SerializedName("request_id")
        public String request_id;
    }
}
