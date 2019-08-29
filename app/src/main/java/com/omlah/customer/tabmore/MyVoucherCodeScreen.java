package com.omlah.customer.tabmore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.omlah.customer.model.MyVoucherCode;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.PromotionDetails;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyVoucherCodeScreen extends BaseActivity implements ServerListener {

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
        setActionBarTitle(getResources().getString(R.string.MyVoucherCode));

        //initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        if (isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(MyVoucherCodeScreen.this, params, RequestID.REQ_MY_VOUCHERCODE, "GET", "");
        } else {
            noInternetAlertDialog();
        }

    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        MyVoucherCode myVoucherCode = (MyVoucherCode)result;

        if(myVoucherCode.data == null){
            errorImageView.setVisibility(View.VISIBLE);
            voucherCodeListView.setVisibility(View.GONE);
        }else{
            if(myVoucherCode.data.size() > 0){
                errorImageView.setVisibility(View.GONE);
                voucherCodeListView.setVisibility(View.VISIBLE);
                voucherCodeListView.setHasFixedSize(true);
                PromotionDateAdapter adapter = new PromotionDateAdapter(this, myVoucherCode.data);
                voucherCodeListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                voucherCodeListView.setAdapter(adapter);
            }else{
                errorImageView.setVisibility(View.VISIBLE);
                voucherCodeListView.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
    }


    private class PromotionDateAdapter extends RecyclerView.Adapter<PromotionDateAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<MyVoucherCode.Data> promotionsList;

        public PromotionDateAdapter(Activity activity, ArrayList<MyVoucherCode.Data> promotionsList) {
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

            holder.promotionNameTextView.setText(promotionsList.get(position).promotion.title);
            holder.promotionDescription.setText(promotionsList.get(position).promotion.description);

            if (!TextUtils.isEmpty(promotionsList.get(position).promotion.promotion_banner)) {
                Picasso.with(activity).load(promotionsList.get(position).promotion.promotion_banner)
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
                                                //    Toast.makeText(activity, "Got Null swatch !!", Toast.LENGTH_SHORT).show();
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

            if(promotionsList.get(position).status.equalsIgnoreCase("pending")){
                holder.bookButton.setText("CODE : "+promotionsList.get(position).promo_code);
            }else{
                if(promotionsList.get(position).promotion.redeem_type.equalsIgnoreCase("multiple")){
                    holder.bookButton.setText("CODE : "+promotionsList.get(position).promo_code);
                }else{
                    holder.bookButton.setText("Redeemed");
                }

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

                    Intent intent = new Intent(MyVoucherCodeScreen.this, PromotionDetails.class);
                    intent.putExtra("screenName","MyCodeScreen");
                    intent.putExtra("promotion_no",promotionsList.get(position).promotion.id);
                    intent.putExtra("promotion_banner",promotionsList.get(position).promotion.promotion_banner);
                    intent.putExtra("description",promotionsList.get(position).promotion.description);
                    intent.putExtra("title",promotionsList.get(position).promotion.title);
                    intent.putExtra("offer_price",promotionsList.get(position).promotion.offer_price);
                    intent.putExtra("merchant_id",promotionsList.get(position).id);
                    intent.putExtra("transitionName", "transition" + position);
                    intent.putExtra("promotion_status",promotionsList.get(position).status);
                    intent.putExtra("promotion_code",promotionsList.get(position).promo_code);
                    intent.putExtra("redeem_type",promotionsList.get(position).promotion.redeem_type);
                    startActivity(intent);

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
}
