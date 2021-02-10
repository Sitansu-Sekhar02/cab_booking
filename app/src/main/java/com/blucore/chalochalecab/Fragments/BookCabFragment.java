package com.blucore.chalochalecab.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.chalochalecab.Activity.GPSTracker;
import com.blucore.chalochalecab.Activity.MainActivity;
import com.blucore.chalochalecab.Activity.Utils;
import com.blucore.chalochalecab.Model.CabBookingModel;
import com.blucore.chalochalecab.R;
import com.blucore.chalochalecab.extra.DirectionFinder;
import com.blucore.chalochalecab.extra.Preferences;
import com.blucore.chalochalecab.extra.Route;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.blucore.chalochalecab.Activity.MainActivity.tvHeaderText;


public class BookCabFragment extends Fragment implements OnMapReadyCallback,DirectionFinderListener, AdapterView.OnItemSelectedListener {
    Location currentLocation;
    public static final String ride_details = "https://admin.chalochalecab.com/Webservices/select_driver_details.php";
    public static final String cancel_booking = "https://admin.chalochalecab.com/Webservices/user_cancel_ride.php";

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private GoogleMap googleMap;
    EditText pickLocation,dropLocation;
    private GoogleMap mMap;
    EditText dropLoc;
    Double latitute,longitute;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();


    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    LinearLayout callDriver;
    LinearLayout shareRide;
    CircleImageView driver_image;
    TextView driver_name;
    TextView vehicle_name;
    TextView vehicle_number;


    RelativeLayout driver_details;
    RelativeLayout TaxiHorizontal;
    LinearLayout otp_lnr;
    LinearLayout track_Mylocation;
    RelativeLayout paymentDetails;

    LinearLayout cancelRide;
    TextView total_bookPrice;
    TextView user_otp;
    private List<CabBookingModel> cabListModel;
    Preferences preferences;
    String driver_id;
    String cab_id;
    String vehicle_no;
    String cab_name;
    String price;
    String vehicle_company;
    String driver_images;
    String driver_name2;
    String driver_number2;
    String otp;

    String sources;
    String destinations;
    String selected;

    Button google_pay;
    String[] reason = { "Find another source to travel", "Plan cancelled", "I got the  cab/auto", "Other reason"};


    Double lat,lng;
    Dialog dialog;


    View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_booking, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLocation();
        GPSTracker mGPS = new GPSTracker(getActivity());
        MainActivity.iv_menu.setImageResource(R.drawable.ic_back);
        callDriver=view.findViewById(R.id.callDriver);
        shareRide=view.findViewById(R.id.shareRide);
        driver_image=view.findViewById(R.id.driver_image);
        driver_name=view.findViewById(R.id.driver_name);
        vehicle_name=view.findViewById(R.id.vehicle_name);
        vehicle_number=view.findViewById(R.id.vehicle_number);
        cancelRide=view.findViewById(R.id.cancelRide);
        total_bookPrice=view.findViewById(R.id.total_price);
        user_otp=view.findViewById(R.id.otp);
        track_Mylocation=view.findViewById(R.id.track_Mylocation);



        google_pay=view.findViewById(R.id.google_pay);

         driver_details=view.findViewById(R.id.driver_details);
        TaxiHorizontal=view.findViewById(R.id.TaxiHorizontal);
        otp_lnr=view.findViewById(R.id.otp_lnr);
        paymentDetails=view.findViewById(R.id.paymentDetails);


        preferences=new Preferences(getActivity());
        cabListModel = new ArrayList<>();

        ProductProgressBar();
        dialog.show();



