package com.blucore.chalochale.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blucore.chalochale.Driver.DriverMainActivity;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private static int SPLASH_SCREEN = 7000;
    Preferences preferences;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //onRequestPermissionsResult();

        //permissionGranted = false;
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        preferences = new Preferences(this);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.imageSplash);
        logo = findViewById(R.id.textlogo);
        slogon = findViewById(R.id.textSlogan);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogon.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (preferences.get("user_id").isEmpty()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    if (preferences.get("roll").equals("driver")) {
                        Intent i = new Intent(SplashActivity.this, DriverMainActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
//
                    }

                }
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 23) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Permission Needed To Run The App", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<String, Integer>();
            // Initial
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
            //Toast.makeText(SplashScreen.this, " Permissions are jddddd", Toast.LENGTH_SHORT).show();
            // Fill with results
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            // Check for ACCESS_FINE_LOCATION
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                // All Permissions Granted
                // Here start the activity
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(preferences.get("user_id").isEmpty())
                        {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            if(preferences.get("roll").equals("driver")){
                                Intent i = new Intent(SplashActivity.this, DriverMainActivity.class);
                                startActivity(i);
                            }else{
                                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(i);
//
                            }

                        }
                        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        finish();
                    }
                },SPLASH_SCREEN);

            } else {
                // Permission Denied
                Toast.makeText(SplashActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                        .show();

                finish();
            }

        }
    }
}*/