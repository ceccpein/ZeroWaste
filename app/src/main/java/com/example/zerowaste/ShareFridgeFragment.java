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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class ShareFridgeFragment extends Fragment {

    private static Button add_user;
    private static Button share;
    private static EditText username;
    private ListView shareList;
    private ArrayAdapter adapter;
    public List<String> share_with = new ArrayList<>();

    public String user_share;

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

        buttonClick();

    }

    @Override
    public void onResume() {
        super.onResume();

        shareList = getView().findViewById(R.id.shareList);

        //sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //String restoredString = sharedPreferences.getString("shareList", null);
        //List<String> shareFridgeList = new ArrayList<String>(Arrays.asList(restoredString.replace("[\"","").replace("\"]","").split("\",\"")));

        Log.d("tag123","onResume");
        adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, share_with);
        shareList.setAdapter(adapter);

    }

    public void buttonClick() {

        add_user = (Button) getView().findViewById(R.id.add_users_btn);
        share = (Button) getView().findViewById(R.id.share_btn);
        username = (EditText) getView().findViewById(R.id.user_name);

        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_with.add(username.getText().toString());
                Log.d("tag1234",share_with.toString());
                //user_share = username.getText().toString();
                Toast.makeText(getActivity().getApplicationContext(), username.getText().toString() + " is added", Toast.LENGTH_SHORT).show();
                username.setText("");
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                //myIntent.putExtra("share", share_with);
                //String shareString = String.join(", ", share_with);
                String shareString = share_with.toString();
                //String usersString = new Gson().toJson(share_with);
                //myIntent.putExtra("share", usersString);
                sharedPreferences = getActivity().getSharedPreferences(MY_PREFS_NAME, getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("shareList",shareString);
                editor.apply();
                //Log.d("tag1234", usersString);
                Log.d("tag1234", shareString);
                startActivity(myIntent);

            }
        });

    }

}

