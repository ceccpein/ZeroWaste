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
import java.util.List;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class GetRecipesFragment extends Fragment {
    Button b1;
    EditText ed1;
    private DatabaseReference mDatabase;
    private List<String> foodItems = new ArrayList<String>();
    private List<String> checkedItems = new ArrayList<>();



    private WebView wv1;
    private LinearLayout linearLayout;

    public GetRecipesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_getrecipes, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //getActivity().setContentView(R.layout.fragment_getrecipes);

        b1=(Button)rootView.findViewById(R.id.button);
        ed1=(EditText)rootView.findViewById(R.id.editText);

        wv1=(WebView)rootView.findViewById(R.id.webView);
        WebSettings settings = wv1.getSettings();
        //settings.setAllowFileAccessFromFileURLs(true);
        //settings.setAllowUniversalAccessFromFileURLs(true);
        wv1.setWebViewClient(new MyBrowser());

        //MyFridgeFragment myFridgeFragment = new MyFridgeFragment();
        //String username = myFridgeFragment.getUsername();
        //Log.d("username: ", username);

        SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        final String username = sharedpreferences.getString("key",null);
        Log.d("Username is: ", username);

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               linearLayout = rootView.findViewById(R.id.rootContainer);
               linearLayout.setVisibility(View.VISIBLE);

               for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                   if (uniqueKeySnapshot.getKey().equals(username)){
                       for (DataSnapshot foodSnapshot : uniqueKeySnapshot.child("food items").getChildren()){
                           final String foodKey = foodSnapshot.getKey();
                           foodItems.add(foodKey);
                           CheckBox checkBox = new CheckBox(getActivity());
                           checkBox.setText(foodKey);
                           checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                           checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                               @Override
                               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                   String msg = "You have " + (isChecked ? "checked" : "unchecked") + " this Check it Checkbox.";
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


                           //Log.d("food: ", foodKey);
                       }
                   }
               }

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

        //CREATING FOOD ITEM CHECKBOXES

        //RelativeLayout relativeLayout = rootView.findViewById(R.id.rootContainer);
        //Log.d("list of food items: ", foodItems.toString());
        // Create Checkbox Dynamically
        //for (int i = 0; i < foodItems.size(); i++){
            //CheckBox checkBox = new CheckBox(this.getActivity());
            //checkBox.setText(foodItems.get(i));
            //checkBox.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                //@Override
                //public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //String msg = "You have " + (isChecked ? "checked" : "unchecked") + " this Check it Checkbox.";
                    //Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                //}
            //});

            // Add Checkbox to RelativeLayout
            //if (relativeLayout != null) {
                //relativeLayout.addView(checkBox);
            //}

        //}

        wv1.setVisibility(View.INVISIBLE);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv1.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);

                String fullUrl = "https://www.recipetineats.com/?s=";
                String url = ed1.getText().toString();
                List<String> fooditems = Arrays.asList(url.split("\\s*,\\s*"));
                for (int i=0; i < fooditems.size(); i++){
                    if (i == 0){
                        fullUrl = fullUrl + fooditems.get(i);
                    }else {
                        fullUrl = fullUrl + "%2C+" + fooditems.get(i);
                    }
                }
                //fullUrl = fullUrl + "&sort=re";

                Log.d("url: ", url);
                Log.d("fullURL ", fullUrl);

                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.getSettings().setJavaScriptEnabled(true);
                wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv1.loadUrl(fullUrl);
            }
        });


        return rootView;
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}