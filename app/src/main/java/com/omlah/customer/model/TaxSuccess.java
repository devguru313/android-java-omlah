package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 07-10-2017.
 */

public class TaxSuccess implements Serializable {


    @SerializedName("tax")
    public Tax tax;
    @SerializedName("customerFees")
    public CustomerFees customerFees;
    @SerializedName("success")
    public int success;

    public static class Tax {
        @SerializedName("taxAmount")
        public String taxAmount;
        @SerializedName("taxPercentage")
        public String taxPercentage;
    }

    public static class CustomerFees {
        @SerializedName("fee_amount")
        public String fee_amount;
        @SerializedName("extra_fees")
        public String extra_fees;
        @SerializedName("fee_option")
        public String fee_option;
    }
}
