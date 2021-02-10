package com.blucore.chalochalecab.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.chalochalecab.R;
import com.blucore.chalochalecab.extra.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {

    public static final String user_info ="https://admin.chalochalecab.com/Webservices/info.php";

    EditText editTextfirstName;
    public static int backPressed = 0;


    EditText  editTextlastName;
    Button btnSignup;
    Preferences preferences;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        editTextfirstName = (EditText) findViewById(R.id.editTextfirstName);
        editTextlastName = (EditText) findViewById(R.id.editTextLastName);
        preferences = new Preferences(this);


        btnSignup = (Button) findViewById(R.id.buttonSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextfirstName.getText().toString().trim().length() == 0) {
                    editTextfirstName.setError("Required first name");
                    editTextfirstName.requestFocus();

                } else if (editTextlastName.getText().toString().trim().length() == 0) {
                    editTextlastName.setError("Required last name ");
                    editTextlastName.requestFocus();

                } else {
                    if (Utils.isNetworkConnectedMainThred(SignUpActivity.this)) {
                        ProgressForMain();
                        UserInformation();
                        dialog.show();
                    } else {
                        // Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                        Toasty.error(SignUpActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

    }
    private void ProgressForMain() {
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

    private void UserInformation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,user_info, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("info",response);

                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("success").equalsIgnoreCase("1"))
                    {
                        String full_name=jsonObject.getString("full_name");
                        String first_name=jsonObject.getString("first_name");
                        String last_name=jsonObject.getString("last_name");

                        preferences.set("full_name",full_name);
                        preferences.set("first_name",first_name);
                        preferences.set("last_name",last_name);

                        preferences.commit();

                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        //overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                Toasty.error(SignUpActivity.this, "Some went wrong ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id",preferences.get("user_id"));
                parameters.put("first_name", editTextfirstName.getText().toString());
                parameters.put("last_name", editTextlastName.getText().toString());
                return parameters;
            }
        };
        RequestQueue rQueue = Volley.newRequestQueue(SignUpActivity.this);
        rQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(SignUpActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
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