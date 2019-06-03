package com.example.zerowaste;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class ShareFridgeFragment extends Fragment {

    private static Button share;
    private static EditText username;
    private DatabaseReference mDatabase;
    private DatabaseReference checkUser;

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

        sharedPreferences = getActivity().getSharedPreferences("autoLogin", getActivity().MODE_PRIVATE);
        user = sharedPreferences.getString("key", null);

        buttonClick();

    }

    public void buttonClick() {

        share = (Button) getView().findViewById(R.id.share_btn);
        username = (EditText) getView().findViewById(R.id.user_name);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUsername(new MyCallback() {
                    @Override
                    public void onCallback(Boolean value) {
                        if (value) {
                            Toast.makeText(getActivity().getApplicationContext(), "You are now sharing fridge with: "+username.getText().toString(), Toast.LENGTH_SHORT).show();

                            Intent myIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);

                            sharedPreferences = getActivity().getSharedPreferences(MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(user,username.getText().toString());
                            editor.apply();
                            Log.d("tag1234", username.getText().toString());

                            startActivity(myIntent);
                        }

                    }
                });

            }
        });

    }

    private void checkUsername(final MyCallback myCallback) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        checkUser = mDatabase.child("users").child(username.getText().toString());

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getActivity(), "This username doesn't exist", Toast.LENGTH_SHORT).show();
                } else {
                    myCallback.onCallback(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public interface MyCallback {
        void onCallback(Boolean value);
    }
}

