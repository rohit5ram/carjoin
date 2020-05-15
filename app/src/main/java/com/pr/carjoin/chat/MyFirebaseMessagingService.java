/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr.carjoin.chat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.pr.carjoin.Constants;
import com.pr.carjoin.NotificationReceiver;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.activities.MainActivity;
import com.pr.carjoin.activities.YourTripsActivity;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";
    private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";
    private static final String CHANNEL_ID = "CarJoinSmallChannel";
    private static int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            Log.i(TAG, "NOT NULL CASE");
            showNotification(remoteMessage.getData(), remoteMessage.getNotification().getBody());
        } else {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            try {
                String title = jsonObject.getString("title");
                String subTitle = null;
                if(jsonObject.has("subTitle")){
                    subTitle = jsonObject.getString("subTitle");
                }
                sendNotification(title, subTitle);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // If you need to handle the generation of a token, initially or after a refresh this is
        // where you should do that.
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            FirebaseDatabase.getInstance().getReference(Util.USERS)
                    .child(firebaseUser.getUid()).child("fcmRegistrationToken").setValue(s);
        }
        Log.d(TAG, "FCM Token: " + s);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title != null && !title.equals("") ? title : "CarJoin")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationChannel mChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "CarJoin", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(NOTIFICATION_ID++, notificationBuilder.build());
    }

    private void showNotification(Map<String, String> payLoad, String messageBody) {
        String operationCode = payLoad.get(Util.OPERATION_CODE);
        Intent intent = new Intent(this, YourTripsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Util.OPERATION_CODE, payLoad.get(Util.OPERATION_CODE));
        intent.putExtra(Util.REF_PATH, payLoad.get(Util.REF_PATH));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("CarJoin")
                .setAutoCancel(true)
                .setContentText(messageBody)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        switch (operationCode) {
            case "5000":
                Intent acceptedIntent = new Intent(this, NotificationReceiver.class);
                Intent declinedIntent = new Intent(this, NotificationReceiver.class);
                acceptedIntent.setAction(Constants.ACCEPTED);
                acceptedIntent.putExtra("actionCode", "5000");
                acceptedIntent.putExtra(Util.REF_PATH, payLoad.get(Util.REF_PATH));
                acceptedIntent.putExtra("valueToUpdate", Constants.ACCEPTED);

                PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0,
                        acceptedIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                declinedIntent.setAction(Constants.REJECTED);
                declinedIntent.putExtra("actionCode", "5000");
                declinedIntent.putExtra(Util.REF_PATH, payLoad.get(Util.REF_PATH));
                declinedIntent.putExtra("valueToUpdate", Constants.REJECTED);

                PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, 0,
                        declinedIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                notificationBuilder.addAction(R.drawable.decline, "Decline", declinePendingIntent);
                notificationBuilder.addAction(R.drawable.accept, "Accept", acceptPendingIntent);
                notificationManager.notify(5000, notificationBuilder.build());
                break;
        }

    }
}
