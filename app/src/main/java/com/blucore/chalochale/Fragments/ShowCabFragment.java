package com.blucore.chalochale.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.blucore.chalochale.Activity.GPSTracker;
import com.blucore.chalochale.Activity.MainActivity;
import com.blucore.chalochale.Activity.Utils;
import com.blucore.chalochale.Model.CabBookingModel;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.DirectionFinder;
import com.blucore.chalochale.extra.Preferences;
import com.blucore.chalochale.extra.Route;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.blucore.chalochale.Activity.MainActivity.tvHeaderText;

public class ShowCabFragment extends Fragment implements OnMapReadyCallback, DirectionFinderListener {
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private GoogleMap googleMap;
    private GoogleMap mMap;
    ArrayList<LatLng> points;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    String id;
    String driver_id;
    String driver_name;
    String cab_number;
    String driver_image;
    String cab_type;
    String cab_bookPrices;
    String  driver_no;

    private GoogleMap gglmap;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();

    public static final String cab_list = "https://chalochalecab.com/Webservices/nearest_ride.php";
    public static final String confirm_ride = "https://chalochalecab.com/Webservices/confirm_user.php";

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    Double latitute, longitute;
    RecyclerView recyclerView;

    Double lat, lng;

    Button confirmRide;
    String source,destination;
    Dialog dialog;
    private List<CabBookingModel> cabListModel;
    CabListAadapter adapter;
    Preferences preferences;
    ImageView noCab;


