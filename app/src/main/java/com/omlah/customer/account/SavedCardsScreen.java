package com.omlah.customer.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.CardErrorModel;
import com.omlah.customer.model.CardListModel;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 06-04-2018.
 */

public class SavedCardsScreen extends BaseActivity implements ServerListener {

    //Create class objects
    ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;
    CardListAdapter cardListAdapter;

    //Create xml file
    @BindView(R.id.backIconImageView)
    ImageView backIconImageView; @BindView(R.id.cardAddButton)
    ImageView cardAddButton;
    @BindView(R.id.savedCardListView)
    ListView savedCardListView;
    @BindView(R.id.errorImageView)
    TextView errorImageView;

    String stripekey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_card_screen);

        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        cardAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SavedCardsScreen.this,AddCardScreen.class);
                intent.putExtra("stripekey",stripekey);
                startActivity(intent);
            }
        });



    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_CARD_DELETE:

                if (isConnectingToInternet()) {
                    final Map<String, String> param = new HashMap<String, String>();
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(SavedCardsScreen.this, param, RequestID.REQ_CARD_LIST, "GET", "");
                } else {
                    noInternetAlertDialog();
                }

                break;

            case REQ_CARD_LIST:

                try {

                    CardListModel cardListModel = (CardListModel) result;

                    if (cardListModel.data.size() >= 3) {
                        cardAddButton.setVisibility(View.GONE);
                    } else {
                        cardAddButton.setVisibility(View.VISIBLE);
                    }

                    if (!cardListModel.data.isEmpty()) {
                        errorImageView.setVisibility(View.GONE);
                        savedCardListView.setVisibility(View.VISIBLE);
                        cardListAdapter = new CardListAdapter(SavedCardsScreen.this, cardListModel.data);
                        savedCardListView.setAdapter(cardListAdapter);
                    }


                    if (cardListModel.payment_settings.stripe_mode.equalsIgnoreCase("live")) {
                        stripekey = cardListModel.payment_settings.stripe_publisherkey_live;
                    } else {
                        stripekey = cardListModel.payment_settings.stripe_publisherkey_test;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }


    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID) {

            case REQ_CARD_DELETE:
                break;

            case REQ_CARD_LIST:

                cardAddButton.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.VISIBLE);
                savedCardListView.setVisibility(View.GONE);

                try{
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
        }

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

            final RelativeLayout contentCardLayout = (RelativeLayout) convertView.findViewById(R.id.contentCardLayout);
            final ImageView cardTypeImageView = (ImageView) convertView.findViewById(R.id.cardTypeImageView);
            final TextView cnumberTextView = (TextView) convertView.findViewById(R.id.cnumberTextView);
            ImageView menuDeleteButton = (ImageView) convertView.findViewById(R.id.menuDeleteButton);

            cnumberTextView.setText("xxxx-xxxx-xxxx-" + dataArrayList.get(i).card_no);

            if (dataArrayList.get(i).card_type.equalsIgnoreCase("VISA")) {
                cardTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.visa_card));
            } else {
                cardTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.master_card));
            }

            menuDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //getBank list
                    if(isConnectingToInternet()){
                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("id",dataArrayList.get(i).id);
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(SavedCardsScreen.this,param, RequestID.REQ_CARD_DELETE,"POST","");
                    }else{
                        noInternetAlertDialog();
                    }

                }
            });

            return convertView;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //getCard list
        if (isConnectingToInternet()) {
            final Map<String, String> param = new HashMap<String, String>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(SavedCardsScreen.this, param, RequestID.REQ_CARD_LIST, "GET", "");
        } else {
            noInternetAlertDialog();
        }

    }
}
