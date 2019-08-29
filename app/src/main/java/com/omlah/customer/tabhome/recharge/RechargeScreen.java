package com.omlah.customer.tabhome.recharge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CircleTransform;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.Utils;
import com.omlah.customer.model.RechargeHistory;
import com.omlah.customer.model.recharge.AutoFetchResponse;
import com.omlah.customer.model.recharge.OperatorListResponse;
import com.omlah.customer.model.recharge.RechargeCountryCode;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.ContactListScreen;
import com.omlah.customer.urls.Constents;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RechargeScreen extends BaseActivity implements ServerListener, View.OnClickListener {

    //Create class objects
    ServerRequestwithHeader serverRequestwithHeader;
    LoginSession loginSession;

    //Create xml files
    @BindView(R.id.searchEditText)EditText searchEditText;
    @BindView(R.id.codeNumberEditText)EditText codeNumberEditText;
    @BindView(R.id.mobileNumberEditText)EditText mobileNumberEditText;
    @BindView(R.id.operatorEditText)EditText operatorEditText;
    @BindView(R.id.amountEditText)EditText amountEditText;
    @BindView(R.id.contactPickImageView)ImageView contactPickImageView;
    @BindView(R.id.continuebutton)Button continuebutton;
    @BindView(R.id.recentTranactionListView)RecyclerView recentTranactionListView;
    @BindView(R.id.errorImageView)TextView errorImageView;
    @BindView(R.id.loadingProgressbar)ProgressBar loadingProgressbar;
    @BindView(R.id.actionbarLayout)RelativeLayout actionbarLayout;
    @BindView(R.id.feebanner)TextView feebanner;
    @BindView(R.id.feeTextView)TextView feeTextView;
    @BindView(R.id.bottom_sheet) RelativeLayout layoutBottomSheet;
    @BindView(R.id.codelayout) RelativeLayout codelayout;
    @BindView(R.id.amountLayout) RelativeLayout amountLayout;
    @BindView(R.id.sheetSearchLayout) RelativeLayout sheetSearchLayout;
    @BindView(R.id.backIconImageView) ImageView backIconImageView;
    @BindView(R.id.backIconImageView2) ImageView backIconImageView2;
    @BindView(R.id.sheetActionBar) TextView sheetActionBar;
    @BindView(R.id.actionBarTitleTextView) TextView actionBarTitleTextView;
    @BindView(R.id.countryCodeListView)RecyclerView countryCodeListView;
    @BindView(R.id.amountListView)RecyclerView amountListView;
    @BindView(R.id.operatorListView)RecyclerView operatorListView;
    @BindView(R.id.flagImageView)ImageView flagImageView;

    RecentTranactionListAdapter recentTranactionListAdapter;
    BottomSheetBehavior sheetBehavior;
    private static int REQ_PICK_CONTACT = 1;
    private static String minDenomination="10",maxDenomination="1000";
    private int selectedPosition = -1;

    CountryCodeAdapter adapter;
    RechargeAmountListAdapter rechargeAmountListAdapter;
    OperatorsListAdapter operatorsListAdapter;
    String selectedCountryFlag="https%3A%2F%2Fs3.amazonaws.com%2Frld-flags%2Fet.svg",selectCountryCurrency="ETB",selectedCountryPhoneCode="+251",
            selectedCountryCode="ET",selectedOperatorId="",selectedOperatorImage="",nameSearchText="";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    ArrayList<String>amountList;
    boolean amountEdit_feature = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_screen);
        hideActionBar();

        //Initialize xml files
        ButterKnife.bind(this);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        loginSession = LoginSession.getInstance(this);

        if(loginSession.getCustomerCountryCode().equalsIgnoreCase("+1")){
            selectedCountryFlag="https%3A%2F%2Fs3.amazonaws.com%2Frld-flags%2Fca.svg";
            selectCountryCurrency="CAD";
            selectedCountryPhoneCode="+1";
            selectedCountryCode="CA";
            codeNumberEditText.setText("+1");
            flagImageView.setImageDrawable(getResources().getDrawable(R.drawable.canada_flg));
        }else{
            selectedCountryFlag="https%3A%2F%2Fs3.amazonaws.com%2Frld-flags%2Fet.svg";
            selectCountryCurrency="ETB";
            selectedCountryPhoneCode="+251";
            selectedCountryCode="ET";
            codeNumberEditText.setText("+251");
            flagImageView.setImageDrawable(getResources().getDrawable(R.drawable.ind_flg));
        }

        //Get Recent list
        getRecentRechargeList();

        //Set click events
        contactPickImageView.setOnClickListener(this);
        continuebutton.setOnClickListener(this);

        mobileNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().isEmpty()) {
                    operatorEditText.setText("");
                    amountEditText.setText("");
                    operatorEditText.setVisibility(View.GONE);
                    amountLayout.setVisibility(View.GONE);
                } else {
                    if (editable.toString().length() >= 5) {
                        operatorEditText.setVisibility(View.VISIBLE);
                    } else {
                        operatorEditText.setText("");
                        amountEditText.setText("");
                        operatorEditText.setVisibility(View.GONE);
                        amountLayout.setVisibility(View.GONE);
                    }
                }
            }
        });


        operatorEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnectingToInternet()){
                    showProgressDialog();
                    Map<String, String> params = new HashMap<>();
                    params.put("phone_number",mobileNumberEditText.getText().toString().trim());
                    params.put("country_code",selectedCountryCode);
                    serverRequestwithHeader.createRequest(RechargeScreen.this, params, RequestID.REQ_AUTO_FETCH, "POST", "");
                }else{
                    noInternetAlertDialog();
                }

            }
        });

        amountEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!amountEdit_feature){

                    sheetActionBar.setText("Select a Amount");
                    sheetSearchLayout.setVisibility(View.GONE);
                    amountListView.setVisibility(View.VISIBLE);
                    countryCodeListView.setVisibility(View.GONE);
                    operatorListView.setVisibility(View.GONE);
                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }

                    Log.e("amountList",""+amountList.toString());
                    rechargeAmountListAdapter = new RechargeAmountListAdapter(RechargeScreen.this,amountList);
                    amountListView.setLayoutManager(new LinearLayoutManager(RechargeScreen.this, LinearLayoutManager.VERTICAL, false));
                    amountListView.setAdapter(rechargeAmountListAdapter);

                }
            }
        });

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {

                if(amountEdit_feature){

                    if (!editable.toString().isEmpty()) {
                        if (amountEditText.getText().toString().equalsIgnoreCase("Enter any amount")) {
                            if (mobileNumberEditText.getText().toString().isEmpty()) {
                                mobileNumberEditText.setError(getResources().getString(R.string.EnterValidNumber));
                            }
                        } else {

                            double min = Double.parseDouble(minDenomination);
                            double max = Double.parseDouble(maxDenomination);

                            if (Double.parseDouble(amountEditText.getText().toString().trim()) > 0) {
                                double amount = Double.parseDouble(amountEditText.getText().toString().trim());
                                double fee = Double.parseDouble("0");
                                double total = amount * fee / 100;

                                feebanner.setVisibility(View.VISIBLE);
                                feeTextView.setVisibility(View.VISIBLE);
                                feeTextView.setText(loginSession.getShowCurrency() + " " + String.format("%.2f", total));

                                if (!(amount >= min)) {
                                    amountEditText.setError("Min : " + loginSession.getShowCurrency() + " " + minDenomination + " - Max : " + loginSession.getShowCurrency() + " " + maxDenomination);
                                    continuebutton.setEnabled(false);
                                } else if (!(amount <= max)) {
                                    amountEditText.setError("Min : " + loginSession.getShowCurrency() + " " + minDenomination + " - Max : " + loginSession.getShowCurrency() + " " + maxDenomination);
                                    continuebutton.setEnabled(false);
                                } else {
                                    continuebutton.setVisibility(View.VISIBLE);
                                    continuebutton.setEnabled(true);
                                }

                                if (fee > 0) {
                                    feebanner.setVisibility(View.VISIBLE);
                                    feeTextView.setVisibility(View.VISIBLE);
                                } else {
                                    feebanner.setVisibility(View.GONE);
                                    feeTextView.setVisibility(View.GONE);
                                }

                            } else {
                                feebanner.setVisibility(View.VISIBLE);
                                feeTextView.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {

                        feebanner.setVisibility(View.GONE);
                        feeTextView.setVisibility(View.GONE);
                    }
                }

            }
        });


        codeNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codelayout.performClick();
            }
        });
        codelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               openBottomSheet();
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //filter method
        searchEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    nameSearchText = s.toString();
                    String filter_text = searchEditText.getText().toString().trim().toLowerCase(Locale.getDefault());

                    adapter.searchFilter(filter_text);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            public void afterTextChanged(Editable s) {

            }
        });

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        backIconImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(RechargeScreen.this);
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

    }

    private void openBottomSheet() {

        if(!loginSession.getCountryCode().isEmpty()){

            hideKeyboard(RechargeScreen.this);
            sheetActionBar.setText("Select a country");
            sheetSearchLayout.setVisibility(View.VISIBLE);
            amountListView.setVisibility(View.GONE);
            countryCodeListView.setVisibility(View.VISIBLE);
            operatorListView.setVisibility(View.GONE);
            searchEditText.setText("");
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            Gson gson = new Gson();
            Object result = gson.fromJson(loginSession.getCountryCode(), RechargeCountryCode.class);
            RechargeCountryCode rechargeCountryCode = (RechargeCountryCode)result;
            countryCodeListView.setHasFixedSize(true);
            adapter = new CountryCodeAdapter(this,rechargeCountryCode.data);
            countryCodeListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            countryCodeListView.setAdapter(adapter);

        }

    }

    private void getRecentRechargeList() {

        String url =  Constents.RECHARGE_HISTORY ;

        if (isConnectingToInternet()) {
            showProgressDialog();
            Map<String, String> params = new HashMap<>();
            serverRequestwithHeader.createRequest(RechargeScreen.this, params, RequestID.REQ_GET_RECHARGE_HISTORY, "GET", url);
        } else {
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID) {

            case REQ_GET_RECHARGE_HISTORY:

                RechargeHistory rechargeHistory = (RechargeHistory) result;
                if (rechargeHistory.data == null) {
                    loadingProgressbar.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.VISIBLE);
                    recentTranactionListView.setVisibility(View.GONE);
                } else {
                    recentTranactionListView.setHasFixedSize(true);
                    recentTranactionListAdapter = new RecentTranactionListAdapter(this, rechargeHistory.data);
                    recentTranactionListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    recentTranactionListView.setAdapter(recentTranactionListAdapter);
                    loadingProgressbar.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.GONE);
                    recentTranactionListView.setVisibility(View.VISIBLE);
                }

                break;

            case REQ_AUTO_FETCH:

                AutoFetchResponse autoFetchResponse = (AutoFetchResponse) result;

                try {

                    selectedOperatorId = autoFetchResponse.data.operatorId;
                    selectedOperatorImage = autoFetchResponse.data.logo;
                    operatorEditText.setText(autoFetchResponse.data.name);

                    amountLayout.setVisibility(View.VISIBLE);
                    amountList = new ArrayList<>();
                    if (autoFetchResponse.data.denominationType.equalsIgnoreCase("FIXED")) {
                        amountEdit_feature = false;
                        amountEditText.setFocusable(false);
                        amountEditText.setFocusableInTouchMode(false);

                        for (AutoFetchResponse.FixedAmounts fixedAmounts : autoFetchResponse.data.fixedAmounts) {
                            amountList.add(fixedAmounts.amount);
                            Log.e("amount", fixedAmounts.amount);
                        }
                    } else {
                        amountEdit_feature = true;
                        amountEditText.setFocusable(true);
                        amountEditText.setFocusableInTouchMode(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(isConnectingToInternet()){
                                showProgressDialog();
                                Map<String, String> params = new HashMap<>();
                                serverRequestwithHeader.createRequest(RechargeScreen.this, params, RequestID.REQ_GET_OPERATOR, "GET", selectedCountryCode);
                            }else{
                                noInternetAlertDialog();
                            }

                        }
                    });
                }


              break;

            case REQ_GET_OPERATOR:

                OperatorListResponse operatorListResponse = (OperatorListResponse)result;

                try{


                    sheetActionBar.setText("Select your operator");
                    sheetSearchLayout.setVisibility(View.GONE);
                    amountListView.setVisibility(View.GONE);
                    countryCodeListView.setVisibility(View.GONE);
                    operatorListView.setVisibility(View.VISIBLE);
                    searchEditText.setText("");
                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }


                    operatorListView.setHasFixedSize(true);
                    operatorsListAdapter = new OperatorsListAdapter(this,operatorListResponse.data);
                    operatorListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    operatorListView.setAdapter(operatorsListAdapter);


                }catch (Exception e){e.printStackTrace();}

                break;

            case REQ_OPERATOR_DETAILS:

                AutoFetchResponse operatorDetailsResponse = (AutoFetchResponse)result;

                selectedOperatorId = operatorDetailsResponse.data.operatorId;
                selectedOperatorImage = operatorDetailsResponse.data.logo;
                operatorEditText.setText(operatorDetailsResponse.data.name);

                amountLayout.setVisibility(View.VISIBLE);
                amountList = new ArrayList<>();
                if (operatorDetailsResponse.data.denominationType.equalsIgnoreCase("FIXED")) {
                    amountEdit_feature = false;
                    amountEditText.setFocusable(false);
                    amountEditText.setFocusableInTouchMode(false);

                    for (AutoFetchResponse.FixedAmounts fixedAmounts : operatorDetailsResponse.data.fixedAmounts) {
                        amountList.add(fixedAmounts.amount);
                        Log.e("amount", fixedAmounts.amount);
                    }
                } else {
                    amountEdit_feature = true;
                    amountEditText.setFocusable(true);
                    amountEditText.setFocusableInTouchMode(true);
                }

                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                break;



        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.contactPickImageView:

                Intent intent1 = new Intent(RechargeScreen.this,ContactListScreen.class);
                intent1.putExtra("Screen","Recharge");
                startActivityForResult(intent1,REQ_PICK_CONTACT);

                break;

            case R.id.continuebutton:

                if (mobileNumberEditText.getText().toString().isEmpty()) {
                    mobileNumberEditText.setError(getResources().getString(R.string.EnterMobileNumber));
                }
                if (operatorEditText.getText().toString().isEmpty()) {
                    operatorEditText.setError(getResources().getString(R.string.Pleaseselectyouroperator));
                } else if (amountEditText.getText().toString().trim().isEmpty()) {
                    amountEditText.setError(getResources().getString(R.string.PleaseEnterAmount));
                } else if (!isConnectingToInternet()) {
                    noInternetAlertDialog();
                } else {

                    Intent intent = new Intent(RechargeScreen.this,RechargeSuccessScreen.class);
                    intent.putExtra("rechargeAmount",amountEditText.getText().toString());
                    intent.putExtra("operatorImage",selectedOperatorImage);
                    intent.putExtra("selectCountryCurrency",selectCountryCurrency);
                    intent.putExtra("selectedCountryFlag",selectedCountryFlag);
                    intent.putExtra("operatorName",operatorEditText.getText().toString());
                    intent.putExtra("phoneCode",selectedCountryPhoneCode);
                    intent.putExtra("phoneNumber",mobileNumberEditText.getText().toString().trim());
                    intent.putExtra("phone_number",selectedCountryPhoneCode+mobileNumberEditText.getText().toString().trim());
                    intent.putExtra("country_code",selectedCountryCode);
                    intent.putExtra("operator_id",selectedOperatorId);
                    intent.putExtra("amount",amountEditText.getText().toString().trim());
                    startActivity(intent);

                }

                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == REQ_PICK_CONTACT && resultCode == REQ_PICK_CONTACT){

                String numberget = data.getStringExtra("NUMBER");
                mobileNumberEditText.setText(numberget);

            }
        }catch (Exception e){e.printStackTrace();}


    }

    private  boolean checkAndRequestPermissions() {


        //READ_CONTACTS
        int READCONTACTS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        //READ CONTACT
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READCONTACTS != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS); }

        //Final Step

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<RechargeCountryCode.Data> dataArrayList;
        ArrayList<RechargeCountryCode.Data> dummydataArrayList;

        public CountryCodeAdapter(Activity activity, ArrayList<RechargeCountryCode.Data> dataArrayList) {
            this.activity = activity;
            this.dataArrayList = dataArrayList;
            this.dummydataArrayList = new ArrayList<>();
            this.dummydataArrayList.addAll(dataArrayList);
        }

        @Override
        public CountryCodeAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.countrycode_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(final CountryCodeAdapter.ItemRowHolder holder, final int position) {

            holder.countryNameTextView.setText(dataArrayList.get(position).name+" ( "+dataArrayList.get(position).callingCodes+" )");

            String userAvatarUrl = dataArrayList.get(position).flag;
            try {
                String prevURL="";
                String decodeURL=userAvatarUrl;
                while(!prevURL.equals(decodeURL))
                {
                    prevURL=decodeURL;
                    decodeURL= URLDecoder.decode( decodeURL, "UTF-8" );
                }
                userAvatarUrl = decodeURL;
            } catch (UnsupportedEncodingException e) {

            }

            Utils.fetchSvg(activity, userAvatarUrl, holder.flagImageView);

            holder.detailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedCountryPhoneCode=dataArrayList.get(position).callingCodes;
                    selectCountryCurrency=dataArrayList.get(position).currencyCode;
                    selectedCountryFlag=dataArrayList.get(position).flag;
                    selectedCountryCode=dataArrayList.get(position).isoName;

                    operatorEditText.setText("");
                    amountEditText.setText("");
                    amountLayout.setVisibility(View.GONE);
                    operatorEditText.setVisibility(View.VISIBLE);
                    String userAvatarUrl = dataArrayList.get(position).flag;
                    try {
                        String prevURL="";
                        String decodeURL=userAvatarUrl;
                        while(!prevURL.equals(decodeURL))
                        {
                            prevURL=decodeURL;
                            decodeURL= URLDecoder.decode( decodeURL, "UTF-8" );
                        }
                        userAvatarUrl = decodeURL;
                    } catch (UnsupportedEncodingException e) {

                    }
                    Utils.fetchSvg(activity, userAvatarUrl,flagImageView);

                    codeNumberEditText.setText(dataArrayList.get(position).callingCodes);
                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != dataArrayList ? dataArrayList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private ImageView flagImageView;
            private TextView countryNameTextView;
            private RelativeLayout detailsLayout;

            public ItemRowHolder(View v) {
                super(v);
                this.flagImageView = (ImageView) v.findViewById(R.id.flagImageView);
                this.countryNameTextView = (TextView) v.findViewById(R.id.countryNameTextView);
                this.detailsLayout = (RelativeLayout) v.findViewById(R.id.detailsLayout);
            }
        }

        public void searchFilter(String filterText) {


            nameSearchText = filterText;
            filterText = filterText.toLowerCase(Locale.getDefault());
            dataArrayList.clear();

            if (filterText.length() == 0) {
                dataArrayList.addAll(dummydataArrayList);
            } else {
                for (RechargeCountryCode.Data contactList : dummydataArrayList) {
                    if (contactList.currencyName.toLowerCase(Locale.getDefault()).contains(filterText) || contactList.callingCodes.toLowerCase(Locale.getDefault()).contains(filterText)) {
                        dataArrayList.add(contactList);
                    }
                }
                if (dataArrayList.size() == 0) {

                }
            }

            notifyDataSetChanged();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class RechargeAmountListAdapter extends RecyclerView.Adapter<RechargeAmountListAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<String> amountdataArrayList;


        public RechargeAmountListAdapter(Activity activity, ArrayList<String> amountdataArrayList) {
            this.activity = activity;
            this.amountdataArrayList = amountdataArrayList;
        }

        @Override
        public RechargeAmountListAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.amount_list_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(final ItemRowHolder holder, final int position) {

            holder.amountTextView.setText(selectCountryCurrency+" "+amountdataArrayList.get(position));

            holder.amountdetailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    amountEditText.setText(amountdataArrayList.get(position));

                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != amountdataArrayList ? amountdataArrayList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private TextView amountTextView;
            private RelativeLayout amountdetailsLayout;

            public ItemRowHolder(View v) {
                super(v);
                this.amountTextView = (TextView) v.findViewById(R.id.amountTextView);
                this.amountdetailsLayout = (RelativeLayout) v.findViewById(R.id.amountdetailsLayout);
            }
        }

    }

    private class OperatorsListAdapter extends RecyclerView.Adapter<OperatorsListAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<OperatorListResponse.Data> operatorDataArrayList;


        public OperatorsListAdapter(Activity activity, ArrayList<OperatorListResponse.Data> operatorDataArrayList) {
            this.activity = activity;
            this.operatorDataArrayList = operatorDataArrayList;
        }

        @Override
        public OperatorsListAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.operator_list_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(final ItemRowHolder holder, final int position) {

            holder.operatorNameTextView.setText(operatorDataArrayList.get(position).name);

            String userAvatarUrl = operatorDataArrayList.get(position).logo;
            try {
                String prevURL="";
                String decodeURL=userAvatarUrl;
                while(!prevURL.equals(decodeURL))
                {
                    prevURL=decodeURL;
                    decodeURL= URLDecoder.decode( decodeURL, "UTF-8" );
                }
                userAvatarUrl = decodeURL;
                Picasso.with(activity).load(userAvatarUrl).into(holder.operatorImageView);
            } catch (UnsupportedEncodingException e) {

            }

            holder.operatorDetailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isConnectingToInternet()){
                        showProgressDialog();
                        Map<String, String> params = new HashMap<>();
                        serverRequestwithHeader.createRequest(RechargeScreen.this, params, RequestID.REQ_OPERATOR_DETAILS, "GET", operatorDataArrayList.get(position).operatorId);
                    }else{
                        noInternetAlertDialog();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != operatorDataArrayList ? operatorDataArrayList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private TextView operatorNameTextView;
            private RelativeLayout operatorDetailsLayout;
            private ImageView operatorImageView;

            public ItemRowHolder(View v) {
                super(v);
                this.operatorNameTextView = (TextView) v.findViewById(R.id.operatorNameTextView);
                this.operatorImageView = (ImageView) v.findViewById(R.id.operatorImageView);
                this.operatorDetailsLayout = (RelativeLayout) v.findViewById(R.id.operatorDetailsLayout);
            }
        }

    }

    private class RecentTranactionListAdapter extends RecyclerView.Adapter<RecentTranactionListAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<RechargeHistory.Data> dataArrayList;
        ArrayList<RechargeHistory.Data> dummyArrayList;

        public RecentTranactionListAdapter(Activity activity, ArrayList<RechargeHistory.Data> dataArrayList) {
            this.activity = activity;
            this.dataArrayList = dataArrayList;
            this.dummyArrayList = new ArrayList<>();
            this.dummyArrayList.addAll(dataArrayList);
        }

        @Override
        public RecentTranactionListAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recent_rechargelist, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(final RecentTranactionListAdapter.ItemRowHolder viewHolder, final int i) {

            String userAvatarUrl = dataArrayList.get(i).provider_image;
            try {
                String prevURL = "";
                String decodeURL = userAvatarUrl;
                while (!prevURL.equals(decodeURL)) {
                    prevURL = decodeURL;
                    decodeURL = URLDecoder.decode(decodeURL, "UTF-8");
                }
                userAvatarUrl = decodeURL;

                Picasso.with(activity).load(userAvatarUrl).transform(new CircleTransform()).into(viewHolder.mobileImageView);

            } catch (UnsupportedEncodingException e) {
            }

            viewHolder.numberTextView.setText(dataArrayList.get(i).callingCodes+" "+dataArrayList.get(i).mobile_no.replace(dataArrayList.get(i).callingCodes,"").trim());

            String stype = dataArrayList.get(i).service_type;
            if(!stype.isEmpty()){
                viewHolder.serviceTextView.setText("Prepaid");
            }

            viewHolder.numberTextView.setTag(i);
            viewHolder.serviceTextView.setTag(i);
            viewHolder.selectImageView.setTag(i);
            viewHolder.contentLayout.setTag(i);

            if (i == selectedPosition) {
                viewHolder.selectImageView.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            } else {
                viewHolder.selectImageView.setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
            }

            viewHolder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCheckChanged(v);
                }
            });

            viewHolder.selectImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCheckChanged(v);
                }

            });
        }

        @Override
        public int getItemCount() {
            return (null != dataArrayList ? dataArrayList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private TextView numberTextView;
            private TextView serviceTextView;
            private ImageView selectImageView;
            private ImageView mobileImageView;
            private RelativeLayout contentLayout;


            public ItemRowHolder(View view) {
                super(view);

                this.mobileImageView = (ImageView) view.findViewById(R.id.mobileImageView);
                this.numberTextView = (TextView) view.findViewById(R.id.numberTextView);
                this.serviceTextView = (TextView) view.findViewById(R.id.serviceTextView);
                this.selectImageView = (ImageView) view.findViewById(R.id.selectImageView);
                this.contentLayout = (RelativeLayout) view.findViewById(R.id.contentLayout);

            }

        }

        private void itemCheckChanged(View v) {
            selectedPosition = (Integer) v.getTag();
            getSelectedItem();
            notifyDataSetChanged();
        }

        //Return the selectedPosition item
        public void getSelectedItem() {
            if (selectedPosition != -1) {
                try{

                    selectedCountryFlag=dataArrayList.get(selectedPosition).flag;
                    selectedCountryPhoneCode=dataArrayList.get(selectedPosition).callingCodes;
                    selectedCountryCode=dataArrayList.get(selectedPosition).country_code;

                    String userAvatarUrl = dataArrayList.get(selectedPosition).flag;
                    try {
                        String prevURL="";
                        String decodeURL=userAvatarUrl;
                        while(!prevURL.equals(decodeURL))
                        {
                            prevURL=decodeURL;
                            decodeURL= URLDecoder.decode( decodeURL, "UTF-8" );
                        }
                        userAvatarUrl = decodeURL;
                    } catch (UnsupportedEncodingException e) {

                    }
                    Utils.fetchSvg(activity, userAvatarUrl, flagImageView);
                    codeNumberEditText.setText(dataArrayList.get(selectedPosition).callingCodes);
                    mobileNumberEditText.setText(dataArrayList.get(selectedPosition).mobile_no.replace(dataArrayList.get(selectedPosition).callingCodes,"").trim());
                    operatorEditText.performClick();

                }catch (Exception e){e.printStackTrace();
                    toast(getResources().getString(R.string.pleasechooseanothernumber));}

            }
        }

    }
}
