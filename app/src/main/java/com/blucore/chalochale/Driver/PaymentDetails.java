package com.blucore.chalochale.Driver;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blucore.chalochale.Activity.GPSTracker;
import com.blucore.chalochale.FirebaseClasses.MyCabFirebaseService;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.DirectionFinder;
import com.blucore.chalochale.extra.Preferences;

import java.io.UnsupportedEncodingException;

public class PaymentDetails extends AppCompatActivity {

    public static int backPressed = 0;
    ImageView back;
    Preferences preferences;

    TextView collect_cash;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_details);

        preferences=new Preferences(this);


        back=findViewById(R.id.ic_back);
        collect_cash=findViewById(R.id.collect_cash);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaymentDetails.this, DriverMainActivity.class);
                startActivity(i);
            }
        });
        collect_cash.setText("\u20B9"+MyCabFirebaseService.riding_price);

    }
    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(PaymentDetails.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new CountDownTimer(5000, 1000) { // adjust the milli seconds here
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    backPressed = 0;
                }
            }.start();
        }
        if (backPressed == 2) {
            backPressed = 0;
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
