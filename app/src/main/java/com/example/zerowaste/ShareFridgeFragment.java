package com.example.zerowaste;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class ShareFridgeFragment extends Fragment {

    //private static Button add_user;
    private static Button share;
    private static EditText username;
    private ListView shareList;
    private ArrayAdapter adapter;
    public List<String> share_with = new ArrayList<>();

    private String user;

    public static final String MY_PREFS_NAME = "ShareFridge";
    private SharedPreferences sharedPreferences;

    public ShareFridgeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sharefridge, container, false);


        return rootView;
    }

    public void onStart() {
        super.onStart();
        Log.d("tag123", "onStart");

        sharedPreferences = getActivity().getSharedPreferences("autoLogin", getActivity().MODE_PRIVATE);
        user = sharedPreferences.getString("key", null);

        buttonClick();

    }

    @Override
    public void onResume() {
        super.onResume();

        shareList = getView().findViewById(R.id.shareList);

        Log.d("tag123","onResume");
        adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, share_with);
        shareList.setAdapter(adapter);

    }

    public void buttonClick() {

        share = (Button) getView().findViewById(R.id.share_btn);
        username = (EditText) getView().findViewById(R.id.user_name);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_with.add(username.getText().toString());
                Toast.makeText(getActivity().getApplicationContext(), "You are now sharing fridge with: "+username.getText().toString(), Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);

                String shareString = share_with.toString();

                sharedPreferences = getActivity().getSharedPreferences(MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(user,shareString);
                editor.apply();
                Log.d("tag1234", shareString);

                startActivity(myIntent);

            }
        });

    }

}

