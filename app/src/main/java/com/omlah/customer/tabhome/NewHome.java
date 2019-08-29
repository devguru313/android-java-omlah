package com.omlah.customer.tabhome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.omlah.customer.R;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.account.ReferaFriendScreen;
import com.omlah.customer.base.BaseFragment;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.MyApplication;
import com.omlah.customer.common.SelectableRoundedImageView;
import com.omlah.customer.fcm.Config;
import com.omlah.customer.model.LoggedUserDetails;
import com.omlah.customer.model.PromotionList;
import com.omlah.customer.otp.NewDeviceFound;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.recharge.BillPaymentScreen;
import com.omlah.customer.tabhome.recharge.RechargeScreen;
import com.omlah.customer.tabmore.TransactionHistoryScreen;
import com.omlah.customer.tabmore.mywallet.AlternateWalletScreen;
import com.omlah.customer.tabmore.mywallet.NewWalletScreen;
import com.omlah.customer.tabmore.rewards.RewardsHistory;
import com.omlah.customer.urls.Constents;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by admin on 01-12-2017.
 */

public class NewHome extends BaseFragment implements View.OnClickListener,ServerListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Create class files
    Utility utility;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml files
    @BindView(R.id.walletBalanceTextView)TextView walletBalanceTextView;
    @BindView(R.id.payMoneyButton)RelativeLayout payMoneyButton;
    @BindView(R.id.requestMoneyButton)RelativeLayout requestMoneyButton;
    @BindView(R.id.rechargeLayout)LinearLayout rechargeLayout;
    @BindView(R.id.billPaymentLayout)LinearLayout billPaymentLayout;
    @BindView(R.id.offerLayout)LinearLayout offerLayout;
    @BindView(R.id.eventLayout)LinearLayout eventLayout;
    @BindView(R.id.oyoPointsLayout)LinearLayout oyoPointsLayout;
    @BindView(R.id.transactionLayout)LinearLayout transactionLayout;
    @BindView(R.id.addButton)ImageView addButton;
    @BindView(R.id.pointsLayout)RelativeLayout pointsLayout;
    @BindView(R.id.gotowalletLayout)LinearLayout gotowalletLayout;
    @BindView(R.id.promotionRecyclerView)RecyclerView promotionRecyclerView;

    //BroadcastReceiver method
    BroadcastReceiver broadcastReceiver;
    PromotionList promotionList;

    private String userLoginDeviceID = "";
    private String tag_countrycode_req = "TagCountrycodeReq";
    private String tag_locationupdate_req = "LocationUpdateRequest";
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private GoogleApiClient mGoogleApiClient;
    boolean getLocationCalled = false;
    boolean getLocation = false;
    private Location mLastLocation;
    double  lat = 0;
    double lang  = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView;
        rootView = inflater.inflate(R.layout.home_screen, container, false);

        //initialize xml file
        ButterKnife.bind(this,rootView);

        //Initialize class objects
        loginSession = LoginSession.getInstance(getActivity());
        utility = Utility.getInstance(getActivity());
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(getActivity());

        //Set click event
        payMoneyButton.setOnClickListener(this);
        requestMoneyButton.setOnClickListener(this);
        rechargeLayout.setOnClickListener(this);
        billPaymentLayout.setOnClickListener(this);
        offerLayout.setOnClickListener(this);
        transactionLayout.setOnClickListener(this);
        pointsLayout.setOnClickListener(this);
        gotowalletLayout.setOnClickListener(this);
        addButton.setOnClickListener(this);
        eventLayout.setOnClickListener(this);
        oyoPointsLayout.setOnClickListener(this);


        //get transactionList
        if(loginSession.getCountryCode().isEmpty()){
            getCountryCodeRequest();
        }
        getLoggedUserDetails();

        //Notification message received method
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean check_update = intent.getBooleanExtra("update", false);
                if(check_update){

                    getLoggedUserDetails();

                }

            }
        };



        return rootView;
    }

    private void getLoggedUserDetails() {

        //Get logged user details
        if (isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(NewHome.this, params, RequestID.REQ_USER_PROFILE, "GET", "");
        } else {
            noInternetAlertDialog();
        }

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.payMoneyButton:

                final Intent intent = new Intent(getActivity(),PayMoneyScreen.class);
                intent.putExtra("screenName","PayMoney");
                startActivity(intent);
                break;

            case R.id.pointsLayout:

                Intent pointsIntent = new Intent(getActivity(), RedeemPointsScreen.class);
                startActivity(pointsIntent);
                break;

            case R.id.requestMoneyButton:

                Intent intent1 = new Intent(getActivity(),PayMoneyScreen.class);
                intent1.putExtra("screenName","RequestMoney");
                startActivity(intent1);
                break;

            case R.id.rechargeLayout:
                Intent rechargeIntent = new Intent(getActivity(),RechargeScreen.class);
                startActivity(rechargeIntent);
                break;

            case R.id.billPaymentLayout:
                Intent billPaymentIntent = new Intent(getActivity(),BillPaymentScreen.class);
                startActivity(billPaymentIntent);
                break;

            case R.id.gotowalletLayout:

                Intent viewWalletIntent3 = new Intent(getActivity(), NewWalletScreen.class);
                startActivity(viewWalletIntent3);

                break;

            case R.id.offerLayout:

                Intent viewReferralIntent = new Intent(getActivity(), ReferaFriendScreen.class);
                startActivity(viewReferralIntent);

                break;

            case R.id.transactionLayout:

                Intent viewTransactionIntent = new Intent(getActivity(), TransactionHistoryScreen.class);
                startActivity(viewTransactionIntent);

                break;

            case R.id.addButton:

             /*   if (bottomSheetDialog == null) {
                    bottomSheetDialog = new Dialog(getActivity());
                    bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    bottomSheetDialog.setContentView(R.layout.dialog_for_update);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                }*/

                final Dialog bottomSheetDialog = new Dialog(getActivity());
                bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                bottomSheetDialog.setContentView(R.layout.bottom_money_add);
                bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

                final EditText addmoneyEditText = (EditText)bottomSheetDialog.findViewById(R.id.addmoneyEditText);
                final Button continuebutton = (Button)bottomSheetDialog.findViewById(R.id.continuebutton);
                Button thousandButton = (Button)bottomSheetDialog.findViewById(R.id.thousandButton);
                Button secondButton = (Button)bottomSheetDialog.findViewById(R.id.secondButton);
                Button thirdButton = (Button)bottomSheetDialog.findViewById(R.id.thirdButton);
                ImageView closeButton = (ImageView)bottomSheetDialog.findViewById(R.id.closeButton);
                TextView currencyCode = (TextView)bottomSheetDialog.findViewById(R.id.currencyCode);

                currencyCode.setText(loginSession.getShowCurrency());

                continuebutton.setEnabled(false);
                addmoneyEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!s.toString().isEmpty() && !s.toString().equals(".")){
                            if(Double.parseDouble(s.toString()) > 0){
                                continuebutton.setEnabled(true);
                                continuebutton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }else{
                            continuebutton.setEnabled(false);
                            continuebutton.setBackgroundColor(getResources().getColor(R.color.dgray));
                        }

                    }
                });

                thousandButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addmoneyEditText.setText("1000");
                    }
                });

                secondButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addmoneyEditText.setText("500");
                    }
                });

                thirdButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addmoneyEditText.setText("100");
                    }
                });

                continuebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String enteredAmount = addmoneyEditText.getText().toString().trim();

                        if (enteredAmount.isEmpty() || enteredAmount.equals(".")) {
                            toast(getResources().getString(R.string.pleaseenteraamount));
                        } else {

                            if (enteredAmount.equals("0")) {
                                toast(getResources().getString(R.string.pleaseenteravalidamount));
                            } else {

                                bottomSheetDialog.dismiss();
                                Intent intent2 = new Intent(getActivity(), AlternateWalletScreen.class);
                                intent2.putExtra("enteramount", enteredAmount);
                                startActivity(intent2);

                            }
                        }

                    }
                });

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();


                break;

            case R.id.eventLayout:

                Intent eventLayoutIntent = new Intent(getActivity(), CouponCodeScreen.class);
                startActivity(eventLayoutIntent);

                break;

            case R.id.oyoPointsLayout:

                Intent popcoinLayout = new Intent(getActivity(),RewardsHistory.class);
                startActivity(popcoinLayout);

                break;
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_USER_PROFILE:

                //try {

                    LoggedUserDetails loggedUserDetails = (LoggedUserDetails) result;

                    userLoginDeviceID = loggedUserDetails.data.user.device_id;

                    if(!loggedUserDetails.data.user.country.poppay_fee.fee_amount.isEmpty()){
                        utility.eventFee = loggedUserDetails.data.user.country.poppay_fee.fee_amount;
                    }else{
                        utility.eventFee = "0";
                    }

                    if(!loggedUserDetails.data.user.country.wallet_transfer_fee.fee_option.isEmpty()){
                        utility.loadmoneyFeeOption = loggedUserDetails.data.user.country.wallet_transfer_fee.fee_option;
                    }else{
                        utility.loadmoneyFeeOption = "";
                    }

                    if(!loggedUserDetails.data.user.country.wallet_transfer_fee.fee_amount.isEmpty()){
                        utility.loadmoneyFeeAmount = loggedUserDetails.data.user.country.wallet_transfer_fee.fee_amount;
                    }else{
                        utility.loadmoneyFeeAmount = "0";
                    }

                    if(!loggedUserDetails.data.user.country.wallet_transfer_fee.extra_fees.isEmpty()){
                        utility.loadmoneyExtraFeeAmount = loggedUserDetails.data.user.country.wallet_transfer_fee.extra_fees;
                    }else{
                        utility.loadmoneyExtraFeeAmount = "0";
                    }


                    loginSession.saveUserProfile(loggedUserDetails.data.user.id,loggedUserDetails.data.user.name, loggedUserDetails.data.user.email, loggedUserDetails.data.user.phone_number, loggedUserDetails.data.user.account_balance, loggedUserDetails.data.user.country.currency_code, loggedUserDetails.data.user.country.currency_symbol, loggedUserDetails.data.user.country.time_zone,loggedUserDetails.data.qr,loggedUserDetails.data.user.country.phone_code);
                    if (loggedUserDetails.data.user.profile_image != null) {
                        loginSession.setProfileImage(loggedUserDetails.data.user.profile_image);
                    }
                    loginSession.setPopcoin(loggedUserDetails.data.user.popcoin_balance);

                    loginSession.setBalanceAmount(loggedUserDetails.data.user.account_balance);
                    walletBalanceTextView.setText(loginSession.getcurrencyCodee() + " "+loginSession.getcurrencySymbol()+  String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(loggedUserDetails.data.user.account_balance)));
                    // popcoinsTextView.setText("R Points - " + loggedUserDetails.data.user.popcoin_balance);

                    if (isConnectingToInternet()) {
                        Map<String, String> params = new HashMap<>();
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(NewHome.this, params, RequestID.REQ_GET_PROMOTION, "GET", "");
                    } else {
                        noInternetAlertDialog();
                    }
