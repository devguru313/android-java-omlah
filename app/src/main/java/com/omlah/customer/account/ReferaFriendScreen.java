package com.omlah.customer.account;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.Shareable;
import com.omlah.customer.model.ReferHistory;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 06-03-2018.
 */

public class ReferaFriendScreen extends BaseActivity implements ServerListener, View.OnClickListener {

    //Create class objects
    Utility utility;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml files
    @BindView(R.id.actionBarTitleTextView)TextView actionBarTitleTextView;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.referHistoryListView)ListView referHistoryListView;
    @BindView(R.id.bannerTextView)TextView bannerTextView;
    @BindView(R.id.referCodeTextView)TextView referCodeTextView;
    @BindView(R.id.copyButton)Button copyButton;
    @BindView(R.id.referCodeUrlEditText)EditText referCodeUrlEditText;
    @BindView(R.id.referLayout)LinearLayout referLayout;
    @BindView(R.id.referHistoryLayout)LinearLayout referHistoryLayout;
    @BindView(R.id.shareRadioButton)RadioButton shareRadioButton;
    @BindView(R.id.viewRadioButton)RadioButton viewRadioButton;
    @BindView(R.id.fb_ShareButton)ShareButton fb_ShareButton;

    @BindView(R.id.referButtonsLayout)LinearLayout referButtonsLayout;
    @BindView(R.id.whatsupShareButton)ImageView whatsupShareButton;
    @BindView(R.id.fbShareButton)ImageView fbShareButton;
    @BindView(R.id.twitterShareButton)ImageView twitterShareButton;
    @BindView(R.id.smsShareButton)ImageView smsShareButton;
    @BindView(R.id.mailShareButton)ImageView mailShareButton;


    String referCode = "";
    String referAmount = "";
    String message= "";
    String appUrl="";

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.refera_friend_screen);
        hideActionBar();
        //Initialize xml files
        ButterKnife.bind(this);

        //setClick linstner
        whatsupShareButton.setOnClickListener(this);
        fbShareButton.setOnClickListener(this);
        twitterShareButton.setOnClickListener(this);
        smsShareButton.setOnClickListener(this);
        mailShareButton.setOnClickListener(this);

        //Initialize class objects
        loginSession  = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        utility       = Utility.getInstance(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Set actionbar title
        actionBarTitleTextView.setText(getResources().getString(R.string.ReferFriend));

        //Get customer info
        getCustomerInfo();

        fb_ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(appUrl))
                                .setContentTitle(message)
                                .build();
                        shareDialog.show(linkContent);
                        fb_ShareButton.setShareContent(linkContent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", referCodeUrlEditText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        shareRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    referLayout.setVisibility(View.VISIBLE);
                    referHistoryLayout.setVisibility(View.GONE);
                    referButtonsLayout.setVisibility(View.VISIBLE);
                }else{

                    referLayout.setVisibility(View.GONE);
                    referHistoryLayout.setVisibility(View.VISIBLE);
                    referButtonsLayout.setVisibility(View.GONE);
                }
            }
        });


        viewRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    referLayout.setVisibility(View.GONE);
                    referHistoryLayout.setVisibility(View.VISIBLE);
                    referButtonsLayout.setVisibility(View.GONE);

                }else{

                    referLayout.setVisibility(View.VISIBLE);
                    referHistoryLayout.setVisibility(View.GONE);
                    referButtonsLayout.setVisibility(View.VISIBLE);
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

    private void getCustomerInfo() {

        if (isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            serverRequestwithHeader.createRequest(ReferaFriendScreen.this, params, RequestID.REQ_REFER_HISTORY,"GET","");
            showProgressDialog();
        } else {
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        try{

            ReferHistory referHistory = (ReferHistory)result;

            //setBanner TextView
            String text1 = getResources().getString(R.string.FirstText)+" "+loginSession.getShowCurrency()+" "+referHistory.data.referral_bonus+" bonus.";
            String text2 = getResources().getString(R.string.SecondText);
            String text3 = getResources().getString(R.string.ThirdText)+" "+loginSession.getShowCurrency()+" "+referHistory.data.welcome_bonus+" bonus.";
            bannerTextView.setText(Html.fromHtml(text1+"<Br>"+text2+"<Br>"+text3));

            //set refer code
            referAmount = referHistory.data.referral_bonus;
            referCode = referHistory.data.referral_detail.referral_code;
            referCodeTextView.setText(referHistory.data.referral_detail.referral_code.toUpperCase());
            referCodeUrlEditText.setText(referHistory.data.refer_link);
            appUrl = referHistory.data.refer_link;

            message = "Enter this CODE "+referCode+" when you register with us and get "+loginSession.getShowCurrency()+" "+referAmount;

            //setreferHistory
            if(!referHistory.data.referral_detail.referral_histories.isEmpty()){
                viewRadioButton.setVisibility(View.VISIBLE);
                ReferHistoryAdapter referHistoryAdapter = new ReferHistoryAdapter(this,referHistory.data.referral_detail.referral_histories);
                referHistoryListView.setAdapter(referHistoryAdapter);
            }else{
                viewRadioButton.setVisibility(View.GONE);
            }

        }catch (Exception e){e.printStackTrace();}


    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.whatsupShareButton:

                try{

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    String mm = "Enter this CODE *"+referCode+"* when you register with us and get "+loginSession.getShowCurrency()+" "+referAmount;
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mm+" "+appUrl);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);

                }catch (Exception e){e.printStackTrace();}

                break;

            case R.id.fbShareButton:

                fb_ShareButton.performClick();

                break;

            case R.id.twitterShareButton:

                try{
                    Shareable shareInstance = new Shareable.Builder(this)
                            .message(message)
                            .socialChannel(Shareable.Builder.TWITTER)
                            .url(appUrl)
                            .build();
                    shareInstance.share();
                }catch (Exception e){e.printStackTrace();}


                break;

            case R.id.smsShareButton:

                try{
                    Shareable shareInstanceSMS = new Shareable.Builder(this)
                            .message(message)
                            .socialChannel(Shareable.Builder.MESSAGES)
                            .url(appUrl)
                            .build();
                    shareInstanceSMS.share();
                }catch (Exception e){e.printStackTrace();}


                break;

            case R.id.mailShareButton:

                try{
                    Shareable shareInstanceMAIL = new Shareable.Builder(this)
                            .message(message)
                            .socialChannel(Shareable.Builder.EMAIL)
                            .url(appUrl)
                            .build();
                    shareInstanceMAIL.share();
                }catch (Exception e){e.printStackTrace();}


                break;
        }
    }

    //ReferHistoryAdapter class
    private class ReferHistoryAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<ReferHistory.Referral_histories> referredLists;

        public ReferHistoryAdapter(Activity activity, ArrayList<ReferHistory.Referral_histories> referredLists) {
            this.activity = activity;
            this.referredLists = referredLists;
        }

        @Override
        public int getCount() {
            return referredLists.size();
        }

        @Override
        public Object getItem(int position) {
            return referredLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_referd_history, null);

            TextView referralNameTextView = (TextView)convertView.findViewById(R.id.referralNameTextView);
            TextView phoneTextView = (TextView)convertView.findViewById(R.id.phoneTextView);
            TextView amountText = (TextView)convertView.findViewById(R.id.amountText);
            TextView statusTextView = (TextView)convertView.findViewById(R.id.statusTextView);
            TextView dateTime = (TextView)convertView.findViewById(R.id.dateTime);


            phoneTextView.setText(referredLists.get(position).user.phone_number);
            referralNameTextView.setText(referredLists.get(position).user.name);
            amountText.setText(loginSession.getShowCurrency()+" "+referredLists.get(position).referral_bonus);
            dateTime.setText(timeZoneConverter(referredLists.get(position).created,loginSession.gettimeZone()));
            statusTextView.setText(referredLists.get(position).status);

            return convertView;
        }
    }


}
