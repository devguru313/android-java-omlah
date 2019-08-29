package com.omlah.customer.qrscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CircleTransform;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.MoneyRequestSuccess;
import com.omlah.customer.model.SentMoneySuccess;
import com.omlah.customer.model.TaxSuccess;
import com.omlah.customer.otp.PassCodeVerifiedScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.SuccessScreen;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.omlah.customer.service.RequestID.REQ_AMOUNT_TAX;
import static com.omlah.customer.service.RequestID.REQ_PAYMONEY;
import static com.omlah.customer.service.RequestID.REQ_REQEST_MONEY;

public class CCScanAndPayScreen extends BaseActivity implements ServerListener{

    LoginSession loginSession;
    ServerRequestwithHeader createRequest;

    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.merchantImageView)ImageView merchantImageView;
    @BindView(R.id.shopNameEditText)TextView shopNameEditText;
    @BindView(R.id.amountlayout)TextView amountlayout;
    @BindView(R.id.amountPayEditText)EditText amountPayEditText;
    @BindView(R.id.descriptionEditText)EditText descriptionEditText;
    @BindView(R.id.fabArrowButton)FloatingActionButton fabArrowButton;
    @BindView(R.id.fabtickButton)FloatingActionButton fabtickButton;
    @BindView(R.id.currencyCodeTextView)TextView currencyCodeTextView;
    @BindView(R.id.foloosiFeeLayout)RelativeLayout foloosiFeeLayout;

    @BindView(R.id.feeAmountLayout)RelativeLayout feeAmountLayout;
    @BindView(R.id.feeAmountText)TextView feeAmountText;
    @BindView(R.id.feeAmountTextView)TextView feeAmountTextView;

    @BindView(R.id.extraFeeAmountLayout)RelativeLayout extraFeeAmountLayout;
    @BindView(R.id.extraFeeText)TextView extraFeeText;
    @BindView(R.id.extraFeeAmountTextView)TextView extraFeeAmountTextView;

    @BindView(R.id.uparrow)ImageView uparrow;
    @BindView(R.id.payableLayout)LinearLayout payableLayout;
    @BindView(R.id.actualAmountTextView)TextView actualAmountTextView;
    @BindView(R.id.feeText)TextView feeText;
    @BindView(R.id.feeTextView)TextView feeTextView;
    @BindView(R.id.typeTextView)TextView typeTextView;
    @BindView(R.id.payableAmountTextView)TextView payableAmountTextView;
    @BindView(R.id.payNowButton)Button payNowButton;
    @BindView(R.id.requestNowButton)Button requestNowButton;
    @BindView(R.id.errorText)TextView errorText;

    double taxAmount=0,fee_amount=0,extra_fees=0;
    String receiverName="",passTotalAmount="",passReceiverNumber,type=""
            ,profile_image="",fee_option="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cc_scanandpay_screen);
        hideActionBar();

        ButterKnife.bind(this);
        loginSession = LoginSession.getInstance(this);
        createRequest = ServerRequestwithHeader.getInstance(this);

        currencyCodeTextView.setText(loginSession.getShowCurrency());
        Intent intent = getIntent();
        if(intent!=null){

            receiverName = intent.getStringExtra("name");
            passReceiverNumber = intent.getStringExtra("number");
            typeTextView.setText(intent.getStringExtra("type"));
            type = intent.getStringExtra("type");
            profile_image = intent.getStringExtra("profile_image");
            shopNameEditText.setText(intent.getStringExtra("name"));

            /*if(sendFrom.equalsIgnoreCase("number")){
                String input = passReceiverNumber;
                String number = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
                shopNameEditText.setText(code+" "+number);
            }else{
                shopNameEditText.setText(intent.getStringExtra("name"));
            }*/

            if(profile_image!=null && !profile_image.isEmpty()){

                String getImage[] = profile_image.split("upload");
                String imageFormat = getImage[0]+"upload/w_250,h_250,c_thumb,g_face,r_max"+getImage[1];
                Picasso.with(this)
                        .load(imageFormat)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(getResources().getDrawable(R.drawable.user_image_post))
                        .transform(new CircleTransform())
                        .into(merchantImageView);
            }

        }

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


        amountPayEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        fabArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(amountPayEditText.getText().toString().isEmpty()) && Double.parseDouble(amountPayEditText.getText().toString()) > 0){

                    fabArrowButton.hide();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabtickButton.show();
                        }
                    },500);
                    descriptionEditText.requestFocus();

                }else{
                    toast("Please enter a amount");
                }
            }
        });

        amountlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabtickButton.hide();
                uparrow.setVisibility(View.GONE);
                payableLayout.setVisibility(View.GONE);
                errorText.setVisibility(View.GONE);
                requestNowButton.setVisibility(View.GONE);
                amountPayEditText.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fabArrowButton.show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                    }
                },500);
            }
        });

        fabtickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                passTotalAmount = amountPayEditText.getText().toString().trim();

                if(type.equalsIgnoreCase("Request from")){

                    hideKeyboard(CCScanAndPayScreen.this);
                    fabtickButton.hide();
                    requestNowButton.setVisibility(View.VISIBLE);

                }else{

                    if (!isConnectingToInternet()) {
                        noInternetAlertDialog();
                    } else {

                        hideKeyboard(CCScanAndPayScreen.this);
                        fabtickButton.hide();
                      //  uparrow.setVisibility(View.VISIBLE);
                      //  payableLayout.setVisibility(View.VISIBLE);
                      //  payNowButton.setVisibility(View.GONE);
                      //  progressbar.setVisibility(View.VISIBLE);
                        actualAmountTextView.setText(String.format("%.2f",Double.parseDouble(passTotalAmount)));
                        Map<String, String> params = new HashMap<>();
                        showProgressDialog();
                        createRequest.createRequest(CCScanAndPayScreen.this, params, REQ_AMOUNT_TAX, "GET", passReceiverNumber + "/" + passTotalAmount);
                    }
                }


            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent payNowButton = new Intent(CCScanAndPayScreen.this, PassCodeVerifiedScreen.class);
                startActivityForResult(payNowButton,4);
            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        requestNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent payNowButton = new Intent(CCScanAndPayScreen.this, PassCodeVerifiedScreen.class);
                startActivityForResult(payNowButton,4);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {
            if (resultCode == 4) {
                if (!isConnectingToInternet()) {
                    noInternetAlertDialog();
                } else {

                    if (type.equalsIgnoreCase("Request from")) {

                        Map<String, String> params = new HashMap<>();
                        params.put("phone_number", passReceiverNumber);
                        params.put("amount", passTotalAmount);
                        params.put("description", descriptionEditText.getText().toString());
                        showProgressDialog();
                        createRequest.createRequest(CCScanAndPayScreen.this, params, REQ_REQEST_MONEY, "POST", "");
                    } else {

                        double NEYPAY_AMOUNT = Double.parseDouble(passTotalAmount);
                        double MY_BALANCE = Double.parseDouble(loginSession.getbalanceAmount());
                        if (MY_BALANCE >= NEYPAY_AMOUNT) {
                            Map<String, String> params = new HashMap<>();
                            params.put("phone_number", passReceiverNumber);
                            params.put("amount", passTotalAmount);
                            params.put("description", descriptionEditText.getText().toString());
                            showProgressDialog();
                            createRequest.createRequest(CCScanAndPayScreen.this, params, REQ_PAYMONEY, "POST", "");
                        } else {
                            toast("Insufficient wallet balance");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        switch (requestID){

            case REQ_AMOUNT_TAX:
                hideProgressDialog();
                TaxSuccess taxSuccess = (TaxSuccess)result;
                payNowButton.setVisibility(View.VISIBLE);

                if(taxSuccess.success == 1){

                    fee_amount = Double.parseDouble(taxSuccess.customerFees.fee_amount);
                    fee_option = taxSuccess.customerFees.fee_option;
                    extra_fees = Double.parseDouble(taxSuccess.customerFees.extra_fees);
                    taxAmount = Double.parseDouble(taxSuccess.tax.taxAmount);

                    //TAX LAYOUT
                    if(taxAmount > 0){
                        feeText.setText("Tax ( "+taxSuccess.tax.taxPercentage+"% )");
                        feeTextView.setText(String.format("%.2f",Double.parseDouble(taxSuccess.tax.taxAmount)));
                        foloosiFeeLayout.setVisibility(View.VISIBLE);
                    }else{
                        foloosiFeeLayout.setVisibility(View.GONE);
                    }

                    //FEE LAYOUT
                    if(fee_amount > 0 || extra_fees > 0){
                        if(fee_option.equalsIgnoreCase("percentage")){
                            feeAmountText.setText("Fee");
                            Double actualAmount = Double.parseDouble(actualAmountTextView.getText().toString().trim());
                            Double feeAmountt = ( actualAmount * fee_amount ) / 100 + extra_fees;
                            feeAmountTextView.setText(String.format(Locale.ENGLISH,"%.2f",feeAmountt));
                        }else{
                            feeAmountText.setText("Fee");
                            feeAmountTextView.setText(String.format(Locale.ENGLISH,"%.2f",fee_amount+extra_fees));
                        }
                        feeAmountLayout.setVisibility(View.VISIBLE);
                    }else{
                        feeAmountLayout.setVisibility(View.GONE);
                    }


                    //EXTRAA FEE AMOUNT
                    if(extra_fees > 0){
                        extraFeeAmountLayout.setVisibility(View.GONE);
                       // extraFeeAmountTextView.setText(String.format(Locale.ENGLISH,"%.2f",extra_fees));
                    }else{
                        extraFeeAmountLayout.setVisibility(View.GONE);
                    }


                    Double totalPay = Double.parseDouble(passTotalAmount) + taxAmount + Double.parseDouble(feeAmountTextView.getText().toString().trim());
                    payableAmountTextView.setText(String.format("%.2f",totalPay));

                    payableLayout.setVisibility(View.VISIBLE);
                    requestNowButton.setVisibility(View.GONE);
                    uparrow.setVisibility(View.VISIBLE);
                    payNowButton.setVisibility(View.VISIBLE);


                }else{

                    requestNowButton.setVisibility(View.VISIBLE);
                    requestNowButton.setText("Proceed to pay");
                    uparrow.setVisibility(View.GONE);
                    payableLayout.setVisibility(View.GONE);
                    foloosiFeeLayout.setVisibility(View.GONE);

                }

                break;

            case REQ_PAYMONEY:

                hideProgressDialog();

                try{
                    SentMoneySuccess sentMoneySuccess = (SentMoneySuccess)result;
                    if(sentMoneySuccess.message.equalsIgnoreCase("Invalid receiver selected")){
                        toast("Invalid receiver selected");
                    }else{
                        Intent intent = new Intent(CCScanAndPayScreen.this, SuccessScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("screen", "PayMoney");
                        intent.putExtra("fromScreen", "CCmoneySendScreen");
                        intent.putExtra("amount", sentMoneySuccess.data.sender_amount);
                        intent.putExtra("currency", sentMoneySuccess.data.send_currency);
                        intent.putExtra("date", sentMoneySuccess.data.created);
                        intent.putExtra("receiverName", receiverName);
                        intent.putExtra("description", sentMoneySuccess.data.description);
                        intent.putExtra("transaction_no", sentMoneySuccess.data.transaction_no);
                        startActivity(intent);
                        finish();
                    }

                }catch (Exception e){e.printStackTrace();
                    toast("Invalid receiver selected");
                }

                break;

            case REQ_REQEST_MONEY:

                hideProgressDialog();

                MoneyRequestSuccess moneyRequestSuccess = (MoneyRequestSuccess) result;

                if (!(moneyRequestSuccess.data == null)) {

                    Intent intent = new Intent(CCScanAndPayScreen.this, SuccessScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    intent.putExtra("screen","RequestMoney");
                    intent.putExtra("fromScreen","RequestMoneyScreen");
                    intent.putExtra("amount", passTotalAmount);
                    intent.putExtra("currency", loginSession.getcurrencyCodee());
                    intent.putExtra("date", moneyRequestSuccess.data.created);

                    if (receiverName.isEmpty()) {
                        intent.putExtra("receiverName", passReceiverNumber);
                    } else {
                        intent.putExtra("receiverName", receiverName);
                    }

                    intent.putExtra("description", descriptionEditText.getText().toString());
                    intent.putExtra("transaction_no", moneyRequestSuccess.data.request_no);


                    startActivity(intent);
                    finish();

                } else {
                    toast("Invalid recipient to request");
                }

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        switch (requestID){

            case REQ_AMOUNT_TAX:
                hideProgressDialog();
                requestNowButton.setVisibility(View.VISIBLE);
                requestNowButton.setText("Invalid RPay customer");
                requestNowButton.setEnabled(false);
                errorText.setVisibility(View.VISIBLE);
                break;

            case REQ_PAYMONEY:
                hideProgressDialog();
                toast(error);
                break;

            case REQ_REQEST_MONEY:
                hideProgressDialog();
                toast(error);
                break;
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

}
