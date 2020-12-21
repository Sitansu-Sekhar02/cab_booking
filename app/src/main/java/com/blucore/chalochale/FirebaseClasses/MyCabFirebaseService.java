package com.blucore.chalochale.FirebaseClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.blucore.chalochale.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyCabFirebaseService extends FirebaseMessagingService {
    public MyCabFirebaseService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage.getNotification().getBody());

    }
    private  void sendNotification(String messageBody){
        Intent intent=new Intent(this,MyCabFirebaseService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuildeter=new NotificationCompat.Builder(this);
        notificationBuildeter.setSmallIcon(R.drawable.logocholochale);
        notificationBuildeter.setContentTitle("Chalo Chale Cab");
        notificationBuildeter.setContentText(messageBody);
        notificationBuildeter.setAutoCancel(true);
        notificationBuildeter.setSound(defaultSoundUri);
        notificationBuildeter.setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuildeter.build());

    }
}
