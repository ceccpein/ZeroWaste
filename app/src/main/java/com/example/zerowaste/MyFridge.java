package com.example.zerowaste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MyFridge extends AppCompatActivity {

    private static Button addfood;

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
                Intent myIntent = new Intent(MyFridge.this, addFood.class);
                Log.d(TAG, "intent created");
                startActivity(myIntent);
            }
        });
    }
}
