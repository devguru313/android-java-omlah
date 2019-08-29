package com.omlah.customer.qrscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hbb20.CountryCodePicker;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CountryAdapter;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.Utility;
import com.omlah.customer.model.CountryList;
import com.omlah.customer.model.CustomerQRcodeDetails;
import com.omlah.customer.model.GetReceiverDetails;
import com.omlah.customer.model.QRcodeDetails;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.ContactListScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.omlah.customer.service.RequestID.REQ_CUSTOMER_QR_DETAILS;
import static com.omlah.customer.service.RequestID.REQ_QR_DETAILS;


/**
 * Created by admin on 28-09-2017.
 */

public class QrcodeScreen extends BaseActivity implements BarcodeTracker.BarcodeGraphicTrackerCallback,ServerListener {

    LoginSession loginSession;

    private static final String TAG = "Barcode-reader";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private boolean checkNumber;

    private ServerRequestwithHeader serverRequestwithHeader;
    private ServerRequest serverRequest;

    /**
     * Initializes the UI and creates the detector pipeline.
     */

    boolean scan = true;
    CountryCodePicker ccpPicker;
    RelativeLayout mobileNumberButton;
    ImageView contactImageView;
    Spinner numberCodeSpinner;
    Button fabArrowButton;

    CountryAdapter customAdapter;
    private CountryList countryList;

    RelativeLayout tezLayout,payMoneyButton,receiveMoneyButton;
    RadioButton allButton,findButton;
    EditText mobileNumberEdiText;