/*
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;

            case REQ_GET_PROMOTION:

                try{
                    promotionList = (PromotionList) result;
                    promotionRecyclerView.setHasFixedSize(true);
                    PromotionDateAdapter adapter = new PromotionDateAdapter(getActivity(), promotionList.data.promotions);
                    promotionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    promotionRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    checkDeviceID();
                }catch (Exception e){e.printStackTrace();}


                break;

            case REQ_RESEND_OTP:
                toast(getResources().getString(R.string.OTPsentsuccessfully));
                Intent intent = new Intent(getActivity(),NewDeviceFound.class);
                intent.putExtra("OTP", result.toString().trim());
                startActivity(intent);
                break;

        }

    }

    private void checkDeviceID() {

        if(loginSession.get_OTP_VERIFICATION()){

            if(userLoginDeviceID.isEmpty()){
                final Map<String, String> param = new HashMap<String, String>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(NewHome.this,param, RequestID.REQ_RESEND_OTP,"POST","");
            }else{

                if(!(loginSession.getDeviceId().equals(userLoginDeviceID))){
                    final Map<String, String> param = new HashMap<String, String>();
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(NewHome.this,param, RequestID.REQ_RESEND_OTP,"POST","");
                }
            }

        }else{

            if(userLoginDeviceID.isEmpty()){
                Intent intent = new Intent(getActivity(),NewDeviceFound.class);
                intent.putExtra("OTP","1234");
                startActivity(intent);
            }else{
                if(!(loginSession.getDeviceId().equals(userLoginDeviceID))){
                    Intent intent = new Intent(getActivity(),NewDeviceFound.class);
                    intent.putExtra("OTP","1234");
                    startActivity(intent);
                }
            }
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID) {

            case REQ_USER_PROFILE:

                if(error.equalsIgnoreCase("LOGOUT")){
                    logoutMethodCall();
                } else {
                    toast(error);
                }


                break;

            case REQ_GET_PROMOTION:
                toast(error);
                checkDeviceID();
                break;

            case REQ_RESEND_OTP:

                try {
                    // toast(error);
                } catch (Exception e) {
                }

                break;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Utility.update_check = false;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        serverRequestwithHeader.cancelPendingRequests(RequestID.REQ_USER_PROFILE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.update_check = false;
        serverRequestwithHeader.cancelPendingRequests(RequestID.REQ_USER_PROFILE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Get Logged user details
        if(!loginSession.getbalanceAmount().isEmpty()){
            walletBalanceTextView.setText(loginSession.getcurrencyCodee() + " "+loginSession.getcurrencySymbol()+  String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(loginSession.getbalanceAmount())));
        }
        Utility.update_check = true;
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

    }

    private class PromotionDateAdapter extends RecyclerView.Adapter<PromotionDateAdapter.ItemRowHolder>{

        Activity activity;
        List<PromotionList.Promotions> promotionsList;

        public PromotionDateAdapter(Activity activity, List<PromotionList.Promotions> promotionsList) {
            this.activity = activity;
            this.promotionsList = promotionsList;


        }

        @Override
        public PromotionDateAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(final PromotionDateAdapter.ItemRowHolder holder, final int position) {

            try {

                if (promotionsList.get(position).transaction.size() > 0) {

                    if (promotionsList.get(position).transaction.get(0).status.equalsIgnoreCase("pending")) {
                        holder.bookButton.setText("Code : " + promotionsList.get(position).transaction.get(0).promo_code);
                    } else if (promotionsList.get(position).transaction.get(0).status.equalsIgnoreCase("redeem")) {

                        if (promotionsList.get(position).redeem_type.equalsIgnoreCase("multiple")) {
                            holder.bookButton.setText("Code : " + promotionsList.get(position).transaction.get(0).promo_code);
                        } else {
                            holder.bookButton.setText("Redeemed");
                        }
                    }
                } else {
                    holder.bookButton.setText("View Code");
                }

                holder.promotionNameTextView.setText(promotionsList.get(position).title);
                holder.promotionDescription.setText(promotionsList.get(position).description);
                Picasso.with(activity).load(promotionsList.get(position).promotion_banner).into(holder.promotionImageView);
                try {
                    if (!TextUtils.isEmpty(promotionsList.get(position).promotion_banner)) {
                        Picasso.with(activity).load(promotionsList.get(position).promotion_banner)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        //When bitmap loaded successfully display bitmap over image view and generate palette
                                        holder.promotionImageView.setImageBitmap(bitmap);
                                        Palette.from(bitmap)
                                                .generate(new Palette.PaletteAsyncListener() {
                                                    @Override
                                                    public void onGenerated(Palette palette) {
                                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                                        //Check if swatch is null or not
                                                        if (textSwatch == null) {
                                                            //If null display toast
                                                            //    Toast.makeText(activity, "Got Null swatch !!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        Drawable roundDrawable = getResources().getDrawable(R.drawable.event_button);
                                                        roundDrawable.setColorFilter(textSwatch.getRgb(), PorterDuff.Mode.SRC_ATOP);

                                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                            holder.bookButton.setBackgroundDrawable(roundDrawable);
                                                        } else {
                                                            holder.bookButton.setBackground(roundDrawable);
                                                        }

                                                        holder.promotionNameTextView.setTextColor(textSwatch.getRgb());//set title text color
                                                        holder.bookButton.setTextColor(getActivity().getResources().getColor(R.color.white));//set text background color or root background color
                                                        // textSwatch.getBodyTextColor(); //Set the body text color if you need
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    } else {
                        holder.promotionImageView.setImageDrawable(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.bookButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.contentlayout.performClick();
                    }
                });
                holder.contentlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), PromotionDetails.class);
                        intent.putExtra("screenName", "HomeScreen");
                        intent.putExtra("promotion_no", promotionsList.get(position).id);
                        intent.putExtra("promotion_banner", promotionsList.get(position).promotion_banner);
                        intent.putExtra("description", promotionsList.get(position).description);
                        intent.putExtra("title", promotionsList.get(position).title);
                        intent.putExtra("offer_price", promotionsList.get(position).offer_price);
                        intent.putExtra("merchant_id", promotionsList.get(position).user.id);
                        intent.putExtra("redeem_type", promotionsList.get(position).redeem_type);

                        if (promotionsList.get(position).transaction.size() > 0) {
                            intent.putExtra("promotion_status", promotionsList.get(position).transaction.get(0).status);
                            intent.putExtra("promotion_code", promotionsList.get(position).transaction.get(0).promo_code);
                        } else {
                            intent.putExtra("promotion_status", "GETCODE");
                            intent.putExtra("promotion_code", "");
                        }
                        getActivity().startActivity(intent);

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (null != promotionsList ? promotionsList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private SelectableRoundedImageView promotionImageView;
            private TextView promotionNameTextView;
            private TextView promotionDescription;
            private Button bookButton;
            private RelativeLayout contentlayout;

            public ItemRowHolder(View v) {
                super(v);
                this.promotionImageView = (SelectableRoundedImageView) v.findViewById(R.id.promotionImageView);
                this.promotionNameTextView = (TextView) v.findViewById(R.id.promotionNameTextView);
                this.promotionDescription = (TextView) v.findViewById(R.id.promotionDescription);
                this.bookButton = (Button) v.findViewById(R.id.bookButton);
                this.contentlayout = (RelativeLayout) v.findViewById(R.id.contentlayout);

            }


        }
    }


    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getActivity(),resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.Thisdeviceisnotsupported), Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void getLocation() {
        if(!getLocationCalled){
            getLocationCalled = true;
            try {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                getLocation = false;
                if (mLastLocation != null) {
                    lat = mLastLocation.getLatitude();
                    lang = mLastLocation.getLongitude();
                    getLocation = true;
                    getCompleteAddressString(lat, lang);
                }else{
                    getCompleteAddressString(lat, lang);
                }

            } catch (SecurityException e) {
                e.printStackTrace();
                getCompleteAddressString(lat, lang);
            }
        }
    }

    private void getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        Log.e("Location", "LAT:" + LATITUDE+" LAN:"+LONGITUDE);
        loginSession.setLatLong(String.valueOf(LATITUDE),String.valueOf(LONGITUDE));
        if(isConnectingToInternet()){
            if(utility.locationUpdate){
                locationUpdateRequest();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    private void locationUpdateRequest() {

        String url = Uri.parse(Constents.LOCATION_UPDATE)
                .buildUpon()
                .appendQueryParameter("lat", loginSession.getLat())
                .appendQueryParameter("lang",loginSession.getLang())
                .build().toString();
        Log.e("Location",url);
        final Map<String, String> params = new HashMap<>();
        StringRequest postRequest = new StringRequest(Request.Method.GET,url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE FROM SERVER", "" + response);
                        if (response != null) {
                            utility.locationUpdate = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        utility.locationUpdate = true;

                    }
                }
        ) {

            @Override
            protected Map<String,String> getParams() {
                Map<String, String> param2 = new HashMap<String, String>();
                param2 = params;
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
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(postRequest,tag_locationupdate_req);
    }

    private void getCountryCodeRequest() {

        String url = Uri.parse(Constents.REQ_COUNTRY_CODE)
                .buildUpon()
                .build().toString();
        Log.e("url",url);
        final Map<String, String> params = new HashMap<>();
        StringRequest postRequest = new StringRequest(Request.Method.GET,url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            Log.e("response",response);
                            loginSession.setCountryCode(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        loginSession.setCountryCode("");
                    }
                }
        ) {

            @Override
            protected Map<String,String> getParams() {
                Map<String, String> param2 = new HashMap<String, String>();
                param2 = params;
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
                return param2;

            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(postRequest,tag_countrycode_req);
    }

    public void logoutMethodCall(){

        toast(getResources().getString(R.string.Sessionexpiredpleaselogintocontinue));
        loginSession.logout();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent intent = new Intent(getActivity(), GetStartedScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();

    }
}
