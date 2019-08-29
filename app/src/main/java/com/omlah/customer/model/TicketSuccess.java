package com.omlah.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 27-10-2017.
 */

public class TicketSuccess implements Serializable {


    @SerializedName("booking_id")
    public String booking_id="";
    @SerializedName("message")
    public String message="";
}
