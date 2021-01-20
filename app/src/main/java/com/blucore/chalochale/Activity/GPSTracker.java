package com.blucore.chalochale.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucore.chalochale.Driver.BackgroundLocationService;
import com.blucore.chalochale.Driver.LocationRepository;
import com.blucore.chalochale.Model.MyLocation;
import com.blucore.chalochale.extra.Preferences;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Service.START_STICKY;
import static android.content.ContentValues.TAG;

public final class GPSTracker extends Service implements LocationListener {
    public static final String latlng_driver = "https://admin.chalochalecab.com/Webservices/map_records.php";
    private final GPSTracker.LocationServiceBinder binder = new LocationServiceBinder();
    private final Context mContext;

    // flag for GPS status
   // private LocationRepository locationRepository;
    public boolean isGPSEnabled = false;
    Preferences preferences;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    double lati,longi;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 120000; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private LocationRepository locationRepository;

    Location mLastLocation;


    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }
    private GPSTracker(){
        mContext = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        locationRepository = new LocationRepository(mContext);

        //startForeground(12345678, getNotification());
    }

    /**
     * Function to get the user's current location
     *
     * @return
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                       // return TODO;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    location=null;
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                        //locationRepository = new LocationRepository(mContext);
                        preferences=new Preferences(mContext);


                      /*  if (locationRepository != null) {
                            MyLocation loc = new MyLocation();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            Log.e("latitude",""+location.getLatitude());
                            Log.e("longitude",""+location.getLongitude());

                            locationRepository.insertLocation(loc);

                        }*/
                        //getDriverLatlng(location.getLatitude(),location.getLongitude());

                        //Toast.makeText(mContext, "LAT: " + location.getLatitude() + "\n LONG: " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        Log.e("testing", "" + latLng);

        if (preferences.get("roll").equalsIgnoreCase("driver")){
            getDriverLatlng(latLng.latitude,latLng.longitude);

        }else {

        }



//    LocationRepository  locationRepository = new LocationRepository(mContext);
//        MyLocation loc = new MyLocation();
//        loc.setLatitude(latLng.latitude);
//        loc.setLongitude(latLng.longitude);
//        locationRepository.insertLocation(loc);
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != mLastLocation && null != providerList && providerList.size() > 0) {
            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
            //String  mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            LocationRepository locationRepository = new LocationRepository(mContext);
            MyLocation loc = new MyLocation();
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            //loc.setTime(Long.parseLong(mLastUpdateTime));

            locationRepository.insertLocation(loc);
            Log.e("123", "" + latLng.latitude);
            Log.e("456", "" + latLng.longitude);


            //getDriverLatlng(latitude,longitude);

            // sessionManager =new SessionManager(mContext);
            //HashMap<String, String> user = sessionManager.getUserDetails();

            // name
            //String name = user.get(SessionManager.Name);

            // email
            //int id = Integer.parseInt(user.get(SessionManager.NEWID));

//        if( id >10000){


            // hitAPI();
//                }
//            else{
//
//                }

            Geocoder geocoder = new Geocoder(mContext,
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();

                    Log.e("mrakerrrrfdyyyyyyyy", "" + state+country+subLocality);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private void getDriverLatlng(final double latitude, final double longitude) {
         StringRequest request = new StringRequest(Request.Method.POST, latlng_driver, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //dialog.cancel();
                    Log.e("driver latlng", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("success").equalsIgnoreCase("1")) {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //dialog.cancel();
                    Log.e("error_response", "" + error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("driverMobileNo", preferences.get("contact_no"));
                    parameters.put("latitude", String.valueOf(latitude));
                    parameters.put("longitude", String.valueOf(longitude));
                    Log.e("latlng", "" + parameters);
                    return parameters;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(request);

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    public class LocationServiceBinder extends Binder {
        public GPSTracker getService() {
            return GPSTracker.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }
}