    String SELECTED_SPINNER_NUMBER="";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    HashMap<String,Integer> list = new HashMap<>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.barcode_capture);
        showBackArrow();
        setActionBarTitle("");

        loginSession = LoginSession.getInstance(this);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        payMoneyButton = (RelativeLayout) findViewById(R.id.payMoneyButton);
        receiveMoneyButton = (RelativeLayout) findViewById(R.id.receiveMoneyButton);
        tezLayout = (RelativeLayout) findViewById(R.id.tezLayout);
        allButton = (RadioButton) findViewById(R.id.allButton);
        findButton = (RadioButton) findViewById(R.id.findButton);
        contactImageView = (ImageView) findViewById(R.id.contactImageView);
        numberCodeSpinner = (Spinner) findViewById(R.id.numberCodeSpinner);
        ccpPicker = (CountryCodePicker) findViewById(R.id.ccpPicker);

        mobileNumberEdiText = (EditText) findViewById(R.id.mobileNumberEdiText);
        fabArrowButton = (Button) findViewById(R.id.fabArrowButton);

        boolean autoFocus = true;
        boolean useFlash = false;

        registerCarrierEditText();

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

        mobileNumberButton = (RelativeLayout)findViewById(R.id.mobileNumberButton);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        /*mobileNumberEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().isEmpty()){

                    if(editable.toString().length() > 5){
                        fabArrowButton.show();
                    }else{
                        fabArrowButton.hide();
                    }
                }else{
                    fabArrowButton.hide();
                }
            }
        });*/

        allButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    tezLayout.setVisibility(View.GONE);
                }
            }
        });

        findButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    tezLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        payMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(QrcodeScreen.this,RippleScreen.class);
                intent.putExtra("type","PAY");
                startActivity(intent);

            }
        });

        receiveMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(QrcodeScreen.this,RippleScreen.class);
                intent.putExtra("type","RECEIVE");
                startActivity(intent);

            }
        });


        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(QrcodeScreen.this,ContactListScreen.class);
                intent1.putExtra("Screen","PayNow");
                startActivityForResult(intent1,1);
            }
        });

        fabArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkNumber){

                    if(isConnectingToInternet()){
                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("phone_number",ccpPicker.getFullNumber());
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(QrcodeScreen.this,param,RequestID.REQ_GET_RECEIVER_DETAILS,"POST","");

                    }else{
                        noInternetAlertDialog();
                    }
                }else{
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
                serverRequest.createRequest(QrcodeScreen.this,param,RequestID.REQ_COUNTRYLIST,"GET","");
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

    @Override
    public void onDetectedQrCode(Barcode barcode) {

        try {

            if (scan) {

                scan = false;

                if (barcode != null) {

                    final Barcode barcode1 = barcode;
                    Point[] p = barcode1.cornerPoints;
                    Log.e("BarcodeBarcode", barcode1.displayValue);

                    if (barcode1.displayValue.contains("scanpay")) {

                        final String splitQR[] = barcode1.displayValue.split("<@>");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (splitQR[1].equalsIgnoreCase("scanpay")) {

                                    if (isConnectingToInternet()) {

                                        Map<String, String> params = new HashMap<>();
                                        params.put("request", splitQR[0].trim());
                                        showProgressDialog();
                                        serverRequestwithHeader.createRequest(QrcodeScreen.this, params, REQ_QR_DETAILS, "POST", "");

                                    } else {
                                        noInternetAlertDialog();
                                    }

                                }

                            }
                        });

                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (isConnectingToInternet()) {

                                    Map<String, String> params = new HashMap<>();
                                    params.put("qr_data", barcode1.displayValue.toString().trim());
                                    showProgressDialog();
                                    serverRequestwithHeader.createRequest(QrcodeScreen.this, params, REQ_CUSTOMER_QR_DETAILS, "POST", "");

                                } else {
                                    noInternetAlertDialog();
                                }


                            }
                        });

                    }


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Handles the requesting of the camera permission.
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    /**
     * Creates and starts the camera.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {

        try{

            Context context = getApplicationContext();

            // A barcode detector is created to track barcodes.  An associated multi-processor instance
            // is set to receive the barcode detection results, track the barcodes, and maintain
            // graphics for each barcode on screen.  The factory is used by the multi-processor to
            // create a separate tracker instance for each barcode.
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                    .setBarcodeFormats(Barcode.ALL_FORMATS)
                    .build();
            BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(this);
            barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

            if (!barcodeDetector.isOperational()) {
                // Note: The first time that an app using the barcode or face API is installed on a
                // device, GMS will download a native libraries to the device in order to do detection.
                // Usually this completes before the app is run for the first time.  But if that
                // download has not yet completed, then the above call will not detect any barcodes
                // and/or faces.
                //
                // isOperational() can be used to check if the required native libraries are currently
                // available.  The detectors will automatically become operational once the library
                // downloads complete on device.
                Log.w(TAG, "Detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this, R.string.low_storage_error,
                            Toast.LENGTH_LONG).show();
                    Log.w(TAG, getString(R.string.low_storage_error));
                }
            }

            // Creates and starts the camera.  Note that this uses a higher resolution in comparison
            // to other detection examples to enable the barcode detector to detect small barcodes
            // at long distances.
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(metrics.widthPixels, metrics.heightPixels)
                    .setRequestedFps(24.0f);

            // make sure that auto focus is an available option
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                builder = builder.setFocusMode(
                        autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
            }

            mCameraSource = builder
                    .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                    .build();

        }catch (Exception e){e.printStackTrace();}

    }

    // Restarts the camera
    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
        scan = true;
    }

    // Stops the camera
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = true;
            boolean useFlash = false;
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_COUNTRYLIST:


                countryList = (CountryList) result;
                Utility.COUNTRY_LIST = result;
                if (countryList.data.countryList.size() > 0) {

                    customAdapter = new CountryAdapter(QrcodeScreen.this, countryList.data.countryList);
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
                    serverRequestwithHeader.createRequest(QrcodeScreen.this, params, RequestID.REQ_BENEFICIARY_LIST, "POST", "");

                } else {
                    noInternetAlertDialog();
                }

                break;

            case REQ_CUSTOMER_QR_DETAILS:

                try{

                    CustomerQRcodeDetails customerQRcodeDetails = (CustomerQRcodeDetails)result;

                    if(customerQRcodeDetails.data.role.equalsIgnoreCase("customer")){

                        Intent intent = new Intent(QrcodeScreen.this, CCScanAndPayScreen.class);
                        intent.putExtra("name", customerQRcodeDetails.data.name);
                        intent.putExtra("profile_image",customerQRcodeDetails.data.profile_image);
                        intent.putExtra("number", customerQRcodeDetails.data.phone_number);
                        intent.putExtra("type","Pay to");

                        //Fee
                        intent.putExtra("fee_amount",customerQRcodeDetails.data.poppay_fee.fee_amount);
                        intent.putExtra("fee_option",customerQRcodeDetails.data.poppay_fee.fee_option);
                        intent.putExtra("extra_fees",customerQRcodeDetails.data.poppay_fee.extra_fees);


                        startActivity(intent);

                    }else{

                        String offerPercentage = "0";
                        String rewardYesNo = "NO";

                        if(!(customerQRcodeDetails.data.reward_settings==null)){

                            if(customerQRcodeDetails.data.reward_settings.reward_option.equalsIgnoreCase("Yes")){

                                Double redeem_reward = Double.parseDouble(customerQRcodeDetails.data.reward_settings.redeem_reward);
                                Double reward_balace = Double.parseDouble(customerQRcodeDetails.data.reward_settings.reward_balance);

                                if(reward_balace > redeem_reward){

                                    offerPercentage = customerQRcodeDetails.data.reward_settings.redeem_reward_percentage;
                                    rewardYesNo = "Yes";

                                }else{
                                    offerPercentage = "0";
                                    rewardYesNo = "NO";
                                }

                            }else{

                                offerPercentage = "0";
                                rewardYesNo = "NO";
                            }

                        }else{
                            offerPercentage = "0";
                            rewardYesNo = "NO";
                        }

                        ArrayList<String>additionalFee = new ArrayList<>();
                        if(customerQRcodeDetails.data.merchantAdditionalFee!=null && customerQRcodeDetails.data.merchantAdditionalFee.size() >0){
                            for(CustomerQRcodeDetails.MerchantAdditionalFee merchantAdditionalFee : customerQRcodeDetails.data.merchantAdditionalFee){
                                additionalFee.add(merchantAdditionalFee.fee_name+"<@>"+changeCustomerCurrency(merchantAdditionalFee.fees,customerQRcodeDetails.data.sender_currency_difference));
                            }
                        }

                        Intent intent = new Intent(QrcodeScreen.this, CMScanAndPayScreen.class);
                        intent.putExtra("user_id",customerQRcodeDetails.data.user_id);
                        intent.putExtra("receiverCurrencyCode",customerQRcodeDetails.data.currency_code);
                        intent.putExtra("bussinessName",customerQRcodeDetails.data.business_name);
                        intent.putExtra("merchant_profile",customerQRcodeDetails.data.profile_image);
                        intent.putExtra("offerPercentage",offerPercentage);
                        intent.putExtra("rewardYesNo",rewardYesNo);
                        intent.putExtra("additionalFee",additionalFee);
                        intent.putExtra("sender_currency_difference",customerQRcodeDetails.data.sender_currency_difference);

                        //Fee
                        intent.putExtra("fee_amount",customerQRcodeDetails.data.poppay_fee.fee_amount);
                        intent.putExtra("fee_option",customerQRcodeDetails.data.poppay_fee.deduction_type);

                        startActivity(intent);

                    }

                }catch (Exception e){e.printStackTrace();}

                break;

            case REQ_QR_DETAILS:

                try{
                    QRcodeDetails qRcodeDetails = (QRcodeDetails)result;
                    Intent intent = new Intent(QrcodeScreen.this, GenerateQRPayScreen.class);
                    intent.putExtra("merchant_id",qRcodeDetails.data.id);
                    intent.putExtra("merchant_name",qRcodeDetails.data.merchant_name);
                    intent.putExtra("merchant_profile",qRcodeDetails.data.merchant_profile);
                    intent.putExtra("description",qRcodeDetails.data.description);
                    intent.putExtra("send_currency",qRcodeDetails.data.send_currency);
                    intent.putExtra("send_amount",qRcodeDetails.data.send_amount);
                    intent.putExtra("amount",qRcodeDetails.data.amount);
                    intent.putExtra("receive_currency",qRcodeDetails.data.receive_currency);
                    intent.putExtra("receive_amount",qRcodeDetails.data.receive_amount);
                    intent.putExtra("subUser",qRcodeDetails.data.subUser);
                    intent.putExtra("offer_amount",qRcodeDetails.data.offer_amount);
                    intent.putExtra("offer_percentage",qRcodeDetails.data.offer_percentage);
                    intent.putExtra("offer_name",qRcodeDetails.data.offer_name);
                    intent.putExtra("oyopay_fee",qRcodeDetails.data.poppay_fee);
                    intent.putExtra("redeemAmountPercentage",qRcodeDetails.data.reward_offer_percentage);
                    intent.putExtra("redeemAmount",qRcodeDetails.data.reward_offer_amount);
                    intent.putExtra("earnPoints",qRcodeDetails.data.future_reward);
                    intent.putExtra("senderRewardBalance",qRcodeDetails.data.sender_reward_balance);
                    intent.putExtra("voucher_title",qRcodeDetails.data.voucher_title);
                    intent.putExtra("voucherPercentage",qRcodeDetails.data.voucher_percentage);
                    intent.putExtra("voucherAmount",qRcodeDetails.data.voucher_amount);
                    intent.putExtra("voucher_description",qRcodeDetails.data.voucher_description);
                    intent.putExtra("voucher_code",qRcodeDetails.data.voucher_id);
                    intent.putExtra("sender_currency_difference",qRcodeDetails.data.sender_currency_difference);

                    ArrayList<String>additionalFee = new ArrayList<>();
                    if(qRcodeDetails.data.merchantAdditionalFee!=null){
                        for(QRcodeDetails.MerchantAdditionalFee merchantAdditionalFee : qRcodeDetails.data.merchantAdditionalFee){
                            additionalFee.add(merchantAdditionalFee.fee_name+"<@>"+changeCustomerCurrency(merchantAdditionalFee.fees,qRcodeDetails.data.sender_currency_difference));
                        }
                    }

                    intent.putExtra("additionalFee",additionalFee);

                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    toast("Invalid ticket to verify");
                }
                break;

            case REQ_GET_RECEIVER_DETAILS:

                try {
                    GetReceiverDetails getReceiverDetails = (GetReceiverDetails) result;
                    if (getReceiverDetails.data.user == null) {
                        toast("Selected customer didn't have RPay account");
                    } else {
                        if (getReceiverDetails.data.user.country.phone_code.equals(SELECTED_SPINNER_NUMBER)) {
                            if (ccpPicker.getFullNumber().equalsIgnoreCase(loginSession.getphoneNumber())) {
                                toast("You cannot send/request money from yourself");
                            } else {

                                Intent intent = new Intent(QrcodeScreen.this, CCScanAndPayScreen.class);
                                intent.putExtra("name", getReceiverDetails.data.user.name);
                                intent.putExtra("number", ccpPicker.getFullNumber());
                                intent.putExtra("profile_image", getReceiverDetails.data.user.profile_image);
                                intent.putExtra("type", "Pay to");

                                intent.putExtra("fee_amount", "0");
                                intent.putExtra("fee_option", "0");
                                intent.putExtra("extra_fees", "0");

                                startActivity(intent);
                            }
                        } else {
                            toast("Please check country code");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("Selected customer didn't have RPay account");
                }

                break;
        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_GET_RECEIVER_DETAILS:

                toast("This customer didn't have RPay account");

                break;

                default:

                    try {

                        AlertDialog.Builder builder = new AlertDialog.Builder(QrcodeScreen.this);
                        builder.setTitle("Alert!!!");
                        builder.setMessage(error);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                finish();
                            }
                        });

                        builder.setCancelable(false);
                        builder.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == 1){

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

    private  boolean checkAndRequestPermissions() {

        //READ_CONTACTS
        int READCONTACTS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READCONTACTS != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS); }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
