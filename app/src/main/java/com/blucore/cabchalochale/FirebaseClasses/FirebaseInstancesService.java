package com.blucore.cabchalochale.FirebaseClasses;

import android.util.Log;

import com.blucore.cabchalochale.extra.Preferences;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseInstancesService  extends FirebaseMessagingService {
    Preferences preferences;

    @Override
    public void onNewToken(String token) {
        //preferences = new Preferences(this);


     /*   Log.d("myfirebasetokenid", "Refreshed token: " + token);
        preferences.set("token",""+token);*/

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String tokan){

    }

}
