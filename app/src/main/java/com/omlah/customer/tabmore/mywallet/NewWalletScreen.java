package com.omlah.customer.tabmore.mywallet;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omlah.customer.R;
import com.omlah.customer.account.AddNewBankAccountScreen;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.YearMonthPickerDialog;
import com.omlah.customer.model.BankList;
import com.omlah.customer.model.CardErrorModel;
import com.omlah.customer.model.CardListModel;
import com.omlah.customer.otp.PassCodeVerifiedScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 09-11-2017.
 */

public class NewWalletScreen extends BaseActivity implements ServerListener{

    //Create class objects
    Utility utility;
    Dialog bankAccountDialog;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequest;

    //Create xml objects
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.backIconImageView2)ImageView backIconImageView2;
    @BindView(R.id.popcoinsBalanceTextView)TextView popcoinsBalanceTextView;
    @BindView(R.id.limitAmountTextView)TextView limitAmountTextView;
    @BindView(R.id.tabRadioGroup)RadioGroup tabRadioGroup;
    @BindView(R.id.addMoneyButton)RadioButton addMoneyButton;
    @BindView(R.id.withdrawButton)RadioButton withdrawButton;

    @BindView(R.id.currencySymbol)TextView currencySymbol;
    @BindView(R.id.currencySymbol2)TextView currencySymbol2;
    @BindView(R.id.enteredAmountTextView)TextView enteredAmountTextView;
    @BindView(R.id.withdrawFeeTextView)TextView withdrawFeeTextView;
    @BindView(R.id.withdrawFeePerTextView)TextView withdrawFeePerTextView;
    @BindView(R.id.cerditToBankTextView)TextView cerditToBankTextView;

    @BindView(R.id.bankNameTextView)TextView bankNameTextView;
    @BindView(R.id.acctNoTextView)TextView acctNoTextView;
    @BindView(R.id.changeButton)TextView changeButton;
    @BindView(R.id.addNewBankTextView)TextView addNewBankTextView;

    @BindView(R.id.scrollView)ScrollView scrollView;
    @BindView(R.id.bankDetailsLayout)RelativeLayout bankDetailsLayout;
    @BindView(R.id.selectBankAccountText)TextView selectBankAccountText;
    @BindView(R.id.confirmButton)Button confirmButton;


    @BindView(R.id.withdrawFirstLayout)RelativeLayout withdrawFirstLayout;
    @BindView(R.id.withdrawAmountEditText)EditText withdrawAmountEditText;
    @BindView(R.id.withdrawPayNowButton)Button withdrawPayNowButton;
    @BindView(R.id.withDrawLayout)RelativeLayout withDrawLayout;

    @BindView(R.id.addMoneyScrollView)ScrollView addMoneyScrollView;
    @BindView(R.id.addMoneyLayout)RelativeLayout addMoneyLayout;
    @BindView(R.id.addMoneyFirstLayout)RelativeLayout addMoneyFirstLayout;
    @BindView(R.id.addMoneyAmountEditText)EditText addMoneyAmountEditText;
    @BindView(R.id.addMoneyPayNowButton)Button addMoneyPayNowButton;


    @BindView(R.id.addMonyEnteredAmountTextView)TextView addMonyEnteredAmountTextView;
    @BindView(R.id.addMonyFeeAmountTextView)TextView addMonyFeeAmountTextView;
    @BindView(R.id.addMonyPayableAmountTextView)TextView addMonyPayableAmountTextView;
    @BindView(R.id.cardTabRadioGroup)RadioGroup cardTabRadioGroup;
    @BindView(R.id.creditCardButton)RadioButton creditCardButton;
    @BindView(R.id.savedCardButton)RadioButton savedCardButton;
    @BindView(R.id.viewAllCardsButton)TextView viewAllCardsButton;
    @BindView(R.id.cardDetailsEnterLayout)LinearLayout cardDetailsEnterLayout;
    @BindView(R.id.showSavedCardLayout)RelativeLayout showSavedCardLayout;
    @BindView(R.id.showEmptyCardLayout)RelativeLayout showEmptyCardLayout;
    @BindView(R.id.cardNumberEditText)EditText cardNumberEditText;
    @BindView(R.id.dateEditText)EditText dateEditText;
    @BindView(R.id.cvvEditText)EditText cvvEditText;
    @BindView(R.id.saveCardCheckBox)CheckBox saveCardCheckBox;
    @BindView(R.id.bottom_sheet) RelativeLayout layoutBottomSheet;
    @BindView(R.id.savedCardListView) ListView savedCardListView;

    @BindView(R.id.cardTypeImage) ImageView cardTypeImage;
    @BindView(R.id.calendarIcon) ImageView calendarIcon;
    @BindView(R.id.cardNumberTextView) TextView cardNumberTextView;
    @BindView(R.id.singleTickImageView) ImageView singleTickImageView;

    @BindView(R.id.withdrawExtraaFeeLayout) RelativeLayout withdrawExtraaFeeLayout;
    @BindView(R.id.extraWithdrawFeeTextView) TextView extraWithdrawFeeTextView;
    @BindView(R.id.extraWithdrawFeeAmountTextView) TextView extraWithdrawFeeAmountTextView;

    double withdrawFee=0,withdrawalExtraFee=0;
    String withdrawShowFee,withdrawal_fee_option = "";

    BankAccountAdapter bankAccountAdapter;
    CardListAdapter cardListAdapter;
    ArrayList<BankList.Data>datas = new ArrayList<>();

    double ENTERAMOUNT=0;
    double CARDENTERAMOUNT=0;
    double passAmount =0;
    String bankID = "", stripekey="";

    boolean payNowButtonClicked = false,savedCardAvailability = false,
            withdrawPayNowButtonClicked = false,addMoneyPayNowButtonClicked = false;
    String a;
    int keyDel;

    BottomSheetBehavior sheetBehavior;

    String  cardNumber="";
    String  cardMonth="";
    String  cardYear="";
    String  cardCvv="";

    //FirstCard Saved
    String selectCardNumber="",selectCardYear="",selectCardID="",seletCardImage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_wallet_screen);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequest = ServerRequestwithHeader.getInstance(this);
        utility = Utility.getInstance(this);

        currencySymbol.setText(loginSession.getShowCurrency());
        currencySymbol2.setText(loginSession.getShowCurrency());
        popcoinsBalanceTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(loginSession.getbalanceAmount())));

        //Fonts
        Typeface font= Typeface.createFromAsset(getAssets(), "font/GothamRounded-Medium.ttf");
        addMoneyButton.setTypeface(font);
        withdrawButton.setTypeface(font);

        //getBank list
        if(isConnectingToInternet()){
            final Map<String, String> param = new HashMap<String, String>();
            showProgressDialog();
            serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_BANK_LIST,"GET","");
        }else{
            noInternetAlertDialog();
        }

        //Switching event
        tabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                switch (i){

                    case R.id.addMoneyButton:

                        addMoneyScrollView.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);

                        if(addMoneyPayNowButtonClicked){
                            addMoneyFirstLayout.setVisibility(View.GONE);
                            addMoneyLayout.setVisibility(View.VISIBLE);
                            confirmButton.setVisibility(View.VISIBLE);
                        }else{
                            addMoneyFirstLayout.setVisibility(View.VISIBLE);
                            addMoneyLayout.setVisibility(View.GONE);
                            confirmButton.setVisibility(View.GONE);
                        }

                        break;

                    case R.id.withdrawButton:

                        addMoneyScrollView.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                        if(withdrawPayNowButtonClicked){
                            withdrawFirstLayout.setVisibility(View.GONE);
                            withDrawLayout.setVisibility(View.VISIBLE);
                            confirmButton.setVisibility(View.VISIBLE);
                        }else{
                            withdrawFirstLayout.setVisibility(View.VISIBLE);
                            withDrawLayout.setVisibility(View.GONE);
                            confirmButton.setVisibility(View.GONE);
                        }

                        break;

                }
            }
        });

        cardTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){

                    case R.id.creditCardButton:

                        cardDetailsEnterLayout.setVisibility(View.VISIBLE);
                        showSavedCardLayout.setVisibility(View.GONE);
                        viewAllCardsButton.setVisibility(View.GONE);
                        showEmptyCardLayout.setVisibility(View.GONE);

                        break;

                    case R.id.savedCardButton:

                        if(savedCardAvailability){

                            cardDetailsEnterLayout.setVisibility(View.GONE);
                            viewAllCardsButton.setVisibility(View.VISIBLE);
                            showSavedCardLayout.setVisibility(View.VISIBLE);
                            showEmptyCardLayout.setVisibility(View.GONE);

                        }else{

                            cardDetailsEnterLayout.setVisibility(View.GONE);
                            showSavedCardLayout.setVisibility(View.GONE);
                            showEmptyCardLayout.setVisibility(View.VISIBLE);
                            viewAllCardsButton.setVisibility(View.GONE);
                        }


                        break;

                }
            }
        });

        //back icon click event
        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        addMoneyAmountEditText.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    int beforeDecimal = 6, afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = addMoneyAmountEditText.getText() + source.toString();

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

        addMoneyAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String amo = addMoneyAmountEditText.getText().toString().trim();
                if(amo.length()>0 && !amo.equals(".")){
                    addMoneyPayNowButton.setVisibility(View.VISIBLE);
                }else{
                    addMoneyPayNowButton.setVisibility(View.GONE);
                }
            }
        });


        withdrawAmountEditText.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    int beforeDecimal = 6, afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = withdrawAmountEditText.getText() + source.toString();

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


        withdrawAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String amo = withdrawAmountEditText.getText().toString().trim();
                if(amo.length()>0 && !amo.equals(".")){
                    withdrawPayNowButton.setVisibility(View.VISIBLE);
                }else{
                    withdrawPayNowButton.setVisibility(View.GONE);
                }
            }
        });


        //Add a new bank account
        addNewBankTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(NewWalletScreen.this,AddNewBankAccountScreen.class);
                startActivityForResult(intent,5);

            }
        });


        withdrawPayNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredAmount = withdrawAmountEditText.getText().toString().trim();
                if(enteredAmount.isEmpty() || enteredAmount.equals(".")){
                    toast(getResources().getString(R.string.PleaseEnterAmount));
                }else{

                    ENTERAMOUNT= Double.parseDouble(enteredAmount);
                    double TOTALAMOUNT = Double.parseDouble(loginSession.getbalanceAmount());

                    double enterAmount = Double.parseDouble(enteredAmount);
                    if(withdrawal_fee_option.equalsIgnoreCase("percentage")){
                     //   withdrawFeePerTextView.setText("Withdrawn Fee ( "+withdrawShowFee+"% )");
                        withdrawFeePerTextView.setText("Withdrawn Fee");
                        withdrawFee = ( enterAmount * withdrawFee ) / 100 ;
                        //withdrawFeeTextView.setText(loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f",withdrawFee));
                    }else{
                        //withdrawFeeTextView.setText(loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f",withdrawFee));
                    }

                    if(ENTERAMOUNT > TOTALAMOUNT){
                        toast(getResources().getString(R.string.PleaseEnterMinimumAmount));
                    }else if(ENTERAMOUNT > withdrawFee+withdrawalExtraFee){
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) { }

                        if(withdrawalExtraFee > 0){
                            withdrawFeeTextView.setText(loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f",withdrawFee+withdrawalExtraFee));
                            withdrawExtraaFeeLayout.setVisibility(View.GONE);
                           // extraWithdrawFeeAmountTextView.setText(loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f",withdrawalExtraFee));
                        }else{
                            withdrawFeeTextView.setText(loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f",withdrawFee));
                            withdrawExtraaFeeLayout.setVisibility(View.GONE);
                        }

                        double sendAmount = enterAmount - withdrawFee - withdrawalExtraFee;
                        passAmount = enterAmount;

                        String one = "<font color=#cc0029>"+"( "+getResources().getString(R.string.Change)+" ) "+"</font>";
                        String two = loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f",enterAmount);
                        enteredAmountTextView.setText(Html.fromHtml(one+" "+two));

                        cerditToBankTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",sendAmount));

                        //Switching layouts part
                        withdrawPayNowButtonClicked=true;
                        withdrawFirstLayout.setVisibility(View.GONE);
                        withDrawLayout.setVisibility(View.VISIBLE);
                        confirmButton.setVisibility(View.VISIBLE);
                    }else{
                        toast(getResources().getString(R.string.Notallowed));
                    }

                }
            }
        });


        addMoneyPayNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredAmount = addMoneyAmountEditText.getText().toString().trim();
                CARDENTERAMOUNT= Double.parseDouble(enteredAmount);

                if (enteredAmount.isEmpty() || enteredAmount.equals(".")) {
                    toast(getResources().getString(R.string.PleaseEnterAmount));
                } else {

                    if (enteredAmount.equals("0")) {
                        toast(getResources().getString(R.string.PleaseEnterValidAmount));
                    } else {
                        addMoneyPayNowButtonClicked = true;
                        addMoneyFirstLayout.setVisibility(View.GONE);
                        confirmButton.setVisibility(View.VISIBLE);
                        addMoneyLayout.setVisibility(View.VISIBLE);
                        double enterAmount = Double.parseDouble(enteredAmount);

                        String one = getResources().getString(R.string.EnterAmount)+" : " + loginSession.getShowCurrency()+" " + String.format(Locale.ENGLISH,"%.2f", enterAmount);
                        String two = "<font color=#cc0029>( CHANGE )</font>";
                        addMonyEnteredAmountTextView.setText(Html.fromHtml(one +" "+ two));

                        if(utility.loadmoneyFeeOption.equalsIgnoreCase("price")){

                            Double feeAmount = Double.parseDouble(utility.loadmoneyFeeAmount);
                            Double loadAmount = CARDENTERAMOUNT;
                            Double extraaFee = Double.parseDouble(utility.loadmoneyExtraFeeAmount);
                            Double finalAmount = feeAmount + loadAmount + extraaFee;

                            addMonyFeeAmountTextView.setText("         Fee : " +loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",feeAmount+extraaFee)+"            ");
                            addMonyPayableAmountTextView.setText("Pay Amount : " +loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",finalAmount)+"               ");

                            if(feeAmount > 0 || extraaFee > 0){
                                addMonyFeeAmountTextView.setVisibility(View.VISIBLE);
                            }else{
                                addMonyFeeAmountTextView.setVisibility(View.VISIBLE);
                            }

                        }else{

                            Double feeAmount = Double.parseDouble(utility.loadmoneyFeeAmount);
                            Double loadAmount = CARDENTERAMOUNT;
                            Double extraaFee = Double.parseDouble(utility.loadmoneyExtraFeeAmount);
                            Double feeAmountPercentage = (loadAmount * feeAmount) / 100 ;
                            Double finalAmount = feeAmountPercentage + loadAmount + extraaFee;
                            addMonyFeeAmountTextView.setText("         Fee : " +loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",feeAmountPercentage+extraaFee)+"            ");
                            addMonyPayableAmountTextView.setText("Pay Amount : " +loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",finalAmount)+"               ");

                            if(feeAmount > 0 || extraaFee > 0){
                                addMonyFeeAmountTextView.setVisibility(View.VISIBLE);
                            }else{
                                addMonyFeeAmountTextView.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(addMoneyButton.isChecked()){

                    if(creditCardButton.isChecked()){
                        validateEnteredCardDetail();
                    }else{
                        Intent intent = new Intent(NewWalletScreen.this, PassCodeVerifiedScreen.class);
                        startActivityForResult(intent,4);
                    }

                }else{

                    if(bankID.isEmpty()){
                        toast(getResources().getString(R.string.PleaseAddBankAccount));
                    }else{
                        Intent intent = new Intent(NewWalletScreen.this, PassCodeVerifiedScreen.class);
                        startActivityForResult(intent,4);
                    }
                }
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bankAccountDialog == null) {

                    bankAccountDialog = new Dialog(NewWalletScreen.this);
                    bankAccountDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    bankAccountDialog.setContentView(R.layout.dialog_for_bankaccount);
                    bankAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    bankAccountDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                }

                ListView accountListView = (ListView) bankAccountDialog.findViewById(R.id.accountListView);
                bankAccountAdapter = new BankAccountAdapter(NewWalletScreen.this,datas);
                accountListView.setAdapter(bankAccountAdapter);
                bankAccountAdapter.notifyDataSetChanged();

                bankAccountDialog.show();
                bankAccountDialog.setCancelable(true);

            }
        });

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

                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(NewWalletScreen.this,
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

        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateEditText.performClick();
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

        layoutBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        backIconImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        viewAllCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }
        });


        addMonyEnteredAmountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addMoneyPayNowButtonClicked = false;
                addMoneyFirstLayout.setVisibility(View.VISIBLE);
                addMoneyLayout.setVisibility(View.GONE);
                confirmButton.setVisibility(View.GONE);
                addMoneyAmountEditText.setText("");

                CARDENTERAMOUNT = 0;
                cardNumberEditText.setText("");
                dateEditText.setText("");
                cvvEditText.setText("");
            }
        });

        enteredAmountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawPayNowButtonClicked = false;
                withdrawFirstLayout.setVisibility(View.VISIBLE);
                withDrawLayout.setVisibility(View.GONE);
                confirmButton.setVisibility(View.GONE);
                withdrawAmountEditText.setText("");

            }
        });


    }

    @Override
    public void onBackPressed() {

        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            finish();
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void validateEnteredCardDetail() {

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

             Intent intent = new Intent(NewWalletScreen.this, PassCodeVerifiedScreen.class);
             startActivityForResult(intent,4);

        }



    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_BANK_LIST:

                try{

                    BankList bankList = (BankList)result;
                    datas = bankList.data;
                    withdrawShowFee = bankList.withdrawal_fee;
                    withdrawFee = Double.parseDouble(bankList.withdrawal_fee);
                    withdrawalExtraFee = Double.parseDouble(bankList.withdrawal_extra_fee);
                    withdrawal_fee_option = bankList.withdrawal_fee_option;

                    if(datas.size() == 1){
                        changeButton.setVisibility(View.GONE);
                        singleTickImageView.setVisibility(View.VISIBLE);
                    }else{
                        changeButton.setVisibility(View.VISIBLE);
                        singleTickImageView.setVisibility(View.GONE);
                    }

                    bankID = datas.get(0).id;
                    bankNameTextView.setText(datas.get(0).bank_name);
                    acctNoTextView.setText(datas.get(0).account_no);

                    bankDetailsLayout.setVisibility(View.VISIBLE);
                    selectBankAccountText.setVisibility(View.VISIBLE);


                    //getCard list
                    if(isConnectingToInternet()){
                        final Map<String, String> param = new HashMap<String, String>();
                        showProgressDialog();
                        serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_CARD_LIST,"GET","");
                    }else{
                        noInternetAlertDialog();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    BankList bankList = (BankList)result;
                    withdrawShowFee = bankList.withdrawal_fee;
                    withdrawFee = Double.parseDouble(bankList.withdrawal_fee);
                    withdrawalExtraFee = Double.parseDouble(bankList.withdrawal_extra_fee);
                    withdrawal_fee_option = bankList.withdrawal_fee_option;
                    bankDetailsLayout.setVisibility(View.GONE);
                    selectBankAccountText.setVisibility(View.GONE);
                    //getCard list
                    if(isConnectingToInternet()){
                        final Map<String, String> param = new HashMap<String, String>();
                        showProgressDialog();
                        serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_CARD_LIST,"GET","");
                    }else{
                        noInternetAlertDialog();
                    }

                }


                break;

            case REQ_WITHDRAW_AMOUNT:

                toast(result.toString());
                double TOTALAMOUNT = Double.parseDouble(loginSession.getbalanceAmount());
                double balanceAmount = TOTALAMOUNT -ENTERAMOUNT;
                loginSession.setBalanceAmount(String.format(Locale.ENGLISH,"%.2f",balanceAmount));
                popcoinsBalanceTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(loginSession.getbalanceAmount())));
                withdrawPayNowButtonClicked = false;
                withdrawFirstLayout.setVisibility(View.VISIBLE);
                withDrawLayout.setVisibility(View.GONE);
                confirmButton.setVisibility(View.GONE);
                withdrawAmountEditText.setText("");
                withdrawPayNowButton.setVisibility(View.GONE);
                ENTERAMOUNT = 0;


               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getResources().getString(R.string.MakeAnotherWithdraw));
                alertDialog.setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        withdrawPayNowButtonClicked = false;
                        withdrawFirstLayout.setVisibility(View.VISIBLE);
                        withDrawLayout.setVisibility(View.GONE);
                        confirmButton.setVisibility(View.GONE);
                        withdrawAmountEditText.setText("");
                        withdrawPayNowButton.setVisibility(View.GONE);
                        ENTERAMOUNT = 0;

                    }
                });
                alertDialog.setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
                alertDialog.show();*/

                break;

            case REQ_CARD_LIST:

                CardListModel cardListModel = (CardListModel)result;

                try {

                    if (!cardListModel.data.isEmpty()) {

                        cardNumberTextView.setText("xxx-xxxx-xxxx-" + cardListModel.data.get(0).card_no);

                        if (cardListModel.data.get(0).card_type.equalsIgnoreCase("VISA")) {
                            cardTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.visa_card));
                        } else {
                            cardTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.master_card));
                        }

                        selectCardNumber = cardListModel.data.get(0).card_no;
                        selectCardYear = cardListModel.data.get(0).card_validity;
                        selectCardID = cardListModel.data.get(0).id;
                        seletCardImage = cardListModel.data.get(0).card_type;

                        if (cardListModel.data.size() >= 3) {
                            saveCardCheckBox.setVisibility(View.GONE);
                        } else {
                            saveCardCheckBox.setVisibility(View.VISIBLE);
                        }

                        cardListAdapter = new CardListAdapter(NewWalletScreen.this, cardListModel.data);
                        savedCardListView.setAdapter(cardListAdapter);

                        savedCardAvailability = true;

                    } else {
                        savedCardAvailability = false;
                        creditCardButton.setChecked(true);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(cardListModel.payment_settings.stripe_mode.equalsIgnoreCase("live")){
                    stripekey = cardListModel.payment_settings.stripe_publisherkey_live;
                }else{
                    stripekey = cardListModel.payment_settings.stripe_publisherkey_test;
                }

                break;

            case REQ_LOAD_MONEY:

                toast(getResources().getString(R.string.PaidSuccessfully));
                finish();
                loginSession.setBalanceAmount(result.toString());

                break;

            case REQ_CARD_DELETE:

                toast(result.toString());

                //getCard list
                if(isConnectingToInternet()){
                    final Map<String, String> param = new HashMap<String, String>();
                    showProgressDialog();
                    serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_CARD_LIST,"GET","");
                }else{
                    noInternetAlertDialog();
                }


                break;

        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
        try {
            switch (requestID) {

                case REQ_BANK_LIST:

                    if (error.equalsIgnoreCase("Logout")) {
                        toast(getResources().getString(R.string.UnauthorizedAccess));
                        loginSession.logout();
                        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        Intent intent = new Intent(this, GetStartedScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    break;

                case REQ_WITHDRAW_AMOUNT:
                    toast(error);
                    break;

                case REQ_CARD_LIST:

                    Object result1 = null;
                    String myString = error;
                    JSONObject jsonArray = new JSONObject(myString);
                    Gson gson4 = new Gson();
                    result1 = gson4.fromJson(jsonArray.toString(), CardErrorModel.class);
                    CardErrorModel cardErrorModel = (CardErrorModel) result1;
                    if(cardErrorModel.payment_settings.stripe_mode.equalsIgnoreCase("live")){
                        stripekey = cardErrorModel.payment_settings.stripe_publisherkey_live;
                    }else{
                        stripekey = cardErrorModel.payment_settings.stripe_publisherkey_test;
                    }

                    creditCardButton.setChecked(true);
                    savedCardAvailability = false;


                    break;

                case REQ_LOAD_MONEY:

                    toast(error);

                    break;

                case REQ_CARD_DELETE:

                    toast(error);

                    break;


            }
        }catch (Exception e){e.printStackTrace();}

    }

    private class BankAccountAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<BankList.Data>dataArrayList;

        public BankAccountAdapter(Activity activity, ArrayList<BankList.Data> dataArrayList) {
            this.activity = activity;
            this.dataArrayList = dataArrayList;
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
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {

            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null)

                convertView = inflater.inflate(R.layout.custom_bankaccount, null);

            final TextView bankNameText = (TextView)convertView.findViewById(R.id.bankNameText);
            TextView acctNoText = (TextView)convertView.findViewById(R.id.acctNoText);
            TextView selectButton = (TextView)convertView.findViewById(R.id.selectButton);

            bankNameText.setText(dataArrayList.get(i).bank_name);
            acctNoText.setText(dataArrayList.get(i).account_no);

            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bankID = dataArrayList.get(i).id;
                    bankNameTextView.setText(dataArrayList.get(i).bank_name);
                    acctNoTextView.setText(dataArrayList.get(i).account_no);
                    bankAccountDialog.dismiss();
                }
            });

            return convertView;
        }
    }

    private class CardListAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<CardListModel.Data>dataArrayList;

        public CardListAdapter(Activity activity, ArrayList<CardListModel.Data> dataArrayList) {
            this.activity = activity;
            this.dataArrayList = dataArrayList;
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
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = activity.getLayoutInflater();
            if (convertView == null)
                convertView = inflater.inflate(R.layout.custom_cardlist, null);

            final RelativeLayout contentCardLayout = (RelativeLayout)convertView.findViewById(R.id.contentCardLayout);
            final ImageView cardTypeImageView = (ImageView)convertView.findViewById(R.id.cardTypeImageView);
            final TextView cnumberTextView = (TextView)convertView.findViewById(R.id.cnumberTextView);
            ImageView menuDeleteButton = (ImageView)convertView.findViewById(R.id.menuDeleteButton);

            cnumberTextView.setText("xxxx-xxxx-xxxx-"+dataArrayList.get(i).card_no);

            if(dataArrayList.get(i).card_type.equalsIgnoreCase("VISA")){
                cardTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.visa_card));
            }else{
                cardTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.master_card));
            }

            contentCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    selectCardID = dataArrayList.get(i).id;
                    selectCardNumber = dataArrayList.get(i).card_no;
                    selectCardYear = dataArrayList.get(i).card_validity;
                    seletCardImage = dataArrayList.get(i).card_type;

                    cardNumberTextView.setText("xxxx-xxxx-xxxx-"+selectCardNumber);

                    if(dataArrayList.get(i).card_type.equalsIgnoreCase("VISA")){
                        cardTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.visa_card));
                    }else{
                        cardTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.master_card));
                    }

                }
            });

            menuDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    //getBank list
                    if(isConnectingToInternet()){
                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("id",dataArrayList.get(i).id);
                        showProgressDialog();
                        serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_CARD_DELETE,"POST","");
                    }else{
                        noInternetAlertDialog();
                    }

                }
            });

            return convertView;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("requestCode",""+requestCode);
        Log.e("resultCode",""+resultCode);

        if(requestCode == 4){

            if(resultCode == 4){

                if(addMoneyButton.isChecked()){

                    Log.e("addMoneyButton","addMoneyButton");

                    if(creditCardButton.isChecked()){

                        Log.e("creditCardButton","creditCardButton");

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
                                        param.put("card_validity",token.getCard().getExpMonth()+"/"+token.getCard().getExpYear());
                                        param.put("amount", String.valueOf(CARDENTERAMOUNT));
                                        param.put("card_type",token.getCard().getBrand());
                                        if(saveCardCheckBox.isChecked()){
                                            param.put("card_save","1");
                                        }else{
                                            param.put("card_save","0");
                                        }
                                        param.put("send_currency",loginSession.getcurrencyCodee());
                                        showProgressDialog();
                                        Log.e("param",""+param);
                                        serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_LOAD_MONEY,"POST","");


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
                    }else{

                        if(isConnectingToInternet()){

                            final Map<String, String> param = new HashMap<String, String>();
                            param.put("stripe_token_id","");
                            param.put("card_no","");
                            param.put("card_validity","");
                            param.put("amount", String.valueOf(CARDENTERAMOUNT));
                            param.put("card_type","");
                            param.put("card_save","");
                            param.put("send_currency",loginSession.getcurrencyCodee());
                            param.put("id",selectCardID);
                            showProgressDialog();
                            Log.e("param",""+param);
                            serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_LOAD_MONEY,"POST","");


                        }else{
                            noInternetAlertDialog();
                        }
                    }

                }else{

                    //getBank list
                    if(isConnectingToInternet()){
                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("amount",String.valueOf(passAmount));
                        param.put("bank_id",bankID);
                        showProgressDialog();
                        serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_WITHDRAW_AMOUNT,"POST","");
                    }else{
                        noInternetAlertDialog();
                    }
                }


            }
        }else{
                //getBank list
                if(isConnectingToInternet()){
                    final Map<String, String> param = new HashMap<String, String>();
                    showProgressDialog();
                    serverRequest.createRequest(NewWalletScreen.this,param, RequestID.REQ_BANK_LIST,"GET","");
                }else{
                    noInternetAlertDialog();
                }

        }
    }
}
