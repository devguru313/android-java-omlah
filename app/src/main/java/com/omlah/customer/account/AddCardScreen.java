package com.omlah.customer.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.YearMonthPickerDialog;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 06-04-2018.
 */

public class AddCardScreen extends BaseActivity implements ServerListener{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequest;

    //Create xml objects
    @BindView(R.id.cardNumberEditText)EditText cardNumberEditText;
    @BindView(R.id.dateEditText)EditText dateEditText;
    @BindView(R.id.cvvEditText)EditText cvvEditText;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.confirmButton)Button confirmButton;

    String a;
    int keyDel;

    String  cardNumber="",
            cardMonth="",
            cardYear="",
            cardCvv="",
            stripekey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_card);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);

        loginSession = LoginSession.getInstance(this);
        serverRequest = ServerRequestwithHeader.getInstance(this);


        Intent intent  =getIntent();
        if(intent!=null){
              stripekey = intent.getStringExtra("stripekey");
        }

        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean flag = true;
                boolean flagOpen = true;
                String eachBlock[] = cardNumberEditText.getText().toString().split("-");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {

                    cardNumberEditText.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((cardNumberEditText.getText().length() + 1) % 5) == 0) {

                            if (cardNumberEditText.getText().toString().split("-").length <= 3) {
                                cardNumberEditText.setText(cardNumberEditText.getText() + "-");
                                cardNumberEditText.setSelection(cardNumberEditText.getText().length());
                            }
                        }
                        a = cardNumberEditText.getText().toString();
                    } else {
                        a = cardNumberEditText.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    cardNumberEditText.setText(a);
                }

                if(flagOpen){
                    if(count >= 19){
                        dateEditText.performClick();
                        cvvEditText.requestFocus();
                        flagOpen = false;
                    }
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(2018,00,01);

                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(AddCardScreen.this,
                        calendar,
                        new YearMonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onYearMonthSet(int year, int month) {

                                double realvalue = month;
                                double add = 1;
                                double finalValue = realvalue+add;
                                String finalstring = String.valueOf(finalValue).replace(".0","");
                                dateEditText.setText(finalstring+"/"+year);
                            }
                        });

                yearMonthPickerDialog.show();
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cardNumber = cardNumberEditText.getText().toString().trim().replace("-", "");
                String cardDateMonth = dateEditText.getText().toString();
                if (!cardDateMonth.isEmpty()) {
                    String split[] = cardDateMonth.split("/");
                    cardMonth = split[0];
                    cardYear = split[1];
                }
                cardCvv = cvvEditText.getText().toString().trim();

                if (cardNumber.isEmpty()) {

                    toast(getResources().getString(R.string.PleaseEnterCardNumber));

                } else if (cardMonth.isEmpty()) {

                    toast(getResources().getString(R.string.PleaseEnterexpiryMonth));

                }else if (cardCvv.isEmpty()) {

                    toast(getResources().getString(R.string.PleaseEnterCvvnumber));

                } else if (cardCvv.length() != 3) {

                    toast(getResources().getString(R.string.PleaseEnterValidCvvnumber));

                } else {

                    final Card card = new Card(cardNumber, Integer.parseInt(cardMonth), Integer.parseInt(cardYear), cardCvv);
                    boolean validation = card.validateCard();
                    if (validation) {

                        showProgressDialog();
                        new Stripe(getApplication()).createToken(card, stripekey, new TokenCallback() {
                            @Override
                            public void onSuccess(Token token) {
                                hideProgressDialog();
                                String stripeToken = token.getId();
                                token.getCard().getCustomerId();

                                if(isConnectingToInternet()){

                                    final Map<String, String> param = new HashMap<String, String>();
                                    param.put("stripe_token_id",stripeToken);
                                    param.put("card_no",token.getCard().getLast4());
                                    param.put("card_type",token.getCard().getBrand());
                                    param.put("card_validity",token.getCard().getExpMonth()+"/"+token.getCard().getExpYear());
                                    showProgressDialog();
                                    Log.e("param",""+param);
                                    serverRequest.createRequest(AddCardScreen.this,param, RequestID.REQ_ADD_CARD,"POST","");


                                }else{
                                    noInternetAlertDialog();
                                }

                            }
                            @Override
                            public void onError(Exception error) {

                                hideProgressDialog();
                            }

                        });


                    }else{
                        toast(getResources().getString(R.string.PleaseEnterValidCardDetails));
                    }

                }
            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {
        hideProgressDialog();
        finish();
    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
        toast(error);
    }
}
