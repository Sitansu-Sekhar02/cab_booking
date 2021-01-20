package com.blucore.chalochale.Driver;

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
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.chalochale.Activity.GPSTracker;
import com.blucore.chalochale.Activity.MainActivity;
import com.blucore.chalochale.Activity.SignUpActivity;
import com.blucore.chalochale.Activity.Utils;
import com.blucore.chalochale.FirebaseClasses.MyCabFirebaseService;
import com.blucore.chalochale.Fragments.DirectionFinderListener;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.DirectionFinder;
import com.blucore.chalochale.extra.Preferences;
import com.blucore.chalochale.extra.Route;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.blucore.chalochale.Activity.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class DriverRouteActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, DirectionFinderListener {

    public static final String end_tripNotification = "https://admin.chalochalecab.com/Webservices/end_trip.php";

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap;

    //private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    LocationRequest mLocationRequest;

    Double lat,lng;


    Double latitute,longitute;
    public static int backPressed = 0;

    private static final int REQUEST_SELECT_PLACE = 101;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();

    Button show_route,end_trip;
    ImageView back;
    LinearLayout callUser;
    TextView otp;
    TextView track_user_location;


    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    Preferences preferences;


    //AlertDialog dialog;
    Dialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_route_fragment);

        preferences = new Preferences(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        MapFragment supportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.driver_route);

        // SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_route);

        supportMapFragment.getMapAsync(this);

       // fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        GPSTracker mGPS = new GPSTracker(this);

        if (mGPS.canGetLocation()) {
            mGPS.getLocation();
            latitute = mGPS.getLatitude();
            longitute = mGPS.getLongitude();
            Log.e("lat",""+latitute);
        } else {
            System.out.println("Unable");
        }
        //fetchLocation();

        show_route=findViewById(R.id.showtrack);
        end_trip=findViewById(R.id.endtrip);
        back=findViewById(R.id.ic_back);
        callUser=findViewById(R.id.call_user);
        otp=findViewById(R.id.otp);
        track_user_location=findViewById(R.id.track_user);
        otp.setText("OTP :"+preferences.get("otp"));

        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+MyCabFirebaseService.user_contact_no));
                startActivity(intent);
            }
        });

        track_user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isNetworkConnectedMainThred(DriverRouteActivity.this)) {

                    TrackUserLocation("",MyCabFirebaseService.from_ads);

                } else {
                    // Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                    Toasty.error(DriverRouteActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();

                }


            }
        });



        String from_address = getIntent().getStringExtra("from_ads");
        Log.e("from",""+from_address);
        String to_address = getIntent().getStringExtra("to_ads");
        Log.e("to",""+to_address);


        /*try {
            new DirectionFinder(this, from_address, to_address).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getLocationFromAddress(to_address);
*/


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverRouteActivity.this, DriverMainActivity.class);
                startActivity(i);
            }
        });

        show_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isNetworkConnectedMainThred(DriverRouteActivity.this)) {

                    DisplayTrack(MyCabFirebaseService.from_ads,MyCabFirebaseService.to_ads);

                } else {
                    // Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                    Toasty.error(DriverRouteActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();

                }

            }
        });
        end_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnectedMainThred(DriverRouteActivity.this)) {
                    ProgressBarEndTrip();
                    dialog.show();
                    EndTrip();

                } else {
                    // Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                    Toasty.error(DriverRouteActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void TrackUserLocation(String completeAddressString, String from_ads) {
        try {
            Uri uri=Uri.parse("https://www.google.co.in/maps/dir/"+""+"/"+from_ads);
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


    private void EndTrip() {
        StringRequest request = new StringRequest(Request.Method.POST, end_tripNotification, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.cancel();
                Log.e("end_ride", response);

                Intent intent=new Intent(DriverRouteActivity.this,PaymentDetails.class);
                startActivity(intent);


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
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }
    private void ProgressBarEndTrip() {
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


    private void DisplayTrack(String from_ads, String to_ads) {
        try {
            Uri uri=Uri.parse("https://www.google.co.in/maps/dir/"+from_ads+"/"+to_ads);
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

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }

    }*/

    private BitmapDescriptor bitmapDescriptorFromVector (Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    MapFragment supportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.driver_route);

                   // SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driver_route);

                    assert supportMapFragment != null;

                    supportMapFragment.getMapAsync(DriverRouteActivity.this);
                }
            }
        });

    }
    private LatLng getLocationFromAddress(String to_ads) {

        Geocoder coder = new Geocoder(this, Locale.getDefault());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(to_ads, 5);
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
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
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
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_taxi_black))
                    .title(routes.startAddress)
                    .position(routes.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_baseline_adjust_24))
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
    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(DriverRouteActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(this,
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state
                            + "," + country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("Your Location");
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getCameraPosition();

        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }

    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

}
