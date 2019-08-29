package com.omlah.customer.tabhome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.model.CheckPhoneNumber;
import com.omlah.customer.model.DbContactList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 21-09-2017.
 */

public class ContactListScreen extends BaseActivity implements ServerListener {

    //Create class objects
    ServerRequest serverRequest;
    ContactsAdapter contactsAdapter;

    //Create xml objects
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.contactListView) RecyclerView contactListView;
    @BindView(R.id.searchEditText)EditText searchEditText;

    //Request permission
    public  static final int RequestPermissionCode  = 1 ;

    //Crate objects
    Cursor cursor;
    String name="", phonenumber="", photoUri="empty";
    String globalSearchName = "";
    String mSearchText,numberSearchText;

    //Create collection
    DbContactList dbContactList;
    ArrayList<DbContactList> dbContactLists = new ArrayList<>();

    String screenName="",NUMBER,CODE,NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_screen);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);
        serverRequest = ServerRequest.getInstance(this);

        //Request run time permission
        EnableRuntimePermission();

        //getIntent
        Intent intent = getIntent();
        if(intent!=null){

            screenName = intent.getStringExtra("Screen");
        }

        //filter method
        searchEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    globalSearchName = s.toString();
                    String filter_text = searchEditText.getText().toString().trim().toLowerCase(Locale.getDefault());

                    contactsAdapter.searchFilter(filter_text);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }

    //Get Contact List
    private void GetContactsIntoArrayList() {

        try{

            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

            while (cursor.moveToNext()) {

                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                photoUri = "empty";

                Log.e("name",":"+name);
                Log.e("phonenumber",":"+phonenumber);

                dbContactList = new DbContactList();
                dbContactList.setName(name);
                dbContactList.setNumber(phonenumber);
                dbContactList.setImage(photoUri);
                dbContactLists.add(dbContactList);

            }

            HashSet<DbContactList> s= new HashSet<DbContactList>();
            s.addAll(dbContactLists);
            dbContactLists = new ArrayList<DbContactList>();
            dbContactLists.addAll(s);



            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
            contactListView.setLayoutManager(linearLayoutManager);
            contactListView.setItemAnimator(new DefaultItemAnimator());
            contactListView.setHasFixedSize(true);
            contactsAdapter = new ContactsAdapter(this,dbContactLists);
            contactListView.setAdapter(contactsAdapter);
            contactsAdapter.notifyDataSetChanged();

            cursor.close();

        }catch (Exception e){e.printStackTrace();
            toast("Permission canceled, now your application cannot access CONTACTS.");
        }

    }

    //Request run time permission
    private void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                ContactListScreen.this,
                Manifest.permission.READ_CONTACTS))
        {
            GetContactsIntoArrayList();

        } else {

            ActivityCompat.requestPermissions(ContactListScreen.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    GetContactsIntoArrayList();

                } else {

                    toast("Permission Canceled, Now your application cannot access CONTACTS.");

                }
                break;
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        try {
            CheckPhoneNumber checkPhoneNumber = (CheckPhoneNumber) result;
            if (checkPhoneNumber.data.user) {
                 toast("This customer didn't have a RPay account");
            } else {
                Intent intent = new Intent();
                intent.putExtra("NUMBER",NUMBER);
                intent.putExtra("NAME",NAME);
                intent.putExtra("CODE", "+"+CODE);
                setResult(1, intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
    }

    private class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{

        private Context context;
        ArrayList<DbContactList> dbContactListsOriginal;
        ArrayList<DbContactList> dbContactListsDummy;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CountryCodePicker ccpPicker;
            private EditText numberEditText;
            private TextView  contactNameTextView, contactNumberTextView,payNowTextView;
            private ImageView contactsImageView;
            private RelativeLayout contactLayout;


            public MyViewHolder(View view) {
                super(view);

                contactLayout           = view.findViewById(R.id.contactLayout);
                contactNameTextView     = view.findViewById(R.id.contactNameTextView);
                contactNumberTextView   = view.findViewById(R.id.contactNumberTextView);
                payNowTextView          = view.findViewById(R.id.payNowTextView);
                contactsImageView       = view.findViewById(R.id.contactsImageView);
                ccpPicker               = view.findViewById(R.id.ccpPicker);
                numberEditText          = view.findViewById(R.id.numberEditText);
            }
        }

        public ContactsAdapter(Context contextt,ArrayList<DbContactList> dbContactListsOriginal) {
            this.context = contextt;
            this.dbContactListsOriginal = dbContactListsOriginal;
            this.dbContactListsDummy = new ArrayList<>();
            this.dbContactListsDummy.addAll(dbContactListsOriginal);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_contact_list, parent, false);


            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder viewHolder, final int i) {

            if(screenName.equalsIgnoreCase("AddContact")){

                viewHolder.payNowTextView.setText("Add contact");

            }else if(screenName.equalsIgnoreCase("PayNow")){

                viewHolder.payNowTextView.setText("Pay Now");

            }else if(screenName.equalsIgnoreCase("RequestNow")){

                viewHolder.payNowTextView.setText("Request Now");

            }else if(screenName.equalsIgnoreCase("HomeScreen")){

                viewHolder.payNowTextView.setText("Select");

            }else{

            }

            viewHolder.contactNameTextView.setText(dbContactListsOriginal.get(i).getName());
            viewHolder.contactNumberTextView.setText(dbContactListsOriginal.get(i).getNumber());

            viewHolder.contactLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    viewHolder.ccpPicker.registerCarrierNumberEditText(viewHolder.numberEditText);
                    viewHolder.ccpPicker.setFullNumber(dbContactListsOriginal.get(i).getNumber().replace("+","").replace(" ","").replace("+","").replace("(","").replace(")",""));

                    String GET_FULL_NUMBER = viewHolder.ccpPicker.getFullNumber();
                    CODE = viewHolder.ccpPicker.getSelectedCountryCode();
                    NUMBER = GET_FULL_NUMBER.substring(CODE.length(),GET_FULL_NUMBER.length());
                    NAME = dbContactListsOriginal.get(i).getName();

                    if (!isConnectingToInternet()) {
                        noInternetAlertDialog();
                    } else {
                        final Map<String, String> param = new HashMap<String, String>();
                        showProgressDialog();
                        serverRequest.createRequest(ContactListScreen.this, param, RequestID.REQ_NUMBER_CHECK, "GET", GET_FULL_NUMBER);
                    }

                }
            });


            if (numberSearchText != null && !numberSearchText.isEmpty()) {

                if(numberSearchText.matches(".*\\d.*")){

                    int startPos = dbContactListsOriginal.get(i).getNumber().toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + mSearchText.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(dbContactListsOriginal.get(i).getNumber());
                        ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{context.getResources().getColor(R.color.colorAccent)});
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder.contactNumberTextView.setText(spannable);
                    } else {
                        viewHolder.contactNumberTextView.setText(dbContactListsOriginal.get(i).getNumber());
                    }

                } else{
                    // does not contain a number
                }


            } else {

                viewHolder.contactNumberTextView.setText(dbContactListsOriginal.get(i).getNumber());
            }

            if (mSearchText != null && !mSearchText.isEmpty()) {

                int startPos = dbContactListsOriginal.get(i).getName().toLowerCase(Locale.US).indexOf(numberSearchText.toLowerCase(Locale.US));
                int endPos = startPos + numberSearchText.length();

                if (startPos != -1) {
                    Spannable spannable = new SpannableString(dbContactListsOriginal.get(i).getName());
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{context.getResources().getColor(R.color.colorAccent)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.contactNameTextView.setText(spannable);
                } else {
                    viewHolder.contactNameTextView.setText(dbContactListsOriginal.get(i).getName());
                }

            } else {
                viewHolder.contactNameTextView.setText(dbContactListsOriginal.get(i).getName());

            }

        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public int getItemCount() {
            return dbContactListsOriginal.size();
        }

        public void searchFilter(String filterText) {
            mSearchText = filterText;
            numberSearchText = filterText;
            filterText = filterText.toLowerCase(Locale.getDefault());
            dbContactListsOriginal.clear();

            if (filterText.length() == 0) {
                dbContactListsOriginal.addAll(dbContactListsDummy);
            } else {
                for (DbContactList dbContactList : dbContactListsDummy) {
                    if (dbContactList.getName().toLowerCase(Locale.getDefault()).contains(filterText) || dbContactList.getNumber().toLowerCase(Locale.getDefault()).contains(filterText)) {
                        dbContactListsOriginal.add(dbContactList);
                    }
                }
                if (dbContactListsOriginal.size() == 0) {
                    toast("No contact found");
                }
            }

            notifyDataSetChanged();

        }
    }

}
