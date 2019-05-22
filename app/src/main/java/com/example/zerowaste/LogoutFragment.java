package com.example.zerowaste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class LogoutFragment extends Fragment {
    Login login = new Login();
    SharedPreferences sharedPreferences;

    public LogoutFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key", 0);
        editor.apply();

        //sharedPreferences = login.getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putInt("key", 0);
        //editor.apply();

        Intent i = new Intent(getActivity().getApplicationContext(), Login.class);
        startActivity(i);
    }


}