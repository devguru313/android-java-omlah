package com.omlah.customer.tabnearby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 23-11-2017.
 */

public class ShopOfferScreen extends BaseActivity{

    //Create class files
    Utility utility;
    LoginSession loginSession;
    OfferAdapter offerAdapter;

    //Create xml objects
    @BindView(R.id.shopImageView)ImageView shopImageView;
    @BindView(R.id.shopNameTextView)TextView shopNameTextView;
    @BindView(R.id.shopAddressTextView)TextView shopAddressTextView;
    @BindView(R.id.rewardofferPercentageTextView)TextView rewardofferPercentageTextView;
    @BindView(R.id.rewardBanner)TextView rewardBanner;
    @BindView(R.id.offerBanner)TextView offerBanner;
    @BindView(R.id.rewardLayout)RelativeLayout rewardLayout;
    @BindView(R.id.offerListView)ListView offerListView;

    String rewardYesNo,rewardPercentage,offerYesNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopoffer_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.ShopOffer));

        //Initialize xml objects
        ButterKnife.bind(this);

        //Initialize class objects
        utility = Utility.getInstance(this);
        loginSession = LoginSession.getInstance(this);

        Intent intent = getIntent();

        if(intent!=null){

            shopNameTextView.setText(intent.getStringExtra("shopName").trim());
            shopAddressTextView.setText(intent.getStringExtra("shopAddress"));
            rewardPercentage = intent.getStringExtra("rewardPercentage");
            rewardYesNo = intent.getStringExtra("rewardYesNo");
            offerYesNo = intent.getStringExtra("offerYesNo");

            if(rewardYesNo.equalsIgnoreCase("yes")){
                rewardBanner.setVisibility(View.VISIBLE);
                rewardLayout.setVisibility(View.VISIBLE);
                rewardofferPercentageTextView.setText(rewardPercentage+"%");
            }else{
                rewardBanner.setVisibility(View.GONE);
                rewardLayout.setVisibility(View.GONE);
            }


            if (intent.getStringExtra("shopImage").equalsIgnoreCase("NoImage")) {
            } else {
                Log.e("URL",loginSession.getProfileImageURL());
                Picasso.with(this).load(loginSession.getProfileImageURL()+intent.getStringExtra("shopImage")).into(shopImageView);
            }
        }

        Log.e("utility.offername",""+utility.offername);

        if(offerYesNo.equalsIgnoreCase("yes")){
            offerBanner.setVisibility(View.VISIBLE);
            offerAdapter = new OfferAdapter(this,utility.offername);
            offerListView.setAdapter(offerAdapter);
            offerListView.setVisibility(View.VISIBLE);
        }else{
            offerListView.setVisibility(View.GONE);
            offerBanner.setVisibility(View.GONE);
            rewardBanner.setVisibility(View.GONE);
        }

    }

    private class OfferAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<String> dataArrayList;
        private LayoutInflater inflater;

        public OfferAdapter(Activity activity, ArrayList<String> dataArrayList) {
            this.activity = activity;
            this.dataArrayList = dataArrayList;
            inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return dataArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.custom_offerlist, parent, false);
                viewHolder.offerNameTextView = (TextView) convertView.findViewById(R.id.offerNameTextView);
                viewHolder.offerPercentageTextView = (TextView) convertView.findViewById(R.id.offerPercentageTextView);
                viewHolder.offerDescriptionTextView = (TextView) convertView.findViewById(R.id.offerDescriptionTextView);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();


            String OFFERNAME[] = dataArrayList.get(position).split("<@>");

            viewHolder.offerNameTextView.setText(OFFERNAME[0]);
            viewHolder.offerPercentageTextView.setText(OFFERNAME[1]+"%");
            if(OFFERNAME[2].equalsIgnoreCase("empty")){
                viewHolder.offerDescriptionTextView.setVisibility(View.GONE);
            }else{
                viewHolder.offerDescriptionTextView.setText(OFFERNAME[2]);
                viewHolder.offerDescriptionTextView.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        private class ViewHolder {

            private TextView offerNameTextView,offerPercentageTextView,offerDescriptionTextView;
        }
    }
}
