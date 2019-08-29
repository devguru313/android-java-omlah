package com.omlah.customer.model.recharge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AutoFetchResponse implements Serializable {

    
    @SerializedName("data")
    public Data data=null;
   
    @SerializedName("message")
    public String message;

    public static class Data {
       
        @SerializedName("suggestedAmounts")
        public ArrayList<SuggestedAmounts> suggestedAmounts;
       
        @SerializedName("fixedAmounts")
        public ArrayList<FixedAmounts> fixedAmounts;
       
        @SerializedName("logo")
        public String logo;
       
        @SerializedName("maxAmount")
        public String maxAmount;
        
        @SerializedName("minAmount")
        public String minAmount;
       
        @SerializedName("isoName")
        public String isoName;
       
        @SerializedName("mostPopularAmount")
        public String mostPopularAmount;
       
        @SerializedName("denominationType")
        public String denominationType;
        
        @SerializedName("name")
        public String name;
        
        @SerializedName("operatorId")
        public String operatorId;
    }

    public static class SuggestedAmounts {
       
        @SerializedName("amount")
        public String amount;
    }

    public static class FixedAmounts {
  
        @SerializedName("amount")
        public String amount;
    }
}
