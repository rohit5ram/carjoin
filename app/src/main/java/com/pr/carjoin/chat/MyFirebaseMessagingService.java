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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pr.carjoin.Constants;
import com.pr.carjoin.NotificationReceiver;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.activities.MainActivity;
import com.pr.carjoin.activities.YourTripsActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        if (remoteMessage.getData() != null && remoteMessage.getNotification() != null &&
                remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData(), remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getBody());
        } else {
            sendNotification("");
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("CarJoin")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
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
