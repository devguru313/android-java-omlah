package com.omlah.customer.tabmore;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.fcm.Config;
import com.omlah.customer.model.TransactionList;
import com.omlah.customer.model.WithdrawList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.MyTransactionScreen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 16-09-2017.
 */

public class TransactionHistoryScreen extends BaseActivity implements ServerListener{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;
    TransactionHistoryAdapeter transactionHistoryAdapeter;
    WithdrawHistoryAdapeter withdrawHistoryAdapeter;

    //Create xml files
    @BindView(R.id.tabRadioGroup)RadioGroup tabRadioGroup;
    @BindView(R.id.allButton)RadioButton allButton;
    @BindView(R.id.paidButton)RadioButton paidButton;
    @BindView(R.id.receivedButton)RadioButton receivedButton;
    @BindView(R.id.topupButton)RadioButton topupButton;
    @BindView(R.id.withdrawButton)RadioButton withdrawButton;
    @BindView(R.id.refundButton)RadioButton refundButton;
    @BindView(R.id.dateTextView)TextView dateTextView;
    @BindView(R.id.errorImageView)TextView errorImageView;
    @BindView(R.id.calendarImageView)ImageView calendarImageView;
    @BindView(R.id.transactionHistoryListView)ListView transactionHistoryListView;
    @BindView(R.id.withDrawHistoryListView)ListView withDrawHistoryListView;

    //BroadcastReceiver method
    BroadcastReceiver broadcastReceiver;

    //Create string files
    String currentTab="All";

    SimpleDateFormat dateFormatter;
    Calendar calendar;
    int date,month,year;

