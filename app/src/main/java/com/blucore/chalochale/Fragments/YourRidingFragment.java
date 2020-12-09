package com.blucore.chalochale.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blucore.chalochale.Activity.MainActivity;
import com.blucore.chalochale.Activity.Utils;
import com.blucore.chalochale.Adapter.YourRideAdapter;
import com.blucore.chalochale.Model.YourRideModel;
import com.blucore.chalochale.R;
import com.blucore.chalochale.extra.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class YourRidingFragment extends Fragment {
    View v;
    private List<YourRideModel> rideModels;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_your_ride, container, false);
       // rideModels=new ArrayList<>();
        ArrayList source = new ArrayList<>(Arrays.asList("Auto"));

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


        recyclerView=v.findViewById(R.id.recyclerView);

        //set adapter
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new YourRideAdapter());

        return v;
    }
}
