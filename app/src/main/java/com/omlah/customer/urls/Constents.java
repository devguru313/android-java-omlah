package com.omlah.customer.urls;

/**
 * Created by admin on 21-07-2015.
 */
public class Constents {

    public static String USER_URL              = "https://omlahusr.omlah.ly/v1/";
    public static String EVENT_URL             = "https://omlahevent.omlah.ly/v1/";
    public static String TRANSACTION_URL       = "https://omlahtrans.omlah.ly/v1/";

    public static String NUMBER_CHECK_URL      = USER_URL  + "check-phone-exist/";
    public static String GET_RECEIVER_DETAIL   = USER_URL  + "referrer-detail";
    public static String COUNTRY_LIST          = USER_URL  + "country/list";
    public static String SIGNUP_URL            = USER_URL  + "customer/signup";
    public static String LOGOUT_URL            = USER_URL  + "logout";
    public static String ADD_PASS_CODE         = USER_URL  + "customer/add-pass-code";
    public static String LOGIN_URL             = USER_URL  + "customer/login";
    public static String VERIFY_PASS_CODE      = USER_URL  + "customer/verify-pass-code";
    public static String USER_PROFILE          = USER_URL  + "customer/user";
    public static String CHANGE_PASSWORD       = USER_URL  + "change-password";
    public static String PROMOTION             = EVENT_URL + "promotions";
    public static String PROMOTION_DETAIL      = EVENT_URL + "promotion/detail/";
    public static String GET_VOUCHER           = EVENT_URL + "customer/request-code";
    public static String FORGET_PASSWORD_OTP   = USER_URL + "forget-password";
    public static String RESEND_OTP            = USER_URL  + "customer/send-otp";
    public static String VERIFY_OTP            = USER_URL  + "customer/verify-otp";


    //NEAR BY
    public static String MERCHANT_SEARCH       = USER_URL  + "search-merchant/";
    public static String SELLER_SEARCH         = USER_URL  + "search-seller/";

    //EVENT
    public static String EVENT_LIST_URL        =  EVENT_URL+"customer/events";
    public static String EVENT_DETAILS_URL     =  EVENT_URL+"customer/event/";
    public static String CURRENCY_CONVERSION   =  USER_URL+"currency-conversion";
    public static String EVENT_BOOK            =  EVENT_URL + "ticket-booking";
    public static String POINTS_TRADING        =  EVENT_URL+"tradings";
    public static String POINTS_BOUGHT         =  EVENT_URL+"redeem-point";
    public static String EVENT_BOOKING_LIST    =  EVENT_URL + "customer/events-booking-list";
    public static String EVENT_BOOKING_DETAIL  =  EVENT_URL + "booking-ticket-details/";

    public static String GET_REWARD_LIST       =  TRANSACTION_URL+"customer/get-reward-list";
    public static String REFER_LIST            =  USER_URL+"get-refer-list";
    public static String GET_REWARD_DETAILS    =  TRANSACTION_URL+"customer/reward-details";

    //MORE
    public static String ADD_CARD              = TRANSACTION_URL + "add-card";
    public static String CARD_DELETE           = TRANSACTION_URL + "delete-card";
    public static String CARD_LIST             = TRANSACTION_URL + "card-list";
    public static String LOAD_MONEY            = TRANSACTION_URL + "load-money";

    public static String BANK_LIST             = USER_URL + "bank-list";
    public static String ADD_BANK_ACCOUNT      = USER_URL + "add-bank";
    public static String WITHDRAW_MONEY        = TRANSACTION_URL + "withdraw";
    public static String WITHDRAW_LIST         = TRANSACTION_URL + "withdrawal-list";
    public static String TRANSACTION_LIST      = TRANSACTION_URL + "customer/transaction-list";
    public static String TRANSACTION_DETAILS   = TRANSACTION_URL + "customer/transaction-detail/";
    public static String PROFILE_IMAGE_UPLOAD  = USER_URL + "customer/profile";

    //Send/Receive Money
    public static String BENEFICIARY_LIST      = USER_URL + "beneficiary-list";
    public static String BENEFICIARY_ADD       = USER_URL + "add-beneficiary";
    public static String BENEFICIARY_DELETE    = USER_URL + "delete-beneficiary/";
    public static String PAYMONEY              = TRANSACTION_URL + "customer/send-money";
    public static String SEND_COINS            = TRANSACTION_URL + "customer/send-coin";
    public static String REQEST_MONEY          = TRANSACTION_URL + "customer/request-payment";
    public static String REQEST_MONEY_LIST     = TRANSACTION_URL + "customer/request-list";

    public static String QR_DETAILS            = EVENT_URL +"customer/qr-details";
    public static String QR_SCAN_AND_PAY       = TRANSACTION_URL +"customer/pay-to-merchant";
    public static String CUSTOMER_QR_DETAILS   = USER_URL + "read-qr";
    public static String AMOUNT_TAX_CHECK      = TRANSACTION_URL + "customer/commission-details/";
    public static String MONEY_REQUEST_CANCEL  = TRANSACTION_URL + "customer/reject-request";
    public static String REQUEST_STATUS_UPDATE = TRANSACTION_URL + "customer/request-status-update";
    public static String FEED                  =  EVENT_URL+"customer/feeds";
    public static String FEED_HIDE             =  EVENT_URL+"customer/hide-feed";
    public static String MY_VOUCHER_CODE       =  EVENT_URL+"customer/promo-codes";


    public static String EMAIL_CHECK_URL       = USER_URL + "check-email-exist/";
    public static String NEW_PASSWORD          = USER_URL + "new-password";
    public static String GET_NETWORK_PROVIDERS = USER_URL + "services";
    public static String RECHARGE              = USER_URL + "make-transaction";
    public static String RECHARGE_HISTORY      = TRANSACTION_URL + "recharge/history";
    public static String LOCATION_UPDATE       =  USER_URL+"customer/location-update";

    //RECHARGE
    public static String REQ_COUNTRY_CODE      = TRANSACTION_URL + "recharge/countries";
    public static String REQ_AUTO_FETCH        = TRANSACTION_URL + "recharge/operator/auto-fetch";
    public static String REQ_OPERATORS         = TRANSACTION_URL + "recharge/operator-list/";
    public static String REQ_OPERATORS_DETAILS = TRANSACTION_URL + "recharge/operator/";
    public static String COMPLETE_RECHARGE     = TRANSACTION_URL + "recharge/complete-recharge";

}
