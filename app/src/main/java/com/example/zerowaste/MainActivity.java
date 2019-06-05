package com.example.zerowaste;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(),100, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //gets repeated every 12th hours
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),43200000, broadcast);

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        String j = sharedpreferences.getString("key",null);
        Log.d("Username", "Username in pref " +j);

        TextView helloname = (TextView) findViewById(R.id.hello_name);
        helloname.setText("Welcome "+j+ "!");
        TextView zerowaste_tw = (TextView) findViewById(R.id.hello_name);
        TextView front_text_tw = (TextView) findViewById(R.id.hello_name);


        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        DataModel[] drawerItem = new DataModel[5];

        //Icons and text in menu
        drawerItem[0] = new DataModel(R.drawable.myfridge, "My Fridge");
        drawerItem[1] = new DataModel(R.drawable.myaccount, "My Account");
        drawerItem[2] = new DataModel(R.drawable.sharefridge, "Share Fridge");
        drawerItem[3] = new DataModel(R.drawable.getrecipe, "Get Recipes");
        //drawerItem[4] = new DataModel(R.drawable.shoppinglist, "Shopping List");
        drawerItem[4] = new DataModel(R.drawable.logout, "Logout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;
        TextView hello = (TextView) findViewById(R.id.hello_name);
        TextView zerowaste = (TextView) findViewById(R.id.zerowaste_text);
        TextView front = (TextView) findViewById(R.id.front_text);

        switch (position) {
            case 0:
                fragment = new MyFridgeFragment();
                hello.setVisibility(View.GONE);
                zerowaste.setVisibility(View.GONE);
                front.setVisibility(View.GONE);
                break;
            case 1:
                fragment = new MyAccountFragment();
                hello.setVisibility(View.GONE);
                zerowaste.setVisibility(View.GONE);
                front.setVisibility(View.GONE);
                break;
            case 2:
                fragment = new ShareFridgeFragment();
                hello.setVisibility(View.GONE);
                zerowaste.setVisibility(View.GONE);
                front.setVisibility(View.GONE);
                break;
            case 3:
                fragment = new GetRecipesFragment();
                hello.setVisibility(View.GONE);
                zerowaste.setVisibility(View.GONE);
                front.setVisibility(View.GONE);
                break;
            case 4:

                SharedPreferences prefs = this.getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("key", null);
                editor.apply();

                Intent i = new Intent(this.getApplicationContext(), Login.class);
                startActivity(i);

                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }



}