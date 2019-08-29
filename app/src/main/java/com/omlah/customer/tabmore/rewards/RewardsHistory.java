package com.omlah.customer.tabmore.rewards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.omlah.customer.model.RewardHistoryModel;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardsHistory extends BaseActivity implements ServerListener{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;
    RewardsHistoryAdapter rewardsHistoryAdapter;

    //Create xml objects
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.rewardHistoryListView)ListView rewardHistoryListView;
    @BindView(R.id.errorImageView)TextView errorImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards_history);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Get Reward History
        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(RewardsHistory.this,params, RequestID.GET_REWARD_LIST,"GET","");

        }else{
            noInternetAlertDialog();
        }

        //Back click event
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

        try{
            RewardHistoryModel rewardHistoryModel = (RewardHistoryModel)result;
            if(rewardHistoryModel.data.size() > 0){
                rewardsHistoryAdapter = new RewardsHistoryAdapter(RewardsHistory.this,rewardHistoryModel.data);
                rewardHistoryListView.setAdapter(rewardsHistoryAdapter);
                rewardHistoryListView.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);
            }else{
                rewardHistoryListView.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            rewardHistoryListView.setVisibility(View.GONE);
            errorImageView.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        rewardHistoryListView.setVisibility(View.GONE);
        errorImageView.setVisibility(View.VISIBLE);

    }

    private class RewardsHistoryAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<RewardHistoryModel.Data> rewardHistoryModels;
        private LayoutInflater inflater;

        public RewardsHistoryAdapter(Activity activity, ArrayList<RewardHistoryModel.Data> rewardHistoryModels) {
            this.activity = activity;
            this.rewardHistoryModels = rewardHistoryModels;
            this.inflater = LayoutInflater.from(activity);
        }


        @Override
        public int getCount() {
            return rewardHistoryModels.size();
        }

        @Override
        public Object getItem(int position) {
            return rewardHistoryModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int i, View view, ViewGroup parent) {
            ViewHolder viewHolder;

            if (view == null) {

                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.custom_reward_history,null);

                //Initialize xml object
                viewHolder.contentLayout        = (RelativeLayout) view.findViewById(R.id.contentLayout);
                viewHolder.shopImageView        = (ImageView) view.findViewById(R.id.shopImageView);
                viewHolder.shopNameTextView     = (TextView) view.findViewById(R.id.shopNameTextView);
                viewHolder.shopAddressTextView  = (TextView) view.findViewById(R.id.shopAddressTextView);
                viewHolder.amountText           = (TextView) view.findViewById(R.id.amountText);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            try{
                if(!rewardHistoryModels.get(i).merchant_detail.profile_image.isEmpty()){
                    Picasso.with(activity)
                            .load(rewardHistoryModels.get(i).merchant_detail.profile_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(viewHolder.shopImageView);
                }
            }catch (Exception e){}


            viewHolder.shopNameTextView.setText(rewardHistoryModels.get(i).merchant_detail.business_name);
            viewHolder.shopAddressTextView.setText(rewardHistoryModels.get(i).merchant_detail.address);
            viewHolder.amountText.setText(rewardHistoryModels.get(i).total_rewards);



            viewHolder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(RewardsHistory.this,RewardDetailsScreen.class);
                    intent.putExtra("merchant_id",rewardHistoryModels.get(i).merchant_detail.id);
                    startActivity(intent);
                }
            });


            viewHolder.shopImageView.setTag(i);
            viewHolder.shopNameTextView.setTag(i);
            viewHolder.shopAddressTextView.setTag(i);
            viewHolder.amountText.setTag(i);


            return view;
        }

        private class ViewHolder {

            private RelativeLayout contentLayout;
            private ImageView shopImageView;
            private TextView shopNameTextView, shopAddressTextView,amountText;

        }
    }
}
