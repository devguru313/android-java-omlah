package com.omlah.customer.tabmore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.fcm.Config;
import com.omlah.customer.model.PopCoinsList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.PayMoneyScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 20-09-2017.
 */

public class PopCoinsScreen extends BaseActivity implements ServerListener{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;
    PopCoinsAdapter popCoinsAdapter;

    //Create xml objects
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.popcoinsListView)ListView popcoinsListView;
    @BindView(R.id.popcoinsBalanceTextView)TextView popcoinsBalanceTextView;
    @BindView(R.id.sendCoinsButton)TextView sendCoinsButton;
    @BindView(R.id.errorImageView)TextView errorImageView;
    @BindView(R.id.PopCoinHistoryText)TextView PopCoinHistoryText;

    //boolean
    boolean b1 = false;

    //BroadcastReceiver method
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popcoins_screen);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Set popcoins
        popcoinsBalanceTextView.setText(loginSession.getPopCoin());

        if(loginSession.getPopCoin().equals("0")){

            sendCoinsButton.setVisibility(View.GONE);

        }else{

            sendCoinsButton.setVisibility(View.VISIBLE);

        }

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        //Get coin details
        getPopCoinsList();

        sendCoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PopCoinsScreen.this,PayMoneyScreen.class);
                intent.putExtra("screenName","Send Coins");
                startActivity(intent);
            }
        });

        //Notification message received method
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean check_update = intent.getBooleanExtra("update", false);

                if(check_update){

                    if(intent.getStringExtra("title").trim().contains("Rewards Received")){

                        String message = intent.getStringExtra("message").replaceAll("[^\\d]", "");

                        getPopCoinsList();

                        try {

                            if (loginSession.getPopCoin().equals("0")) {

                                sendCoinsButton.setVisibility(View.GONE);

                            } else {

                                sendCoinsButton.setVisibility(View.VISIBLE);

                            }

                        } catch (Exception e) {
                        }

                    }

                }
            }
        };
    }

    private void getPopCoinsList() {

        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            params.put("status","all");
            params.put("transfer_type","coin");
            showProgressDialog();
            serverRequestwithHeader.createRequest(PopCoinsScreen.this,params, RequestID.REQ_POPCOINS_LIST,"POST","");

        }else{
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        try{
            PopCoinsList popCoinsList = (PopCoinsList)result;
            ArrayList<PopCoinsList.Transactions> popCoinsLists = popCoinsList.data.transactions;
            PopCoinHistoryText.setVisibility(View.VISIBLE);
            PopCoinHistoryText.setText("Rewards History");
            popcoinsListView.setVisibility(View.VISIBLE);
            errorImageView.setVisibility(View.GONE);
            popCoinsAdapter = new PopCoinsAdapter(PopCoinsScreen.this,popCoinsLists);
            popcoinsListView.setAdapter(popCoinsAdapter);
        }catch (Exception e){e.printStackTrace();

            PopCoinHistoryText.setVisibility(View.GONE);
            PopCoinHistoryText.setText("Rewards History");
            popcoinsListView.setVisibility(View.GONE);
            errorImageView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
        PopCoinHistoryText.setVisibility(View.GONE);
        PopCoinHistoryText.setText("Rewards History");
        popcoinsListView.setVisibility(View.GONE);
        errorImageView.setVisibility(View.VISIBLE);
    }

    private class PopCoinsAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<PopCoinsList.Transactions>popCoinsLists;
        private LayoutInflater inflater;

        public PopCoinsAdapter(Activity activity, ArrayList<PopCoinsList.Transactions> popCoinsLists) {
            this.activity = activity;
            this.popCoinsLists = popCoinsLists;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return popCoinsLists.size();
        }

        @Override
        public Object getItem(int i) {
            return popCoinsLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {

                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.custom_popcoin_list,null);

                //Initialize xml object
                viewHolder.typeTextView      = (TextView) view.findViewById(R.id.typeTextView);
                viewHolder.amountTextView    = (TextView) view.findViewById(R.id.amountTextView);
                viewHolder.shopNameTextView  = (TextView) view.findViewById(R.id.shopNameTextView);
                viewHolder.timeTextView      = (TextView) view.findViewById(R.id.timeTextView);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if(popCoinsLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID())){
                loginSession.setPopcoin(popCoinsLists.get(i).sender.popcoin_balance);
                popcoinsBalanceTextView.setText(loginSession.getPopCoin());
            }else if(popCoinsLists.get(i).receiver.id.equalsIgnoreCase(loginSession.getCustomerID())){
                loginSession.setPopcoin(popCoinsLists.get(i).receiver.popcoin_balance);
                popcoinsBalanceTextView.setText(loginSession.getPopCoin());
            }

            if(popCoinsLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID())){


                if(popCoinsLists.get(i).receiver.business_name!=null){
                    viewHolder.shopNameTextView.setText(popCoinsLists.get(i).receiver.business_name);
                }else{
                    viewHolder.shopNameTextView.setText(popCoinsLists.get(i).receiver.name);
                }

                if(popCoinsLists.get(i).transaction_type.equalsIgnoreCase("c-m")){
                    viewHolder.typeTextView.setText("Rewards spent");
                }else{
                    viewHolder.typeTextView.setText("Rewards sent");
                }

                viewHolder.amountTextView.setText(" - "+popCoinsLists.get(i).pop_coin);


            }else{

                viewHolder.typeTextView.setText("Rewards received");
                viewHolder.amountTextView.setText(" + "+popCoinsLists.get(i).pop_coin);

                if(popCoinsLists.get(i).sender.business_name!=null){
                    viewHolder.shopNameTextView.setText(popCoinsLists.get(i).sender.business_name);
                }else{
                    viewHolder.shopNameTextView.setText(popCoinsLists.get(i).sender.name);
                }

            }


            viewHolder.timeTextView.setText(timeZoneConverter(popCoinsLists.get(i).created,loginSession.gettimeZone()));

            viewHolder.typeTextView.setTag(i);
            viewHolder.amountTextView.setTag(i);
            viewHolder.shopNameTextView.setTag(i);
            viewHolder.timeTextView.setTag(i);

            return view;
        }

        private class ViewHolder {

            private TextView typeTextView, amountTextView,shopNameTextView,timeTextView;

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
}
