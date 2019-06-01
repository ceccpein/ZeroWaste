package com.example.zerowaste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    private static EditText username;
    private static EditText password;
    private static Button sign_up;
    private static Button sign_in;

    private static ImageView longVege;

    SharedPreferences sharedpreferences;
    int autoSave;

    private DatabaseReference mDatabase;
    private DatabaseReference users;
    private DatabaseReference user;

    String TAG = "tag12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        int j = sharedpreferences.getInt("key", 0);

        //Default is 0 so autologin is disabled
        if (j > 0) {
            Intent activity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(activity);
        }
        */

        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        String j = sharedpreferences.getString("key",null);

        if (j != null) {
            Intent activity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(activity);
        }
        buttonClick();
    }

    public void buttonClick() {
        sign_up = findViewById(R.id.signUp);
        sign_in = findViewById(R.id.signIn);
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);

        longVege = findViewById(R.id.imageView);
        longVege.setImageResource(R.drawable.long_vegetable);
        //Log.d(TAG, username.getText().toString());


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!username.getText().toString().equals("") && (!password.getText().toString().equals(""))) {
                    checkUsername(new MySecondCallback() {
                        @Override
                        public void onSecCallback(Boolean value) {
                            if (value) {
                                writeNewUser(username.getText().toString(), password.getText().toString());
                                toastmsg("New user created. Username: " + username.getText().toString());
                                Intent myIntent = new Intent(Login.this, MainActivity.class);
                                startActivity(myIntent);
                                //autoSave = 1;
                                String uname = username.getText().toString();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                //editor.putInt("key", autoSave);
                                editor.putString("key", uname);
                                editor.apply();
                            }
                        }
                    });
                    /*
                    writeNewUser(username.getText().toString(), password.getText().toString());
                    toastmsg("New user created. Username: " + username.getText().toString());
                    Intent myIntent = new Intent(Login.this, MainActivity.class);
                    startActivity(myIntent);
                    //autoSave = 1;
                    String uname = username.getText().toString();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //editor.putInt("key", autoSave);
                    editor.putString("key", uname);
                    editor.apply();
                    */

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
                if (username.getText().toString().equals("") || (password.getText().toString().equals(""))) {
                    toastmsg("You didn't fill in all the fields");
                } else {
                    readPassword(new MyCallback() {
                        @Override
                        public void onCallback(String value) {
                            Log.d(TAG, "password i readPAssword: " + value);
                            if (value.equals(password.getText().toString())) {
                                //Once you click login, it will add 1 to shredPreference which will allow autologin in onCreate

                                Toast.makeText(Login.this, username.getText().toString() + " is signed in", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(Login.this, MainActivity.class);
                                startActivity(myIntent);
                                //autoSave = 1;
                                String uname = username.getText().toString();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                //editor.putInt("key", autoSave);
                                editor.putString("key", uname);
                                editor.apply();
                            } else {
                                toastmsg("Wrong password");
                            }
                        }
                    });
                }
            }
        });
    }
    private void writeNewUser(String name, String pw) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(name).child("password").setValue(pw);
        mDatabase.child("users").child(name).child("food items");

    }

    private void readUser(final MySecondCallback myCallback) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        users = mDatabase.child("users");

        final List<Boolean> checkName = new ArrayList<Boolean>();
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
                if (dataSnapshot.exists()) {
                    String pass = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "Password "+ pass);
                    myCallback.onCallback(pass);
                } else {
                    toastmsg("Username doesn't exits/wrong username");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void checkUsername(final MySecondCallback myCallback) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mDatabase.child("users").child(username.getText().toString());

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    toastmsg("This username already exits");
                } else {
                    myCallback.onSecCallback(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void toastmsg(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

    public interface MyCallback {
        void onCallback(String value);
    }

    public interface MySecondCallback {
        void onSecCallback(Boolean value);
    }

    public String getUsername() {
        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        String j = sharedpreferences.getString("key",null);
        Log.d(TAG, "Username in pref " +j);
        //return username.getText().toString();
        return j;
    }

}