package com.blucore.cabchalochale.Driver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.cabchalochale.Activity.MainActivity;
import com.blucore.cabchalochale.Activity.Utils;
import com.blucore.cabchalochale.Fragments.RidingHistoryDetailsFragment;
import com.blucore.cabchalochale.Model.DriverRideModel;
import com.blucore.cabchalochale.Model.YourRideModel;
import com.blucore.cabchalochale.R;
import com.blucore.cabchalochale.extra.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DriverRidingFragment extends Fragment {
    View v;
    private List<DriverRideModel> rideModels;
    public static final String riding_history = "https://admin.chalochalecab.com/Webservices/driverRideHistory.php";

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    YourRideAdapter mAdapter;
    Preferences preferences;
    LinearLayout empty;
    LinearLayout llcartItem;
    Dialog dialog;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_your_ride, container, false);
        rideModels=new ArrayList<>();
       // ArrayList source = new ArrayList<>(Arrays.asList("Auto"));
        preferences=new Preferences(getActivity());


        //setvalue
        DriverMainActivity.tvHeaderText.setText("Your Rides");
        DriverMainActivity.iv_menu.setImageResource(R.drawable.ic_back);
        DriverMainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), DriverMainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Intent i = new Intent(getActivity(), DriverMainActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        //replaceFragmentWithAnimation(new DashboardFragment());

                        return true;
                    }
                }
                return false;
            }
        });
        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            ProductProgressBar();
            dialog.show();
            RidingHistory();

        } else {

            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }




        recyclerView=v.findViewById(R.id.recyclerView);
        empty=v.findViewById(R.id.empty);
        llcartItem=v.findViewById(R.id.llcartItem);

        return v;
    }

    private void ProductProgressBar() {
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

    private void RidingHistory() {
        StringRequest request = new StringRequest(Request.Method.POST, riding_history, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("DriverRiding_history", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("driverRides");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Object = jsonArray.getJSONObject(i);
                            //model class
                            DriverRideModel rideList = new DriverRideModel();
                            String source = Object.getString("from_address");
                            String destination = Object.getString("to_address");
                            String journey_date = Object.getString("date");
                            String price = Object.getString("price");
                            String user_number = Object.getString("customer_mobile_no");
                            String payment_mode = Object.getString("payment_method");


                            rideList.setSource(source);
                            rideList.setDestination(destination);
                            rideList.setJouney_date(journey_date);
                            rideList.setCab_price("\u20b9"+price);
                            rideList.setUser_number(user_number);
                            rideList.setPayment_mode(payment_mode);
                            rideModels.add(rideList);
                        }
                        setAdapter();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (rideModels.isEmpty()){
                    llcartItem.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    llcartItem.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
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
                parameters.put("mobile_no",preferences.get("contact_no"));
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new YourRideAdapter(rideModels, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    //=============================Adapter====================================================//


    public class YourRideAdapter extends  RecyclerView.Adapter<YourRideAdapter.ViewHolder> {
        //ArrayList source;
        private List<DriverRideModel> mModel;
        Context context;

      //  ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public YourRideAdapter(List<DriverRideModel> mModel, Context context) {
            this.mModel = mModel;
            this.context = context;
        }


        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_riding_history, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.Date.setText(mModel.get(position).getJouney_date());
            holder.total_price.setText(mModel.get(position).getCab_price());
            holder.payment_mode.setText(mModel.get(position).getPayment_mode());
            holder.user_no.setText(mModel.get(position).getUser_number());
            holder.source.setText(mModel.get(position).getSource());
            holder.destination.setText(mModel.get(position).getDestination());


        }


        @Override
        public int getItemCount() {
            return mModel.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView Date;
            TextView user_no;
            TextView total_price;
            TextView source;
            TextView destination;
            TextView payment_mode;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                Date = (TextView) itemView.findViewById(R.id.date);
                user_no = (TextView) itemView.findViewById(R.id.user_no);
                total_price = (TextView) itemView.findViewById(R.id.total_price);
                source = (TextView) itemView.findViewById(R.id.source_address);
                destination = (TextView) itemView.findViewById(R.id.dest_address);
                payment_mode = (TextView) itemView.findViewById(R.id.payment_mode);

            }
        }


    }

}
