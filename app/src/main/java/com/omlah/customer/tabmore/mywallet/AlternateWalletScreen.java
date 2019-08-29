package com.omlah.customer.tabmore.mywallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.YearMonthPickerDialog;
import com.omlah.customer.model.CardErrorModel;
import com.omlah.customer.model.CardListModel;
import com.omlah.customer.otp.PassCodeVerifiedScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.google.gson.Gson;
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

public class AlternateWalletScreen extends BaseActivity implements ServerListener{

    //Create class objects
    Utility utility;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequest;

    @BindView(R.id.backIconImageView)ImageView backIconImageView;

    @BindView(R.id.addMonyEnteredAmountTextView)TextView addMonyEnteredAmountTextView;
    @BindView(R.id.cardTabRadioGroup)RadioGroup cardTabRadioGroup;
    @BindView(R.id.creditCardButton)RadioButton creditCardButton;
    @BindView(R.id.cardDetailsEnterLayout)LinearLayout cardDetailsEnterLayout;
    @BindView(R.id.cardNumberEditText)EditText cardNumberEditText;
    @BindView(R.id.dateEditText)EditText dateEditText;
    @BindView(R.id.cvvEditText)EditText cvvEditText;
    @BindView(R.id.saveCardCheckBox)CheckBox saveCardCheckBox;

    @BindView(R.id.savedCardButton)RadioButton savedCardButton;
    @BindView(R.id.showSavedCardLayout)RelativeLayout showSavedCardLayout;
    @BindView(R.id.cardTypeImage) ImageView cardTypeImage;
    @BindView(R.id.calendarIcon) ImageView calendarIcon;
    @BindView(R.id.cardNumberTextView) TextView cardNumberTextView;
    @BindView(R.id.viewAllCardsButton)TextView viewAllCardsButton;
    @BindView(R.id.bottom_sheet) RelativeLayout layoutBottomSheet;
    @BindView(R.id.savedCardListView) ListView savedCardListView;
    @BindView(R.id.confirmButton)Button confirmButton;

    @BindView(R.id.feeAmountLayout)RelativeLayout feeAmountLayout;
    @BindView(R.id.feeAmountTextView)TextView feeAmountTextView;
    @BindView(R.id.payableAmountLayout)RelativeLayout payableAmountLayout;
    @BindView(R.id.payableAmountTextView)TextView payableAmountTextView;

    CardListAdapter cardListAdapter;

    boolean savedCardAvailability = false,cvvTextEntered=false;
    double CARDENTERAMOUNT=0;

