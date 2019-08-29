package com.omlah.customer.model.recharge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RechargeCountryCode implements Serializable {

    
    @SerializedName("data")
    public ArrayList<Data> data;
    @SerializedName("message")
    public String message;

    public static class Data {
      
        @SerializedName("callingCodes")
        public String callingCodes;
    
        @SerializedName("flag")
        public String flag;
     
        @SerializedName("currencyName")
        public String currencyName;
       
        @SerializedName("currencyCode")
        public String currencyCode;
       
        @SerializedName("name")
        public String name;
   
        @SerializedName("isoName")
        public String isoName;
    }
}
