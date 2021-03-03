package com.blucore.cabchalochale.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blucore.cabchalochale.Activity.MainActivity;
import com.blucore.cabchalochale.Activity.Utils;
import com.blucore.cabchalochale.R;
import com.blucore.cabchalochale.extra.Preferences;

import es.dmoral.toasty.Toasty;


public class UserProfileFragment extends Fragment  {
    TextView userFirstName;
    TextView userLastName;
    TextView userContact;
    Preferences preferences;
    TextView tvChange;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_userdetails, container, false);

        userFirstName=view.findViewById(R.id.first_name);
        userLastName=view.findViewById(R.id.last_name);
        userContact=view.findViewById(R.id.phone_number);
        tvChange=view.findViewById(R.id.tvChange);


        preferences=new Preferences(getActivity());

        MainActivity.tvHeaderText.setText("Your Profile");
        userContact.setText(preferences.get("contact_no"));
        userFirstName.setText(preferences.get("first_name"));
        userLastName.setText(preferences.get("last_name"));


        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                    replaceFragmentWithAnimation(new UpdateUserDetailsFragment());
                } else {
                    Toasty.error(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });





        MainActivity.iv_menu.setImageResource(R.drawable.ic_back);
        MainActivity.iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            }
        });


        return view;
    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }



}
