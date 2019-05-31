package com.example.zerowaste;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MyFridgeFragment extends Fragment {
    private DatabaseReference mDatabase;
    String TAG = "tag12346";
    private static Button addfood;

    ArrayList<String> shareList;

    SharedPreferences sharedpreferences;

    static final ArrayList<ArrayList<String>> foodExpiredList = new ArrayList<>();
    final ArrayList<ArrayList<String>> foodExpiresList = new ArrayList<>();


    public MyFridgeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){ super.onCreate(savedInstanceState); }


    @Override
    public void onStart() {
        super.onStart();

        //may have to remove these lines?
        mDatabase = FirebaseDatabase.getInstance().getReference();

        addfood = (getView().findViewById(R.id.add_item));

        addfood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Add food has been clicked");
                Intent newIntent = new Intent(getActivity().getApplicationContext(), addFood.class);
                //Log.d(TAG, "intent created");
                startActivity(newIntent);
            }
        });

        final List<String> dataList = new ArrayList<String>();
        final ListView listview = (ListView) getView().findViewById(R.id.listview);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, dataList);

        final ArrayList<ArrayList<String>> foodExpList = new ArrayList<ArrayList<String>>();



        readData(new MyCallback() {
            @Override
            public void onCallback(String value) {

                value = value.substring(1,value.length()-1);
                String[] allItems1 = value.split(",");
                ArrayList<String> allItems = new ArrayList<String>(Arrays.asList(allItems1));
                for (String item : allItems) {
                    String[] foodexp = item.split("=");
                    String food = foodexp[0];
                    String exp = foodexp[1];
                    ArrayList<String> foodexp2 = new ArrayList<String>();
                    foodexp2.add(food);
                    foodexp2.add(exp);
                    foodExpList.add(foodexp2);

                    dataList.add(food + ":  " + exp);
                }
                findExpiringFood(foodExpList);



                listview.setAdapter(arrayAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                        TextView myMsg = new TextView(getActivity());
                        myMsg.setText("Are you sure you want to delete "+ dataList.get(position) +"?");
                        myMsg.setTextSize(20);

                        new AlertDialog.Builder(getActivity())
                                .setCustomTitle(myMsg)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        parent.removeViewInLayout(view);
                                        removeGrocery(dataList.get(position));

                                    }
                                })

                                .setNegativeButton("No",null)
                                .create()
                                .show();

                    }
                });
    }

    @Override
    public ArrayList<String> findExpiringFood(ArrayList<ArrayList<String>> foodlist) {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String todaysDate = simpleDateFormat.format(new Date());
        String[] ddmmyyyyTodaysArray = todaysDate.split("-");
        ArrayList<String> ddmmyyyyTodaysList = new ArrayList<String>(Arrays.asList(ddmmyyyyTodaysArray));

        for (ArrayList<String> foodexppair : foodlist) {
            //Log.d(TAG, todaysDate + " " + foodexppair.get(1));
            String[] ddmmyyyyArray = foodexppair.get(1).split("-");
            ArrayList<String> ddmmyyyyList = new ArrayList<String>(Arrays.asList(ddmmyyyyArray));
            if (Integer.parseInt(ddmmyyyyList.get(2)) < Integer.parseInt(ddmmyyyyTodaysList.get(2))
                    || Integer.parseInt(ddmmyyyyList.get(2)) == Integer.parseInt(ddmmyyyyTodaysList.get(2))
                    && Integer.parseInt(ddmmyyyyList.get(1)) < Integer.parseInt(ddmmyyyyTodaysList.get(1))
                    || Integer.parseInt(ddmmyyyyList.get(2)) == Integer.parseInt(ddmmyyyyTodaysList.get(2))
                    && Integer.parseInt(ddmmyyyyList.get(1)) == Integer.parseInt(ddmmyyyyTodaysList.get(1))
                    && Integer.parseInt(ddmmyyyyList.get(0)) < Integer.parseInt(ddmmyyyyTodaysList.get(0))) {
                Log.d(TAG,"Food has expired: "+ foodexppair);
                foodExpiredList.add(foodexppair);
                //mark red?
            }

            if (Integer.parseInt(ddmmyyyyList.get(2)) == Integer.parseInt(ddmmyyyyTodaysList.get(2))
                    && Integer.parseInt(ddmmyyyyList.get(1)) == Integer.parseInt(ddmmyyyyTodaysList.get(1))
                    && Integer.parseInt(ddmmyyyyList.get(0)) > Integer.parseInt(ddmmyyyyTodaysList.get(0))
                    && (Integer.parseInt(ddmmyyyyList.get(0)) - Integer.parseInt(ddmmyyyyTodaysList.get(0))) <= 2) {
                Log.d(TAG,"Food is about to expire " + foodexppair);
                foodExpiresList.add(foodexppair);
            }

        }
        return null;
    }


});

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(getActivity().getApplicationContext(),100, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60000, broadcast);

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_myfridge, container, false);

        return rootView;
    }

    public void removeGrocery(String groceryanddate) {
        String user = getUsername();
        String grocery;
        groceryanddate = groceryanddate.replace(" ", "");
        String[] groceryanddate2 = groceryanddate.split(":");
        grocery = groceryanddate2[0];
        //Log.d(TAG,"raw input " + groceryanddate + ", split "+ groceryanddate2 + ", grocery " + grocery);
        DatabaseReference remove = mDatabase.child("users").child(user).child("food items").child(grocery);
        remove.removeValue();
    }

    public void readData(final MyCallback myCallback) {
        Log.d(TAG, "Readdata has been called");

        String user = getUsername();
        getUsernameList();
        Log.d(TAG+"username readDb", user);

        //Log.d("tag123 usernamelist", shareList.toString());
        shareList.add(user);

        Log.d(TAG, "User: "+ user);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.d(TAG, "Read from database");
        if (shareList.size() == 1) {
            Log.d("tag123", "not sharing with anybody");
            String path = "/users/"+user+"/food items";
            Log.d(TAG, "Path: "+ path);
            DatabaseReference myRef = database.getReference(path);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("tag123", dataSnapshot.toString());
                        String value = dataSnapshot.getValue().toString();
                        Log.d(TAG, "Value readdata; "+value);
                        myCallback.onCallback(value);
                    } else {
                        Toast.makeText(getActivity(), "Your fridge is empty", Toast.LENGTH_SHORT).show();
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
        for (String username : shareList) {
            String path = "/users/"+username+"/food items";
            Log.d(TAG, "Path: "+ path);
            DatabaseReference myRef = database.getReference(path);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("tag123", dataSnapshot.toString());
                        String value = dataSnapshot.getValue().toString();
                        Log.d(TAG, "Value readdata; "+value);
                        myCallback.onCallback(value);
                    } else {
                        Toast.makeText(getActivity(), "Your fridge is empty", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    public void readShare(final MyCallback2 myCallback) {
        String user = getUsername();
        String path = "/users/"+user+"/share";
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG + "sharelist from db", dataSnapshot.getValue().toString());
                    String value = dataSnapshot.getValue().toString();
                    myCallback.onCallback(value);
                } else {
                    Toast.makeText(getActivity(), "You are not sharing fridge", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public interface MyCallback {
        void onCallback(String value);
        ArrayList<String> findExpiringFood(ArrayList<ArrayList<String>> foodlist);
    }

    public interface MyCallback2 {
        void onCallback(String value);
    }

    public String getUsername() {
        sharedpreferences = this.getActivity().getSharedPreferences("autoLogin", getActivity().getApplicationContext().MODE_PRIVATE);
        String j = sharedpreferences.getString("key",null);
        Log.d(TAG, "Username in pref " +j);
        return j;
    }

    public void getUsernameList() {
        /*
        sharedpreferences = this.getActivity().getSharedPreferences("ShareFridge", getActivity().getApplicationContext().MODE_PRIVATE);
        String j = sharedpreferences.getString("shareList",null);
        if (j == null) {
            return null;
        } else {
            Log.d("tag123 ulist bf con", j);
            String[] TempUsernames = j.replace("[","").replace("]","").split(",");
            ArrayList<String> usernames = new ArrayList<>(Arrays.asList(TempUsernames));
            return usernames;
        }
        */
        readShare(new MyCallback2() {
            @Override
            public void onCallback(String value) {
                Log.d(TAG+"in readshare",value);
                String[] tempusernames = value.split(",");
                ArrayList<String> shareListDB = new ArrayList<>(Arrays.asList(tempusernames));
            }
        });
    }
}