package com.omlah.customer.account;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.policy.TimeWindow;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CircleTransform;
import com.omlah.customer.common.FileUtil;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.RoundedImageView;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 28-10-2017.
 */

public class ProfileScreen extends BaseActivity  implements ServerListener{

    //Create class objects
    ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;
    Dialog imageChooseDialog;

    //Create xml file
    @BindView(R.id.profileImageView)RoundedImageView profileImageView;
    @BindView(R.id.firstNameEditText)EditText firstNameEditText;
    @BindView(R.id.numberEditText)EditText numberEditText;
    @BindView(R.id.emailEditText)EditText emailEditText;
    @BindView(R.id.countryEditText)EditText countryEditText;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.changeImageButton)ImageView changeImageButton;
    @BindView(R.id.updateButton)Button updateButton;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private String PhotoEncodeString="";
    File actualImage,compressedImage;

    String cloudURL="";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession   = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        PhotoEncodeString="";

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }

        firstNameEditText.setText(loginSession.getname());


        if(loginSession.getCustomerCountryCode().equalsIgnoreCase("+251")){
            numberEditText.setText(normalizePhoneNumber(loginSession.getphoneNumber(),loginSession.getCustomerCountryCode().replace("+","")));
        }else{
            numberEditText.setText(normalizePhoneNumber(loginSession.getphoneNumber(),loginSession.getCustomerCountryCode()).replace("+",""));
        }

        emailEditText.setText(loginSession.getemail());

        if(!loginSession.getProfileImage().isEmpty()){

            if(loginSession.getProfileImage().contains("cloudinary")){
                String getImage[] = loginSession.getProfileImage().split("upload");
                String imageFormat = getImage[0]+"upload/w_250,h_250,c_thumb,g_face,r_max"+getImage[1];

                Log.e("imageFormat",""+imageFormat);

                Picasso.with(this)
                        .load(imageFormat)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .transform(new CircleTransform())
                        .into(profileImageView);
            }else{

                Picasso.with(this)
                        .load(loginSession.getProfileImage())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .transform(new CircleTransform())
                        .into(profileImageView);
            }


        }

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileImageView.performClick();
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageChooseDialog == null) {

                    imageChooseDialog = new Dialog(ProfileScreen.this);
                    imageChooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    imageChooseDialog.setContentView(R.layout.dialog_for_chooseimage);
                    imageChooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    imageChooseDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    TextView buttonTakePicture = (TextView)imageChooseDialog.findViewById(R.id.buttonTakePicture);
                    TextView buttonChooseImage = (TextView)imageChooseDialog.findViewById(R.id.buttonChooseImage);

                    buttonTakePicture.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            imageChooseDialog.cancel();
                            boolean result=checkCameraPermission(ProfileScreen.this);
                            userChoosenTask ="Take Photo";
                            if(result)
                            toast("Loading...");
                            cameraIntent();

                        }
                    });

                    buttonChooseImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            imageChooseDialog.cancel();
                            boolean result=checkPermission(ProfileScreen.this);
                            userChoosenTask ="Choose from Library";
                            if(result)
                            toast("Loading...");
                            galleryIntent();

                        }
                    });

                }
                imageChooseDialog.setCancelable(true);
                imageChooseDialog.show();

            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });



        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!cloudURL.isEmpty()){
                    String entername = firstNameEditText.getText().toString().trim();
                    if(entername.isEmpty()){
                        firstNameEditText.setError("Please enter a name");
                    }else if(isConnectingToInternet()){
                        uploadProfileImage(entername,cloudURL);
                    }else{
                        noInternetAlertDialog();
                    }
                }else{
                    String entername = firstNameEditText.getText().toString().trim();
                    if(entername.equalsIgnoreCase(loginSession.getname())){
                        toast("Please make any changes");
                    }else{
                        if(entername.isEmpty()){
                            firstNameEditText.setError("Please enter a name");
                        }else if(isConnectingToInternet()){
                            uploadProfileImage(entername,loginSession.getProfileImage());
                        }else{
                            noInternetAlertDialog();
                        }
                    }

                }


            }
        });


    }

    @Override
    public void onBackPressed() {

        if (!cloudURL.isEmpty()) {
            if (isConnectingToInternet()) {
                uploadProfileImage(loginSession.getname(), cloudURL);
            } else {
                noInternetAlertDialog();
            }
        } else {
            finish();
        }
    }

    private void galleryIntent() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            toast("Permission canceled, now your application cannot access CAMERA.");
        }
    }

    private void cameraIntent()
    {
        try{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }catch (Exception e){e.printStackTrace();
            toast("Permission canceled, now your application cannot access CAMERA.");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }

                break;

            case MY_PERMISSIONS_REQUEST_READ_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap photo = (Bitmap) data.getExtras().get("data");
        profileImageView.setImageBitmap(photo);
        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), photo);
        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        String IDD = String.valueOf(System.currentTimeMillis());
        String requestId = MediaManager.get().upload(finalFile.getAbsolutePath())
                .unsigned("ntaxmkaj")
                .option("public_id",IDD)
                .constrain(TimeWindow.immediate())
                .dispatch();
        cloudURL = MediaManager.get().url().generate(IDD+".jpg");

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profileImageView.setImageBitmap(bm);

        try {
            actualImage = FileUtil.from(this, data.getData());
            String IDD = String.valueOf(System.currentTimeMillis());
            String requestId = MediaManager.get().upload(Uri.fromFile(actualImage))
                    .unsigned("ntaxmkaj")
                    .option("public_id",IDD)
                    .constrain(TimeWindow.immediate())
                    .dispatch();
            cloudURL = MediaManager.get().url().generate(IDD+".jpg");


            //compressImage();
        } catch (IOException e) {
            toast("Failed to read picture data!");
            e.printStackTrace();
        }
    }


    private void uploadProfileImage(String name,String profileURL) {

        if(isConnectingToInternet()){
            Map<String, String> params = new HashMap<>();
            params.put("name",name);
            params.put("business_name",name);
            params.put("email",loginSession.getemail());
            params.put("phone_number",loginSession.getphoneNumber());
            params.put("profile_image",profileURL);
            showProgressDialog();
            serverRequestwithHeader.createRequest(ProfileScreen.this,params, RequestID.REQ_PROFILE_IMAGE_IPLOAD,"POST","");
        }else{
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        toast("Profile uploaded successfully!");
        if (!cloudURL.isEmpty()) {
            loginSession.setProfileImage(cloudURL);
        }
        loginSession.setName(firstNameEditText.getText().toString().trim());

        if(!loginSession.getProfileImage().isEmpty()){
            Log.e("IMAGE",loginSession.getProfileImage());
            Picasso.with(this)
                    .load(loginSession.getProfileImage())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .into(profileImageView);
        }

        finish();
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