    //ListView Scroll
    public String PAGE="1";
    String method = "NoLoad";
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    private ArrayList<TransactionList.Transactions> transactionsArrayList;
    private ArrayList<TransactionList.Transactions> LoadtransactionsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.TransactionHistory));

        //Initialize xml objects
        ButterKnife.bind(this);

        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.footer_view, null);
        mHandler = new MyHandler();
        transactionsArrayList = new ArrayList<>();
        LoadtransactionsArrayList = new ArrayList<>();

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //set font
        Typeface font= Typeface.createFromAsset(getAssets(), "font/GothamRounded-Medium.ttf");
        allButton.setTypeface(font);
        paidButton.setTypeface(font);
        receivedButton.setTypeface(font);
        topupButton.setTypeface(font);
        withdrawButton.setTypeface(font);
        refundButton.setTypeface(font);

        calendar = Calendar.getInstance();
        date=calendar.get(Calendar.DATE);
        month=calendar.get(Calendar.MONTH);
        year=calendar.get(Calendar.YEAR);

        //Default method
        currentTab = "All";
        method = "NoLoad";
        getResponseMethod("all","1");

        //Switching event
        tabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                switch (i){

                    case R.id.allButton:

                        if(!currentTab.equalsIgnoreCase("All")){

                            currentTab = "All";
                            method = "NoLoad";
                            getResponseMethod("all","1");
                        }

                        break;

                    case R.id.paidButton:

                        if(!currentTab.equalsIgnoreCase("Paid")){

                            currentTab = "Paid";

                            method = "NoLoad";
                            getResponseMethod("paid","1");
                        }

                        break;

                    case R.id.receivedButton:

                        if(!currentTab.equalsIgnoreCase("Received")){

                            currentTab = "Received";

                            method = "NoLoad";
                            getResponseMethod("received","1");
                        }

                        break;

                    case R.id.topupButton:

                        if(!currentTab.equalsIgnoreCase("Topup")){

                            currentTab = "Topup";

                            method = "NoLoad";
                            getResponseMethod("topup","1");
                        }

                        break;

                    case R.id.withdrawButton:

                        if(!currentTab.equalsIgnoreCase("Withdraw")){

                            currentTab = "Withdraw";

                            method = "NoLoad";
                            getResponseMethod("Withdraw","1");
                        }

                        break;

                    case R.id.refundButton:

                        if (!currentTab.equalsIgnoreCase("Refund")) {

                            currentTab = "Refund";

                            method = "NoLoad";
                            getResponseMethod("refund", "1");
                        }

                        break;
                }
            }
        });


        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(TransactionHistoryScreen.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        dateTextView.setText(dateFormatter.format(newDate.getTime()));
						/*from.setText(dateFormatter.format(newDate.getTime()));
						shortListDate = dateFormatter.format(newDate.getTime());*/
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                fromDatePickerDialog.show();
            }
        });

        //Notification message received method
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean check_update = intent.getBooleanExtra("update", false);

                if(check_update){

                        currentTab = "All";
                        allButton.setChecked(true);
                        getResponseMethod("all","1");

                }
            }
        };


    }

    private void getResponseMethod(String passingType,String page) {
        //Get user details
        if (isConnectingToInternet()) {

                if (passingType.equalsIgnoreCase("Withdraw")) {

                        Map<String, String> params = new HashMap<>();
                        params.put("status", passingType);
                        params.put("transfer_type", "money");
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(TransactionHistoryScreen.this, params, RequestID.REQ_WITHDRAW_LIST, "GET", "");

                } else {


                        Map<String, String> params = new HashMap<>();
                        params.put("status", passingType);
                        params.put("transfer_type", "money");
                        params.put("limit", "50");
                        params.put("page", "1");
                        Log.e("passingType",passingType);
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(TransactionHistoryScreen.this, params, RequestID.REQ_TRANSACTION_LIST, "POST", "");


                }

        } else {
            noInternetAlertDialog();
        }

    }

    private class TransactionHistoryAdapeter extends BaseAdapter {

        Activity activity;
        ArrayList<TransactionList.Transactions>recentTransactionLists;
        private LayoutInflater inflater;


        public TransactionHistoryAdapeter(Activity activity, ArrayList<TransactionList.Transactions> recentTransactionLists) {
            this.activity = activity;
            this.recentTransactionLists = recentTransactionLists;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {

            return recentTransactionLists.size();
        }

        @Override
        public Object getItem(int i) {
            return recentTransactionLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void addListItemToAdapter(ArrayList<TransactionList.Transactions> list) {
            //Add list to current array list of data
            recentTransactionLists.addAll(list);
            //Notify UI
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            final ViewHolder viewHolder;

            if (view == null) {

                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.custom_recenttranaction_list,null);

                //Initialize xml object
                viewHolder.typeTextView     = (TextView) view.findViewById(R.id.typeTextView);
                viewHolder.amountTextView   = (TextView) view.findViewById(R.id.amountTextView);
                viewHolder.shopNameTextView = (TextView) view.findViewById(R.id.shopNameTextView);
                viewHolder.timeTextView     = (TextView) view.findViewById(R.id.timeTextView);
                viewHolder.typeImageView    = (ImageView) view.findViewById(R.id.typeImageView);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            Log.e("transaction_type",recentTransactionLists.get(i).transaction_type);

            if(recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("f-c")||
                    recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("s-c")||
                    recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("a-c")||
                    recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("m-c") ||
                    recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("b-c")){

                if(recentTransactionLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID())){

                    if (recentTransactionLists.get(i).receiver.business_name != null) {
                        viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).receiver.business_name);
                    } else {

                        if (recentTransactionLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID()) &&
                                recentTransactionLists.get(i).receiver_id.equalsIgnoreCase(loginSession.getCustomerID())) {

                            if (recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("f-c")) {
                                viewHolder.shopNameTextView.setText(getResources().getString(R.string.BonusCredited));
                            } else {
                                viewHolder.shopNameTextView.setText(getResources().getString(R.string.Creditwallet));
                            }

                        } else {
                            viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).receiver.name);
                        }

                    }

                    if (recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("f-c") || recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("b-c")) {
                        viewHolder.amountTextView.setText("+ " + loginSession.getShowCurrency() + " "+ changeCustomerCurrency(recentTransactionLists.get(i).sender_amount,recentTransactionLists.get(i).sender_currency_difference));
                    } else {
                        viewHolder.amountTextView.setText("- "+ loginSession.getShowCurrency() + " "+ changeCustomerCurrency(recentTransactionLists.get(i).sender_amount,recentTransactionLists.get(i).sender_currency_difference));
                    }


                }else{

                    if(recentTransactionLists.get(i).sender.business_name!=null){
                        viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).sender.business_name);
                    }else{
                        viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).sender.name);
                    }

                     viewHolder.amountTextView.setText("+ "+ loginSession.getShowCurrency() + " "+ changeCustomerCurrency(recentTransactionLists.get(i).receive_amount,recentTransactionLists.get(i).sender_currency_difference));
                 ///   viewHolder.amountTextView.setText("+ "+recentTransactionLists.get(i).receive_currency+" "+loginSession.getcurrencySymbol()+ String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(recentTransactionLists.get(i).receive_amount)));
                }

                if(Double.parseDouble(recentTransactionLists.get(i).refund_id) > 0){

                    if(recentTransactionLists.get(i).refund_transaction.refund_status.equalsIgnoreCase("success")){
                        viewHolder.typeTextView.setText("Refunded");
                    }else{
                        viewHolder.typeTextView.setText(getResources().getString(R.string.Refund));
                    }

                    double total = Double.parseDouble(recentTransactionLists.get(i).receive_amount);
                    double refund_total = Double.parseDouble(recentTransactionLists.get(i).refund_transaction.refund_amount);
                    double finalAmount = total - refund_total;

                    if(recentTransactionLists.get(i).refund_transaction.refund_type.equalsIgnoreCase("full")){
                        viewHolder.amountTextView.setText("+ "+ loginSession.getShowCurrency() + " "+ changeCustomerCurrency(recentTransactionLists.get(i).receive_amount,recentTransactionLists.get(i).sender_currency_difference));
                    }else{
                        viewHolder.amountTextView.setText("+ "+ loginSession.getShowCurrency() + " "+ changeCustomerCurrency(String.valueOf(finalAmount),recentTransactionLists.get(i).sender_currency_difference));
                    }

                    viewHolder.typeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.received));
                }else{
                    viewHolder.typeTextView.setText(getResources().getString(R.string.Topup));
                    viewHolder.typeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.topup));
                }

                if(currentTab.equalsIgnoreCase("refund")){
                    viewHolder.amountTextView.setText("+ "+ loginSession.getShowCurrency() + " "+ changeCustomerCurrency(recentTransactionLists.get(i).refund_transaction.refund_amount,recentTransactionLists.get(i).sender_currency_difference));
                }

            }else{

                if(recentTransactionLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID()) &&
                        !(recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("p-c"))){

                    if(recentTransactionLists.get(i).transaction_type.equalsIgnoreCase("c-r")){

                        viewHolder.shopNameTextView.setText("RPay-Recharge");

                        if(recentTransactionLists.get(i).status.equalsIgnoreCase("Failed")){
                            viewHolder.typeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.paid));
                            viewHolder.typeTextView.setText(getResources().getString(R.string.Failed));
                            viewHolder.amountTextView.setText(loginSession.getShowCurrency()+" "+" 0.00");
                        }else{
                            viewHolder.typeTextView.setText(getResources().getString(R.string.Paid));
                            viewHolder.typeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.paid));
                            viewHolder.amountTextView.setText("- "+loginSession.getShowCurrency() + " "+changeCustomerCurrency(recentTransactionLists.get(i).sender_amount,recentTransactionLists.get(i).sender_currency_difference));
                        }

                    }else{

                        viewHolder.typeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.paid));
                        viewHolder.typeTextView.setText(getResources().getString(R.string.Paid));

                        if(recentTransactionLists.get(i).receiver.business_name!=null){
                            viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).receiver.business_name);
                        }else{
                            viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).receiver.name);
                        }

                        viewHolder.amountTextView.setText("- "+loginSession.getShowCurrency() + " "+changeCustomerCurrency(recentTransactionLists.get(i).sender_amount,recentTransactionLists.get(i).sender_currency_difference));

                    }

                }else {

                    viewHolder.typeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.received));
                    viewHolder.typeTextView.setText(getResources().getString(R.string.Received));

                    if(recentTransactionLists.get(i).sender.business_name!=null){
                        viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).sender.business_name);
                    }else{
                        viewHolder.shopNameTextView.setText(recentTransactionLists.get(i).sender.name);
                    }

                    viewHolder.amountTextView.setText("+ "+loginSession.getShowCurrency()+" "+  changeCustomerCurrency(recentTransactionLists.get(i).receive_amount,recentTransactionLists.get(i).sender_currency_difference));
                }
            }

            viewHolder.timeTextView.setTextLocale(Locale.ENGLISH);

            if(currentTab.equalsIgnoreCase("Refund")){
                viewHolder.timeTextView.setText(timeZoneConverter(recentTransactionLists.get(i).created,loginSession.gettimeZone()));
            }else{
                viewHolder.timeTextView.setText(timeZoneConverter(recentTransactionLists.get(i).created,loginSession.gettimeZone()));
            }

            viewHolder.typeTextView.setTag(i);
            viewHolder.amountTextView.setTag(i);
            viewHolder.shopNameTextView.setTag(i);
            viewHolder.timeTextView.setTag(i);
            viewHolder.typeImageView.setTag(i);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(currentTab.equalsIgnoreCase("Refund")){

                        Intent intent = new Intent(TransactionHistoryScreen.this, RefundTransactionScreen.class);
                        intent.putExtra("ID",recentTransactionLists.get(i).transaction_no);
                        intent.putExtra("type",viewHolder.typeTextView.getText().toString());
                        intent.putExtra("typeAmount",viewHolder.amountTextView.getText().toString());
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(TransactionHistoryScreen.this, MyTransactionScreen.class);
                        intent.putExtra("ID",recentTransactionLists.get(i).transaction_no);
                        intent.putExtra("typeName",viewHolder.typeTextView.getText().toString());
                        intent.putExtra("typeAmount",viewHolder.amountTextView.getText().toString());
                        intent.putExtra("businessName",viewHolder.shopNameTextView.getText().toString());
                        startActivity(intent);
                    }

                }
            });

            return view;
        }

        private class ViewHolder {

            private TextView  typeTextView, amountTextView,shopNameTextView,timeTextView;
            private ImageView typeImageView;

        }
    }


    @Override
    public void onSuccess(Object result, RequestID requestID) {

        if(method.equalsIgnoreCase("NoLoad")){
            hideProgressDialog();
        }else{

        }

        try {
            errorImageView.setVisibility(View.GONE);

            if (currentTab.equalsIgnoreCase("Withdraw")) {

                transactionHistoryListView.setVisibility(View.GONE);
                transactionHistoryListView.setAdapter(null);

                withDrawHistoryListView.setVisibility(View.VISIBLE);
                WithdrawList withdrawList = (WithdrawList) result;
                ArrayList<WithdrawList.Data> withdrawLists = withdrawList.data;
                withdrawHistoryAdapeter = new WithdrawHistoryAdapeter(TransactionHistoryScreen.this, withdrawLists);
                withDrawHistoryListView.setAdapter(withdrawHistoryAdapeter);


            } else {

                if (method.equalsIgnoreCase("NoLoad")) {

                    withDrawHistoryListView.setVisibility(View.GONE);
                    withDrawHistoryListView.setAdapter(null);

                    transactionHistoryListView.setVisibility(View.VISIBLE);
                    TransactionList transactionList = (TransactionList) result;
                    transactionsArrayList = transactionList.data.transactions;
                    transactionHistoryAdapeter = new TransactionHistoryAdapeter(TransactionHistoryScreen.this, transactionsArrayList);
                    transactionHistoryListView.setAdapter(transactionHistoryAdapeter);

                } else {

                    LoadtransactionsArrayList.clear();
                    TransactionList transactionList = (TransactionList) result;
                    LoadtransactionsArrayList = transactionList.data.transactions;

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            withDrawHistoryListView.setVisibility(View.GONE);
            withDrawHistoryListView.setAdapter(null);
            transactionHistoryListView.setVisibility(View.GONE);
            transactionHistoryListView.setAdapter(null);
            errorImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {


        if(method.equalsIgnoreCase("NoLoad")){
            hideProgressDialog();
        }else{

        }

        errorImageView.setVisibility(View.VISIBLE);

        if(currentTab.equalsIgnoreCase("Withdraw")){

            withDrawHistoryListView.setVisibility(View.GONE);
            transactionHistoryListView.setVisibility(View.GONE);
            transactionHistoryListView.setAdapter(null);

        }else{
            withDrawHistoryListView.setVisibility(View.GONE);
            transactionHistoryListView.setVisibility(View.GONE);
            transactionHistoryListView.setAdapter(null);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Utility.update_check = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.update_check = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utility.update_check = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

    }

    private class WithdrawHistoryAdapeter extends BaseAdapter{

        Activity activity;
        ArrayList<WithdrawList.Data>withdrawLists;
        private LayoutInflater inflater;


        public WithdrawHistoryAdapeter(Activity activity, ArrayList<WithdrawList.Data> withdrawLists) {
            this.activity = activity;
            this.withdrawLists = withdrawLists;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return withdrawLists.size();
        }

        @Override
        public Object getItem(int i) {
            return withdrawLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;

            if (view == null) {

                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.custom_recenttranaction_list,null);

                //Initialize xml object
                viewHolder.typeTextView     = (TextView) view.findViewById(R.id.typeTextView);
                viewHolder.amountTextView   = (TextView) view.findViewById(R.id.amountTextView);
                viewHolder.shopNameTextView = (TextView) view.findViewById(R.id.shopNameTextView);
                viewHolder.timeTextView     = (TextView) view.findViewById(R.id.timeTextView);
                viewHolder.typeImageView    = (ImageView) view.findViewById(R.id.typeImageView);
                viewHolder.statusTextView   = (TextView) view.findViewById(R.id.statusTextView);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            //Set Font
            /*Typeface tf2 = Typeface.createFromAsset(activity.getAssets(), "font/Lato-Bold.ttf");
            viewHolder.typeTextView.setTypeface(tf2);
            viewHolder.amountTextView.setTypeface(tf2);
            Typeface tf3 = Typeface.createFromAsset(activity.getAssets(), "font/Lato-Regular.ttf");
            viewHolder.shopNameTextView.setTypeface(tf3);*/

            viewHolder.typeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.paid));
            viewHolder.typeTextView.setText(getResources().getString(R.string.AmountTransferredTo));

            viewHolder.timeTextView.setText(String.format(Locale.ENGLISH,timeZoneConverter(withdrawLists.get(i).created,loginSession.gettimeZone())));
            viewHolder.shopNameTextView.setText(withdrawLists.get(i).bank_name+" - "+withdrawLists.get(i).account_no);
            viewHolder.amountTextView.setText("-"+loginSession.getcurrencyCodee()+" "+loginSession.getcurrencySymbol()+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(withdrawLists.get(i).withdrawal_amount)));
           // viewHolder.amountTextView.setText("- "+loginSession.getShowCurrency() + " "+changeCustomerCurrency(withdrawLists.get(i).withdrawal_amount,withdrawLists.get(i).sender_currency_difference));

            viewHolder.statusTextView.setVisibility(View.VISIBLE);

            if(withdrawLists.get(i).status.equalsIgnoreCase("pending")){
                viewHolder.statusTextView.setText(getResources().getString(R.string.Pending));
                viewHolder.statusTextView.setTextColor(activity.getResources().getColor(R.color.yellow));
            }else if(withdrawLists.get(i).status.equalsIgnoreCase("success")){
                viewHolder.statusTextView.setText(getResources().getString(R.string.Success));
                viewHolder.statusTextView.setTextColor(activity.getResources().getColor(R.color.green));
            }else{
                viewHolder.statusTextView.setText(withdrawLists.get(i).status);
            }


            viewHolder.typeTextView.setTag(i);
            viewHolder.amountTextView.setTag(i);
            viewHolder.shopNameTextView.setTag(i);
            viewHolder.timeTextView.setTag(i);
            viewHolder.typeImageView.setTag(i);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(TransactionHistoryScreen.this, WithdrawTransactionScreen.class);
                    intent.putExtra("Type","Amount Transferred To");
                    intent.putExtra("Date",timeZoneConverter(withdrawLists.get(i).created,loginSession.gettimeZone()));
                    intent.putExtra("Bankname",withdrawLists.get(i).bank_name);
                    intent.putExtra("AccountNumber",withdrawLists.get(i).account_no);
                    intent.putExtra("WithdrawalAmount",withdrawLists.get(i).withdrawal_amount);
                    intent.putExtra("withdrawalFee",withdrawLists.get(i).withdrawal_fee);
                    intent.putExtra("totalAmount",withdrawLists.get(i).total_amount);
                    intent.putExtra("referenceNo",withdrawLists.get(i).reference_no);
                    intent.putExtra("status",withdrawLists.get(i).status);
                    startActivity(intent);
                }
            });

            return view;
        }

        private class ViewHolder {

            private TextView  typeTextView, amountTextView,shopNameTextView,timeTextView,statusTextView;
            private ImageView typeImageView;

        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    transactionHistoryListView.addFooterView(ftView);
                    break;
                case 1:
                    //Update data adapter and UI
                    transactionHistoryAdapeter.addListItemToAdapter((ArrayList<TransactionList.Transactions>)msg.obj);
                    //Remove loading view after update listview
                    transactionHistoryListView.removeFooterView(ftView);
                    isLoading=false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            //Add footer view after get data
            mHandler.sendEmptyMessage(0);
            //Search more data
            ArrayList<TransactionList.Transactions> lstResult = getMoreData();
            //Delay time to show loading footer when debug, remove it when release
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Send the result to Handle
            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);

        }
    }

    private ArrayList<TransactionList.Transactions> getMoreData() {
        ArrayList<TransactionList.Transactions>lst = new ArrayList<>();

        method = "Load";

        double num1 = Double.parseDouble("1");
        double num2 = Double.parseDouble(PAGE);
        double sum = num1 + num2;
        getResponseMethod("all", String.valueOf(PAGE).replace(".0",""));
        lst=LoadtransactionsArrayList;

        return lst;
    }

}