    String stripekey="";
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
        setContentView(R.layout.alternate_wallet_screen);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);
        cvvEditText.setEnabled(false);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequest = ServerRequestwithHeader.getInstance(this);
        utility = Utility.getInstance(this);

        Intent intent = getIntent();
        if(intent!=null){

            String enterAmount = intent.getStringExtra("enteramount");
            CARDENTERAMOUNT = Double.parseDouble(enterAmount);
            addMonyEnteredAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",CARDENTERAMOUNT));


            if(utility.loadmoneyFeeOption.equalsIgnoreCase("price")){

                Double feeAmount = Double.parseDouble(utility.loadmoneyFeeAmount);
                Double loadAmount = CARDENTERAMOUNT;
                Double extraaFee = Double.parseDouble(utility.loadmoneyExtraFeeAmount);
                Double finalAmount = feeAmount + loadAmount + extraaFee;
                feeAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",feeAmount+extraaFee));
                payableAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",finalAmount));

                if(feeAmount > 0 || extraaFee > 0){
                    feeAmountLayout.setVisibility(View.VISIBLE);
                }else{
                    feeAmountLayout.setVisibility(View.VISIBLE);
                }

            }else{

                Double feeAmount = Double.parseDouble(utility.loadmoneyFeeAmount);
                Double loadAmount = CARDENTERAMOUNT;
                Double extraaFee = Double.parseDouble(utility.loadmoneyExtraFeeAmount);
                Double feeAmountPercentage = (loadAmount * feeAmount) / 100 ;
                Double finalAmount = feeAmountPercentage + loadAmount + extraaFee;
                feeAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",feeAmountPercentage+extraaFee));
                payableAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",finalAmount));

                if(feeAmount > 0 || extraaFee > 0){
                    feeAmountLayout.setVisibility(View.VISIBLE);
                }else{
                    feeAmountLayout.setVisibility(View.VISIBLE);
                }
            }

        }

        //getCard list
        if(isConnectingToInternet()){
            final Map<String, String> param = new HashMap<String, String>();
            showProgressDialog();
            serverRequest.createRequest(AlternateWalletScreen.this,param, RequestID.REQ_CARD_LIST,"GET","");
        }else{
            noInternetAlertDialog();
        }

        cardTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){

                    case R.id.creditCardButton:

                        if(cvvTextEntered){
                            confirmButton.setVisibility(View.VISIBLE);
                            cvvEditText.setEnabled(true);
                        }else{
                            confirmButton.setVisibility(View.GONE);
                            cvvEditText.setEnabled(false);
                        }

                        cardDetailsEnterLayout.setVisibility(View.VISIBLE);
                        showSavedCardLayout.setVisibility(View.GONE);
                        viewAllCardsButton.setVisibility(View.GONE);

                        break;

                    case R.id.savedCardButton:


                        if(savedCardAvailability){

                            try  {
                                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            } catch (Exception e) {

                            }

                            confirmButton.setVisibility(View.VISIBLE);
                            cardDetailsEnterLayout.setVisibility(View.GONE);
                            viewAllCardsButton.setVisibility(View.VISIBLE);
                            showSavedCardLayout.setVisibility(View.VISIBLE);

                        }else{

                            toast(getResources().getString(R.string.Nocards));
                            creditCardButton.setChecked(true);
                            cardDetailsEnterLayout.setVisibility(View.VISIBLE);
                            showSavedCardLayout.setVisibility(View.GONE);
                            viewAllCardsButton.setVisibility(View.GONE);
                        }


                        break;

                }
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


        //back icon click event
        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
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
                        cvvEditText.setEnabled(true);
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


        cvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {

                    if (s.length() >= 3) {
                        cvvTextEntered = true;
                        confirmButton.setVisibility(View.VISIBLE);
                    } else {
                        cvvTextEntered = false;
                        confirmButton.setVisibility(View.GONE);
                    }
                } else {
                    cvvTextEntered = false;
                    confirmButton.setVisibility(View.GONE);
                }

            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(2018,00,01);

                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(AlternateWalletScreen.this,
                        calendar,
                        new YearMonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onYearMonthSet(int year, int month) {

                                double realvalue = month;
                                double add = 1;
                                double finalValue = realvalue+add;
                                String finalstring = String.valueOf(finalValue).replace(".0","");
                                dateEditText.setText(finalstring+"/"+year);
                                cvvEditText.setEnabled(true);
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


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (creditCardButton.isChecked()) {
                    validateEnteredCardDetail();
                } else {
                    Intent intent = new Intent(AlternateWalletScreen.this, PassCodeVerifiedScreen.class);
                    startActivityForResult(intent, 4);
                }

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

            Intent intent = new Intent(AlternateWalletScreen.this, PassCodeVerifiedScreen.class);
            startActivityForResult(intent,4);

        }



    }


    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_CARD_LIST:

                CardListModel cardListModel = (CardListModel)result;

                if(!(cardListModel.data == null)){

                    cardNumberTextView.setText("xxx-xxxx-xxxx-"+cardListModel.data.get(0).card_no);

                    if(cardListModel.data.get(0).card_type.equalsIgnoreCase("VISA")){
                        cardTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.visa_card));
                    }else{
                        cardTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.master_card));
                    }

                    selectCardNumber = cardListModel.data.get(0).card_no;
                    selectCardYear = cardListModel.data.get(0).card_validity;
                    selectCardID = cardListModel.data.get(0).id;
                    seletCardImage = cardListModel.data.get(0).card_type;

                    if(cardListModel.data.size() >= 3){
                        saveCardCheckBox.setVisibility(View.GONE);
                    }else{
                        saveCardCheckBox.setVisibility(View.VISIBLE);
                    }

                    cardListAdapter = new CardListAdapter(AlternateWalletScreen.this,cardListModel.data);
                    savedCardListView.setAdapter(cardListAdapter);

                    savedCardAvailability = true;

                }else{
                    savedCardAvailability = false;
                    creditCardButton.setChecked(true);

                }

                if(cardListModel.payment_settings.stripe_mode.equalsIgnoreCase("live")){
                    stripekey = cardListModel.payment_settings.stripe_publisherkey_live;
                }else{
                    stripekey = cardListModel.payment_settings.stripe_publisherkey_test;
                }


                break;

            case REQ_CARD_DELETE:

                toast(result.toString());

                //getCard list
                if(isConnectingToInternet()){
                    final Map<String, String> param = new HashMap<String, String>();
                    showProgressDialog();
                    serverRequest.createRequest(AlternateWalletScreen.this,param, RequestID.REQ_CARD_LIST,"GET","");
                }else{
                    noInternetAlertDialog();
                }


                break;

            case REQ_LOAD_MONEY:

                toast(getResources().getString(R.string.MoneyloadtowalletSuccessfully));
                finish();
                loginSession.setBalanceAmount(result.toString());

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
        try {
            switch (requestID) {

                case REQ_CARD_LIST:

                    Object result1 = null;
                    String myString = error;
                    JSONObject jsonArray = new JSONObject(myString);
                    Gson gson4 = new Gson();
                    result1 = gson4.fromJson(jsonArray.toString(), CardErrorModel.class);
                    CardErrorModel cardErrorModel = (CardErrorModel) result1;
                    if (cardErrorModel.payment_settings.stripe_mode.equalsIgnoreCase("live")) {
                        stripekey = cardErrorModel.payment_settings.stripe_publisherkey_live;
                    } else {
                        stripekey = cardErrorModel.payment_settings.stripe_publisherkey_test;
                    }

                    creditCardButton.setChecked(true);
                    savedCardAvailability = false;


                    break;

                case REQ_CARD_DELETE:

                    toast(error);

                    break;

                case REQ_LOAD_MONEY:

                    toast(error);

                    break;

            }
        }catch (Exception e){e.printStackTrace();}

    }

    private class CardListAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<CardListModel.Data> dataArrayList;

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
                        serverRequest.createRequest(AlternateWalletScreen.this,param, RequestID.REQ_CARD_DELETE,"POST","");
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

        if (resultCode == 4) {

            if (creditCardButton.isChecked()) {

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

                            if (isConnectingToInternet()) {

                                final Map<String, String> param = new HashMap<String, String>();
                                param.put("stripe_token_id", stripeToken);
                                param.put("card_no", token.getCard().getLast4());
                                param.put("card_validity", token.getCard().getExpMonth() + "/" + token.getCard().getExpYear());
                                param.put("amount", String.valueOf(CARDENTERAMOUNT));
                                param.put("card_type", token.getCard().getBrand());
                                if (saveCardCheckBox.isChecked()) {
                                    param.put("card_save", "1");
                                } else {
                                    param.put("card_save", "0");
                                }
                                param.put("send_currency", loginSession.getcurrencyCodee());
                                showProgressDialog();
                                Log.e("param", "" + param);
                                serverRequest.createRequest(AlternateWalletScreen.this, param, RequestID.REQ_LOAD_MONEY, "POST", "");


                            } else {
                                noInternetAlertDialog();
                            }

                        }

                        @Override
                        public void onError(Exception error) {

                            hideProgressDialog();
                        }

                    });
                } else {

                    toast("Please enter a valid card details");
                }
            } else {

                if (isConnectingToInternet()) {

                    final Map<String, String> param = new HashMap<String, String>();
                    param.put("stripe_token_id", "");
                    param.put("card_no", "");
                    param.put("card_validity", "");
                    param.put("amount", String.valueOf(CARDENTERAMOUNT));
                    param.put("card_type", "");
                    param.put("card_save", "");
                    param.put("send_currency", loginSession.getcurrencyCodee());
                    param.put("id", selectCardID);
                    showProgressDialog();
                    Log.e("param", "" + param);
                    serverRequest.createRequest(AlternateWalletScreen.this, param, RequestID.REQ_LOAD_MONEY, "POST", "");


                } else {
                    noInternetAlertDialog();
                }
            }

        }
    }

}
