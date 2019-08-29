package com.omlah.customer.model.recharge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RechargeSuccessResponse implements Serializable {


    @SerializedName("data")
    public Data data;

    @SerializedName("message")
    public String message;

    public static class Data {
        @SerializedName("transaction_id")
        public String transaction_id;

        @SerializedName("receiver_id")
        public String receiver_id;

        @SerializedName("sender_id")
        public String sender_id;

        @SerializedName("serviceProvider")
        public String serviceProvider;

        @SerializedName("serviceImage")
        public String serviceImage;

        @SerializedName("transactionDate")
        public String transactionDate;

        @SerializedName("pinDetail")
        public PinDetail pinDetail;

        @SerializedName("customIdentifier")
        public String customIdentifier;

        @SerializedName("deliveredAmountCurrencyCode")
        public String deliveredAmountCurrencyCode;

        @SerializedName("deliveredAmount")
        public String deliveredAmount;

        @SerializedName("requestedAmountCurrencyCode")
        public String requestedAmountCurrencyCode;

        @SerializedName("requestedAmount")
        public String requestedAmount;

        @SerializedName("operatorId")
        public String operatorId;

        @SerializedName("countryCode")
        public String countryCode;

        @SerializedName("senderPhone")
        public String senderPhone;

        @SerializedName("recipientPhone")
        public String recipientPhone;

        @SerializedName("transactionId")
        public String transactionId;
    }

    public static class PinDetail {

        @SerializedName("validity")
        public String validity;

        @SerializedName("ivr")
        public String ivr;

        @SerializedName("code")
        public String code;

        @SerializedName("info3")
        public String info3;

        @SerializedName("info2")
        public String info2;

        @SerializedName("info1")
        public String info1;

        @SerializedName("serial")
        public String serial;
    }
}
