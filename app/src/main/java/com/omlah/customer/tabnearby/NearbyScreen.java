package com.omlah.customer.tabnearby;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseFragment;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.MerchantList;
import com.omlah.customer.model.SellerList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 15-09-2017.
 */

public class NearbyScreen extends BaseFragment implements ServerListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Create class objects
    Utility utility;
    ServerRequestwithHeader serverRequest;
    NearbyShopsAdapter nearbyShopsAdapter;
    NearbySellerAdapter nearbySellerAdapter;
    LoginSession loginSession;

    //Create xml objects
    @BindView(R.id.tabRadioGroup)
    RadioGroup tabRadioGroup;
    @BindView(R.id.shopsButton)
    RadioButton shopsButton;
    @BindView(R.id.sellerButton)
    RadioButton sellerButton;
    @BindView(R.id.locationEditText)
    EditText locationEditText;
    @BindView(R.id.gpsButton)
    ImageView gpsButton;
    @BindView(R.id.nearByListView)
    ListView nearByListView;
    @BindView(R.id.errorImageView)
    TextView errorImageView;
    @BindView(R.id.contentLayout)
    LinearLayout contentLayout;
    @BindView(R.id.closeIconImageView)
    ImageView closeIconImageView;

    String currentTab = "";
    String globalSearchName = "";
    String mSearchText = "";

    String rewardYesNo ="";
    String rewardPercentage ="";
    String offerYesNo ="";

    //Location permission
    GoogleApiClient googleApiClient;
    public Location location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nearby_screen, container, false);

        //Initialize xml objects
        ButterKnife.bind(this, rootView);

        //Initialize class objects
        utility = Utility.getInstance(getActivity());
        loginSession = LoginSession.getInstance(getActivity());
        serverRequest = ServerRequestwithHeader.getInstance(getActivity());

        //Fonts
        Typeface font= Typeface.createFromAsset(getActivity().getAssets(), "font/GothamRounded-Medium.ttf");
        sellerButton.setTypeface(font);
        shopsButton.setTypeface(font);

        //Initialize google api client
        //googleApiClient initialize
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(NearbyScreen.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .build();

        //Default method
        currentTab = "Shop";

        //handler to make loading slow
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLatLang();
                Log.e("Working", "Handler");
            }
        }, 100);


        //gps click event
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLatLang();

            }
        });

        //radioGroub click event
        tabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                switch (i) {

                    case R.id.shopsButton:

                        if (!currentTab.equalsIgnoreCase("Shop")) {

                            currentTab = "Shop";
                            locationEditText.setText("");
                            locationEditText.setHint("Search by shops name");

                            shopAdapterMethod();
                        }

                        break;

                    case R.id.sellerButton:

                        if (!currentTab.equalsIgnoreCase("Seller")) {

                            currentTab = "Seller";

                            sellerAdapterMethod();
                            locationEditText.setText("");
                            locationEditText.setHint(getResources().getString(R.string.Searchbyagentsname));
                        }

                        break;

                }
            }
        });

        //filter method
        locationEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    globalSearchName = s.toString();
                    String filter_text = locationEditText.getText().toString().trim().toLowerCase(Locale.getDefault());

                    if (currentTab.equalsIgnoreCase("Shop")) {
                        nearbyShopsAdapter.searchShopFilter(filter_text);
                    } else {
                        nearbySellerAdapter.searchSellerFilter(filter_text);
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){
                    closeIconImageView.setVisibility(View.VISIBLE);
                }else{
                    closeIconImageView.setVisibility(View.GONE);
                }
            }
        });

        closeIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                locationEditText.setText("");
                hideKeyboard(getActivity());

            }
        });

        return rootView;
    }

    private void getLatLang() {

        if (currentTab.equalsIgnoreCase("Shop")) {

            shopAdapterMethod();

        } else {

            sellerAdapterMethod();
        }
    }

    private void sellerAdapterMethod() {

        if (location != null) {
            double Lat = location.getLatitude();
            double Lon = location.getLongitude();
            Log.e("latlaog", "La" + Lat + Lon);
            loginSession.setLatLong(String.valueOf(Lat), String.valueOf(Lon));
        } else {

            loginSession.setLatLong("10.691803", "-61.222503");
        }

        //Get SellerResponse
        if (isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            showCustomProgressDialog();
            serverRequest.createRequest(NearbyScreen.this, params, RequestID.REQ_SELLER_LIST, "GET", "");
        } else {
            noInternetAlertDialog();
        }

    }

    private void shopAdapterMethod() {

        if (location != null) {
            double Lat = location.getLatitude();
            double Lon = location.getLongitude();
            Log.e("latlaog", "La" + Lat + Lon);
            loginSession.setLatLong(String.valueOf(Lat), String.valueOf(Lon));
        } else {
            loginSession.setLatLong("10.691803", "-61.222503");
        }

        //Get MerchantResponse
        if (isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            showCustomProgressDialog();
            serverRequest.createRequest(NearbyScreen.this, params, RequestID.REQ_MERCHANT_LIST, "GET", "");
        } else {
            noInternetAlertDialog();
        }

    }

    private class NearbyShopsAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<MerchantList.Data> merchantDataOriginal;
        ArrayList<MerchantList.Data> merchantDataDummy;

        public NearbyShopsAdapter(Activity activity, ArrayList<MerchantList.Data> strings) {
            this.activity = activity;
            this.merchantDataOriginal = strings;
            this.merchantDataDummy = new ArrayList<>();
            this.merchantDataDummy.addAll(merchantDataOriginal);
        }

        @Override
        public int getCount() {

            try{return merchantDataOriginal.size();}catch (Exception e){}
            return 0;
        }

        @Override
        public Object getItem(int i) {

            try{return merchantDataOriginal.get(i);}catch (Exception e){}
            return 0;

        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

          /*  try {*/
                LayoutInflater inflater = activity.getLayoutInflater();
                if (view == null)
                    view = inflater.inflate(R.layout.custom_nearby_shoplist, null);

                RelativeLayout contentLayout = (RelativeLayout) view.findViewById(R.id.contentLayout);
                TextView shopNameTextView = (TextView) view.findViewById(R.id.shopNameTextView);
                TextView shopAddresstextView = (TextView) view.findViewById(R.id.shopAddresstextView);
                TextView distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
                TextView restaurant = (TextView) view.findViewById(R.id.restaurant);
                TextView Grocery = (TextView) view.findViewById(R.id.Grocery);
                TextView laundry = (TextView) view.findViewById(R.id.laundry);
                TextView navigateTextView = (TextView) view.findViewById(R.id.navigateTextView);
                TextView shopOffertextView = (TextView) view.findViewById(R.id.shopOffertextView);
                TextView rewardIndication = (TextView) view.findViewById(R.id.rewardIndication);

                shopNameTextView.setText(merchantDataOriginal.get(i).business_name.trim());
                shopAddresstextView.setText(merchantDataOriginal.get(i).address.trim());
                distanceTextView.setText(merchantDataOriginal.get(i).distance);

                List<MerchantList.Merchant_categories> merchant_categories = new ArrayList<>();
                merchant_categories = merchantDataOriginal.get(i).merchant_categories;

                if (!merchantDataOriginal.get(i).offers.isEmpty() && merchantDataOriginal.get(i).offers.size() > 0) {
                    shopOffertextView.setText(merchantDataOriginal.get(i).offers.get(0).percentage + "% OFF");
                    shopOffertextView.setVisibility(View.VISIBLE);
                } else {
                    shopOffertextView.setVisibility(View.GONE);
                }


                ArrayList<String> categories = new ArrayList<>();

                try{
                    for (MerchantList.Merchant_categories merchant_categories1 : merchant_categories) {
                        categories.add(merchant_categories1.business_category.category_name);
                    }
                }catch (Exception e){e.printStackTrace();}


                if (categories.size() >= 3) {

                    restaurant.setVisibility(View.VISIBLE);
                    Grocery.setVisibility(View.VISIBLE);
                    laundry.setVisibility(View.VISIBLE);

                    restaurant.setText(categories.get(0));
                    Grocery.setText(categories.get(1));
                    laundry.setText(categories.get(2));

                } else if (categories.size() == 2) {

                    restaurant.setVisibility(View.VISIBLE);
                    Grocery.setVisibility(View.VISIBLE);


                    restaurant.setText(categories.get(0));
                    Grocery.setText(categories.get(1));

                } else if (categories.size() == 1) {
                    restaurant.setText(categories.get(0));
                    restaurant.setVisibility(View.VISIBLE);
                } else {

                }

                navigateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {

                            Uri uri = Uri.parse("google.navigation:q=" + Double.parseDouble(merchantDataOriginal.get(i).latitude) + "," + Double.parseDouble(merchantDataOriginal.get(i).longitude));

                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }
                });

                if (mSearchText != null && !mSearchText.isEmpty()) {

                    int startPos = merchantDataOriginal.get(i).business_name.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + mSearchText.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(merchantDataOriginal.get(i).business_name);
                        ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{activity.getResources().getColor(R.color.colorAccent)});
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        shopNameTextView.setText(spannable);
                    } else {
                        shopNameTextView.setText(merchantDataOriginal.get(i).business_name);
                    }

                } else {
                    shopNameTextView.setText(merchantDataOriginal.get(i).business_name);

                }

                if(!(merchantDataOriginal.get(i).merchant_reward_settings==null)){
                    if (!merchantDataOriginal.get(i).merchant_reward_settings.isEmpty()) {
                        if (merchantDataOriginal.get(i).merchant_reward_settings.get(0).reward_option.equalsIgnoreCase("yes")) {
                            rewardIndication.setVisibility(View.VISIBLE);
                        } else {
                            rewardIndication.setVisibility(View.GONE);
                        }
                    } else {
                        rewardIndication.setVisibility(View.GONE);
                    }
                }else {
                    rewardIndication.setVisibility(View.GONE);
                }

                //click event
                contentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!(merchantDataOriginal.get(i).merchant_reward_settings == null)) {
                            if (!merchantDataOriginal.get(i).merchant_reward_settings.isEmpty()) {
                                if (merchantDataOriginal.get(i).merchant_reward_settings.get(0).reward_option.equalsIgnoreCase("yes")) {
                                    rewardYesNo = "Yes";
                                    rewardPercentage = merchantDataOriginal.get(i).merchant_reward_settings.get(0).redeem_reward_percentage;
                                } else {
                                    rewardYesNo = "No";
                                    rewardPercentage = "";
                                }
                            } else {
                                rewardYesNo = "No";
                                rewardPercentage = "";
                            }
                        } else {
                            rewardYesNo = "No";
                            rewardPercentage = "";
                        }

                        if (!merchantDataOriginal.get(i).offers.isEmpty() && merchantDataOriginal.get(i).offers.size() > 0) {
                            offerYesNo = "Yes";
                        } else {
                            offerYesNo = "No";
                        }

                        if (!merchantDataOriginal.get(i).offers.isEmpty() && merchantDataOriginal.get(i).offers.size() > 0) {
                            utility.offername.clear();

                            for (MerchantList.Offers OFFER : merchantDataOriginal.get(i).offers) {

                                if (OFFER.description != null && !OFFER.description.isEmpty()) {
                                    utility.offername.add(OFFER.offer_name + "<@>" + OFFER.percentage + "<@>" + OFFER.description);
                                } else {
                                    utility.offername.add(OFFER.offer_name + "<@>" + OFFER.percentage + "<@>" + "empty");
                                }
                            }

                        }
                        if (offerYesNo.equalsIgnoreCase("yes") || rewardYesNo.equalsIgnoreCase("yes")) {
                            Intent intent = new Intent(getActivity(), ShopOfferScreen.class);
                            if (merchantDataOriginal.get(i).banner_image != null) {
                                intent.putExtra("shopImage", merchantDataOriginal.get(i).banner_image);
                            } else {
                                intent.putExtra("shopImage", "NoImage");
                            }
                            intent.putExtra("shopName", merchantDataOriginal.get(i).business_name);
                            intent.putExtra("shopAddress", merchantDataOriginal.get(i).address);
                            intent.putExtra("rewardPercentage", rewardPercentage);
                            intent.putExtra("rewardYesNo", rewardYesNo);
                            intent.putExtra("offerYesNo", offerYesNo);
                            getActivity().startActivity(intent);
                        }


                    }
                });
            /* }catch (Exception e) {
            }*/
            return view;
        }

        public void searchShopFilter(String filterText) {

            try {
                mSearchText = filterText;
                filterText = filterText.toLowerCase(Locale.getDefault());
                merchantDataOriginal.clear();

                if (filterText.length() == 0) {
                    merchantDataOriginal.addAll(merchantDataDummy);
                } else {
                    for (MerchantList.Data contactList : merchantDataDummy) {
                        if (contactList.business_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
                            merchantDataOriginal.add(contactList);
                        }
                    }
                    if (merchantDataOriginal.size() == 0) {
                        //toast("No shop found");
                    }
                }
            }catch (Exception e){e.printStackTrace();}
            notifyDataSetChanged();
        }
    }

    private class NearbySellerAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<SellerList.Data> sellerDataOriginal;
        ArrayList<SellerList.Data> sellerDataDummy;

        public NearbySellerAdapter(Activity activity, ArrayList<SellerList.Data> strings) {
            this.activity = activity;
            this.sellerDataOriginal = strings;
            this.sellerDataDummy = new ArrayList<>();
            this.sellerDataDummy.addAll(sellerDataOriginal);
        }

        @Override
        public int getCount() {
            try{   return sellerDataOriginal.size();}catch (Exception e){e.printStackTrace();}
            return 0;
        }

        @Override
        public Object getItem(int i) {
            try{ return sellerDataOriginal.get(i);}catch (Exception e){e.printStackTrace();}
            return 0;

        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            try {

                LayoutInflater inflater = activity.getLayoutInflater();
                if (view == null)
                    view = inflater.inflate(R.layout.custom_nearby_sellerlist, null);

                TextView sellerNameTextView = (TextView) view.findViewById(R.id.sellerNameTextView);
                TextView shopAddresstextView = (TextView) view.findViewById(R.id.shopAddresstextView);
                TextView distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
                TextView navigateTextView = (TextView) view.findViewById(R.id.navigateTextView);


                sellerNameTextView.setText(sellerDataOriginal.get(i).getBusiness_name());
                shopAddresstextView.setText(sellerDataOriginal.get(i).getAddress().trim());
                distanceTextView.setText(sellerDataOriginal.get(i).getDistance());


                navigateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {

                            Uri uri = Uri.parse("google.navigation:q=" + Double.parseDouble(sellerDataOriginal.get(i).getLatitude()) + "," + Double.parseDouble(sellerDataOriginal.get(i).getLongitude()));

                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                });


                if (mSearchText != null && !mSearchText.isEmpty()) {

                    int startPos = sellerDataOriginal.get(i).getBusiness_name().toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + mSearchText.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(sellerDataOriginal.get(i).getBusiness_name());
                        ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{activity.getResources().getColor(R.color.colorAccent)});
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        sellerNameTextView.setText(spannable);
                    } else {
                        sellerNameTextView.setText(sellerDataOriginal.get(i).getBusiness_name());
                    }

                } else {
                    sellerNameTextView.setText(sellerDataOriginal.get(i).getBusiness_name());

                }

            }catch (Exception e){e.printStackTrace();}

            return view;
        }

        public void searchSellerFilter(String filterText) {

            try {

            mSearchText = filterText;
            filterText = filterText.toLowerCase(Locale.getDefault());
            sellerDataOriginal.clear();

            if (filterText.length() == 0) {
                sellerDataOriginal.addAll(sellerDataDummy);
            } else {
                for (SellerList.Data contactList : sellerDataDummy) {
                    if (contactList.getBusiness_name().toLowerCase(Locale.getDefault()).contains(filterText)) {
                        sellerDataOriginal.add(contactList);
                    }
                }
                if (sellerDataOriginal.size() == 0) {
                    //toast("No Seller found");
                }
            }

            notifyDataSetChanged();

            }catch(Exception e){e.printStackTrace();}
        }
    }

    ///////////////////////////Server handler method ////////////////////////////

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideCustomProgressDialog();

        switch (requestID) {

            case REQ_MERCHANT_LIST:

                MerchantList merchantList = (MerchantList) result;
                ArrayList<MerchantList.Data> datas = merchantList.data;

                nearbyShopsAdapter = new NearbyShopsAdapter(getActivity(), datas);
                nearByListView.setAdapter(nearbyShopsAdapter);

                contentLayout.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);

                break;

            case REQ_SELLER_LIST:

                SellerList sellerList = (SellerList) result;

                ArrayList<SellerList.Data> sellerListData = sellerList.data;

                nearbySellerAdapter = new NearbySellerAdapter(getActivity(), sellerListData);
                nearByListView.setAdapter(nearbySellerAdapter);

                contentLayout.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideCustomProgressDialog();
        switch (requestID) {

            case REQ_MERCHANT_LIST:

                contentLayout.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                errorImageView.setText(getResources().getString(R.string.Nostoresfound));

                break;

            case REQ_SELLER_LIST:

                contentLayout.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                errorImageView.setText(getResources().getString(R.string.Nosellerssfound));

                break;
        }

    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity() , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        googleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
