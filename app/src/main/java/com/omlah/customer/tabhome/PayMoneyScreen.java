package com.omlah.customer.tabhome;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CircleTransform;
import com.omlah.customer.common.CountryAdapter;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.Utility;
import com.omlah.customer.model.BeneficiaryList;
import com.omlah.customer.model.CountryList;
import com.omlah.customer.model.GetReceiverDetails;
import com.omlah.customer.qrscanner.CCScanAndPayScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 18-09-2017.
 */

public class PayMoneyScreen extends BaseActivity implements ServerListener {

    //Create class objects
    LoginSession loginSession;
    private ServerRequestwithHeader serverRequestwithHeader;
    private ServerRequest serverRequest;
    ContactsAdapter contactsAdapter;

    //Create xml objects
    @BindView(R.id.contactListView)
    ListView contactListView;
    @BindView(R.id.chooseBanner)
    TextView chooseBanner;
    @BindView(R.id.bannertext)
    TextView bannertext;
    @BindView(R.id.errorImageView)
    TextView errorImageView;
    @BindView(R.id.contactImageView)
    ImageView contactImageView;
    @BindView(R.id.mobileNumberEdiText)
    EditText mobileNumberEdiText;
    @BindView(R.id.payNowButton)
    Button payNowButton;
    @BindView(R.id.numberCodeSpinner)
    Spinner numberCodeSpinner;
    @BindView(R.id.ccpPicker)
    CountryCodePicker ccpPicker;

    //String
    private CountryList countryList;
    String screenName = "",SELECTED_SPINNER_NUMBER;
    private boolean checkNumber;

