    package com.omlah.customer.service;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.omlah.customer.model.CardListModel;
import com.omlah.customer.model.EarningHistoryModel;
import com.omlah.customer.model.GetReceiverDetails;
import com.omlah.customer.model.MerchantList;
import com.omlah.customer.model.MyVoucherCode;
import com.omlah.customer.model.PointsTradingList;
import com.omlah.customer.model.PromotionList;
import com.omlah.customer.model.RechargeHistory;
import com.omlah.customer.model.RedeemPointsbuy;
import com.omlah.customer.model.ReferHistory;
import com.omlah.customer.model.RewardHistoryModel;
import com.google.gson.Gson;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.BankList;
import com.omlah.customer.model.BeneficiaryList;
import com.omlah.customer.model.CustomerQRcodeDetails;
import com.omlah.customer.model.EventList;
import com.omlah.customer.model.EventModelDetails;
import com.omlah.customer.model.EventModelList;
import com.omlah.customer.model.FeedModelList;
import com.omlah.customer.model.LoggedUserDetails;
import com.omlah.customer.model.MoneyRequestList;
import com.omlah.customer.model.MoneyRequestSuccess;
import com.omlah.customer.model.PopCoinsList;
import com.omlah.customer.model.QRScanSuccess;
import com.omlah.customer.model.QRcodeDetails;
import com.omlah.customer.model.SellerList;
import com.omlah.customer.model.SendPopCoinSuccess;
import com.omlah.customer.model.SentMoneySuccess;
import com.omlah.customer.model.TaxSuccess;
import com.omlah.customer.model.TicketBookingDetails;
import com.omlah.customer.model.TicketSuccess;
import com.omlah.customer.model.TransactionDetails;
import com.omlah.customer.model.TransactionList;
import com.omlah.customer.model.VoucherDetails;
import com.omlah.customer.model.WithdrawList;
import com.omlah.customer.model.recharge.AutoFetchResponse;
import com.omlah.customer.model.recharge.OperatorListResponse;
import com.omlah.customer.model.recharge.RechargeSuccessResponse;
import com.omlah.customer.urls.Constents;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ServerRequestwithHeader {

    /***************************   Declare common classes and objects   **********************************/
    public static final String TAG = ServerRequestwithHeader.class
            .getSimpleName();

    private static RequestQueue queue = null;
    public static ServerRequestwithHeader serverRequest=null;
    public static ServerListener serverListener=null;
    LoginSession loginSession;
    Utility utility;
    Map<String,String> param;
    RequestID requestID;
    Context context;
    Activity activity;
    String METHOD="",passingDtat_forGET;


    /********************************* Initialize queue ****************************************/
    public ServerRequestwithHeader(Context context) {

        if (queue == null) {

            queue = Volley.newRequestQueue(context);

        }
        this.context = context;
    }

    /********************************* Initialize Server request ****************************************/
    public static ServerRequestwithHeader getInstance(Context context) {

        if (serverRequest == null) {

            serverRequest = new ServerRequestwithHeader(context);
        }

        return serverRequest;
    }

    /********************************* Create String Request ****************************************/
    public void createRequest(final ServerListener serverListener1, Map<String,String> params, final RequestID requestid, String method, String Data) {


        param = params;
        serverListener     = serverListener1;
        loginSession       = LoginSession.getInstance(activity);
        utility            = Utility.getInstance(activity);
        requestID          = requestid;
        passingDtat_forGET = Data;
        METHOD             = method;

        Log.e("PASSING TO SERVER", "" + param);


        //POST METHOD
        /*************************/
        if(METHOD.equalsIgnoreCase("POST")){

            StringRequest postRequest = new StringRequest(Request.Method.POST, getURL(),

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("RESPONSE FROM SERVER", "" + response);
                            Log.e("URL-POST", ""+getURL());

                            if (response != null) {

                                try {

                                    JSONObject jsonObject = new JSONObject(response);

                                    switch (requestID) {

                                        case REQ_ADD_BENEFICIARY:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_STATUS_UPDATE:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_FEED_HIDE:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_PROFILE_IMAGE_IPLOAD:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_CURRENCY_CONVERSION:

                                            serverListener.onSuccess(jsonObject.getJSONObject("data").getString("amount"), requestID);

                                            break;

                                        case REQ_RESEND_OTP:

                                            serverListener.onSuccess(jsonObject.getJSONObject("data").getString("otp"), requestID);

                                            break;

                                        case REQ_VERIFY_OTP:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_ADD_PASSCODE:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_VERIFY_PASSCODE:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_WITHDRAW_AMOUNT:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_CHANGE_PASSWORD:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_LOAD_MONEY:

                                            serverListener.onSuccess(jsonObject.getJSONObject("data").getString("sender_balance_after_transaction"), requestID);

                                            break;

                                        case REQ_ADD_CARD:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_POST_RECHARGE:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_VOCUHER_CODE:

                                            serverListener.onSuccess(jsonObject.getString("data"), requestID);

                                            break;

                                        case REQ_ADD_BANK_ACCOUNT:

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
                                    if(networkResponse.statusCode == 401){
                                        logoutMethodCall();
                                    }else{
                                        String jsonError = new String(networkResponse.data);
                                        JSONObject jsonObject = new JSONObject(jsonError);
                                        Log.e("jsonError",jsonError);
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
                protected Map<String,String> getParams() {
                    Map<String, String> param2 = new HashMap<String, String>();

                    param2 = param;

                    return param2;

                }

                @Override
                public  Map<String, String> getHeaders() throws AuthFailureError
                {

                    final Map<String, String> param2 = new HashMap<>();
                    // param2.put("X-Consumer-Custom-ID",loginSession.getToken());
                    param2.put("X-Consumer-Custom-ID",loginSession.getUser_id());
                    param2.put("platform","android");
                    param2.put("auth_token",loginSession.getToken());
                    Log.e("HEADER",param2.toString());
                    return param2;


                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            postRequest.setTag(TextUtils.isEmpty(requestID.toString()) ? TAG : requestID);
            queue.add(postRequest);

            //DELETE METHOD
            /*************************/
        }else if(METHOD.equalsIgnoreCase("DELETE")){

            StringRequest postRequest = new StringRequest(Request.Method.DELETE, getURL(),

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("RESPONSE FROM SERVER", "" + response);
                            Log.e("URL-DELETE", ""+getURL());

                            if (response != null) {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);

                                    switch (requestID) {

                                        case REQ_LOGOUT:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_DELETE_BENEFICIARY:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        case REQ_CARD_DELETE:

                                            serverListener.onSuccess(jsonObject.getString("message"), requestID);

                                            break;

                                        default:

                                            serverListener.onSuccess(getJsonModelType(response), requestID);

                                            break;

                                    }

                                }
                                catch (JSONException e)
                                {
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
                                Log.e("networkResponse",""+networkResponse.data);
                                if (networkResponse != null && networkResponse.data != null) {
                                    if(networkResponse.statusCode == 401){
                                        logoutMethodCall();
                                    }else{
                                        String jsonError = new String(networkResponse.data);
                                        Log.e("jsonError",jsonError);
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
                protected Map<String,String> getParams() {
                    Map<String, String> param2 = new HashMap<String, String>();

                    param2 = param;

                    return param2;

                }

                @Override
                public  Map<String, String> getHeaders() throws AuthFailureError
                {

                    final Map<String, String> param2 = new HashMap<>();
                    param2.put("X-Consumer-Custom-ID",loginSession.getUser_id());
                    param2.put("platform","android");
                    param2.put("Content-Type","application/json");
                    param2.put("auth_token",loginSession.getToken());

                    Log.e("header",param2.toString());
                    return param2;

                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            postRequest.setTag(TextUtils.isEmpty(requestID.toString()) ? TAG : requestID);
            queue.add(postRequest);


            //GET METHOD
            /*************************/
        }else{

            StringRequest postRequest = new StringRequest(Request.Method.GET, getURL(),

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("RESPONSE FROM SERVER", "" + response);
                            Log.e("URL-GET", ""+getURL());


                            if (response != null) {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);

                                        switch (requestID) {

                                            case REQ_MONEYREQUEST_REJECT:
                                                serverListener.onSuccess(jsonObject.getString("message"), requestID);
                                                break;

                                            default:
                                                serverListener.onSuccess(getJsonModelType(response), requestID);
                                                break;
                                        }


                                }
                                catch (JSONException e)
                                {
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
                                    if(networkResponse.statusCode == 401){
                                        logoutMethodCall();
                                    }else{

                                        String jsonError = new String(networkResponse.data);
                                        Log.e("jsonError",jsonError);
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
                protected Map<String,String> getParams() {
                    Map<String, String> param2 = new HashMap<String, String>();

                    param2 = param;

                    return param2;

                }

                @Override
                public  Map<String, String> getHeaders() throws AuthFailureError
                {
                    final Map<String, String> param2 = new HashMap<>();

                    param2.put("X-Consumer-Custom-ID",loginSession.getUser_id());
                    param2.put("platform","android");
                    param2.put("Content-Type","application/json");
                    param2.put("auth_token",loginSession.getToken());

                    Log.e("header",param2.toString());
                    return param2;

                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            postRequest.setTag(TextUtils.isEmpty(requestID.toString()) ? TAG : requestID);
            queue.add(postRequest);

        }


    }

    //Get url based on requestID
    public String getURL()
    {
        String URL=null;

        switch (requestID)
        {
            case REQ_BENEFICIARY_LIST:

                URL = Constents.BENEFICIARY_LIST;

                break;

            case REQ_ADD_BENEFICIARY:

                URL = Constents.BENEFICIARY_ADD;

                break;

            case REQ_USER_PROFILE:

                URL = Constents.USER_PROFILE;

                break;

            case REQ_AMOUNT_TAX:

                URL = Constents.AMOUNT_TAX_CHECK + passingDtat_forGET;
                break;

            case REQ_PAYMONEY:
                URL = Constents.PAYMONEY;
                break;

            case REQ_REQEST_MONEY:
                URL = Constents.REQEST_MONEY;
                break;

            case REQ_MONEYREQUEST_LIST:
                URL = Constents.REQEST_MONEY_LIST;
                break;

            case REQ_TRANSACTION_LIST:
                URL = Constents.TRANSACTION_LIST;
                break;

            case REQ_POPCOINS_LIST:
                URL = Constents.TRANSACTION_LIST;
                break;

            case REQ_TRANSACTION_DEATILS:
                URL = Constents.TRANSACTION_DETAILS+passingDtat_forGET;
                break;

            case REQ_MONEYREQUEST_REJECT:

                URL = Constents.MONEY_REQUEST_CANCEL+"/"+passingDtat_forGET;

                break;

            case REQ_STATUS_UPDATE:

                URL = Constents.REQUEST_STATUS_UPDATE;

                break;

            case REQ_DELETE_BENEFICIARY:

                URL = Constents.BENEFICIARY_DELETE+passingDtat_forGET;
                break;

            case REQ_QR_DETAILS:

                URL = Constents.QR_DETAILS;

                break;

            case REQ_CUSTOMER_QR_DETAILS:

                URL = Constents.CUSTOMER_QR_DETAILS;

                break;

            case REQ_SCAN_AND_PAY:

                URL = Constents.QR_SCAN_AND_PAY;

                break;

            case REQ_SEND_COINS:

                URL = Constents.SEND_COINS;

                break;

            case REQ_FEED:

                URL = Constents.FEED;

                break;

            case REQ_FEED_HIDE:

                URL = Constents.FEED_HIDE;

                break;

            case REQ_LOGOUT:

                URL = Constents.LOGOUT_URL;

                break;

            case REQ_EVENT_LIST:

                URL = Constents.EVENT_LIST_URL;

                break;

            case REQ_MY_VOUCHERCODE:
                URL = Constents.MY_VOUCHER_CODE;
                break;

            case REQ_EVENT_DETAILS:

                URL = Constents.EVENT_DETAILS_URL+passingDtat_forGET;

                break;

            case REQ_EVENT_BOOKING:

                URL = Constents.EVENT_BOOK;

                break;

            case REQ_BOOKING_DETAILS:

                URL = Constents.EVENT_BOOKING_DETAIL+passingDtat_forGET;

                break;

            case REQ_BOOKING_EVENT_LIST:

                URL = Constents.EVENT_BOOKING_LIST;

                break;

            case REQ_PROFILE_IMAGE_IPLOAD:

                URL = Constents.PROFILE_IMAGE_UPLOAD;

                break;

            case REQ_CURRENCY_CONVERSION:

                URL = Constents.CURRENCY_CONVERSION;

                break;

            case REQ_RESEND_OTP:

                URL = Constents.RESEND_OTP;

                break;

            case REQ_VERIFY_OTP:

                URL = Constents.VERIFY_OTP;

                break;

            case REQ_ADD_PASSCODE:

                URL = Constents.ADD_PASS_CODE;

                break;

            case REQ_VERIFY_PASSCODE:

                URL = Constents.VERIFY_PASS_CODE;

                break;

            case REQ_ADD_BANK_ACCOUNT:

                URL = Constents.ADD_BANK_ACCOUNT;

                break;

            case REQ_BANK_LIST:

                URL = Constents.BANK_LIST;

                break;

            case REQ_WITHDRAW_AMOUNT:

                URL = Constents.WITHDRAW_MONEY;

                break;

            case REQ_WITHDRAW_LIST:

                URL = Constents.WITHDRAW_LIST;

                break;

            case REQ_CHANGE_PASSWORD:

                URL = Constents.CHANGE_PASSWORD;

                break;

            case REQ_CARD_LIST:

                URL = Constents.CARD_LIST;

                break;

            case REQ_LOAD_MONEY:

                URL = Constents.LOAD_MONEY;

                break;

            case REQ_CARD_DELETE:

                URL = Constents.CARD_DELETE;

                break;

            case REQ_ADD_CARD:

                URL = Constents.ADD_CARD;

                break;

            case REQ_REFER_HISTORY:

                URL = Constents.REFER_LIST;

                break;

            case GET_REWARD_LIST:

                URL = Constents.GET_REWARD_LIST;

                break;

            case GET_REWARD_DETAILS:

                URL = Constents.GET_REWARD_DETAILS;

                break;

            case REQ_GET_PROVIDERS:

                URL = Constents.GET_NETWORK_PROVIDERS;

                break;

            case REQ_POST_RECHARGE:

                URL = Constents.RECHARGE;

                break;

            case REQ_GET_RECHARGE_HISTORY:

                URL = Constents.RECHARGE_HISTORY;

                break;

            case REQ_GET_PROMOTION:
                URL = Constents.PROMOTION;
                break;

            case REQ_VOCUHER_DETAILS:
                URL = Constents.PROMOTION_DETAIL + passingDtat_forGET;
                break;

            case REQ_VOCUHER_CODE:
                URL = Constents.GET_VOUCHER;
                break;

            case REQ_SELLER_LIST:
                URL = Constents.SELLER_SEARCH + loginSession.getLat() + "/" + loginSession.getLang();
                break;

            case REQ_MERCHANT_LIST:
                URL = Constents.MERCHANT_SEARCH+loginSession.getLat()+"/"+loginSession.getLang();
                break;

            case REQ_GET_TRADINGS:
                URL = Constents.POINTS_TRADING;
                break;

            case REQ_REDEEM_POINTS:
                URL = Constents.POINTS_BOUGHT;
                break;

            case REQ_AUTO_FETCH:
                URL = Constents.REQ_AUTO_FETCH;
                break;

            case REQ_GET_OPERATOR:
                URL = Constents.REQ_OPERATORS+passingDtat_forGET;
                break;

            case REQ_OPERATOR_DETAILS:
                URL = Constents.REQ_OPERATORS_DETAILS+passingDtat_forGET;
                break;

            case REQ_COMPLETE_RECHARGE:
                URL = Constents.COMPLETE_RECHARGE;
                break;

            case REQ_GET_RECEIVER_DETAILS:
                URL = Constents.GET_RECEIVER_DETAIL;
                break;

            default:

                URL = Constents.LOGIN_URL;

                break;
        }

        return URL;
    }


     //Method to give appropriate class to parse the JSON data
     // @return Class based on request ID ser ver class edited s

    @SuppressWarnings("rawtypes")
    private Class getModel()
    {
        Class model=null;
        switch (requestID) {

            case REQ_BENEFICIARY_LIST:

                model = BeneficiaryList.class;

                break;

            case REQ_USER_PROFILE:

                model = LoggedUserDetails.class;

                break;

            case REQ_AMOUNT_TAX:

                model = TaxSuccess.class;

                break;

            case REQ_PAYMONEY:
                model = SentMoneySuccess.class;
                break;

            case REQ_TRANSACTION_LIST:
                model = TransactionList.class;
                break;

            case REQ_TRANSACTION_DEATILS:
                model = TransactionDetails.class;
                break;

            case REQ_POPCOINS_LIST:
                model = PopCoinsList.class;
                break;

            case REQ_REQEST_MONEY:
                model = MoneyRequestSuccess.class;
                break;

            case REQ_MONEYREQUEST_LIST:
                model = MoneyRequestList.class;
                break;

            case REQ_QR_DETAILS:

                model = QRcodeDetails.class;

                break;

            case REQ_CUSTOMER_QR_DETAILS:

                model = CustomerQRcodeDetails.class;

                break;

            case REQ_MY_VOUCHERCODE:
                model = MyVoucherCode.class;
                break;

            case REQ_SCAN_AND_PAY:

                model = QRScanSuccess.class;

                break;

            case REQ_SEND_COINS:

                model = SendPopCoinSuccess.class;

                break;

            case REQ_FEED:

                model = FeedModelList.class;

                break;

            case REQ_EVENT_LIST:

                model = EventModelList.class;

                break;

            case REQ_EVENT_DETAILS:

                model = EventModelDetails.class;

                break;

            case REQ_EVENT_BOOKING:

                model = TicketSuccess.class;

                break;

            case REQ_BOOKING_DETAILS:

                model = TicketBookingDetails.class;

                break;

            case REQ_BOOKING_EVENT_LIST:

                model = EventList.class;

                break;


            case REQ_BANK_LIST:

                model = BankList.class;

                break;

            case REQ_WITHDRAW_LIST:

                model = WithdrawList.class;

                break;

            case REQ_CHANGE_PASSWORD:

                model = String.class;

                break;

            case REQ_CARD_LIST:

                model = CardListModel.class;

                break;

            case REQ_REFER_HISTORY:

                model = ReferHistory.class;

                break;

            case GET_REWARD_LIST:

                model = RewardHistoryModel.class;

                break;

            case GET_REWARD_DETAILS:

                model = EarningHistoryModel.class;

                break;

            case REQ_GET_PROVIDERS:

                model = NetworkListModel.class;

                break;

            case REQ_GET_RECHARGE_HISTORY:

                model = RechargeHistory.class;

                break;

            case REQ_VOCUHER_DETAILS:
                model = VoucherDetails.class;
                break;


            case REQ_GET_PROMOTION:
                model = PromotionList.class;
                break;

            case REQ_MERCHANT_LIST:
                model = MerchantList.class;
                break;

            case REQ_SELLER_LIST:
                model = SellerList.class;
                break;

            case REQ_GET_TRADINGS:
                model = PointsTradingList.class;
                break;

            case REQ_REDEEM_POINTS:
                model = RedeemPointsbuy.class;
                break;

            case REQ_AUTO_FETCH:
                model = AutoFetchResponse.class;
                break;

            case REQ_GET_OPERATOR:
                model = OperatorListResponse.class;
                break;

            case REQ_OPERATOR_DETAILS:
                model = AutoFetchResponse.class;
                break;

            case REQ_COMPLETE_RECHARGE:
                model = RechargeSuccessResponse.class;
                break;

            case REQ_GET_RECEIVER_DETAILS:
                model = GetReceiverDetails.class;
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

        } catch (Exception e) {
            e.printStackTrace();;
            Log.e("ServerRequestHandler ", "" + e);
        }
        return result;
    }


    /*********************************** CANCEL SERVER REQUEST ********************************************/
    public void cancelPendingRequests(RequestID tag) {
        if (queue != null) {
            queue.cancelAll(tag);
        }
    }

    private void logoutMethodCall() {

        serverListener.onFailure("LOGOUT",requestID);
    }
}
