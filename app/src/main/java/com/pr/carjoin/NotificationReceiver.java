package com.pr.carjoin;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vishnu on 16/4/17.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String actionCode = intent.getStringExtra("actionCode");
        switch (actionCode) {
            case "5000":
                String refPath = intent.getStringExtra(Util.REF_PATH);
                String valueToUpdate = intent.getStringExtra("valueToUpdate");
                FirebaseDatabase.getInstance().getReference(refPath).setValue(valueToUpdate);
                notificationManager.cancel(Integer.parseInt(actionCode));
                break;
        }
    }
}
