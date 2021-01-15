package com.blucore.chalochale.Driver;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.chalochale.Activity.LoginActivity;
import com.blucore.chalochale.Activity.Utils;
import com.blucore.chalochale.FirebaseClasses.MyCabFirebaseService;
import com.blucore.chalochale.Model.CabBookingModel;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AcceptUserRideActivity extends AppCompatActivity {

    public static final String driver_status = "https://admin.chalochalecab.com/Webservices/confirm_user.php";
    private static final int RED = 0xE63A04;
    private static final int BLUE = 0xE4AC04;
    private static final int WHITE = 0xFFFFFF;


    TextView otp;
    Preferences preferences;
    public static int backPressed = 0;
    String status;

    private List<CabBookingModel> cabListModel;

    Dialog dialog;

    TextView from_address;
    TextView to_address;
    TextView cust_mobile;
    TextView tv_request;
    Button accept_ride;
    Button reject_ride;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_notification_popup);

        otp=findViewById(R.id.otp_verify);
        from_address=findViewById(R.id.from_address);
        to_address=findViewById(R.id.to_address);
        accept_ride=findViewById(R.id.accept_ride);
        reject_ride=findViewById(R.id.cancel_ride);
        cust_mobile=findViewById(R.id.user_number);
        tv_request=findViewById(R.id.tv_request);

        ValueAnimator colorAnim = ObjectAnimator.ofInt(tv_request, "textColor", RED, BLUE,WHITE);
        colorAnim.setDuration(1000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();

       /* Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tv_request.startAnimation(anim);*/

        preferences=new Preferences(this);
        cabListModel = new ArrayList<>();

        from_address.setText(MyCabFirebaseService.from_ads);
        to_address.setText(MyCabFirebaseService.to_ads);
        cust_mobile.setText(MyCabFirebaseService.user_contact_no);
        accept_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isNetworkConnectedMainThred(AcceptUserRideActivity.this)) {
                    ProductProgressBar();
                    dialog.show();
                    AcceptRide(MyCabFirebaseService.from_ads,MyCabFirebaseService.to_ads);
                } else {
                    Toasty.error(AcceptUserRideActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
                //status="1";

            }
        });
        reject_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isNetworkConnectedMainThred(AcceptUserRideActivity.this)) {
                    ProductProgressBar();
                    dialog.show();
                    RejectRide();
                } else {
                    Toasty.error(AcceptUserRideActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
                //status="0";

            }
        });
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

    private void RejectRide() {
        StringRequest request = new StringRequest(Request.Method.POST, driver_status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("reject_ride", response);

                Intent intent=new Intent(AcceptUserRideActivity.this,DriverMainActivity.class);
                startActivity(intent);
               /* try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equalsIgnoreCase("1"))
                    {
                        //replaceFragmentWithAnimation(new Dri());
                        Intent intent=new Intent(AcceptUserRideActivity.this,DriverMainActivity.class);
                        startActivity(intent);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

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
                parameters.put("acceptRejectStatus","0");
                Log.e("PARAM",""+parameters);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void AcceptRide(final String from_ads, final String to_ads) {
        StringRequest rqst = new StringRequest(Request.Method.POST, driver_status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("accept_ride", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                        String otp = jsonObject.getString("otp");

                        preferences.set("otp",otp);
                        preferences.commit();

                        Intent intent =new Intent(AcceptUserRideActivity.this, DriverRouteActivity.class);
                        intent.putExtra("from_ads", from_ads);
                        intent.putExtra("to_ads", to_ads);
                        startActivity(intent);

                        //replaceFragmentWithAnimation(new RouteOfRidingFragment());
/*
                        JSONArray jsonArray = jsonObject.getJSONArray("driver_details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject Object = jsonArray.getJSONObject(i);
                            //model class
                            CabBookingModel cabList = new CabBookingModel();
                            String cab_id = Object.getString("id");
                            String vehicle_no = Object.getString("vehicle_no");
                            String cab_name = Object.getString("vehicle_type");
                            String price = Object.getString("total_price");
                            String vehicle_company=Object.getString("vehicle_compony");
                            String driver_images="http://admin.chalochalecab.com/" + Object.getString("driver_image");
                            String driver_name2 = Object.getString("driver_name");
                            String driver_number2 = Object.getString("driverMobileNo");
                            String otp = Object.getString("otp");

                            preferences.set("otp",otp);
                            preferences.set("price",price);
                            preferences.commit();

                            cabList.setCab_id(cab_id);
                            cabList.setCab_name(cab_name);
                            cabList.setCab_price(price);
                            cabList.setDriver_image(driver_images);
                            cabList.setDriver_name(driver_name2);
                            cabList.setCab_company(vehicle_company);
                            cabList.setVehicle_type_id(vehicle_no);
                            cabList.setDriver_number(driver_number2);
                            cabList.setOtp(otp);

                            cabListModel.add(cabList);
                        }*/

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
                parameters.put("acceptRejectStatus", "1");
                Log.e("par",""+parameters);

                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(rqst);

    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        //FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        //transaction.addToBackStack(null);
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(AcceptUserRideActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
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
