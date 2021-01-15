package com.blucore.chalochale.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.blucore.chalochale.Activity.MainActivity;
import com.blucore.chalochale.Activity.Utils;
import com.blucore.chalochale.Model.YourRideModel;
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

public class YourRidingFragment extends Fragment {
    View v;
    private List<YourRideModel> rideModels;
    public static final String riding_history = "https://admin.chalochalecab.com/Webservices/getUsersData.php";

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
        MainActivity.tvHeaderText.setText("Your Rides");
        MainActivity.iv_menu.setImageResource(R.drawable.ic_back);
        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
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

        //set adapter
       /* layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new YourRideAdapter());*/

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
                Log.e("riding_history", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("userData");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Object = jsonArray.getJSONObject(i);
                            //model class
                            YourRideModel rideList = new YourRideModel();
                            //String id = Object.getString("id");
                            String source = Object.getString("from_add");
                            String destination = Object.getString("to_add");
                            //String driver_id=Object.getString("driver_id");
                            //Log.e("id_driv",""+driver_id);
                            String journey_date = Object.getString("dateTime");
                            String price = Object.getString("total_price");
                            //String vehicle_company=Object.getString("vehicle_compony");
                            String cab_image = "http://admin.chalochalecab.com/" + Object.getString("vehicle_image");
                            String driver_image ="http://admin.chalochalecab.com/" + Object.getString("driver_image");

                            String cab_number = Object.getString("vehicle_no");
                            String driver_name = Object.getString("driver_name");
                            String driver_number = Object.getString("driverMobileNo");


                            //cabList.setCab_id(id);
                            rideList.setSource(source);
                            rideList.setDestination(destination);
                            rideList.setCab_image(cab_image);
                            rideList.setJouney_date(journey_date);
                            rideList.setCab_price("\u20b9"+price);
                            rideList.setCab_number(cab_number);
                            rideList.setDriver_image(driver_image);
                            rideList.setDriver_name(driver_name);
                            rideList.setDriver_number(driver_number);
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
                parameters.put("user_id",preferences.get("user_id"));
                //parameters.put("id", String.valueOf(166));
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

    public void replaceFragmentWithAnimation(Fragment fragment, String driver_name, String driver_number, String journey_date, String cab_image,String driver_image,String price) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        Bundle bundle = new Bundle();
        bundle.putString("driver_name",driver_name);
        bundle.putString("driverMobileNo",driver_number);
        bundle.putString("dateTime",journey_date);
        bundle.putString("vehicle_image",cab_image);
        bundle.putString("driver_image",driver_image);
        bundle.putString("total_price",price);

        //bundle.putString("order_date_month",);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    //=============================Adapter====================================================//


    public class YourRideAdapter extends  RecyclerView.Adapter<YourRideAdapter.ViewHolder> {
        //ArrayList source;
        private List<YourRideModel> mModel;
        Context context;

      //  ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public YourRideAdapter(List<YourRideModel> mModel, Context context) {
            this.mModel = mModel;
            this.context = context;
        }

        /* public YourRideAdapter(ArrayList<HashMap<String, String>> favList) {
                    data = favList;
                }

                public YourRideAdapter() {

                }
        */
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_userriding, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.Date.setText(mModel.get(position).getJouney_date());
            holder.Cab_no.setText(mModel.get(position).getCab_number());
            holder.total_price.setText(mModel.get(position).getCab_price());
            holder.source.setText(mModel.get(position).getSource());
            holder.destination.setText(mModel.get(position).getDestination());

            holder.rlmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String driver_name=mModel.get(position).getDriver_name();
                    String driver_number=mModel.get(position).getDriver_number();
                    String journey_date=mModel.get(position).getJouney_date();
                    String cab_image=mModel.get(position).getCab_image();
                    String driver_image=mModel.get(position).getDriver_image();
                    String price=mModel.get(position).getCab_price();


                    replaceFragmentWithAnimation(new RidingHistoryDetailsFragment(),driver_name,driver_number,journey_date,cab_image,driver_image,price);
                }
            });
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
            TextView Cab_no;
            TextView total_price;
            TextView source;
            TextView destination;
            LinearLayout rlmain;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                Date = (TextView) itemView.findViewById(R.id.date);
                Cab_no = (TextView) itemView.findViewById(R.id.cab_no);
                total_price = (TextView) itemView.findViewById(R.id.total_price);
                source = (TextView) itemView.findViewById(R.id.source_address);
                destination = (TextView) itemView.findViewById(R.id.dest_address);
                rlmain = itemView.findViewById(R.id.rlmain);

            }
        }


    }

}
