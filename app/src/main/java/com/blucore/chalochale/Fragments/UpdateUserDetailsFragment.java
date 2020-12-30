package com.blucore.chalochale.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.chalochale.Activity.MainActivity;
import com.blucore.chalochale.Activity.Utils;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class UpdateUserDetailsFragment extends Fragment {

    public static final String update_userDetails="https://admin.chalochalecab.com/Webservices/update_user_info.php";
    EditText EdfirstName,EdlastName;
    Button btnSave;
    Preferences preferences;
    Dialog dialog;


    //view
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v=inflater.inflate(R.layout.fragment_update_user_details, container, false);

        //setvalue
        MainActivity.tvHeaderText.setText("Update Profile");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_back);

        preferences=new Preferences(getActivity());

        EdfirstName=v.findViewById(R.id.edfirstName);
        EdlastName=v.findViewById(R.id.edlastName);
        btnSave=v.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EdfirstName.getText().toString().trim().length() == 0) {
                    EdfirstName.setError("Required first name");
                    EdfirstName.requestFocus();

                } else if (EdlastName.getText().toString().trim().length() == 0) {
                    EdlastName.setError("Required last name ");
                    EdlastName.requestFocus();

                } else {
                    if (Utils.isNetworkConnectedMainThred(getActivity())) {
                        UpdateDetails();
                        ProgressForAddess();
                        dialog.show();
                    } else {
                        Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });


        //Onclick listener
        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragmentWithAnimation(new UserProfileFragment());
            }
        });
        return v;

    }

    private void ProgressForAddess() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
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

    private void UpdateDetails() {
        StringRequest request = new StringRequest(Request.Method.POST,update_userDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("details",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("success").equalsIgnoreCase("1"))
                    {
                        preferences.set("first_name",jsonObject.getString("first_name"));
                        preferences.set("last_name",jsonObject.getString("last_name"));
                        preferences.commit();
                        replaceFragmentWithAnimation(new UserProfileFragment());
                        Toasty.success(getActivity(),"Profile Update Successfully",Toast.LENGTH_SHORT).show();
                    }
                    //replaceFragmentWithAnimation(new CheckoutFragment());


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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id",preferences.get("user_id"));
                parameters.put("first_name", EdfirstName.getText().toString());
                parameters.put("last_name", EdlastName.getText().toString());
                return parameters;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }


    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
}