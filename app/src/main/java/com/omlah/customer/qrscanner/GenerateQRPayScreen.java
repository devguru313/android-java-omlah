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
import android.text.TextWatcher;
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


/**
 * Created by admin on 07-10-2017.
 */

public class GenerateQRPayScreen extends BaseActivity implements ServerListener {

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml files
    @BindView(R.id.shopNameEditText)TextView shopNameEditText;
    @BindView(R.id.OfferLayout)RelativeLayout OfferLayout;
    @BindView(R.id.offerEditText)TextView offerEditText;
    @BindView(R.id.offerText)TextView offerText;
    @BindView(R.id.couponLayout)RelativeLayout couponLayout;
    @BindView(R.id.voucherEditText)TextView voucherEditText;
    @BindView(R.id.coupontext)TextView coupontext;
    @BindView(R.id.scrollView)ScrollView scrollView;
    @BindView(R.id.redeemPointLayout)RelativeLayout redeemPointLayout;
    @BindView(R.id.rewardText)TextView rewardText;
    @BindView(R.id.redeemRewardPointsEditText)TextView redeemRewardPointsEditText;
    @BindView(R.id.currencyCodeTextView)TextView currencyCodeTextView;
    @BindView(R.id.amountEditText)TextView amountEditText;
    @BindView(R.id.amountPayEditText)EditText amountPayEditText;
    @BindView(R.id.amountPayEditText2)TextView amountPayEditText2;
    @BindView(R.id.foloosiFeeLayout)RelativeLayout foloosiFeeLayout;
    @BindView(R.id.oyopayFeeEditText)TextView oyopayFeeEditText;
    @BindView(R.id.feeText)TextView feeText;
    @BindView(R.id.descriptionEditText)EditText descriptionEditText;
    @BindView(R.id.earnPointsTextView)TextView earnPointsTextView;
    @BindView(R.id.TipsEnterText)TextView TipsEnterText;
    @BindView(R.id.tipEditText)EditText tipEditText;
    @BindView(R.id.payNowButton)Button payNowButton;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.merchantImageView)ImageView merchantImageView;
    @BindView(R.id.fabArrowButton)ImageView fabArrowButton;
    @BindView(R.id.payableLayout)LinearLayout payableLayout;
    @BindView(R.id.uparrow)ImageView uparrow;
    @BindView(R.id.additionalFeeRecyclerView)RecyclerView additionalFeeRecyclerView;

    String merchant_id = "", merchant_name = "", merchant_profile = "", description = "",
            send_currency = "", send_amount = "", receive_currency = "", receive_amount = "", subUser="", offer_amount = "",
            offer_percentage = "",offer_name="",oyopay_fee="0",redeemAmount="0",earnPoints="0",redeemAmountPercentage="0",
            senderRewardBalance="0", voucher_title="",voucher_percentage="0",voucher_Amount="0",voucher_description="",
            voucher_code="",sender_currency_difference="";

    double additionFEE=0,sendAmount=0,offerAmount=0,showAmount=0,totalAmount=0,rewardOfferAmount=0,voucherAmount=0,tipAmount=0;

    ArrayList<String> additioanlFees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_to_merchant);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        currencyCodeTextView.setText(loginSession.getcurrencyCodee());

        fabArrowButton.setVisibility(View.GONE);
        payableLayout.setVisibility(View.VISIBLE);
        uparrow.setVisibility(View.VISIBLE);
        hideKeyboard(GenerateQRPayScreen.this);

        Intent intent = getIntent();
        if(intent!=null){

            merchant_id      = intent.getStringExtra("merchant_id");
            merchant_name    = intent.getStringExtra("merchant_name");
            merchant_profile = intent.getStringExtra("merchant_profile");
            description      = intent.getStringExtra("description");
            send_currency    = intent.getStringExtra("send_currency");
            send_amount      = intent.getStringExtra("send_amount");
            receive_currency = intent.getStringExtra("receive_currency");
            receive_amount   = intent.getStringExtra("receive_amount");
            subUser          = intent.getStringExtra("subUser");
            offer_amount     = intent.getStringExtra("offer_amount");
            offer_percentage = intent.getStringExtra("offer_percentage");
            offer_name       = intent.getStringExtra("offer_name");
            oyopay_fee       = intent.getStringExtra("oyopay_fee");
            redeemAmountPercentage     = intent.getStringExtra("redeemAmountPercentage");
            redeemAmount     = intent.getStringExtra("redeemAmount");
            earnPoints       = intent.getStringExtra("earnPoints");
            senderRewardBalance  = intent.getStringExtra("senderRewardBalance");

            voucher_title        = intent.getStringExtra("voucher_title");
            voucher_percentage   = intent.getStringExtra("voucherPercentage");
            voucher_Amount   = intent.getStringExtra("voucherAmount");
            voucher_description  = intent.getStringExtra("voucher_description");
            voucher_code  = intent.getStringExtra("voucher_code");
            sender_currency_difference  = intent.getStringExtra("sender_currency_difference");

            additioanlFees         = (ArrayList<String>) getIntent().getSerializableExtra("additionalFee");


            if(additioanlFees.size() > 0){

                Log.e("additioanlFees",""+additioanlFees.toString());

                additionalFeeRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
                additionalFeeRecyclerView.setLayoutManager(linearLayoutManager);
                additionalFeeRecyclerView.setItemAnimator(new DefaultItemAnimator());
                additionalFeeRecyclerView.setHasFixedSize(true);
                AdditionalFeeListAdapter copiedCodeListAdapter = new AdditionalFeeListAdapter(this,additioanlFees);
                additionalFeeRecyclerView.setAdapter(copiedCodeListAdapter);
                copiedCodeListAdapter.notifyDataSetChanged();

            }else{
                additionalFeeRecyclerView.setVisibility(View.GONE);
            }


            if(additioanlFees.size() > 0){
                for(String ss : additioanlFees){
                    String amount[] = ss.split("<@>");
                    additionFEE =+Double.parseDouble(amount[1]);
                }
            }

            //Currency changed when customer currency and merchant currency different means
            send_amount     = changeCustomerCurrency(send_amount,sender_currency_difference);
            receive_amount  = changeCustomerCurrency(receive_amount,sender_currency_difference);
            offer_amount    = changeCustomerCurrency(offer_amount,sender_currency_difference);
            oyopay_fee      = changeCustomerCurrency(oyopay_fee,sender_currency_difference);
            redeemAmount    = changeCustomerCurrency(redeemAmount,sender_currency_difference);
            voucher_Amount  = changeCustomerCurrency(voucher_Amount,sender_currency_difference);


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

            shopNameEditText.setText(merchant_name);

            //Send Amount to server
            sendAmount = Double.parseDouble(send_amount);

            //voucher
            voucherAmount = Double.parseDouble(voucher_Amount);
            if(voucherAmount>0){
                couponLayout.setVisibility(View.VISIBLE);
                coupontext.setText("Voucher ( "+voucher_percentage+"% )");
                voucherEditText.setText("- "+String.format(Locale.ENGLISH,"%.2f",voucherAmount));
            }else{
                couponLayout.setVisibility(View.GONE);
            }

            //Offer amount
            offerAmount = Double.parseDouble(offer_amount);
            offerText.setText(getResources().getString(R.string.offer)+" ( "+offer_percentage+"% )");
            offerEditText.setText("- "+String.format(Locale.ENGLISH,"%.2f",offerAmount));

            //Show Amount
            rewardOfferAmount = Double.parseDouble(redeemAmount);
            showAmount = sendAmount+offerAmount+voucherAmount+rewardOfferAmount;
            amountEditText.setText(String.format(Locale.ENGLISH,"%.2f",showAmount));

            //show OyopayFee
            Double oyopayFee = Double.parseDouble(oyopay_fee);

            if(oyopayFee>0){
                feeText.setText(getResources().getString(R.string.PopPayFee));
                oyopayFeeEditText.setText(String.format(Locale.ENGLISH,"%.2f",oyopayFee));
                foloosiFeeLayout.setVisibility(View.VISIBLE);
            }else{
                foloosiFeeLayout.setVisibility(View.GONE);
            }

            //redeem amount

            showAmount = Double.parseDouble(String.format("%.2f",showAmount));
            oyopayFee = Double.parseDouble(String.format("%.2f",oyopayFee));
            rewardOfferAmount = Double.parseDouble(String.format("%.2f",rewardOfferAmount));
            offerAmount = Double.parseDouble(String.format("%.2f",offerAmount));
            voucherAmount = Double.parseDouble(String.format("%.2f",voucherAmount));
            totalAmount = showAmount+oyopayFee-rewardOfferAmount-offerAmount-voucherAmount + additionFEE;
            amountPayEditText.setText(String.format(Locale.ENGLISH,"%.2f",totalAmount));
            amountPayEditText2.setText(String.format(Locale.ENGLISH,"%.2f",totalAmount));

            //
            if(rewardOfferAmount > 0){
                redeemPointLayout.setVisibility(View.VISIBLE);
                rewardText.setText(getResources().getString(R.string.RewardOffer)+" ( "+redeemAmountPercentage+"% )");
                redeemRewardPointsEditText.setText("- "+String.format(Locale.ENGLISH,"%.2f",rewardOfferAmount));
            }else{
                redeemPointLayout.setVisibility(View.GONE);
            }

            if(Double.parseDouble(earnPoints) > 0){
                earnPointsTextView.setVisibility(View.VISIBLE);
                earnPointsTextView.setText(getResources().getString(R.string.Youget)+" "+earnPoints+" "+getResources().getString(R.string.RewardPointsAfterPaymentSuccess));
            }else{
                earnPointsTextView.setVisibility(View.GONE);
            }

            //
            if(oyopayFee > 0){
                foloosiFeeLayout.setVisibility(View.VISIBLE);
            }else{
                foloosiFeeLayout.setVisibility(View.GONE);
            }

            //
            if(offerAmount > 0){
                OfferLayout.setVisibility(View.VISIBLE);
            }else{
                OfferLayout.setVisibility(View.GONE);
            }


            descriptionEditText.setText(description);

        }

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scrollView.scrollTo(0,0);
                hideKeyboard(GenerateQRPayScreen.this);


            }
        });

        tipEditText.addTextChangedListener(new TextWatcher() {
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
                        tipAmount = Double.parseDouble(s.toString());
                        double amount = totalAmount;
                        double newtotalAmount = tipAmount+amount+additionFEE;
                        if(amountPayEditText.isShown()){
                            amountPayEditText.setText(String.format(Locale.ENGLISH,"%.2f",newtotalAmount));
                            amountPayEditText2.setText(String.format(Locale.ENGLISH,"%.2f",newtotalAmount));
                        }else{
                            amountEditText.setText(String.format(Locale.ENGLISH,"%.2f",newtotalAmount));
                        }
                    }else{
                        tipAmount = 0;
                    }
                }else{

                    tipAmount = 0;
                    if(amountPayEditText.isShown()){
                        amountPayEditText.setText(String.format(Locale.ENGLISH,"%.2f",totalAmount));
                        amountPayEditText2.setText(String.format(Locale.ENGLISH,"%.2f",totalAmount));
                    }else{
                        amountEditText.setText(String.format(Locale.ENGLISH,"%.2f",totalAmount));
                    }
                }
            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
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

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                scrollView.scrollTo(0,0);
                hideKeyboard(GenerateQRPayScreen.this);

                double NEYPAY_AMOUNT =Double.parseDouble(amountPayEditText2.getText().toString().trim());
                double MY_BALANCE = Double.parseDouble(loginSession.getbalanceAmount());

                if(MY_BALANCE >= NEYPAY_AMOUNT){

                    Intent intent1 = new Intent(GenerateQRPayScreen.this, PassCodeVerifiedScreen.class);
                    startActivityForResult(intent1,4);

                }else{
                    toast(getResources().getString(R.string.Insufficientwalletbalance));
                }

            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        switch (requestID) {

            case REQ_SCAN_AND_PAY:

                QRScanSuccess qrScanSuccess = (QRScanSuccess)result;

                if(qrScanSuccess.message.equalsIgnoreCase("paid successfully")){

                    toast(qrScanSuccess.message);
                    Intent intent = new Intent(GenerateQRPayScreen.this, SuccessScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("screen", "PayMoney");
                    intent.putExtra("fromScreen", "CCmoneySendScreen");
                    intent.putExtra("amount", changeCustomerCurrency(qrScanSuccess.data.sender_amount,sender_currency_difference));
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
        switch (requestID){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 4) {
            if (resultCode == 4) {

                if(isConnectingToInternet()){

                    Map<String, String> params = new HashMap<>();
                    params.put("merchant_id",merchant_id);
                    params.put("description",description);
                    params.put("send_currency",send_currency);
                    params.put("send_amount",changePayCurrency(send_amount,sender_currency_difference));
                    params.put("receive_currency",receive_currency);
                    params.put("receive_amount",changePayCurrency(receive_amount,sender_currency_difference));
                    params.put("offer_name",offer_name);
                    params.put("offer_amount",changePayCurrency(offer_amount,sender_currency_difference));
                    params.put("percentage",offer_percentage);
                    params.put("tip_amount", String.valueOf(tipAmount));
                    params.put("subUser",subUser);
                    params.put("sender_reward_balance",senderRewardBalance);
                    params.put("reward_offer_percentage",redeemAmountPercentage);
                    params.put("reward_offer_amount",changePayCurrency(redeemAmount,sender_currency_difference));
                    params.put("future_reward",earnPoints);
                    params.put("voucher_title",voucher_title);
                    params.put("voucher_percentage",voucher_percentage);
                    params.put("voucher_amount",changePayCurrency(voucher_Amount,sender_currency_difference));
                    params.put("voucher_id",voucher_code);
                    params.put("scan_type","scan_pay");
                    Log.e("params",""+params);
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(GenerateQRPayScreen.this,params, REQ_SCAN_AND_PAY,"POST","");

                }else{
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
