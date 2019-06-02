package com.example.zerowaste;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

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


    private WebView wv1;

    public GetRecipesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_getrecipes, container, false);
        //getActivity().setContentView(R.layout.fragment_getrecipes);

        b1=(Button)rootView.findViewById(R.id.button);
        ed1=(EditText)rootView.findViewById(R.id.editText);

        wv1=(WebView)rootView.findViewById(R.id.webView);
        WebSettings settings = wv1.getSettings();
        //settings.setAllowFileAccessFromFileURLs(true);
        //settings.setAllowUniversalAccessFromFileURLs(true);
        wv1.setWebViewClient(new MyBrowser());

        MyFridgeFragment fridge = new MyFridgeFragment();
        List<String> foodItems = fridge.getDataList();
        Log.d("datalist: ", foodItems.toString());



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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