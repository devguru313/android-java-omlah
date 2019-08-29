package com.omlah.customer.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.CheckPhoneNumber;
import com.omlah.customer.model.CountryList;
import com.omlah.customer.model.SignUpDetail;
import com.omlah.customer.model.UserDetails;
import com.omlah.customer.urls.Constents;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class ServerRequest {

    private static RequestQueue queue = null;
    public static ServerRequest serverRequest = null;
    public static ServerListener serverListener = null;
    LoginSession loginSession;
    Map<String, String> param;
    RequestID requestID;
    Context context;
    String passingDtat_forGET;
    Activity activity;

    public ServerRequest(Context context) {

        if (queue == null) {

            queue = Volley.newRequestQueue(context);
        }
        this.context = context;
    }

    public static ServerRequest getInstance(Context context) {

        if (serverRequest == null) {

            serverRequest = new ServerRequest(context);
        }

        return serverRequest;
    }

    public void createRequest(ServerListener serverListener1, Map<String, String> params, RequestID requestid,String method,String data) {

        param = params;
        loginSession = LoginSession.getInstance(activity);
        serverListener = serverListener1;
        requestID = requestid;
        passingDtat_forGET = data;
        Log.e("PASSING DATA", "" + param);
        String METHOD = method;

        if(METHOD.equalsIgnoreCase("GET")){

            StringRequest postRequest = new StringRequest(Request.Method.GET, getURL(),

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("RESPONSE", "" + response);
                            Log.e("GET_URL", "" + getURL());

                            if (response != null) {

                                try {

                                    JSONObject jsonObject = new JSONObject(response);

                                    switch (requestID) {

                                        case REQ_EMAIL_CHECK:
                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);
                                            break;

                                        case REQ_COUNTRYLIST:

                                            serverListener.onSuccess(getJsonModelType(response), requestID);
                                            break;

                                        default:
                                            serverListener.onSuccess(getJsonModelType(response), requestID);

                                            break;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    serverListener.onFailure("Please try again!!", requestID);
                                }
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            try {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.data != null) {
                                    if (networkResponse.statusCode == 401) {
                                        loginSession.logout();
                                    } else {
                                        String jsonError = new String(networkResponse.data);
                                        Log.e("jsonError", jsonError);
                                        JSONObject jsonObject = new JSONObject(jsonError);
                                        Log.e("ErrorListener", "" + jsonObject.getJSONObject("error").getString("message"));
                                        serverListener.onFailure(jsonObject.getJSONObject("error").getString("message"), requestID);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                serverListener.onFailure("Please try again!!", requestID);
                            }

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    return param;
                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(postRequest);

        }else{

            StringRequest postRequest = new StringRequest(Request.Method.POST, getURL(),

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("RESPONSE", "" + response);

                            if (response != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    switch (requestID) {

                                        case REQ_LOGIN:

                                            serverListener.onSuccess(getJsonModelType(response), requestID);
                                            break;

                                        case REQ_SIGNUP:

                                            serverListener.onSuccess(getJsonModelType(response), requestID);
                                            break;

                                        case REQ_FORGET_PASSWORD:
                                            serverListener.onSuccess(jsonObject.getJSONObject("data").getString("otp"), requestID);
                                            break;

                                        case REQ_SET_NEW_PASSWORD:
                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);
                                            break;

                                        default:
                                            serverListener.onSuccess(getJsonModelType(response), requestID);

                                            break;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    serverListener.onFailure("Please try again!!", requestID);
                                }
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            try {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.data != null) {
                                    if (networkResponse.statusCode == 401) {
                                        loginSession.logout();
                                    } else {
                                        String jsonError = new String(networkResponse.data);
                                        JSONObject jsonObject = new JSONObject(jsonError);
                                        if (jsonObject.has("error")) {
                                            serverListener.onFailure(jsonObject.getJSONObject("error").getString("message"), requestID);
                                        }else{
                                            serverListener.onFailure(jsonObject.getString("message"), requestID);
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                serverListener.onFailure("Please try again!!", requestID);
                            }
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    return param;
                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(postRequest);

        }
    }

    //Get url based on requestID
    public String getURL() {
        String URL = null;

        switch (requestID) {

            case REQ_NUMBER_CHECK:
                URL = Constents.NUMBER_CHECK_URL+passingDtat_forGET;
                break;

            case REQ_EMAIL_CHECK:
                URL = Constents.EMAIL_CHECK_URL + passingDtat_forGET;
                break;

            case REQ_LOGIN:
                URL = Constents.LOGIN_URL;
                break;

            case REQ_COUNTRYLIST:
                URL = Constents.COUNTRY_LIST;
                break;

            case REQ_SIGNUP:
                URL = Constents.SIGNUP_URL;
                break;

            case REQ_FORGET_PASSWORD:
                URL = Constents.FORGET_PASSWORD_OTP;
                break;

            case REQ_SET_NEW_PASSWORD:
                URL = Constents.NEW_PASSWORD;
                break;

            default:
                URL = Constents.USER_URL;
                break;

        }

        return URL;
    }


    /**
     * Method to give appropriate class to parse the JSON data
     *
     * @return Class based on request ID ser ver class edited s
     */
    @SuppressWarnings("rawtypes")
    private Class getModel() {
        Class model = null;
        switch (requestID) {

            case REQ_NUMBER_CHECK:
                model = CheckPhoneNumber.class;
                break;

            case REQ_LOGIN:
                model = UserDetails.class;
                break;

            case REQ_COUNTRYLIST:
                model = CountryList.class;
                break;

            case REQ_SIGNUP:
                model = SignUpDetail.class;
                break;

            case REQ_FORGET_PASSWORD:
                model = String.class;
                break;

            case REQ_SET_NEW_PASSWORD:
                model = String.class;
                break;

            default:
                model = String.class;
                break;
        }

        return model;
    }

    private Object getJsonModelType(String data) {
        Object result = null;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(data, getModel());
            Log.e("success", "" + result);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ServerRequestHandler ", "" + e);
        }
        return result;
    }

}
