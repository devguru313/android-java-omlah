package com.omlah.customer.tabhome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.SelectableRoundedImageView;
import com.omlah.customer.model.PromotionList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponCodeScreen extends BaseActivity implements ServerListener {

    //Create class files
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    @BindView(R.id.voucherCodeListView)RecyclerView voucherCodeListView;
    @BindView(R.id.errorImageView)TextView errorImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_voucher_code);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.Coupons));

        //initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        if (isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(CouponCodeScreen.this, params, RequestID.REQ_GET_PROMOTION, "GET", "");
        } else {
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        PromotionList promotionList = (PromotionList) result;
        if(Double.parseDouble(promotionList.data.total_count) > 0){
            voucherCodeListView.setVisibility(View.VISIBLE);
            errorImageView.setVisibility(View.GONE);
            voucherCodeListView.setHasFixedSize(true);
            PromotionDateAdapter adapter = new PromotionDateAdapter(this, promotionList.data.promotions);
            voucherCodeListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            voucherCodeListView.setAdapter(adapter);
        }else{
            voucherCodeListView.setVisibility(View.GONE);
            errorImageView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
        voucherCodeListView.setVisibility(View.GONE);
        errorImageView.setVisibility(View.VISIBLE);
    }

    private class PromotionDateAdapter extends RecyclerView.Adapter<PromotionDateAdapter.ItemRowHolder>{

        Activity activity;
        List<PromotionList.Promotions> promotionsList;

        public PromotionDateAdapter(Activity activity, List<PromotionList.Promotions> promotionsList) {
            this.activity = activity;
            this.promotionsList = promotionsList;


        }

        @Override
        public PromotionDateAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(final PromotionDateAdapter.ItemRowHolder holder, final int position) {

            if(promotionsList.get(position).transaction.size() > 0){

                if(promotionsList.get(position).transaction.get(0).status.equalsIgnoreCase("pending")){
                    holder.bookButton.setText("Code : "+promotionsList.get(position).transaction.get(0).promo_code);
                }else if(promotionsList.get(position).transaction.get(0).status.equalsIgnoreCase("redeem")){
                    if(promotionsList.get(position).redeem_type.equalsIgnoreCase("multiple")){
                        holder.bookButton.setText("Code : "+promotionsList.get(position).transaction.get(0).promo_code);
                    }else{
                        holder.bookButton.setText("Redeemed");
                    }
                }
            }else{
                holder.bookButton.setText("View Code");
            }

            holder.promotionNameTextView.setText(promotionsList.get(position).title);
            holder.promotionDescription.setText(promotionsList.get(position).description);
            if (!TextUtils.isEmpty(promotionsList.get(position).promotion_banner)) {
                Picasso.with(activity).load(promotionsList.get(position).promotion_banner)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                //When bitmap loaded successfully display bitmap over image view and generate palette
                                holder.promotionImageView.setImageBitmap(bitmap);
                                Palette.from(bitmap)
                                        .generate(new Palette.PaletteAsyncListener() {
                                            @Override
                                            public void onGenerated(Palette palette) {
                                                Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                                //Check if swatch is null or not
                                                if (textSwatch == null) {
                                                    //If null display toast
                                                  //  Toast.makeText(activity, "Got Null swatch !!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                Drawable roundDrawable = getResources().getDrawable(R.drawable.event_button);
                                                roundDrawable.setColorFilter(textSwatch.getRgb(), PorterDuff.Mode.SRC_ATOP);

                                                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                    holder.bookButton.setBackgroundDrawable(roundDrawable);
                                                } else {
                                                    holder.bookButton.setBackground(roundDrawable);
                                                }

                                                holder.promotionNameTextView.setTextColor(textSwatch.getRgb());//set title text color
                                                holder.bookButton.setTextColor(getResources().getColor(R.color.white));//set text background color or root background color
                                                // textSwatch.getBodyTextColor(); //Set the body text color if you need
                                            }
                                        });
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            } else {
                holder.promotionImageView.setImageDrawable(null);
            }

            holder.bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.contentlayout.performClick();
                }
            });


            holder.contentlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(activity, PromotionDetails.class);
                    intent.putExtra("screenName","CouponCodeScreen");
                    intent.putExtra("promotion_no",promotionsList.get(position).id);
                    intent.putExtra("promotion_banner",promotionsList.get(position).promotion_banner);
                    intent.putExtra("description",promotionsList.get(position).description);
                    intent.putExtra("title",promotionsList.get(position).title);
                    intent.putExtra("offer_price",promotionsList.get(position).offer_price);
                    intent.putExtra("merchant_id",promotionsList.get(position).user.id);
                    intent.putExtra("redeem_type",promotionsList.get(position).redeem_type);

                    if(promotionsList.get(position).transaction.size() > 0){
                        intent.putExtra("promotion_status",promotionsList.get(position).transaction.get(0).status);
                        intent.putExtra("promotion_code",promotionsList.get(position).transaction.get(0).promo_code);
                    }else{
                        intent.putExtra("promotion_status","GETCODE");
                        intent.putExtra("promotion_code","");
                    }
                    startActivityForResult(intent,897);


                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != promotionsList ? promotionsList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private SelectableRoundedImageView promotionImageView;
            private TextView promotionNameTextView;
            private TextView promotionDescription;
            private Button bookButton;
            private RelativeLayout contentlayout;

            public ItemRowHolder(View v) {
                super(v);
                this.promotionImageView = (SelectableRoundedImageView) v.findViewById(R.id.promotionImageView);
                this.promotionNameTextView = (TextView) v.findViewById(R.id.promotionNameTextView);
                this.promotionDescription = (TextView) v.findViewById(R.id.promotionDescription);
                this.bookButton = (Button) v.findViewById(R.id.bookButton);
                this.contentlayout = (RelativeLayout) v.findViewById(R.id.contentlayout);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 897 ){

            if(resultCode == 897 ){

                if (isConnectingToInternet()) {
                    Map<String, String> params = new HashMap<>();
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(CouponCodeScreen.this, params, RequestID.REQ_GET_PROMOTION, "GET", "");
                } else {
                    noInternetAlertDialog();
                }

            }
        }
    }
}
