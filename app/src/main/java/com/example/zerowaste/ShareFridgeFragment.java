package com.example.zerowaste;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class ShareFridgeFragment extends Fragment {

    public ShareFridgeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sharefridge, container, false);

        return rootView;
    }

}

