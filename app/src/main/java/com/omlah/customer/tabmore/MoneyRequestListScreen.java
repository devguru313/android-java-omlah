package com.omlah.customer.tabmore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.fcm.Config;
import com.omlah.customer.model.MoneyRequestList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.SuccessScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 20-09-2017.
 */

public class MoneyRequestListScreen extends BaseActivity implements ServerListener {

    //Create class Objects
    Utility utility;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.moneyRequestListView)ListView moneyRequestListView;
    @BindView(R.id.errorImageView)TextView errorImageView;

    //BroadcastReceiver method
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_request_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.MoneyRequest));

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        utility = Utility.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Get money request response
        getMoneyRequestList();

        //Notification message received method
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean check_update = intent.getBooleanExtra("update", false);

                if(check_update){

                        getMoneyRequestList();

                }
            }
        };

    }

    private void getMoneyRequestList() {

        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(MoneyRequestListScreen.this,params, RequestID.REQ_MONEYREQUEST_LIST,"GET","");

        }else{
            noInternetAlertDialog();
        }
    }

    private class MoneyRequestAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<MoneyRequestList.Data>moneyRequestLists;
        private LayoutInflater inflater;

        public MoneyRequestAdapter(Activity activity, ArrayList<MoneyRequestList.Data> moneyRequestLists) {
            this.activity = activity;
            this.moneyRequestLists = moneyRequestLists;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return moneyRequestLists.size();
        }

        @Override
        public Object getItem(int i) {
            return moneyRequestLists.get(i);
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
                view = inflater.inflate(R.layout.custom_moneyrequest_list,null);

                //Initialize xml object
                viewHolder.typeNameTextView   = (TextView) view.findViewById(R.id.typeNameTextView);
                viewHolder.amountTextView     = (TextView) view.findViewById(R.id.amountTextView);
                viewHolder.numbertextView     = (TextView) view.findViewById(R.id.numbertextView);
                viewHolder.statusTextView     = (TextView) view.findViewById(R.id.statusTextView);
                viewHolder.timetextView       = (TextView) view.findViewById(R.id.timetextView);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if(moneyRequestLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID())){

                viewHolder.typeNameTextView.setText(getResources().getString(R.string.RequestReceivedFrom));
                viewHolder.amountTextView.setText(moneyRequestLists.get(i).send_currency+" "+loginSession.getcurrencySymbol()+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(moneyRequestLists.get(i).send_amount)));
                viewHolder.numbertextView.setText(moneyRequestLists.get(i).requester.name);

            }else{

                viewHolder.typeNameTextView.setText(getResources().getString(R.string.RequestSentTo));
                viewHolder.amountTextView.setText(moneyRequestLists.get(i).request_currency+" "+loginSession.getcurrencySymbol()+String.format("%.2f",Double.parseDouble(moneyRequestLists.get(i).request_amount)));
                viewHolder.numbertextView.setText(moneyRequestLists.get(i).sender_detail.name);

            }

            viewHolder.timetextView.setText(timeZoneConverter(moneyRequestLists.get(i).created,loginSession.gettimeZone()));

            if(moneyRequestLists.get(i).status.equalsIgnoreCase("Pending")){

                viewHolder.statusTextView.setTextColor(activity.getResources().getColor(R.color.yellow));
                viewHolder.statusTextView.setText(getResources().getString(R.string.Pending));

            }else if(moneyRequestLists.get(i).status.equalsIgnoreCase("Failed")){

                viewHolder.statusTextView.setTextColor(activity.getResources().getColor(R.color.red));
                viewHolder.statusTextView.setText(getResources().getString(R.string.Failed));

            } else{

                viewHolder.statusTextView.setTextColor(activity.getResources().getColor(R.color.green));
                viewHolder.statusTextView.setText("Success");
            }
            viewHolder.typeNameTextView.setTag(i);
            viewHolder.amountTextView.setTag(i);
            viewHolder.numbertextView.setTag(i);
            viewHolder.statusTextView.setTag(i);
            viewHolder.timetextView.setTag(i);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(moneyRequestLists.get(i).status.equalsIgnoreCase("Pending")){

                        Intent intent = new Intent(MoneyRequestListScreen.this,MoneyRequestSuccessScreen.class);

                        if(moneyRequestLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID())){

                            intent.putExtra("type","Request received");
                            intent.putExtra("id", moneyRequestLists.get(i).id);
                            intent.putExtra("amount", moneyRequestLists.get(i).send_amount);
                            intent.putExtra("currency",moneyRequestLists.get(i).send_currency);
                            intent.putExtra("receiverName", moneyRequestLists.get(i).requester.name);
                            intent.putExtra("receiverNumber", moneyRequestLists.get(i).requester.phone_number);
                            Log.e("amount",moneyRequestLists.get(i).send_amount);

                        }else{

                            intent.putExtra("type","Request sent");
                            intent.putExtra("id", moneyRequestLists.get(i).id);
                            intent.putExtra("amount", moneyRequestLists.get(i).request_amount);
                            intent.putExtra("currency",moneyRequestLists.get(i).request_currency);
                            intent.putExtra("receiverName", moneyRequestLists.get(i).sender_detail.name);
                            intent.putExtra("receiverNumber", moneyRequestLists.get(i).sender_detail.phone_number);
                            Log.e("amount",moneyRequestLists.get(i).request_amount);
                        }

                        intent.putExtra("date",  moneyRequestLists.get(i).created);
                        intent.putExtra("description",  moneyRequestLists.get(i).description);
                        intent.putExtra("transaction_no",  moneyRequestLists.get(i).request_no);
                        startActivityForResult(intent,1);

                    }else if(moneyRequestLists.get(i).status.equalsIgnoreCase("Success")){

                        Intent intent = new Intent(MoneyRequestListScreen.this, SuccessScreen.class);
                        intent.putExtra("screen","RequestMoney");
                        intent.putExtra("fromScreen","MoneyRequestList");

                        if(moneyRequestLists.get(i).sender_id.equalsIgnoreCase(loginSession.getCustomerID())){

                            intent.putExtra("amount", moneyRequestLists.get(i).send_amount);
                            intent.putExtra("currency",moneyRequestLists.get(i).send_currency);
                            intent.putExtra("receiverName", moneyRequestLists.get(i).requester.name);

                        }else{

                            intent.putExtra("amount", moneyRequestLists.get(i).request_amount);
                            intent.putExtra("currency",moneyRequestLists.get(i).request_currency);
                            intent.putExtra("receiverName", moneyRequestLists.get(i).sender_detail.name);

                        }

                        intent.putExtra("date",  moneyRequestLists.get(i).created);
                        intent.putExtra("description",  moneyRequestLists.get(i).description);
                        intent.putExtra("transaction_no",  moneyRequestLists.get(i).request_no);
                        startActivity(intent);
                    }

                }
            });

            return view;
        }

        private class ViewHolder {

            private TextView typeNameTextView, amountTextView,numbertextView,statusTextView,timetextView;

        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        try{
            MoneyRequestList moneyRequestList = (MoneyRequestList)result;
            if(!moneyRequestList.message.equalsIgnoreCase("No record(s) found!")){
                ArrayList<MoneyRequestList.Data>moneyRequestLists = moneyRequestList.data;
                errorImageView.setVisibility(View.GONE);
                moneyRequestListView.setVisibility(View.VISIBLE);
                MoneyRequestAdapter moneyRequestAdapter = new MoneyRequestAdapter(this,moneyRequestLists);
                moneyRequestListView.setAdapter(moneyRequestAdapter);
            }else{
                errorImageView.setVisibility(View.VISIBLE);
                moneyRequestListView.setVisibility(View.GONE);
            }

        }catch (Exception e){e.printStackTrace();}

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        errorImageView.setVisibility(View.VISIBLE);
        moneyRequestListView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == 1){

                //Get money request response
                getMoneyRequestList();
            }
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
