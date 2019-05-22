package com.example.zerowaste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class shareFridge extends AppCompatActivity {

    private static Button add_user;
    private static Button share;
    private static EditText username;
    private ListView shareList;
    private ArrayAdapter adapter;
    public List<String> share_with = new ArrayList<>();

    public MyFridge sharedFridge = new MyFridge();

    public String user_share;

    public static final String MY_PREFS_NAME = "ShareFridge";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        buttonClick();
    }

    @Override
    protected void onResume() {
        super.onResume();

        shareList = findViewById(R.id.shareList);

        //sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //String restoredString = sharedPreferences.getString("shareList", null);
        //List<String> shareFridgeList = new ArrayList<String>(Arrays.asList(restoredString.replace("[\"","").replace("\"]","").split("\",\"")));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, share_with);
        shareList.setAdapter(adapter);

    }

    public void buttonClick() {

        add_user = (Button) findViewById(R.id.add_users_btn);
        share = (Button) findViewById(R.id.share_btn);
        username = (EditText) findViewById(R.id.user_name);

        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_with.add(username.getText().toString());
                //user_share = username.getText().toString();
                Toast.makeText(shareFridge.this, username.getText().toString() + " is added", Toast.LENGTH_SHORT).show();
                username.setText("");
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(shareFridge.this, MyFridge.class);
                //myIntent.putExtra("share", share_with);
                String usersString = new Gson().toJson(share_with);
                myIntent.putExtra("share", usersString);
                //sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                //SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putString("shareList",usersString);
                //editor.apply();
                Log.d("tag1234", usersString);
                startActivity(myIntent);

            }
        });

    }
}
