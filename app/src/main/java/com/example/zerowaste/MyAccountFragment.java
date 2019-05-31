package com.example.zerowaste;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class MyAccountFragment extends Fragment {

    TextView userIn;
    TextView shareFridge;
    SharedPreferences sharedpreferences;


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
        String j = sharedpreferences.getString("key",null);
        userIn.setText(j);

        shareFridge = (TextView) getView().findViewById(R.id.shareFridge);
        sharedpreferences = this.getActivity().getSharedPreferences("ShareFridge", getActivity().MODE_PRIVATE);
        String shareList = sharedpreferences.getString("shareList", null);
        if (shareList == null) {
            shareFridge.setText("You are not sharing fridge with anyone");
        } else {
            shareFridge.setText("You are sharing fridge with: "+shareList);
        }

    }



}
