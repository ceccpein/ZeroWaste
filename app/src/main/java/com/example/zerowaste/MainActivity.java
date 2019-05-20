package com.example.zerowaste;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static EditText username;
    private static EditText password;
    private static Button sign_up;
    private static Button sign_in;

    private DatabaseReference mDatabase;
    private DatabaseReference users;

    //final Boolean authenticated = null;
    //final Boolean usernameCheck = null;

    String TAG = "tag12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonClick();

    }

    public void buttonClick() {
        sign_up = findViewById(R.id.signUp);
        sign_in = findViewById(R.id.signIn);
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        Log.d(TAG, username.getText().toString());

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!username.getText().toString().equals("") && (!password.getText().toString().equals(""))) {
                    writeNewUser(username.getText().toString(), password.getText().toString());
                    toastmsg("New user created. Username: " + username.getText().toString());
                    Intent myIntent = new Intent(MainActivity.this, MyFridge.class);
                    startActivity(myIntent);
                } else {
                    toastmsg("You didn't fill in all the fields");
                }
                /*
                readUser(new MySecondCallback() {
                    @Override
                    public void onSecCallback(Boolean value) {
                        if (value) {
                            writeNewUser(username.getText().toString(), password.getText().toString());
                            //Toast.makeText(MainActivity.this, "New user created. Username: "+ username.getText().toString(), Toast.LENGTH_SHORT).show();
                            toastmsg("New user created. Username: " + username.getText().toString());
                            Intent myIntent = new Intent(MainActivity.this, MyFridge.class);
                            startActivity(myIntent);
                        } else {
                            toastmsg(username.getText().toString() + " is taken");
                        }
                    }
                });
                */

            //}else {
            //        toastmsg("You didn't fill in all the fields");
            //    }
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readPassword(new MyCallback() {
                    @Override
                    public void onCallback(String value) {
                        if (value.equals(password.getText().toString())) {
                            Toast.makeText(MainActivity.this, username.getText().toString() +" is signed in", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(MainActivity.this, MyFridge.class);
                            startActivity(myIntent);
                        } else {
                            toastmsg("Wrong pass");
                        }
                    }
                });
            }
        });

    }
    private void writeNewUser(String name, String pw) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(name).child("password").setValue(pw);

    }

    private void readUser(final MySecondCallback myCallback) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        users = mDatabase.child("users");

        final List<Boolean> checkName = new ArrayList<Boolean>();
        Log.d(TAG, "inne i checkUsername");
        checkName.set(0,null);

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean status = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(username.getText().toString())) {
                        //Log.d(TAG, snapshot.getKey());
                        //usernameCheck = false;
                        status = false;
                    } else {
                        status = true;
                    }

                } myCallback.onSecCallback(status);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void readPassword(final MyCallback myCallback) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        users = mDatabase.child("users").child(username.getText().toString()).child("password");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pass = dataSnapshot.getValue(String.class);
                myCallback.onCallback(pass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void toastmsg(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public interface MyCallback {
        void onCallback(String value);
    }

    public interface MySecondCallback {
        void onSecCallback(Boolean value);
    }

    public String getUsername() {
        return username.getText().toString();
    }
}

