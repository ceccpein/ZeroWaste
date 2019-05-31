package com.example.zerowaste;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class MyAccountFragment extends Fragment {

    TextView userIn;
    TextView shareFridge;
    SharedPreferences sharedpreferences;
    Button stopSharing;

    String username;


    public MyAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_myaccount, container, false);

        return rootView;
    }

    public void onStart() {
        super.onStart();

        userIn = (TextView) getView().findViewById(R.id.userSingedIn);
        sharedpreferences = this.getActivity().getSharedPreferences("autoLogin", getActivity().MODE_PRIVATE);
        username = sharedpreferences.getString("key",null);
        userIn.setText(username);

        shareFridge = (TextView) getView().findViewById(R.id.shareFridge);
        sharedpreferences = this.getActivity().getSharedPreferences("ShareFridge", getActivity().MODE_PRIVATE);
        String shareList = sharedpreferences.getString(username, null);
        if (shareList == null) {
            shareFridge.setText("You are not sharing fridge with anyone");
        } else {
            shareFridge.setText("You are sharing fridge with: "+shareList);
        }

        stopSharing = (Button) getView().findViewById(R.id.stopShare);
        stopSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences("ShareFridge", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(username, null);
                editor.apply();
                shareFridge.setText("You are not sharing fridge with anyone");
            }
        });

    }



}