    HashMap<String,Integer> list = new HashMap<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymoney_screen);
        showBackArrow();

        //Initialize xml objects
        ButterKnife.bind(this);
        registerCarrierEditText();

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        serverRequest = ServerRequest.getInstance(this);

        //GetCountryCode
        getCountryCodeResponse();

        numberCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_SPINNER_NUMBER = countryList.data.countryList.get(position).phone_code.trim();
                if(SELECTED_SPINNER_NUMBER!=null && !SELECTED_SPINNER_NUMBER.isEmpty()){
                    if(SELECTED_SPINNER_NUMBER.equalsIgnoreCase("+1")){
                        ccpPicker.setDefaultCountryUsingNameCode("US");
                        ccpPicker.resetToDefaultCountry();
                    }else{
                        ccpPicker.setDefaultCountryUsingPhoneCode(Integer.parseInt(SELECTED_SPINNER_NUMBER.replace("+","")));
                        ccpPicker.resetToDefaultCountry();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Get Intent
        Intent intent = getIntent();
        if (intent != null) {

            screenName = intent.getStringExtra("screenName");
            if (screenName.equalsIgnoreCase("PayMoney")) {
                setActionBarTitle(getResources().getString(R.string.SendMoney));
                bannertext.setText(getResources().getString(R.string.Entermobilenumbertopay));
            } else {
                setActionBarTitle(getResources().getString(R.string.RequestMoney));
                bannertext.setText(getResources().getString(R.string.Entermobilenumbertorequest));
            }
        }

        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(PayMoneyScreen.this,ContactListScreen.class);
                if(screenName.equalsIgnoreCase("PayMoney")){
                    intent1.putExtra("Screen","PayNow");
                }else{
                    intent1.putExtra("Screen","RequestNow");
                }

                startActivityForResult(intent1,1);
            }
        });


        mobileNumberEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().isEmpty()){

                    if(editable.toString().length() > 5){
                        payNowButton.setVisibility(View.VISIBLE);
                    }else{
                        payNowButton.setVisibility(View.GONE);
                    }
                }else{
                    payNowButton.setVisibility(View.GONE);
                }
            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkNumber) {
                    if (isConnectingToInternet()) {
                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("phone_number", ccpPicker.getFullNumber());
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(PayMoneyScreen.this, param, RequestID.REQ_GET_RECEIVER_DETAILS, "POST", "");

                    } else {
                        noInternetAlertDialog();
                    }

                } else {
                    toast(getResources().getString(R.string.EnterValidNumber));
                }


            }
        });

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
    }


    private void getCountryCodeResponse() {

        if(!Utility.COUNTRY_LIST.toString().isEmpty()){
            onSuccess(Utility.COUNTRY_LIST,RequestID.REQ_COUNTRYLIST);
        }else{
            if(!isConnectingToInternet()){
                noInternetAlertDialog();
            }else{
                final Map<String, String> param = new HashMap<String, String>();
                showProgressDialog();
                serverRequest.createRequest(PayMoneyScreen.this,param,RequestID.REQ_COUNTRYLIST,"GET","");
            }
        }

    }


    private void registerCarrierEditText() {

        ccpPicker.registerCarrierNumberEditText(mobileNumberEdiText);
        ccpPicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                checkNumber = isValidNumber;
            }
        });

        ccpPicker.registerCarrierNumberEditText(mobileNumberEdiText);

    }

    private  boolean checkAndRequestPermissions() {


        //READ_CONTACTS
        int READCONTACTS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        //READ CONTACT
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READCONTACTS != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS); }

        //Final Step

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private class ContactsAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<BeneficiaryList.Data> dbContactLists;
        private LayoutInflater inflater;

        public ContactsAdapter(Activity activity, ArrayList<BeneficiaryList.Data> dbContactLists) {
            this.activity = activity;
            this.dbContactLists = dbContactLists;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return dbContactLists.size();
        }

        @Override
        public Object getItem(int i) {
            return dbContactLists.get(i);
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
                view = inflater.inflate(R.layout.custom_contact_list, null);

                //Initialize xml object
                viewHolder.contactNameTextView = (TextView) view.findViewById(R.id.contactNameTextView);
                viewHolder.contactNumberTextView = (TextView) view.findViewById(R.id.contactNumberTextView);
                viewHolder.payNowTextView = (TextView) view.findViewById(R.id.payNowTextView);
                viewHolder.contactsImageView = (ImageView) view.findViewById(R.id.contactsImageView);
                viewHolder.ccpPicker               = (CountryCodePicker) view.findViewById(R.id.ccpPicker);
                viewHolder.numberEditText          = (EditText) view.findViewById(R.id.numberEditText);

                viewHolder.ccpPicker.registerCarrierNumberEditText(viewHolder.numberEditText);


                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            if(dbContactLists.get(i).beneficiary_user.profile_image!=null && !dbContactLists.get(i).beneficiary_user.profile_image.isEmpty()){

                String getImage[] = dbContactLists.get(i).beneficiary_user.profile_image.split("upload");
                String imageFormat = getImage[0]+"upload/w_250,h_250,c_thumb,g_face,r_max"+getImage[1];
                Picasso.with(activity)
                        .load(imageFormat)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(getResources().getDrawable(R.drawable.user_image_post))
                        .transform(new CircleTransform())
                        .into(viewHolder.contactsImageView);
            }

            if (screenName.equalsIgnoreCase("PayMoney")) {
                viewHolder.payNowTextView.setText("Pay now");
            } else {
                viewHolder.payNowTextView.setText("Request now");
            }

            viewHolder.contactNameTextView.setText(dbContactLists.get(i).beneficiary_name);

            //Seprate country code and number
            viewHolder.ccpPicker.setFullNumber(dbContactLists.get(i).beneficiary_user.phone_number);
            viewHolder.contactNumberTextView.setText(viewHolder.ccpPicker.getFormattedFullNumber());

            viewHolder.contactNameTextView.setTag(i);
            viewHolder.contactNumberTextView.setTag(i);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(PayMoneyScreen.this, CCScanAndPayScreen.class);
                    intent.putExtra("name",dbContactLists.get(i).beneficiary_name);

                    intent.putExtra("number",dbContactLists.get(i).beneficiary_user.phone_number);

                    intent.putExtra("profile_image",dbContactLists.get(i).beneficiary_user.profile_image);

                    if (screenName.equalsIgnoreCase("PayMoney")) {
                        intent.putExtra("type","Pay to");
                    } else {
                        intent.putExtra("type","Request from");
                    }

                    intent.putExtra("fee_amount","0");
                    intent.putExtra("fee_option","0");
                    intent.putExtra("extra_fees","0");

                    startActivity(intent);

                }
            });

            return view;
        }

        private class ViewHolder {

            private CountryCodePicker ccpPicker;
            private EditText numberEditText;
            private TextView contactNameTextView, contactNumberTextView, payNowTextView;
            private ImageView contactsImageView;

        }
    }

    //Response handle class
    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_COUNTRYLIST:


                countryList = (CountryList) result;
                Utility.COUNTRY_LIST = result;
                if (countryList.data.countryList.size() > 0) {

                    CountryAdapter customAdapter = new CountryAdapter(PayMoneyScreen.this, countryList.data.countryList);
                    numberCodeSpinner.setAdapter(customAdapter);

                    list.clear();
                    try{
                        int index = 0;
                        for(CountryList.CountriesList list12 : countryList.data.countryList){
                            list.put(list12.phone_code,index);
                            index = index + 1;
                        }
                        numberCodeSpinner.setSelection(list.get(loginSession.getCustomerCountryCode()));
                    }catch (Exception e){e.printStackTrace();}

                }


                //Get BenificaryList
                if (isConnectingToInternet()) {

                    Map<String, String> params = new HashMap<>();
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(PayMoneyScreen.this, params, RequestID.REQ_BENEFICIARY_LIST, "POST", "");

                } else {
                    noInternetAlertDialog();
                }

                break;

            case REQ_GET_RECEIVER_DETAILS:

                try{
                GetReceiverDetails getReceiverDetails = (GetReceiverDetails) result;
                if (getReceiverDetails.data.user==null) {
                    toast("Selected customer didn't have RPay account");
                }else{
                    if(getReceiverDetails.data.user.country.phone_code.equals(SELECTED_SPINNER_NUMBER)){

                        if(ccpPicker.getFullNumber().equalsIgnoreCase(loginSession.getphoneNumber())){
                            toast("You cannot send/request money from yourself");
                        }else{

                            Intent intent = new Intent(PayMoneyScreen.this, CCScanAndPayScreen.class);
                            intent.putExtra("name",getReceiverDetails.data.user.name);
                            intent.putExtra("number",ccpPicker.getFullNumber());
                            intent.putExtra("profile_image",getReceiverDetails.data.user.profile_image);

                            if (screenName.equalsIgnoreCase("PayMoney")) {
                                intent.putExtra("type","Pay to");
                            } else {
                                intent.putExtra("type","Request from");
                            }

                            intent.putExtra("fee_amount","0");
                            intent.putExtra("fee_option","0");
                            intent.putExtra("extra_fees","0");

                            startActivity(intent);
                        }
                    }else{

                        toast("Please check country code");
                    }

                }
                }catch (Exception e){
                    e.printStackTrace();
                    toast("Selected customer didn't have RPay account");
                }

                break;

            case REQ_BENEFICIARY_LIST:

                try {

                    BeneficiaryList contactList = (BeneficiaryList) result;
                    if(!(contactList.data.size() > 0)){
                        chooseBanner.setVisibility(View.GONE);
                        errorImageView.setVisibility(View.VISIBLE);
                    }else{
                        ArrayList<BeneficiaryList.Data> familyDbContactLists = contactList.data;
                        contactListView.setVisibility(View.VISIBLE);
                        chooseBanner.setVisibility(View.VISIBLE);
                        errorImageView.setVisibility(View.GONE);
                        contactsAdapter = new ContactsAdapter(this, familyDbContactLists);
                        contactListView.setAdapter(contactsAdapter);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_GET_RECEIVER_DETAILS:

                toast("This customer didn't have Rpay account");

                break;

                default:

                    contactListView.setVisibility(View.GONE);
                    chooseBanner.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.VISIBLE);

                    break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 1) {
                String NUMBER = data.getStringExtra("NUMBER");
                String NAME = data.getStringExtra("NAME");
                String CODE = data.getStringExtra("CODE");

                try {
                    numberCodeSpinner.setSelection(list.get(CODE));
                    mobileNumberEdiText.setText(NUMBER);
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("Please choose a valid number");
                }
            }
        }
    }
}
