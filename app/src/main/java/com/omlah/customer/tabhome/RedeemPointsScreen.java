package com.omlah.customer.tabhome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.PointsTradingList;
import com.omlah.customer.model.RedeemPointsbuy;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.omlah.customer.service.RequestID.REQ_REDEEM_POINTS;

public class RedeemPointsScreen extends BaseActivity implements ServerListener {

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml files
    @BindView(R.id.pointsRecyclerView)RecyclerView pointsRecyclerView;
    @BindView(R.id.totalPointsTextView)TextView totalPointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem_points_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.Offers));

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        loginSession = LoginSession.getInstance(this);

        totalPointsTextView.setText(loginSession.getPopCoin());

        //Get points list
        if(isConnectingToInternet()){
            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(RedeemPointsScreen.this, params, RequestID.REQ_GET_TRADINGS, "GET", "");
        }else{
            noInternetAlertDialog();
        }

    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_GET_TRADINGS:

                PointsTradingList pointsTradingList = (PointsTradingList)result;
                pointsRecyclerView.setHasFixedSize(true);
                RedeemPointsAdapter adapter = new RedeemPointsAdapter(this,pointsTradingList.data.point_tradings);
                pointsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                pointsRecyclerView.setAdapter(adapter);

                break;

            case REQ_REDEEM_POINTS:

                RedeemPointsbuy redeemPointsbuy = (RedeemPointsbuy)result;
                toast(redeemPointsbuy.message);
                Intent intent = new Intent(RedeemPointsScreen.this,BaseScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                /*loginSession.setPopcoin(redeemPointsbuy.data.available_points);

                //Get points list
                if(isConnectingToInternet()){
                    Map<String, String> params = new HashMap<>();
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(RedeemPointsScreen.this, params, RequestID.REQ_GET_TRADINGS, "GET", "");
                }else{
                    noInternetAlertDialog();
                }*/

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
    }

    private class RedeemPointsAdapter extends RecyclerView.Adapter<RedeemPointsAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<PointsTradingList.Point_tradings> promotionsList;

        public RedeemPointsAdapter(Activity activity, ArrayList<PointsTradingList.Point_tradings> promotionsList) {
            this.activity = activity;
            this.promotionsList = promotionsList;
        }

        @Override
        public RedeemPointsAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_pointslayout, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(final RedeemPointsAdapter.ItemRowHolder holder, final int position) {

            holder.itemNameTextView.setText(promotionsList.get(position).title);
            holder.itemPointstextView.setText(promotionsList.get(position).redeem_points);

            if(promotionsList.get(position).transaction.size()>0){
                holder.redeembutton.setVisibility(View.VISIBLE);
                holder.redeembutton.setText("Redeemed");
                holder.redeembutton.setEnabled(false);
                holder.redeembutton.setBackgroundTintList(getResources().getColorStateList(R.color.dgray));
            }else{

                if(Double.parseDouble(loginSession.getPopCoin()) >= Double.parseDouble(promotionsList.get(position).redeem_points)) {

                    holder.redeembutton.setVisibility(View.VISIBLE);
                    holder.redeembutton.setText("Redeem Now");
                    holder.redeembutton.setEnabled(true);
                    holder.redeembutton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));

                }else{

                    holder.redeembutton.setVisibility(View.VISIBLE);
                    holder.redeembutton.setText("Redeem Now");
                    holder.redeembutton.setEnabled(false);
                    holder.redeembutton.setBackgroundTintList(getResources().getColorStateList(R.color.dgray));
                }


            }

            if(!promotionsList.get(position).trade_banner.isEmpty()){
                Picasso.with(activity).load(promotionsList.get(position).trade_banner).into(holder.redeemItemImageView);
            }else{
                holder.redeemItemImageView.setImageDrawable(null);
            }

            holder.redeembutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Get points list
                    if(isConnectingToInternet()){
                        Map<String, String> params = new HashMap<>();
                        params.put("trading_id",promotionsList.get(position).id);
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(RedeemPointsScreen.this, params, REQ_REDEEM_POINTS, "POST", "");
                    }else{
                        noInternetAlertDialog();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != promotionsList ? promotionsList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private TextView itemNameTextView;
            private ImageView redeemItemImageView;
            private TextView itemPointstextView;
            private Button redeembutton;

            public ItemRowHolder(View v) {
                super(v);
                this.itemNameTextView = (TextView) v.findViewById(R.id.itemNameTextView);
                this.redeemItemImageView = (ImageView) v.findViewById(R.id.redeemItemImageView);
                this.itemPointstextView = (TextView) v.findViewById(R.id.itemPointstextView);
                this.redeembutton = (Button) v.findViewById(R.id.redeembutton);

            }

        }
    }

}
