package com.pr.carjoin.chat;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pr.carjoin.R;


public class MSGService extends GcmListenerService {

    SharedPreferences prefs;
    NotificationCompat.Builder notification;
    NotificationManager manager;
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);


    public MSGService() {
        super();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        prefs = getSharedPreferences("Chat", 0);

        if(!prefs.getString("CURRENT_ACTIVE","").equals(data.getString("fromu"))) {
            sendNotification(data.getString("msg"), data.getString("fromu"), data.getString("name"));
        }
        Log.i("TAG", "Received: " + data.getString("msg"));
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.i("MSGSERVICE"," Delete Message");
    }

    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
        Log.i(msgId, " Sent");
    }

    @Override
    public void onSendError(String msgId, String error) {
        super.onSendError(msgId, error);
        Log.e(msgId, error);
    }

    private void sendNotification(String msg,String mobno,String name) {

        Bundle args = new Bundle();
        args.putString("mobno", mobno);
        args.putString("name", name);
        args.putString("msg", msg);
        Intent chat = new Intent(this, ChatActivity.class);
        chat.putExtra("INFO", args);
        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(name);
        notification.setContentText(msg);
        notification.setTicker("New Message !");
        notification.setSmallIcon(R.mipmap.ic_launcher);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
                chat, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }
}