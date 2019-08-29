package com.omlah.customer.tabmore;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CircleTransform;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.BeneficiaryList;
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

/**
 * Created by admin on 20-09-2017.
 */

public class MyBeneficiaryScreen extends BaseActivity implements ServerListener{

    //Create class objects
    private ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;
    private ContactsAdapter contactsAdapter;

    //Create xml objects
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.plusImageView)ImageView plusImageView;
    @BindView(R.id.tabRadioGroup)RadioGroup tabRadioGroup;
    @BindView(R.id.familyButton)RadioButton familyButton;
    @BindView(R.id.businessButton)RadioButton businessButton;
    @BindView(R.id.myContactsListView)ListView myContactsListView;
    @BindView(R.id.errorImageView)TextView errorImageView;

    //Create string files
    String currentTab="Family";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_contacts_screen);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader  =  ServerRequestwithHeader.getInstance(this);
        loginSession   = LoginSession.getInstance(this);

        currentTab = "Family";
        familyAdapterMethod();

        //Switching event
        tabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                switch (i){

                    case R.id.familyButton:

                        if(!currentTab.equalsIgnoreCase("Family")){

                            currentTab = "Family";

                            familyAdapterMethod();
                        }

                        break;

                    case R.id.businessButton:

                        if(!currentTab.equalsIgnoreCase("Business")){

                            currentTab = "Business";

                            businessAdapterMethod();
                        }

                        break;

                }
            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent intent1 = new Intent(MyBeneficiaryScreen.this,ContactListScreen.class);
                intent1.putExtra("Screen","AddContact");
                startActivityForResult(intent1,1);*/

                Intent intent1 = new Intent(MyBeneficiaryScreen.this,AddBeneficiary.class);
                intent1.putExtra("Screen","AddContact");
                startActivityForResult(intent1,1);

            }
        });
    }

    private void businessAdapterMethod() {

    }

    private void familyAdapterMethod() {

        //Get BenificaryList
        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(MyBeneficiaryScreen.this,params, RequestID.REQ_BENEFICIARY_LIST,"POST","");


        }else{
            noInternetAlertDialog();
        }

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
                view = inflater.inflate(R.layout.custom_contact_list,null);

                //Initialize xml object
                viewHolder.contactNameTextView     = (TextView) view.findViewById(R.id.contactNameTextView);
                viewHolder.contactNumberTextView   = (TextView) view.findViewById(R.id.contactNumberTextView);
                viewHolder.payNowTextView          = (TextView) view.findViewById(R.id.payNowTextView);
                viewHolder.numberEditText          = (EditText) view.findViewById(R.id.numberEditText);
                viewHolder.contactsImageView       = (ImageView) view.findViewById(R.id.contactsImageView);
                viewHolder.ccpPicker               = (CountryCodePicker) view.findViewById(R.id.ccpPicker);

                viewHolder.ccpPicker.registerCarrierNumberEditText(viewHolder.numberEditText);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            Log.e("name",dbContactLists.get(i).beneficiary_user.name);

            viewHolder.contactNameTextView.setText(dbContactLists.get(i).beneficiary_name);
            viewHolder.payNowTextView.setText(getResources().getString(R.string.Remove));

            //Seprate country code and number
            viewHolder.ccpPicker.setFullNumber(dbContactLists.get(i).beneficiary_user.phone_number);
            viewHolder.contactNumberTextView.setText(viewHolder.ccpPicker.getFormattedFullNumber());

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

            viewHolder.contactNameTextView.setTag(i);
            viewHolder.contactNumberTextView.setTag(i);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String ID = dbContactLists.get(i).id;

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

                    alertDialog.setTitle(getResources().getString(R.string.Alert));

                    alertDialog.setMessage(getResources().getString(R.string.AreYouSureWantToDelete));

                    alertDialog.setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //Get BenificaryList
                            if(isConnectingToInternet()){

                                Map<String, String> params = new HashMap<>();
                                showProgressDialog();
                                serverRequestwithHeader.createRequest(MyBeneficiaryScreen.this,params, RequestID.REQ_DELETE_BENEFICIARY,"DELETE",ID);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        hideProgressDialog();
                                        currentTab = "Family";
                                        familyAdapterMethod();

                                    }
                                },3000);

                            }else{
                                noInternetAlertDialog();
                            }

                        }
                    });

                    alertDialog.setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                    alertDialog.setCancelable(true);

                }
            });

            return view;
        }

        private class ViewHolder {

            private CountryCodePicker ccpPicker;
            private EditText numberEditText;
            private TextView  contactNameTextView,payNowTextView,contactNumberTextView;
            private ImageView contactsImageView;

        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_BENEFICIARY_LIST:

                try{
                    BeneficiaryList contactList = (BeneficiaryList)result;

                    if(contactList.data.size() > 0){
                        ArrayList<BeneficiaryList.Data> familyDbContactLists = contactList.data;
                        myContactsListView.setVisibility(View.VISIBLE);
                        errorImageView.setVisibility(View.GONE);
                        contactsAdapter = new ContactsAdapter(this, familyDbContactLists);
                        myContactsListView.setAdapter(contactsAdapter);
                    }else{
                        myContactsListView.setVisibility(View.GONE);
                        errorImageView.setVisibility(View.VISIBLE);
                        myContactsListView.setAdapter(null);
                    }

                }catch (Exception e){e.printStackTrace();
                    myContactsListView.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.VISIBLE);
                    myContactsListView.setAdapter(null);
                }

                break;

            case REQ_DELETE_BENEFICIARY:

                toast(result.toString());
                familyAdapterMethod();

                break;
        }


    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID) {

            case REQ_BENEFICIARY_LIST:

                myContactsListView.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                myContactsListView.setAdapter(null);

                break;

            case REQ_DELETE_BENEFICIARY:

                toast(error);

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == 1){

                //Get money request response
                familyAdapterMethod();
            }
        }
    }
}
