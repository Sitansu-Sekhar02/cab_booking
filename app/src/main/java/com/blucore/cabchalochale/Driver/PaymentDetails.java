package com.blucore.cabchalochale.Driver;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.cabchalochale.Activity.Utils;
import com.blucore.cabchalochale.FirebaseClasses.MyCabFirebaseService;
import com.blucore.cabchalochale.R;
import com.blucore.cabchalochale.extra.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PaymentDetails extends AppCompatActivity {
    public static final String payment_status = "https://admin.chalochalecab.com/Webservices/payment_status.php";

    public static int backPressed = 0;
    ImageView back;
    LinearLayout tv_Cash;
    LinearLayout tv_Online;
    Preferences preferences;
    Dialog dialog;

    TextView collect_cash;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_details);

        preferences=new Preferences(this);


        back=findViewById(R.id.ic_back);
        collect_cash=findViewById(R.id.collect_cash);
        tv_Cash=findViewById(R.id.tv_Cash);
        tv_Online=findViewById(R.id.tv_Online);

        tv_Cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnectedMainThred(PaymentDetails.this)) {
                    ProductProgressBar();
                    dialog.show();
                    PaymentStatus("1");
                } else {
                    Toasty.error(PaymentDetails.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        tv_Online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnectedMainThred(PaymentDetails.this)) {
                    ProductProgressBar();
                    dialog.show();
                    PaymentStatus("0");
                } else {
                    Toasty.error(PaymentDetails.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaymentDetails.this, DriverMainActivity.class);
                startActivity(i);
            }
        });
        collect_cash.setText("\u20B9"+MyCabFirebaseService.riding_price);

    }

    private void PaymentStatus(final String status) {
        StringRequest request = new StringRequest(Request.Method.POST, payment_status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("payment status", response);

                  try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equalsIgnoreCase("1"))
                    {
                        //replaceFragmentWithAnimation(new Dri());
                        Intent i = new Intent(PaymentDetails.this, DriverMainActivity.class);
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Log.e("error_response", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id",MyCabFirebaseService.cust_userId);
                parameters.put("driver_id", MyCabFirebaseService.driver_id);
                parameters.put("price", MyCabFirebaseService.riding_price);
                parameters.put("status", status);
                Log.e("status",""+parameters);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
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

    private void ProductProgressBar() {
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_for_load);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
    }

}
