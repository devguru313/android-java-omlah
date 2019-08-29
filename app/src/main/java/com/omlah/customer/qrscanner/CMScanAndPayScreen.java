package com.omlah.customer.qrscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CircleTransform;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.QRScanSuccess;
import com.omlah.customer.otp.PassCodeVerifiedScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.SuccessScreen;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.omlah.customer.service.RequestID.REQ_SCAN_AND_PAY;

public class CMScanAndPayScreen extends BaseActivity implements ServerListener{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    String merchantId="";
    String receiverCurrencyCode="";
    String conversionAmount="",merchant_name="",merchant_profile="",tipAmount="0",offerPercentage="0",rewardYesNo="No";

    //Create xml objects
    @BindView(R.id.payNowButton)Button payNowButton;
    @BindView(R.id.amountPayEditText)EditText amountPayEditText;
    @BindView(R.id.amountEditText)TextView amountEditText;
    @BindView(R.id.descriptionEditText)EditText descriptionEditText;
    @BindView(R.id.rewardText)TextView rewardText;
    @BindView(R.id.redeemRewardPointsEditText)TextView redeemRewardPointsEditText;
    @BindView(R.id.amountPayEditText2)TextView amountPayEditText2;
    @BindView(R.id.tipEditText)EditText tipEditText;
    @BindView(R.id.scrollView)ScrollView scrollView;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.feeText)TextView feeText;
    @BindView(R.id.oyopayFeeEditText)EditText oyopayFeeEditText;
    @BindView(R.id.merchantImageView)ImageView merchantImageView;
    @BindView(R.id.shopNameEditText)TextView shopNameEditText;
    @BindView(R.id.TipsEnterText)TextView TipsEnterText;
    @BindView(R.id.OfferLayout)RelativeLayout OfferLayout;
    @BindView(R.id.redeemPointLayout)RelativeLayout redeemPointLayout;
    @BindView(R.id.couponLayout)RelativeLayout couponLayout;
    @BindView(R.id.foloosiFeeLayout)RelativeLayout foloosiFeeLayout;
    @BindView(R.id.fabArrowButton)ImageView fabArrowButton;
    @BindView(R.id.uparrow)ImageView uparrow;
    @BindView(R.id.payableLayout)LinearLayout payableLayout;
    @BindView(R.id.amountlayout)TextView amountlayout;
    @BindView(R.id.earnPointsTextView)TextView earnPointsTextView;
    @BindView(R.id.receiverCurrencyCodeTextView)TextView receiverCurrencyCodeTextView;
    @BindView(R.id.additionalFeeRecyclerView)
    RecyclerView additionalFeeRecyclerView;

    double redeemAmount=0,additionFEE = 0;
    String fee_option="",fee_amount="0",sender_currency_difference="";


    ArrayList<String> additioanlFees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_merchant_screen);
        hideActionBar();

        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        OfferLayout.setVisibility(View.GONE);
        couponLayout.setVisibility(View.GONE);
        amountPayEditText.setFocusableInTouchMode(true);
        amountPayEditText.setFocusable(true);
        amountPayEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);


        Intent intent = getIntent();
        if(intent!=null){
            merchantId             = intent.getStringExtra("user_id");
            receiverCurrencyCode   = intent.getStringExtra("receiverCurrencyCode");
            merchant_name          = intent.getStringExtra("bussinessName");
            merchant_profile       = intent.getStringExtra("merchant_profile");
            offerPercentage        = intent.getStringExtra("offerPercentage");
            rewardYesNo            = intent.getStringExtra("rewardYesNo");
            fee_option             = intent.getStringExtra("fee_option");
            fee_amount             = intent.getStringExtra("fee_amount");
            sender_currency_difference= intent.getStringExtra("sender_currency_difference");
            additioanlFees         = (ArrayList<String>) getIntent().getSerializableExtra("additionalFee");

            if(merchant_profile!=null && !merchant_profile.isEmpty()){

                String getImage[] = merchant_profile.split("upload");
                String imageFormat = getImage[0]+"upload/w_250,h_250,c_thumb,g_face,r_max"+getImage[1];
                Picasso.with(this)
                        .load(imageFormat)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(getResources().getDrawable(R.drawable.user_image_post))
                        .transform(new CircleTransform())
                        .into(merchantImageView);
            }


            receiverCurrencyCodeTextView.setText(loginSession.getcurrencyCodee());

            shopNameEditText.setText(merchant_name);

            if(rewardYesNo.equalsIgnoreCase("YES")){
                redeemPointLayout.setVisibility(View.VISIBLE);
            }else{
                redeemPointLayout.setVisibility(View.GONE);
            }

            if(additioanlFees.size() > 0){

                Log.e("additioanlFees",""+additioanlFees.toString());

                additionalFeeRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
                additionalFeeRecyclerView.setLayoutManager(linearLayoutManager);
                additionalFeeRecyclerView.setItemAnimator(new DefaultItemAnimator());
                additionalFeeRecyclerView.setHasFixedSize(true);
                AdditionalFeeListAdapter copiedCodeListAdapter = new AdditionalFeeListAdapter(this,additioanlFees);
                additionalFeeRecyclerView.setAdapter(copiedCodeListAdapter);
                copiedCodeListAdapter.notifyDataSetChanged();

            }else{
                additionalFeeRecyclerView.setVisibility(View.GONE);
            }
        }


        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        amountPayEditText.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    int beforeDecimal = 6, afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = amountPayEditText.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        }
                        else if (temp.toString().indexOf(".") == -1) {
                            // no decimal point placed yet
                            if (temp.length() > beforeDecimal) {
                                return "";
                            }
                        } else {
                            temp = temp.substring(temp.indexOf(".") + 1);
                            if (temp.length() > afterDecimal) {
                                return "";
                            }
                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });

        amountPayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){

                    if (Double.parseDouble(s.toString()) > 0) {

                        amountEditText.setText(String.format("%.2f",Double.parseDouble(s.toString())));

                        double tip = 0;
                        if(!tipEditText.getText().toString().trim().isEmpty()){
                            tip = Double.parseDouble(tipEditText.getText().toString().trim());
                        }else{
                            tip = 0;
                        }


                        if(additioanlFees.size() > 0){
                            for(String ss : additioanlFees){
                                String amount[] = ss.split("<@>");
                                additionFEE =+Double.parseDouble(amount[1]);
                            }
                        }

                        double offerPer = Double.parseDouble(offerPercentage);
                        double totalAmount =  Double.parseDouble(s.toString());
                        redeemAmount  = (totalAmount * offerPer ) / 100 ;
                        redeemRewardPointsEditText.setText("- "+String.format(Locale.ENGLISH,"%.2f",redeemAmount));
                        rewardText.setText(getResources().getString(R.string.RewardOffer)+" ( "+offerPercentage+"% )");
                        double amount = Double.parseDouble(amountPayEditText.getText().toString().trim());

                        double FEE_AMOUNT=0;

                        if(fee_option.equalsIgnoreCase("percentage")){
                            feeText.setText("Fee");
                            FEE_AMOUNT = (amount-redeemAmount) * Double.parseDouble(fee_amount) / 100 ;
                        }else{
                            FEE_AMOUNT = Double.parseDouble(fee_amount);
                        }
                        oyopayFeeEditText.setText(String.format("%.2f",FEE_AMOUNT));

                        if(FEE_AMOUNT > 0){
                            foloosiFeeLayout.setVisibility(View.VISIBLE);
                        }else{
                            foloosiFeeLayout.setVisibility(View.GONE);
                        }

                        double TOTAL = amount + FEE_AMOUNT + tip - redeemAmount + additionFEE;
                        amountPayEditText2.setText(String.format(Locale.ENGLISH,"%.2f",TOTAL));

                        if(FEE_AMOUNT > 0 || redeemAmount > 0 || tip > 0 || totalAmount>0){
                            amountPayEditText2.setVisibility(View.VISIBLE);
                        }else{
                            amountPayEditText2.setVisibility(View.GONE);
                        }

                    }

                }else{

                    amountEditText.setText("");
                    redeemRewardPointsEditText.setText("");
                    foloosiFeeLayout.setVisibility(View.GONE);
                    amountPayEditText2.setText("");
                    tipEditText.setText("");
                    amountPayEditText2.setVisibility(View.GONE);
                }
            }
        });



        tipEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){

                    if (Double.parseDouble(s.toString()) > 0) {

                        Double amount = Double.parseDouble(amountPayEditText.getText().toString());
                        double fee = Double.parseDouble("0");
                        double tip = Double.parseDouble(s.toString());
                        double total = (amount-redeemAmount) * fee / 100 ;
                        double ovTotal = amount + tip + total - redeemAmount + additionFEE ;
                        amountPayEditText2.setText(String.format(Locale.ENGLISH,"%.2f",ovTotal));
                        amountPayEditText2.setVisibility(View.VISIBLE);

                    }else{

                        Double amount = Double.parseDouble(amountPayEditText.getText().toString());
                        double fee = Double.parseDouble("0");
                        double total = (amount-redeemAmount) * fee / 100 ;
                        double oveTotal = amount + total - redeemAmount + additionFEE;
                        amountPayEditText2.setText(String.format(Locale.ENGLISH,"%.2f",oveTotal));


                       /* if(fee > 0 || redeemAmount > 0){
                            amountPayEditText2.setVisibility(View.VISIBLE);
                        }else{
                            amountPayEditText2.setVisibility(View.GONE);
                        }
*/
                    }
                }else{

                    if(!amountPayEditText.getText().toString().isEmpty()){

                        Double amount = Double.parseDouble(amountPayEditText.getText().toString());
                        double fee = Double.parseDouble("0");
                        double total = (amount-redeemAmount) * fee / 100 ;
                        double oveTotal = amount + total - redeemAmount + additionFEE;
                        amountPayEditText2.setText(String.format(Locale.ENGLISH,"%.2f",oveTotal));


                       /* if(fee > 0 || redeemAmount > 0){
                            amountPayEditText2.setVisibility(View.VISIBLE);
                        }else{
                            amountPayEditText2.setVisibility(View.GONE);
                        }*/
                    }

                }

            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scrollView.scrollTo(0,0);
                hideKeyboard(CMScanAndPayScreen.this);

                if (amountPayEditText.getText().toString().trim().isEmpty()) {
                    toast(getResources().getString(R.string.PleaseEnterAmount));
                } else{

                    double NEYPAY_AMOUNT = Double.parseDouble(amountPayEditText2.getText().toString().trim());
                    double MY_BALANCE = Double.parseDouble(loginSession.getbalanceAmount());

                    if(MY_BALANCE >= NEYPAY_AMOUNT){

                        Intent intent1 = new Intent(CMScanAndPayScreen.this, PassCodeVerifiedScreen.class);
                        startActivityForResult(intent1,4);

                    }else{
                        toast("Insufficient wallet balance");
                    }

                }

            }
        });

        TipsEnterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tipEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, payNowButton.getBottom());
                    }
                });

            }
        });


        fabArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amountPayEditText.getText().toString().trim().isEmpty()) {
                    toast(getResources().getString(R.string.PleaseEnterAmount));
                } else {
                    fabArrowButton.setVisibility(View.GONE);
                    payableLayout.setVisibility(View.VISIBLE);
                    uparrow.setVisibility(View.VISIBLE);
                    hideKeyboard(CMScanAndPayScreen.this);
                }
            }
        });

        amountlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amountPayEditText.setSelection(amountPayEditText.getText().toString().length());
                fabArrowButton.setVisibility(View.VISIBLE);
                payableLayout.setVisibility(View.GONE);
                uparrow.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });
    }


    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_SCAN_AND_PAY:

                QRScanSuccess qrScanSuccess = (QRScanSuccess)result;

                if(qrScanSuccess.message.equalsIgnoreCase("paid successfully")){

                    toast(qrScanSuccess.message);
                    Intent intent = new Intent(CMScanAndPayScreen.this, SuccessScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("screen", "PayMoney");
                    intent.putExtra("fromScreen", "CCmoneySendScreen");
                    intent.putExtra("amount", qrScanSuccess.data.sender_amount);
                    intent.putExtra("currency", qrScanSuccess.data.send_currency);
                    intent.putExtra("date", qrScanSuccess.data.created);
                    intent.putExtra("receiverName",merchant_name);
                    intent.putExtra("description", qrScanSuccess.data.description);
                    intent.putExtra("transaction_no", qrScanSuccess.data.transaction_no);
                    startActivity(intent);
                    finish();
                }else{
                    toast(qrScanSuccess.message);
                }

                break;
        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {
            if (resultCode == 4) {

                tipAmount = tipEditText.getText().toString().trim();

                if (isConnectingToInternet()) {
                    conversionAmount = amountPayEditText.getText().toString().trim();
                    Double amount = Double.parseDouble(amountPayEditText.getText().toString());
                    double passAmount = amount - redeemAmount;
                    Double Cmount = Double.parseDouble(conversionAmount);
                    double cpassAmount = Cmount - redeemAmount;
                    Map<String, String> params = new HashMap<>();
                    params.put("merchant_id",merchantId);
                    params.put("description",descriptionEditText.getText().toString().trim());
                    params.put("send_currency",loginSession.getcurrencyCodee());
                    params.put("send_amount",String.format("%.2f",passAmount));
                    params.put("tip_amount",tipAmount);
                    params.put("receive_currency",receiverCurrencyCode);
                    params.put("reward_offer_amount",String.format("%.2f",redeemAmount));
                    params.put("receive_amount",String.format("%.2f",cpassAmount));
                    params.put("subUser","0");
                    params.put("scan_type","profile_scan");
                    Log.e("params",""+params);
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(CMScanAndPayScreen.this,params, REQ_SCAN_AND_PAY,"POST","");

                } else {

                    noInternetAlertDialog();
                }

            }
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


    private class AdditionalFeeListAdapter extends RecyclerView.Adapter<AdditionalFeeListAdapter.MyViewHolder>{

        private Context context;
        private ArrayList<String> additioanlFees;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView additionalFeeText,additionalFeeTextView;

            public MyViewHolder(View view) {
                super(view);
                additionalFeeText = view.findViewById(R.id.additionalFeeText);
                additionalFeeTextView = view.findViewById(R.id.additionalFeeTextView);
            }
        }

        public AdditionalFeeListAdapter(Context contextt, ArrayList<String> additioanlFees) {
            this.context = contextt;
            this.additioanlFees = additioanlFees;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.additinalfee_list, parent, false);


            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            String splitValue[] = additioanlFees.get(position).split("<@>");
            holder.additionalFeeText.setText(splitValue[0]);
            holder.additionalFeeTextView.setText(String.format("%.2f",Double.parseDouble(splitValue[1])));
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public int getItemCount() {
            return additioanlFees.size();
        }

    }
}
