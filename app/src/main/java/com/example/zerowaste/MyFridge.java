package com.example.zerowaste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyFridge extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private static Button addfood;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    public List<String> shareWith;


    String TAG = "tag12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);
        addfood = (findViewById(R.id.add_item));

        addfood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add food has been clicked");
                Intent newIntent = new Intent(MyFridge.this, addFood.class);
                Log.d(TAG, "intent created");
                startActivity(newIntent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent.getStringExtra("share") != null) {
        //if (intent != null) {
            saveUsers(intent);
            shareWith = getUserlist();
            //for (String user : shareWith) {
            //    Log.d("tag12 users", user);
            //}
        } else {
            Log.d("tag12", "the intent is empty");
        }

        Log.d("tag12", "onResume");
    }

    public void saveUsers(Intent intent) {
        //Log.d("tag12 myfridge","saveUsers");
        //Log.d("tag12 intent", String.valueOf(intent));
        //final ArrayList<String> savedExtra = intent.getStringArrayListExtra("share with");
        final String savedExtra = intent.getStringExtra("share");
        Log.d("tag12 extras", savedExtra);

        sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("share_with",savedExtra);
        editor.apply();

    }

    public List<String> getUserlist() {

        sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredString = sharedPreferences.getString("share_with", null);

        List<String> userList = new ArrayList<String>(Arrays.asList(restoredString.replace("[\"","").replace("\"]","").split("\",\"")));
        Log.d("tag12 restoredstring", restoredString);
        Log.d("tag12 tempList", String.valueOf(userList));

        return userList;
    }


}
