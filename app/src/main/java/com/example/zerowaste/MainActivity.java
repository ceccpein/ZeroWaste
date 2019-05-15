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
    private ValueEventListener postListener;

    String TAG = "tag12346";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //may have to remove these lines?
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child("ingvild").child("matvarer").child("bananas").setValue("10 mai");
        //goes into users, ingvild, matvarer, bananas og setter bananas med 10 mai

        mDatabase.child("posts").setValue(new Post("ingvilds","nilsson"));
        readDB();

    }

    public static class Post {

        public String author;
        public String title;

        public Post(String author, String title) {
            this.author = author;
            this.title = title;
        }

    }

    // Get a reference to our posts

    public void readDB() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/posts/");

    // Attach a listener to read the data at our posts reference
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, dataSnapshot.getValue().toString());
                String s = dataSnapshot.getValue().toString();
                Log.d(TAG, s);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });}






}

