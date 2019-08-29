package com.omlah.customer.service;


public interface ServerListener {

    public void onSuccess(Object result, RequestID requestID);

    public void onFailure(String error, RequestID requestID);

}
