package com.omlah.customer.tabmore.rewards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.customseekbar.DynamicSeekBarView;
import com.omlah.customer.model.EarningHistoryModel;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardDetailsScreen extends BaseActivity implements ServerListener, SeekBar.OnSeekBarChangeListener{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;
    EarningHistoryAdapter earningHistoryAdapter;

    //Create xml objects
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.actionBarTitleTextview)TextView actionBarTitleTextview;
    @BindView(R.id.earningHistoryListView)ListView earningHistoryListView;
    @BindView(R.id.errorImageView)TextView errorImageView;

    @BindView(R.id.rewardBalanceTextView)TextView rewardBalanceTextView;
    @BindView(R.id.offerConditionTextView)TextView offerConditionTextView;
    @BindView(R.id.rewardSeekBar)DynamicSeekBarView rewardSeekBar;
    @BindView(R.id.seekbarStartAmount)TextView seekbarStartAmount;
    @BindView(R.id.finalRewardAmountTextView)TextView finalRewardAmountTextView;

    @BindView(R.id.contentLayout)RelativeLayout contentLayout;
    @BindView(R.id.offerLayout)RelativeLayout offerLayout;
    @BindView(R.id.offerBannerText)TextView offerBannerText;

    int customerReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards_details_screen);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);
        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        rewardSeekBar.setSeekBarChangeListener(this);

        Intent intent = getIntent();
        if(intent!=null){

            String merchantID  = intent.getStringExtra("merchant_id");
            Log.e("merchantID",merchantID);
            if(isConnectingToInternet()){

                Map<String, String> params = new HashMap<>();
                params.put("merchant_id",merchantID);
                showProgressDialog();
                serverRequestwithHeader.createRequest(RewardDetailsScreen.this,params, RequestID.GET_REWARD_DETAILS,"POST","");

            }else{
                noInternetAlertDialog();
            }
        }

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
        EarningHistoryModel earningHistoryModel = (EarningHistoryModel)result;

        actionBarTitleTextview.setText(earningHistoryModel.data.merchant_detail.business_name);

        rewardBalanceTextView.setText(earningHistoryModel.data.total_rewards);
        finalRewardAmountTextView.setText(earningHistoryModel.data.merchant_detail.merchant_reward_settings.get(0).redeem_reward);

        customerReward = Integer.parseInt(earningHistoryModel.data.total_rewards);
        int finalReward    = Integer.parseInt(earningHistoryModel.data.merchant_detail.merchant_reward_settings.get(0).redeem_reward);

        if(finalReward > customerReward){

            rewardSeekBar.setVisibility(View.VISIBLE);
            seekbarStartAmount.setVisibility(View.VISIBLE);
            finalRewardAmountTextView.setVisibility(View.VISIBLE);
            offerConditionTextView.setVisibility(View.VISIBLE);
            offerLayout.setVisibility(View.GONE);

            rewardSeekBar.setInfoText(earningHistoryModel.data.total_rewards,Integer.parseInt(earningHistoryModel.data.total_rewards));
            rewardSeekBar.setEnabled(false);

            int minuesReward =  finalReward - customerReward;
            offerConditionTextView.setText(getResources().getString(R.string.Need)+" "+minuesReward+" "+getResources().getString(R.string.MorePointsFor)+" "+earningHistoryModel.data.merchant_detail.merchant_reward_settings.get(0).redeem_reward_percentage+"% "+getResources().getString(R.string.off));

        }else{

            rewardSeekBar.setVisibility(View.GONE);
            seekbarStartAmount.setVisibility(View.GONE);
            finalRewardAmountTextView.setText("");
            finalRewardAmountTextView.setVisibility(View.GONE);
            offerConditionTextView.setVisibility(View.GONE);
            offerLayout.setVisibility(View.VISIBLE);
            offerBannerText.setText(getResources().getString(R.string.YouEligibleFor)+" "+earningHistoryModel.data.merchant_detail.merchant_reward_settings.get(0).redeem_reward_percentage+"% "+getResources().getString(R.string.OffOnYouNextPurchase));

        }




        earningHistoryAdapter = new EarningHistoryAdapter(RewardDetailsScreen.this,earningHistoryModel.data.orderList);
        earningHistoryListView.setAdapter(earningHistoryAdapter);

        contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setProgress(customerReward);
        int seekBarmax = Integer.parseInt(finalRewardAmountTextView.getText().toString().trim());
        seekBar.setMax(seekBarmax);
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private class EarningHistoryAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<EarningHistoryModel.OrderList> rewardHistoryModels;
        private LayoutInflater inflater;

        public EarningHistoryAdapter(Activity activity, ArrayList<EarningHistoryModel.OrderList> rewardHistoryModels) {
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
        public View getView(int i, View view, ViewGroup parent) {
            ViewHolder viewHolder;

            if (view == null) {

                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.custom_earning_history,null);

                //Initialize xml object
                viewHolder.typeImageView        = (ImageView) view.findViewById(R.id.typeImageView);
                viewHolder.orderAmountTextView     = (TextView) view.findViewById(R.id.orderAmountTextView);
                viewHolder.earningPointTextView  = (TextView) view.findViewById(R.id.earningPointTextView);
                viewHolder.orderIdTextView     = (TextView) view.findViewById(R.id.orderIdTextView);
                viewHolder.timeTextView           = (TextView) view.findViewById(R.id.timeTextView);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.orderAmountTextView.setText(loginSession.getShowCurrency()+" "+rewardHistoryModels.get(i).transaction.sender_amount);
            viewHolder.earningPointTextView.setText(rewardHistoryModels.get(i).rewards_earned+" "+getResources().getString(R.string.Points));
            viewHolder.orderIdTextView.setText(rewardHistoryModels.get(i).transaction.transaction_no);
            viewHolder.timeTextView.setText(timeZoneConverter(rewardHistoryModels.get(i).transaction.created,loginSession.gettimeZone()));

            viewHolder.typeImageView.setTag(i);
            viewHolder.orderAmountTextView.setTag(i);
            viewHolder.earningPointTextView.setTag(i);
            viewHolder.orderIdTextView.setTag(i);
            viewHolder.timeTextView.setTag(i);


            return view;
        }

        private class ViewHolder {

            private ImageView typeImageView;
            private TextView orderAmountTextView, earningPointTextView,orderIdTextView,timeTextView;

        }
    }
}
