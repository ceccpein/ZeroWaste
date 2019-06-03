package com.example.zerowaste;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class GetRecipesFragment extends Fragment {
    private Button b1;
    private EditText ed1;
    private LinearLayout linearLayout;
    private DatabaseReference mDatabase;
    private List<String> foodItems = new ArrayList<String>();
    private List<String> checkedItems = new ArrayList<>();
    private WebView wv1;
    private String username;
    private String shareName;

    public GetRecipesFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_getrecipes, container, false);
        this.b1=(Button)rootView.findViewById(R.id.button);
        this.ed1 = (EditText)rootView.findViewById(R.id.editText);
        this.wv1=(WebView)rootView.findViewById(R.id.webView);
        linearLayout = rootView.findViewById(R.id.rootContainer);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        wv1.setWebViewClient(new MyBrowser());
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        this.username = sharedpreferences.getString("key",null);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("ShareFridge", Context.MODE_PRIVATE);
        this.shareName = prefs.getString(username, null);
        Log.d("Username is: ", username);

        getFoodFromDatabase(username);
        getFoodFromDatabase(shareName);

        wv1.setVisibility(View.INVISIBLE);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ed1.getText().toString();

                if (!url.isEmpty()){
                    Log.d("url", "is not empty");
                    if (url.contains(",")){
                        foodItems = new LinkedList<String>(Arrays.asList(url.split(",")));
                    }else{
                        foodItems = new LinkedList<String>(Arrays.asList(url.split(" ")));
                    }
                    loadURL(foodItems);
                    Log.d("foodItems url: ", foodItems.toString());
                    foodItems.removeAll(foodItems);
                }else if (!checkedItems.isEmpty()){
                    Log.d("checked items ", "is not empty");
                    loadURL(checkedItems);
                    Log.d("fooditems checkbox: ", checkedItems.toString());
                    checkedItems.removeAll(checkedItems);
                }else{
                    return;
                }

            }
        });

    }

    public void loadURL(List<String> food){
        String fullUrl = "https://www.recipetineats.com/?s=";
        for (int i=0; i < food.size(); i++){
            if (i == 0){
                fullUrl = fullUrl + food.get(i);
            }else {
                fullUrl = fullUrl + "%2C+" + food.get(i);
            }
        }
        wv1.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        wv1.loadUrl(fullUrl);
    }

    public void getFoodFromDatabase(final String name){
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linearLayout.setVisibility(View.VISIBLE);
                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                    if (uniqueKeySnapshot.getKey().equals(name)){
                        for (DataSnapshot foodSnapshot : uniqueKeySnapshot.child("food items").getChildren()){
                            final String foodKey = foodSnapshot.getKey();
                            foodItems.add(foodKey);
                            CheckBox checkBox = new CheckBox(getActivity());
                            checkBox.setText(foodKey);
                            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    String msg = "You have " + (isChecked ? "checked" : "unchecked") + " this.";
                                    if (isChecked && !checkedItems.contains(foodKey)){
                                        checkedItems.add(foodKey);
                                    }else if (!isChecked && checkedItems.contains(foodKey)){
                                        checkedItems.remove(foodKey);
                                    }
                                    Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    Log.d("checked list", checkedItems.toString());

                                }
                            });

                            // Add Checkbox to RelativeLayout
                            if (linearLayout != null) {
                                linearLayout.addView(checkBox);
                            }

                        }
                    }
                    else if (uniqueKeySnapshot.getKey().equals("user2")){
                        for (DataSnapshot foodSnapshot : uniqueKeySnapshot.child("food items").getChildren()){
                            final String foodKey = foodSnapshot.getKey();
                            foodItems.add(foodKey);
                            CheckBox checkBox = new CheckBox(getActivity());
                            checkBox.setText(foodKey);
                            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    String msg = "You have " + (isChecked ? "checked" : "unchecked") + " this.";
                                    if (isChecked && !checkedItems.contains(foodKey)){
                                        checkedItems.add(foodKey);
                                    }else if (!isChecked && checkedItems.contains(foodKey)){
                                        checkedItems.remove(foodKey);
                                    }
                                    Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    Log.d("checked list", checkedItems.toString());

                                }
                            });

                            // Add Checkbox to RelativeLayout
                            if (linearLayout != null) {
                                linearLayout.addView(checkBox);
                            }

                        }


                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}