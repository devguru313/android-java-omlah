package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class VoucherDetails implements Serializable{

    @SerializedName("data")
    public Data data=null;
    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("promotion_terms")
        public ArrayList<Promotion_terms> promotion_terms=null;
    }
    public static class Promotion_terms {
        @SerializedName("promotion_detail")
        public String promotion_detail="";
    }
}