        if (Utils.isNetworkConnectedMainThred(getActivity())) {

            RideDetails();
         /*   new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                }
            }, 0);*/

        } else {
            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        tvHeaderText.setVisibility(View.GONE);
        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });


        Bundle ba = getArguments();
         sources = ba.getString("source");
         destinations = ba.getString("destination");
         getLocationFromAddress(destinations);
        // Log.e("destination",""+getLocationFromAddress());

        try {
            new DirectionFinder(this, sources, destinations).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        if (mGPS.canGetLocation()) {
            mGPS.getLocation();
            latitute = mGPS.getLatitude();
            longitute = mGPS.getLongitude();
        } else {
            System.out.println("Unable");
        }

        return view;
    }

    private void CancelRideBooking() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_book);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        //findId
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvNo);
        Spinner reason_spin = dialog.findViewById(R.id.spinner_reason);
        reason_spin.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,reason);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //final String spin =reason_spin.getSelectedItem().toString();
        //Setting the ArrayAdapter data on the Spinner
        reason_spin.setAdapter(adapter);


        dialog.show();

        //set listener
        tvYes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                LoaderProgress();
                CancelRide(selected);
                dialog.show();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void LoaderProgress() {
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

    private void CancelRide(final String selected) {
        StringRequest request = new StringRequest(Request.Method.POST, cancel_booking, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("cancel_ride", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equalsIgnoreCase("1"))
                    {
                        Intent intent=new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);
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
                parameters.put("user_id", preferences.get("user_id"));
                parameters.put("driver_mobile_no",driver_number2);
                parameters.put("cancel_reason",selected);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }

    private void RideDetails() {
        StringRequest rqst = new StringRequest(Request.Method.POST, ride_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("ride_details",""+ response.length());
                //replaceFragmentWithAnimation(new BookCabFragment(),source,destination);
                try {
                    JSONObject JSNobject = new JSONObject(response);
                    if (JSNobject.getString("success").equalsIgnoreCase("true")) {
                        Log.e("responsess",""+JSNobject);
                        dialog.cancel();
                        JSONArray jsonArray = JSNobject.getJSONArray("get_driver_details");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Object = jsonArray.getJSONObject(i);
                            //model class
                             CabBookingModel cabList = new CabBookingModel();
                             cab_id = Object.getString("id");
                              driver_id = Object.getString("driver_id");
                            vehicle_no = Object.getString("vehicle_no");
                             cab_name = Object.getString("vehicle_type");
                             price = Object.getString("total_price");
                             vehicle_company=Object.getString("vehicle_compony");
                             driver_images="http://admin.chalochalecab.com/" + Object.getString("driver_image");
                            //String cab_number = Object.getString("vehicle_number");
                             driver_name2 = Object.getString("driver_name");
                             driver_number2 = Object.getString("driverMobileNo");
                             otp = Object.getString("otp");

                            cabList.setCab_id(cab_id);
                            cabList.setCab_name(cab_name);
                            cabList.setCab_price(price);
                            cabList.setDriver_id(driver_id);
                           // cabList.setCab_number(cab_number);
                            cabList.setDriver_image(driver_images);
                            cabList.setDriver_name(driver_name2);
                            cabList.setCab_company(vehicle_company);
                            cabList.setVehicle_type_id(vehicle_no);
                            cabList.setDriver_number(driver_number2);
                            cabList.setOtp(otp);

                            cabListModel.add(cabList);
                        }

                        driver_details.setVisibility(View.VISIBLE);
                        TaxiHorizontal.setVisibility(View.VISIBLE);
                        otp_lnr.setVisibility(View.VISIBLE);
                        paymentDetails.setVisibility(View.VISIBLE);
                        track_Mylocation.setVisibility(View.VISIBLE);

                        driver_name.setText(driver_name2);
                        vehicle_name.setText(vehicle_company);
                        vehicle_number.setText(vehicle_no);
                        total_bookPrice.setText("\u20b9"+price);
                        user_otp.setText("OTP :"+otp);

                        Glide.with(getActivity())
                                .load(driver_images)
                                .into(driver_image);

                        cancelRide.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                                    CancelRideBooking();
                                } else {

                                    Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                                }
                                // CancelRideBooking();

                            }
                        });
                        callDriver.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+driver_number2));
                                startActivity(intent);
                            }
                        });
                        shareRide.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent shareIntent =new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                String shareBody="Your body here";
                                String subject="Your subject here";
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(shareIntent, "Share via"));
                            }
                        });

                        google_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), RazorpayActivity.class);
                                startActivity(i);

                            }
                        });
                        track_Mylocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Utils.isNetworkConnectedMainThred(getActivity())) {

                                    GetMyLocation(sources,destinations);

                                } else {
                                    // Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                                    Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                    }else {


                        RideDetails();

                          /*  new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    *//* Create an Intent that will start the Menu-Activity. *//*

                                }
                            }, 100);*/

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
                parameters.put("user_id",preferences.get("user_id"));
                Log.e("paramaet",""+parameters);
                //parameters.put("driver_id", MyCabFirebaseService.driver_id);
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(rqst);

    }

    private void GetMyLocation(String sources, String destinations) {

        try {
            Uri uri=Uri.parse("https://www.google.co.in/maps/dir/"+sources+"/"+destinations);
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Uri uri=Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps,maps");
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

    }

    private void ProductProgressBar() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_for_lookingcab);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
    }

    private LatLng getLocationFromAddress(String destination) {

        Geocoder coder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(destination, 5);
            if (address == null) {
                return p1;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            lat=location.getLatitude();
            lng=location.getLongitude();
            Log.e("latlang",""+p1);

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(BookCabFragment.this);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }

    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route routes : route) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routes.startLocation, 13));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_adjust_24))
                    .title(routes.startAddress)
                    .position(routes.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_point))
                    .title(routes.endAddress)
                    .position(routes.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLACK).
                    width(10);

            for (int i = 0; i < routes.points.size(); i++)
                polylineOptions.add(routes.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLng origin = new LatLng(latitute, longitute);
            LatLng dest = new LatLng(lat, lng);
            Log.e("dest", "" + dest);
            builder.include(origin);
            builder.include(dest);
            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            mMap.animateCamera(cu);


        }

    }
    private BitmapDescriptor bitmapDescriptorFromVector (Context context,int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //((TextView) parent.getChildAt(0)).setTextColor(Color.RED); /* if you want your item to be white */
       // Toast.makeText(getActivity(),reason[position] , Toast.LENGTH_SHORT).show();
         selected = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