    View view;
    public static String cab_id;
    public static String driver_id2;
    public  static  String driver_names;
    public  static  String driver_images;
    public  static  String vehicle_type;
    public  static  String vehicle_number;
    public  static  String total_bookPrice;
    public  static  String driver_number;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_cabs, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //fetchLocation();
        GPSTracker mGPS = new GPSTracker(getActivity());
        MainActivity.iv_menu.setImageResource(R.drawable.ic_back);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.cab_map);
        mapFragment.getMapAsync(this);

        preferences=new Preferences(getActivity());

        Bundle b = getArguments();
         source = b.getString("source");
         destination = b.getString("destination");
        getLocationFromAddress(destination);
        recyclerView = view.findViewById(R.id.car_list);
        noCab=view.findViewById(R.id.noCab);

        // Log.e("destination",""+getLocationFromAddress());

        cabListModel = new ArrayList<>();


        try {
            new DirectionFinder(this, source, destination).execute();
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
        tvHeaderText.setVisibility(View.GONE);
        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });
        confirmRide = view.findViewById(R.id.confirm_ride);
        confirmRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                    ProductProgressBar();
                    dialog.show();
                    ConfirmRide();
                } else {

                    Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }


            }
        });

       // ArrayList cabNames = new ArrayList<>(Arrays.asList("Auto","Sedan","Mini"));
        //setCabAadapter();

       /* RecyclerView recyclerView = view.findViewById(R.id.car_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CabListAadapter customAdapter = new CabListAadapter(getActivity(), cabNames);
        recyclerView.setAdapter(customAdapter);*/

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            ProductProgressBar();
            dialog.show();
            CabList();

        } else {

            Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private void ConfirmRide() {
        StringRequest rqst = new StringRequest(Request.Method.POST, confirm_ride, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("confirm_rideDetails", response);
                replaceFragmentWithAnimation(new BookCabFragment(),source,destination);

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
                parameters.put("driver_id",driver_id);
                Log.e("driver_id",""+parameters);
                //parameters.put("paymentMethod, ","COD" );
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(rqst);

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


    private void CabList() {
        StringRequest request = new StringRequest(Request.Method.POST, cab_list, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("cab_list", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equalsIgnoreCase("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Cab");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Object = jsonArray.getJSONObject(i);
                            //model class
                            CabBookingModel cabList = new CabBookingModel();
                            String cab_id = Object.getString("id");
                            String driver_id=Object.getString("driver_id");
                            Log.e("id_driv",""+driver_id);
                            String cab_name = Object.getString("vehicle_type");
                            String price = Object.getString("price");
                            String vehicle_company=Object.getString("vehicle_compony");
                            String cab_image = "http://admin.chalochalecab.com/" + Object.getString("vehicle_image");
                            String driver_image="http://admin.chalochalecab.com/" + Object.getString("driver_photo");

                            String cab_number = Object.getString("vehicle_number");
                            String driver_name = Object.getString("driver_name");
                            String driver_number = Object.getString("driver_mobile_no");
                            preferences.set("driver_id",driver_id);
                            preferences.commit();


                            cabList.setCab_id(cab_id);
                            cabList.setDriver_id(driver_id);
                            cabList.setCab_name(cab_name);
                            cabList.setCab_image(cab_image);
                            cabList.setCab_price("\u20b9"+price);
                            cabList.setCab_number(cab_number);
                            cabList.setDriver_image(driver_image);
                            cabList.setDriver_name(driver_name);
                            cabList.setCab_company(vehicle_company);
                            cabList.setDriver_number(driver_number);
                            cabListModel.add(cabList);
                        }
                        setAdapter();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (cabListModel.isEmpty()){
                    noCab.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    confirmRide.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    confirmRide.setVisibility(View.VISIBLE);
                    noCab.setVisibility(View.GONE);
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
        adapter = new CabListAadapter(cabListModel, getActivity());
        recyclerView.setAdapter(adapter);

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
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.e("latlang", "" + p1);

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;

    }

    public void replaceFragmentWithAnimation(Fragment fragment,String source,String destination) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putString("source",source);
        bundle.putString("destination",destination);
        fragment.setArguments(bundle);
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    public LatLng getLocationFromAddress(Context context, String destination) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(destination, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
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
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
                //marker.showInfoWindow();
            }

        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                 marker.remove();
                //marker.showInfoWindow();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        ArrayList<LatLng> locations = new ArrayList();
        locations.add(new LatLng(118.580669255285365, 73.74254638744726));
        locations.add(new LatLng(18.59059450137149, 73.72555191223913));
        locations.add(new LatLng(18.59970940309996, 73.7673305611182));
        locations.add(new LatLng(18.60379728631155, 73.74187737837005));


        for (LatLng location : locations) {
            mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_local_taxi_24))
                    .position(location)
                    .title("available taxi"));
        }
        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.dot_circle))
                    .title(route.startAddress)
                    .position(route.startLocation)));


            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(route.endAddress)
                    .position(route.endLocation)));
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLACK).
                    width(10);


            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLng origin = new LatLng(latitute,longitute);
            LatLng dest = new LatLng(lat, lng);
            Log.e("dest",""+dest);
            builder.include(origin);
            builder.include(dest);
            LatLngBounds bounds = builder.build();
            //this.showCurveyPolyline(origin,dest,0.5);

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            mMap.animateCamera(cu);


        }
    }

    private void showCurveyPolyline(LatLng origin, LatLng dest,double k) {
//Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(origin,dest);
        double h = SphericalUtil.computeHeading(origin, dest);

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(origin, d*0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1-k*k)*d*0.5/(2*k);
        double r = (1+k*k)*d*0.5/(2*k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));


        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, origin);
        double h2 = SphericalUtil.computeHeading(c, dest);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 100;
        double step = (h2 -h1) / numpoints;
        for (int i=0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
        }

        //Draw polyline
        mMap.addPolyline(options.width(10)
                .color(Color.BLACK)
                .geodesic(true)
                .pattern(pattern));

    }

    public void rotateMarker(final Marker marker, final float toRotation, final float st,final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;
        final LatLng startPosition = marker.getPosition();
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;
                double currentLat = (lat - startPosition.latitude) * t + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * t + startPosition.longitude;
                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }

            }
        });
    }


    //=============================Adapter====================================================//

    public class CabListAadapter extends  RecyclerView.Adapter<CabListAadapter.ViewHolder> {
        // ArrayList cabNames;
        //private int selectedPosition = 0;


        private List<CabBookingModel> mModel;
        Context context;
        private int selectedItem;

        public CabListAadapter(List<CabBookingModel> mModel, Context context) {
            this.mModel = mModel;
            this.context = context;
            selectedItem = 0;
        }



        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cab_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.Cabname.setText(mModel.get(position).getCab_name());
            Glide.with(context)
                    .load(mModel.get(position).getCab_image())
                    .into(holder.Cabimage);
            holder.CabPrice.setText(mModel.get(position).getCab_price());

            holder.cabList.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));

            if (selectedItem == position) {
                holder.cabList.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDarkR));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = mModel.get(position).getCab_id();
                    driver_id=mModel.get(position).getDriver_id();
                    Log.e("drivers_id", "" + driver_id);

                    driver_name=mModel.get(position).getDriver_name();
                    driver_image=mModel.get(position).getDriver_image();
                    cab_number=mModel.get(position).getCab_number();
                    cab_type=mModel.get(position).getCab_company();
                    cab_bookPrices=mModel.get(position).getCab_price();
                    driver_no=mModel.get(position).getDriver_number();

                    Log.e("cab_id", "" + id);
                    cab_id=id;
                    driver_id2=driver_id;
                    driver_images=driver_image;
                    driver_names=driver_name;
                    vehicle_number=cab_number;
                    vehicle_type=cab_type;
                    total_bookPrice=cab_bookPrices;
                    driver_number=driver_no;



                    int previousItem = selectedItem;
                    selectedItem = position;
                    notifyItemChanged(previousItem);
                    notifyItemChanged(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mModel.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cabList;
            TextView Cabname;
            TextView CabTime;
            TextView CabPrice;
            ImageView Cabimage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                Cabname = (TextView) itemView.findViewById(R.id.tvCabName);
                Cabimage = (ImageView) itemView.findViewById(R.id.Cabimage);
                CabPrice = (TextView) itemView.findViewById(R.id.tvCabprice);
                CabTime = (TextView) itemView.findViewById(R.id.tvTime);
                cabList = itemView.findViewById(R.id.card_view);


            }
        }
    }
}

