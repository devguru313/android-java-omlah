package com.omlah.customer.tabhome.recharge;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.RechargeHistory;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.ContactListScreen;
import com.omlah.customer.urls.Constents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BillPaymentScreen extends BaseActivity implements ServerListener, View.OnClickListener {


    //Create class objects
    ServerRequestwithHeader serverRequestwithHeader;
    LoginSession loginSession;

    //Create xml files
    @BindView(R.id.mobileNumberEditText)EditText mobileNumberEditText;
    @BindView(R.id.amountEditText)EditText amountEditText;
    @BindView(R.id.contactPickImageView)ImageView contactPickImageView;
    @BindView(R.id.continuebutton)Button continuebutton;
    @BindView(R.id.recentTranactionListView)ListView recentTranactionListView;
    @BindView(R.id.errorImageView)TextView errorImageView;
    @BindView(R.id.loadingProgressbar)ProgressBar loadingProgressbar;
    @BindView(R.id.amountLayout)RelativeLayout amountLayout;
    @BindView(R.id.feebanner)TextView feebanner;
    @BindView(R.id.feeTextView)TextView feeTextView;
    @BindView(R.id.ebRadioButton)RadioButton ebRadioButton;
    @BindView(R.id.dthRadioButton)RadioButton dthRadioButton;
    @BindView(R.id.serviceRadioButton)RadioButton serviceRadioButton;


    RecentTranactionListAdapter recentTranactionListAdapter;
    private static int REQ_PICK_CONTACT = 1;
    private static String minDenomination="10",maxDenomination="1000";
    private int selectedPosition = -1;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billpayment_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.BillPayment));

        //Initialize xml files
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        loginSession = LoginSession.getInstance(this);

        //Get Recent list
        //getRecentRechargeList();

        //Set click events
        contactPickImageView.setOnClickListener(this);
        continuebutton.setOnClickListener(this);

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().isEmpty()) {
                    if (amountEditText.getText().toString().equalsIgnoreCase("Enter any amount")) {
                        if (mobileNumberEditText.getText().toString().isEmpty()) {
                            mobileNumberEditText.setError(getResources().getString(R.string.EnterValidNumber));
                        }
                    }else {

                        double min = Double.parseDouble(minDenomination);
                        double max = Double.parseDouble(maxDenomination);

                        if(Double.parseDouble(amountEditText.getText().toString().trim()) > 0 ){
                            double amount = Double.parseDouble(amountEditText.getText().toString().trim());
                            double fee = Double.parseDouble("0");
                            double total = amount * fee / 100 ;

                            feebanner.setVisibility(View.VISIBLE);
                            feeTextView.setVisibility(View.VISIBLE);
                            feeTextView.setText(loginSession.getShowCurrency()+" "+String.format("%.2f",total));

                            if(!(amount >= min)){
                                amountEditText.setError("Min : "+loginSession.getShowCurrency()+" "+minDenomination+" - Max : "+loginSession.getShowCurrency()+" "+maxDenomination);
                                continuebutton.setEnabled(false);
                            }else if(!(amount<= max)){
                                amountEditText.setError("Min : "+loginSession.getShowCurrency()+" "+minDenomination+" - Max : "+loginSession.getShowCurrency()+" "+maxDenomination);
                                continuebutton.setEnabled(false);
                            }else{
                                continuebutton.setVisibility(View.VISIBLE);
                                continuebutton.setEnabled(true);
                            }

                            if(fee > 0){
                                feebanner.setVisibility(View.VISIBLE);
                                feeTextView.setVisibility(View.VISIBLE);
                            }else{
                                feebanner.setVisibility(View.GONE);
                                feeTextView.setVisibility(View.GONE);
                            }

                        }else{
                            feebanner.setVisibility(View.VISIBLE);
                            feeTextView.setVisibility(View.VISIBLE);
                        }

                    }
                }else{

                    feebanner.setVisibility(View.GONE);
                    feeTextView.setVisibility(View.GONE);
                }
            }
        });


        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
    }


    private void getRecentRechargeList() {

        String url =  Constents.RECHARGE_HISTORY ;

        if (isConnectingToInternet()) {
            showProgressDialog();
            Map<String, String> params = new HashMap<>();
            serverRequestwithHeader.createRequest(BillPaymentScreen.this, params, RequestID.REQ_GET_RECHARGE_HISTORY, "GET", url);
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
                    recentTranactionListAdapter = new RecentTranactionListAdapter(this, rechargeHistory.data);
                    recentTranactionListView.setAdapter(recentTranactionListAdapter);
                    loadingProgressbar.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.GONE);
                    recentTranactionListView.setVisibility(View.VISIBLE);
                    recentTranactionListAdapter.setFilter("prepaid");
                }
                break;

        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        switch (requestID) {

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.contactPickImageView:

                Intent intent1 = new Intent(BillPaymentScreen.this,ContactListScreen.class);
                intent1.putExtra("Screen","Recharge");
                startActivityForResult(intent1,REQ_PICK_CONTACT);

                break;

            case R.id.continuebutton:

                if (mobileNumberEditText.getText().toString().isEmpty()) {
                    mobileNumberEditText.setError(getResources().getString(R.string.EnterMobileNumber));
                } else if (!(mobileNumberEditText.getText().toString().length() >= 7)) {
                    mobileNumberEditText.setError(getResources().getString(R.string.EnterValidNumber));
                } else if (amountEditText.getText().toString().trim().isEmpty()) {
                    amountEditText.setError(getResources().getString(R.string.PleaseEnterAmount));
                } else if (!isConnectingToInternet()) {
                    noInternetAlertDialog();
                } else {

                    Map<String, String> params = new HashMap<>();
                    final String amount = amountEditText.getText().toString().trim();
                    final String number = mobileNumberEditText.getText().toString().trim();
                    showProgressDialog();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            hideProgressDialog();

                            Intent intent = new Intent(BillPaymentScreen.this,RechargeSuccessScreen.class);
                            intent.putExtra("amount",amount);
                            intent.putExtra("number",number);
                            intent.putExtra("msg","BillPayment Successfully");
                            if(ebRadioButton.isChecked()){
                                intent.putExtra("type","EB");
                            }else if(dthRadioButton.isChecked()){
                                intent.putExtra("type","DTH");
                            }else{
                                intent.putExtra("type","SERVICE");
                            }

                            startActivity(intent);

                        }
                    },1000);
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

    //Adapter class
    private class RecentTranactionListAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<RechargeHistory.Data> dataArrayList;
        ArrayList<RechargeHistory.Data> dummyArrayList;
        private LayoutInflater inflater;

        public RecentTranactionListAdapter(Activity activity, ArrayList<RechargeHistory.Data> dataArrayList) {
            this.activity = activity;
            this.dataArrayList = dataArrayList;
            this.dummyArrayList = new ArrayList<>();
            this.dummyArrayList.addAll(dataArrayList);
            inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return dataArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ViewHolder();

                //inflate the layout on basis of boolean
                view = inflater.inflate(R.layout.custom_recent_rechargelist, viewGroup, false);

                viewHolder.numberTextView = (TextView) view.findViewById(R.id.numberTextView);
                viewHolder.serviceTextView = (TextView) view.findViewById(R.id.serviceTextView);
                viewHolder.selectImageView = (ImageView) view.findViewById(R.id.selectImageView);
                viewHolder.contentLayout = (RelativeLayout) view.findViewById(R.id.contentLayout);

                view.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) view.getTag();

            viewHolder.numberTextView.setText(dataArrayList.get(i).mobile_no);

            String stype = dataArrayList.get(i).service_type;
            if(!stype.isEmpty()){
                viewHolder.serviceTextView.setText(dataArrayList.get(i).service_type.substring(0,1).toUpperCase()+
                        dataArrayList.get(i).service_type.substring(1,dataArrayList.get(i).service_type.length()));
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

            return view;
        }

        private void itemCheckChanged(View v) {
            selectedPosition = (Integer) v.getTag();
            getSelectedItem();
            notifyDataSetChanged();
        }

        //Return the selectedPosition item
        public void getSelectedItem() {
            if (selectedPosition != -1) {
                try {
                    mobileNumberEditText.setText("+88 "+dataArrayList.get(selectedPosition).mobile_no);
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("please choose another number");
                }

            }
        }
        public void setFilter(String service) {

            Log.e("11",service);

            String filterText = service.toLowerCase(Locale.getDefault());
            dataArrayList.clear();

            if (filterText.length() == 0) {
                dataArrayList.addAll(dummyArrayList);
            } else {
                Log.e("22",service);
                for (RechargeHistory.Data contactList : dummyArrayList) {
                    if (contactList.service_type.toLowerCase(Locale.getDefault()).contains(filterText)) {
                        dataArrayList.add(contactList);
                        Log.e("33",service);
                    }
                }
                if (dataArrayList.size() == 0) {
                    errorImageView.setVisibility(View.VISIBLE);
                    recentTranactionListView.setVisibility(View.GONE);
                }else{
                    errorImageView.setVisibility(View.GONE);
                    recentTranactionListView.setVisibility(View.VISIBLE);
                }
            }

            notifyDataSetChanged();
        }

        private class ViewHolder {

            private TextView numberTextView;
            private TextView serviceTextView;
            private ImageView selectImageView;
            private RelativeLayout contentLayout;
        }
    }

    private  boolean checkAndRequestPermissions() {

        //READ_CONTACTS
        int READCONTACTS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READCONTACTS != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS); }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
