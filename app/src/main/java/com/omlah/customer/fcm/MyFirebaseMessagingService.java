package com.omlah.customer.fcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.base.Utility;
import com.omlah.customer.otp.LuncherPassCodeCheck;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    //Get UserId
    Utility utility;
    Activity context;
    SharedPreferences userDetailsPreferences,notificationPreferences;
    SharedPreferences.Editor userDetailsEditor,notificationEditor;
    String userid="";
    boolean notificationEnabled;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        //Get UserID from Session
        utility = Utility.getInstance(context);
        userDetailsPreferences =getSharedPreferences("USERDETAILS", Context.MODE_PRIVATE);
        userDetailsEditor = userDetailsPreferences.edit();
        userid = userDetailsPreferences.getString("user_id", "");
        Log.e("userid",userid);

        //Get notification settings
        notificationPreferences =getSharedPreferences("Notification", Context.MODE_PRIVATE);
        notificationEditor = notificationPreferences.edit();
        notificationEnabled = notificationPreferences.getBoolean("onoff",true);
        Log.e("notification",""+notificationEnabled);

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound(notificationEnabled);
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {

            String type      = "";
            String transaction_id      = "";
            String title      = "";
            String message    = "";
            String imageUrl   = "";
            String timestamp  = "";

            if(json.has("type")){
                type       = json.getString("type");
            }

            if (json.has("transaction_id")) {
                transaction_id = json.getString("transaction_id");
            }

            title      = json.getString("title");
            message    = json.getString("message");
            imageUrl   = json.getString("image");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {



                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("update", true);
                pushNotification.putExtra("title", title);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound(notificationEnabled);

                prepareNotificationEvent(transaction_id,title,message,timestamp,type);


            }else if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("title", title);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("update", true);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound(notificationEnabled);

                prepareNotificationEvent(transaction_id,title,message,timestamp,type);

            }  else {
                // app is in background, show the notification in notification tray

                Intent resultIntent = null;

                if(userid.isEmpty()){
                    resultIntent = new Intent(getApplicationContext(), GetStartedScreen.class);
                }else{
                    resultIntent = new Intent(getApplicationContext(), LuncherPassCodeCheck.class);
                }

                resultIntent.putExtra("title", title);
                resultIntent.putExtra("update", true);
                resultIntent.putExtra("message", message);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound(notificationEnabled);

                prepareNotificationEvent(transaction_id,type,title,message,timestamp);

            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void prepareNotificationEvent(String transaction_id,String title, String message,String timestamp,String type) {

        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            if(userid.isEmpty()){
                showNotificationMessage(transaction_id,type,getApplicationContext(), title, message, timestamp, new Intent(getApplicationContext(),GetStartedScreen.class));
            }else{
                showNotificationMessage(transaction_id,type,getApplicationContext(), title, message, timestamp, new Intent(getApplicationContext(),LuncherPassCodeCheck.class));
            }

        }else{

            if(userid.isEmpty()){
                showNotificationMessage(transaction_id,type,getApplicationContext(), title, message, timestamp, new Intent(getApplicationContext(),GetStartedScreen.class));
            }else{
                showNotificationMessage(transaction_id,type,getApplicationContext(), title, message, timestamp, new Intent(getApplicationContext(),BaseScreen.class));
            }
        }

    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(String transaction_id,String type,Context context, String title, String message, String timeStamp, Intent intent) {


        Log.e("type",type);

        if(notificationEnabled){
            notificationUtils = new NotificationUtils(context);
            if(type.trim().equalsIgnoreCase("Money Received")){
                utility.SCREEN_NAME = "MyTransactionScreen";
                utility.TRANSACTION_NUMBER = transaction_id;
            }else if(type.trim().equalsIgnoreCase("Money Requested")){
                utility.SCREEN_NAME = "MoneyRequestListScreen";
            }else if(type.trim().equalsIgnoreCase("Feed Posted")){
                utility.SCREEN_NAME = "FeedScreen";
            }else{
                utility.SCREEN_NAME = "HOMESCREEN";
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
        }
    }

}
