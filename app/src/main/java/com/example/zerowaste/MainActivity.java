package com.example.zerowaste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    String TAG = "tag12346";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //may have to remove these lines?
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child("ingvild").child("password").setValue("hesterbest2");
        //goes into users, ingvild, and sets password with hesterbest2



    }




}

