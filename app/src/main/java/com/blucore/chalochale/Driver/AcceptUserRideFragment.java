package com.blucore.chalochale.Driver;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.Preferences;
import com.google.android.gms.maps.SupportMapFragment;

import static android.content.Context.MODE_PRIVATE;

public class AcceptUserRideFragment extends AppCompatActivity {
    TextView total_km;
    TextView from_address;
    TextView to_address;
    Button accept_ride;
    Button reject_ride;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_notification_popup);

        total_km=findViewById(R.id.total_distance);
        from_address=findViewById(R.id.from_address);
        to_address=findViewById(R.id.to_address);
        accept_ride=findViewById(R.id.accept_ride);
        reject_ride=findViewById(R.id.cancel_ride);

    }

}
