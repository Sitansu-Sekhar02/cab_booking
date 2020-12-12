package com.blucore.chalochale.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blucore.chalochale.Activity.MainActivity;
import com.blucore.chalochale.Model.YourRideModel;
import com.blucore.chalochale.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    public class YourRideAdapter extends  RecyclerView.Adapter<YourRideAdapter.ViewHolder> {
        ArrayList source;
        private List<YourRideModel> mModel;
        Context context;

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public YourRideAdapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public YourRideAdapter() {

        }

        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_userriding, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.rlmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    replaceFragmentWithAnimation(new RidingHistoryDetailsFragment());
                }
            });
        }

       /* @Override
        public void onBindViewHolder(@NonNull com.blucore.chalochale.Adapter.YourRideAdapter.ViewHolder holder, int position) {
            // holder.source.setText(source.get(position).toString());
            //holder.imageView.setImageResource(listdata[position].getImgId());
            holder.rlmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    replaceFragmentWithAnimation(new RidingHistoryDetailsFragment());
                }
            });

        }*/

        @Override
        public int getItemCount() {
            return 1;
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
