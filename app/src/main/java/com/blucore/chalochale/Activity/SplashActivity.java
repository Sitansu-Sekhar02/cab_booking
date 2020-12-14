package com.blucore.chalochale.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.blucore.chalochale.Driver.DriverMainActivity;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.Preferences;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN=5000;
    Preferences preferences;
    Animation topAnim,bottomAnim;
    ImageView image;
    TextView logo,slogon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        preferences=new Preferences(this);


        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image=findViewById(R.id.imageSplash);
        logo=findViewById(R.id.textlogo);
        slogon=findViewById(R.id.textSlogan);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogon.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(preferences.get("user_id").isEmpty())
                {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                   /* if(preferences.get("roll").equals("driver")){
                        Intent i = new Intent(SplashActivity.this, DriverMainActivity.class);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(SplashActivity.this, DriverMainActivity.class);
                        startActivity(i);
//                         Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                        startActivity(i);
                    }*/
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                finish();
            }
        },SPLASH_SCREEN);


    }
}