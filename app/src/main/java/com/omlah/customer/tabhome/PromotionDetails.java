package com.omlah.customer.tabhome;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.common.ScratchTextView;
import com.omlah.customer.model.VoucherDetails;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PromotionDetails extends BaseActivity implements ServerListener{

    Dialog alertDialog;
    ServerRequestwithHeader serverRequestwithHeader;

    @BindView(R.id.promotionImageView)ImageView promotionImageView;
    @BindView(R.id.promotionDescriptionTextView)TextView promotionDescriptionTextView;
    @BindView(R.id.couponCodeTextView)TextView couponCodeTextView;
    @BindView(R.id.buyNowButton)Button buyNowButton;
    @BindView(R.id.dummyScratchTextView)TextView dummyScratchTextView;
    @BindView(R.id.voucherCodeTag)TextView voucherCodeTag;
    @BindView(R.id.voucherCodeLayout)LinearLayout voucherCodeLayout;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.promotionTermsListView)RecyclerView promotionTermsListView;

    private String promotion_no="",merchant_id="",promotion_status="",promotion_code="",redeem_type="";
    private boolean scratched=false;
    private String screenName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_details);
        hideActionBar();

        ButterKnife.bind(this);

        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        Intent intent = getIntent();
        if(intent!=null){
            couponCodeTextView.setText("");
            Picasso.with(this).load(intent.getStringExtra("promotion_banner"))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            //When bitmap loaded successfully display bitmap over image view and generate palette
                            promotionImageView.setImageBitmap(bitmap);
                            Palette.from(bitmap)
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                            //Check if swatch is null or not
                                            if (textSwatch == null) {
                                                //If null display toast
                                           //     Toast.makeText(getApplicationContext(), "Got Null swatch !!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }


                                            Window window = getWindow();
                                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                window.setStatusBarColor(textSwatch.getRgb());
                                            }
                                            buyNowButton.setBackgroundColor(textSwatch.getRgb());

                                            //holder.promotionNameTextView.setTextColor(textSwatch.getRgb());//set title text color
                                            //holder.bookButton.setBackgroundColor(textSwatch.getRgb());//set text background color or root background color
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
            promotionDescriptionTextView.setText(intent.getStringExtra("description"));
            promotion_no = intent.getStringExtra("promotion_no");
            merchant_id = intent.getStringExtra("merchant_id");
            promotion_status = intent.getStringExtra("promotion_status");
            promotion_code = intent.getStringExtra("promotion_code");
            screenName = intent.getStringExtra("screenName");
            redeem_type = intent.getStringExtra("redeem_type");

            if (isConnectingToInternet()) {
                Map<String, String> params = new HashMap<>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(PromotionDetails.this, params, RequestID.REQ_VOCUHER_DETAILS, "GET", promotion_no);
            } else {
                noInternetAlertDialog();
            }
        }

        if(promotion_status.equalsIgnoreCase("pending")){
            buyNowButton.setVisibility(View.GONE);

            dummyScratchTextView.setVisibility(View.GONE);
            voucherCodeLayout.setVisibility(View.VISIBLE);
            couponCodeTextView.setText(promotion_code);
            voucherCodeTag.setText("Your Code");

        }else if(promotion_status.equalsIgnoreCase("redeem")){

            if(redeem_type.equalsIgnoreCase("multiple")){
                buyNowButton.setVisibility(View.GONE);
                dummyScratchTextView.setVisibility(View.GONE);
                voucherCodeLayout.setVisibility(View.VISIBLE);
                couponCodeTextView.setText(promotion_code);
                voucherCodeTag.setText("Your Code");
            }else{
                voucherCodeTag.setText("Redeemed Code");
                buyNowButton.setVisibility(View.GONE);
                dummyScratchTextView.setVisibility(View.GONE);
                voucherCodeLayout.setVisibility(View.GONE);
            }

        }else{
            buyNowButton.setVisibility(View.VISIBLE);
        }


        dummyScratchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyNowButton.performClick();
            }
        });

        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(buyNowButton.getText().toString().trim().equalsIgnoreCase("BACK")){
                    onBackPressed();
                }else{
                    if (isConnectingToInternet()) {
                        Map<String, String> params = new HashMap<>();
                        params.put("merchant_id", merchant_id);
                        params.put("promotion_id", promotion_no);
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(PromotionDetails.this, params, RequestID.REQ_VOCUHER_CODE, "POST", "");

                    } else {
                        noInternetAlertDialog();
                    }
                }

            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

    }

    @Override
    public void onSuccess(final Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_VOCUHER_CODE:

                if (alertDialog == null) {

                    alertDialog = new Dialog(PromotionDetails.this);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.dialog_for_vocuhercode);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                }

                final ScratchTextView voucherCodeTextView = (ScratchTextView)alertDialog.findViewById(R.id.voucherCodeTextView);
                final Button continueButton = (Button)alertDialog.findViewById(R.id.continueButton);
                final TextView scratchText = (TextView) alertDialog.findViewById(R.id.scratchText);
                final ImageView backIconImageView = (ImageView) alertDialog.findViewById(R.id.backIconImageView);

                voucherCodeTextView.setRevealListener(new ScratchTextView.IRevealListener() {
                    @Override
                    public void onRevealed(ScratchTextView scratchTextView) {
                        scratched=true;
                        continueButton.setVisibility(View.VISIBLE);
                        buyNowButton.setText("BACK");
                        scratchText.setVisibility(View.GONE);
                        dummyScratchTextView.setVisibility(View.GONE);
                        voucherCodeLayout.setVisibility(View.VISIBLE);
                        couponCodeTextView.setText(result.toString());
                        voucherCodeTag.setText("Your Code");
                    }

                    @Override
                    public void onRevealPercentChangedListener(ScratchTextView scratchTextView, float v) {

                    }
                });

                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                voucherCodeTextView.setText(result.toString());

                backIconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(voucherCodeTextView.isRevealed()){
                            alertDialog.dismiss();
                        }else{
                            voucherCodeTextView.reveal();
                            scratched=true;
                        }

                    }
                });

                alertDialog.setCancelable(false);
                alertDialog.show();


                break;

            case REQ_VOCUHER_DETAILS:

                VoucherDetails promotions = (VoucherDetails)result;
                ArrayList<String> terms = new ArrayList<>();

                if(!(promotions.data.promotion_terms == null)){

                    if (promotions.data.promotion_terms.size() > 0) {

                        for (VoucherDetails.Promotion_terms promotion_terms : promotions.data.promotion_terms) {
                            if (!promotion_terms.promotion_detail.isEmpty()) {
                                terms.add(promotion_terms.promotion_detail);
                            }

                        }
                    }
                }

                terms.add("The code can be redeemed once per user");
                terms.add("Coupon code is valid for all users on particular store");
                terms.add("Coupon redemption & Payments should be done only on latest RPay android and iOS App");
                promotionTermsListView.setHasFixedSize(true);
                PromotionTermsDateAdapter adapter = new PromotionTermsDateAdapter(this, terms);
                promotionTermsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                promotionTermsListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
    }

    @Override
    public void onBackPressed(){
        if(alertDialog!=null && alertDialog.isShowing()){
            if(scratched){
                alertDialog.dismiss();
            }
        }else{

            if(scratched){
                if(screenName.equalsIgnoreCase("CouponCodeScreen")){
                    setResult(897);
                    finish();
                }else if(screenName.equalsIgnoreCase("HomeScreen")){
                    Intent intent = new Intent(PromotionDetails.this,BaseScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    finish();
                }

            }else{
                finish();
            }


        }
    }

    private class PromotionTermsDateAdapter extends RecyclerView.Adapter<PromotionTermsDateAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<String> promotionsList;

        public PromotionTermsDateAdapter(Activity activity, ArrayList<String> promotionsList) {
            this.activity = activity;
            this.promotionsList = promotionsList;

        }
        @Override
        public PromotionTermsDateAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_terms_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(final PromotionTermsDateAdapter.ItemRowHolder holder, final int position) {

            holder.termsTextView.setText(promotionsList.get(position));
        }

        @Override
        public int getItemCount() {
            return (null != promotionsList ? promotionsList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            TextView termsTextView;

            public ItemRowHolder(View v) {
                super(v);
                this.termsTextView = (TextView) v.findViewById(R.id.termsTextView);

            }


        }
    }
}
