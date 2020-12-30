package com.blucore.chalochale.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.blucore.chalochale.Driver.DriverMainActivity;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import in.aabhasjindal.otptextview.OtpTextView;

public class LoginActivity extends AppCompatActivity  {
    EditText editNumber;
    TextView txtPassword,txtCab;
    Button btnLogin,btnSignup;
    Dialog dialog;
    private TextView buttonConfirm;
    private ImageView ivClose;
    String newToken;
    //String user_status;

    private String phone;
    AwesomeValidation awesomeValidation;

    public static final String mobile_otp = "https://admin.chalochalecab.com/Webservices/login.php";
    public static final String confirm_otp = "https://admin.chalochalecab.com/Webservices/confirm.php";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_OTP = "otp";
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    Preferences preferences;


    //JSON Tag from response from server
    public static final String TAG_RESPONSE= "ErrorMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editNumber = (EditText) findViewById(R.id.editNumber);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        btnLogin = findViewById(R.id.btnLogin);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        preferences = new Preferences(this);

       /* FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                 newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

            }
        });
*/
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {

                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e("newToken", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        newToken = task.getResult();

                        // Log and toast
                        String msg = newToken;
                        Log.e("newToken", msg);
                       // Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        txtCab=findViewById(R.id.cabText);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/NunitoExtraBold.ttf");
        txtCab.setTypeface(face);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.editNumber, "[5-9]{1}[0-9]{9}$", R.string.invalid_PhoneNumber);

        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    if (Utils.isNetworkConnectedMainThred(LoginActivity.this)) {
                        Register();
                    } else {
                        Toasty.error(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d("error", "onResponse: ");

                }
            }

        });


    }

    private void Register() {

        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);

        //Getting user data
        phone  = editNumber.getText().toString().trim();

        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST,mobile_otp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("success111",response);
                editNumber.setText("");
                loading.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("success").equalsIgnoreCase("1"))
                    {
                        confirmOtp();
                        String number=jsonObject.getString("contact_no");
                        String user_id=jsonObject.getString("user_id");
                        String user_status=jsonObject.getString("user_status");
                        String role=jsonObject.getString("roll");
                       // String driver_name=jsonObject.getString("driverName");

                        preferences.set("contact_no",number);
                        preferences.set("user_id",user_id);
                        //preferences.set("driverName",driver_name);
                        preferences.set("user_status",user_status);
                        preferences.set("roll",role);
                        preferences.commit();

                    }else{

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toasty.error(LoginActivity.this, "Some went wrong ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("contact_no", editNumber.getText().toString());
                parameters.put("token_id",newToken);
                return parameters;
            }
        };
        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
        rQueue.add(stringRequest);

    }

    private void confirmOtp()  {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.otp_dialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final ImageView ivClose=dialog.findViewById(R.id.ivClose);
        final OtpTextView otpTextView=dialog.findViewById(R.id.otp_view);

        TextView verify=dialog.findViewById(R.id.validate_button);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.isNetworkConnectedMainThred(LoginActivity.this)) {
                    ProgressForSignup();
                    dialog.show();
                    ConfirmLoginOtp(otpTextView.getOTP());

                } else {
                    Toasty.error(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void ProgressForSignup() {
        dialog = new Dialog(LoginActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
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

    private void ConfirmLoginOtp(final String otp) {
       // final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "Authenticating", "Please wait while we check the entered code", false,false);
        //startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
       // final String otp = editTextConfirmOtp.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, confirm_otp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();

                Log.e("forgot password",response);

                if(response.equalsIgnoreCase("OTP Verified successfully")){
                    if (preferences.get("roll").equalsIgnoreCase("driver"))
                    {
                        Intent i = new Intent(LoginActivity.this, DriverMainActivity.class);
                        startActivity(i);
                    }else{
                        if (preferences.get("user_status").equalsIgnoreCase("Existing User")){
                            Intent i=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(i);

                        }else {
                            Intent in = new Intent(LoginActivity.this, SignUpActivity.class);
                            startActivity(in);
                        }

                    }
                  /*  if (preferences.get("user_status").equalsIgnoreCase("Existing User")){
                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);

                    }else {
                        Intent in = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(in);
                    }
*/
                }else{
                    Toast.makeText(LoginActivity.this,"Wrong OTP Please Try Again",Toast.LENGTH_LONG).show();
                    //Asking user to enter otp again
                     confirmOtp();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Log.e("error_response", "" + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("contact_no",preferences.get("contact_no"));
                param.put("otp",otp);
                Log.e("confirm ", "otp" + param);
                return param;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);
    }

}