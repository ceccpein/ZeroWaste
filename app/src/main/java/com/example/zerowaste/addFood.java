//Code from:
//https://www.journaldev.com/9976/android-date-time-picker-dialog

package com.example.zerowaste;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class addFood extends AppCompatActivity implements View.OnClickListener{

    private Button btnDatePicker;
    private EditText txtDate;
    private int mYear, mMonth, mDay;
    private Button btnAddFood;
    private EditText txtFood;
    private DatabaseReference mDatabase;
    private int ChosenY = 0;
    private int ChosenM = 0;
    private int ChosenD = 0;
    private Button btnDone;

    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);

        btnDatePicker = (Button) findViewById(R.id.btn_date);
        txtDate = (EditText) findViewById(R.id.in_date);
        btnAddFood = (Button) findViewById(R.id.add_btn);
        txtFood = (EditText) findViewById(R.id.food_item);
        btnDone = (Button) findViewById(R.id.done_btn);

        btnDatePicker.setOnClickListener(this);
        btnAddFood.setOnClickListener(this);
        btnDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            ChosenY = year;
                            ChosenM = monthOfYear;
                            ChosenD = dayOfMonth;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
            datePickerDialog.getDatePicker();
        }
        if (v == btnAddFood) {

            mDatabase = FirebaseDatabase.getInstance().getReference();
            String username = getUsername();
            String fooditem = txtFood.getText().toString();
            String expriationDate = ChosenD + "-" + ChosenM + "-" + ChosenY;

            if (!fooditem.equals("") && ChosenY != 0 && ChosenM != 0 && ChosenD != 0) {
                mDatabase.child("users").child(username).child("food items").child(fooditem).setValue(expriationDate);
                toastmsg(fooditem + " is added to your fridge");
                txtFood.setText("");
                txtDate.setText("");
            } else {
                toastmsg("You need to fill out all the fields");
            }
        }
        if (v == btnDone) {
            //Intent myIntent = new Intent(addFood.this, MyFridgeFragment.class);
            //this.startActivity(myIntent);
            /*
            Fragment fridgeFragment = new MyFridgeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fridge_fragment, fridgeFragment).commit();
            */
            Intent myIntent = new Intent(addFood.this, MainActivity.class);
            this.startActivity(myIntent);
        }

    }

    private void toastmsg(String message) {
        Toast.makeText(addFood.this, message, Toast.LENGTH_SHORT).show();
    }

    public String getUsername() {
        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        String j = sharedpreferences.getString("key",null);
        //Log.d(TAG, "Username in pref " +j);
        //return username.getText().toString();
        return j;
    }

}

