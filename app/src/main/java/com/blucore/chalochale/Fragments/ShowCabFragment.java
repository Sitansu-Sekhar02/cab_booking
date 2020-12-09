package com.blucore.chalochale.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blucore.chalochale.Activity.GPSTracker;
import com.blucore.chalochale.Activity.MainActivity;
import com.blucore.chalochale.Adapter.CabListAadapter;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.DirectionFinder;
import com.blucore.chalochale.extra.Route;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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


    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    Double latitute,longitute;
    Double longt,lati;

    Button confirmRide;


    View view;



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

        Bundle b = getArguments();
        String source = b.getString("source");
        String destination = b.getString("destination");
       // getLocationFromAddress(context,strAddress);

        Log.e("source", "" + source);


        try {
            new DirectionFinder(this, source, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        if (mGPS.canGetLocation()) {
            mGPS.getLocation();
            latitute = mGPS.getLatitude();
            longitute = mGPS.getLongitude();
        }else if (mGPS.canGetLocation()){
            mGPS.getLocation();
            lati = mGPS.getLatitude();
            longt = mGPS.getLongitude();
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
                replaceFragmentWithAnimation(new BookCabFragment());

            }
        });

        ArrayList cabNames = new ArrayList<>(Arrays.asList("Auto"));


        RecyclerView recyclerView = view.findViewById(R.id.car_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CabListAadapter customAdapter = new CabListAadapter(getActivity(), cabNames);
        recyclerView.setAdapter(customAdapter);

        return view;
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
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

       /* LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng origin = new LatLng(latitute,longitute);
        Log.e("origin",""+origin);

        LatLng dest = new LatLng(longt,lati);
        Log.e("dest",""+dest);
*/



//the include method will calculate the min and max bound.
        /*builder.include(origin);
        builder.include(dest);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);


        LatLng lng = new LatLng(latitute, longitute);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("")
                .position(lng)));*/

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


       /* LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);*/


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

           // Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.ic_baseline_local_taxi_24);


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_baseline_local_taxi_24))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
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
//the include method will calculate the min and max bound.

            LatLng origin = new LatLng(latitute,longitute);
            Log.e("origin",""+origin);
            LatLng dest = new LatLng(18.568279920687598, 73.91413851204486);
            Log.e("dest",""+dest);
            builder.include(origin);
            builder.include(dest);
            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            mMap.animateCamera(cu);

        }


    }


}

