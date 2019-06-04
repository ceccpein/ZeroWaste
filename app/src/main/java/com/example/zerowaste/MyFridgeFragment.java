package com.example.zerowaste;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import static android.graphics.Color.rgb;


public class MyFridgeFragment extends Fragment {

    private DatabaseReference mDatabase;
    private static Button addfood;

    ArrayList<String> shareList;
    List<String> dataList;
    SharedPreferences sharedpreferences;
    ArrayAdapter<String> arrayAdapter;

    ListView listview;


    static final ArrayList<ArrayList<String>> foodExpiredList = new ArrayList<>();
    final ArrayList<ArrayList<String>> foodExpiresList = new ArrayList<>();

    public MyFridgeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){ super.onCreate(savedInstanceState); }


    @Override
    public void onStart() {
        super.onStart();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addfood = (getView().findViewById(R.id.add_item));

        addfood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity().getApplicationContext(), addFood.class);
                startActivity(newIntent);
            }
        });

        dataList = new ArrayList<String>();
        listview = (ListView) getView().findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.custom_foodlist, dataList);
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
                /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                        TextView myMsg = new TextView(getActivity());
                        myMsg.setText("Are you sure you want to delete "+ dataList.get(position) +"?");
                        myMsg.setTextSize(20);

                        new AlertDialog.Builder(getActivity())
                                .setCustomTitle(myMsg)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        removeGrocery(dataList.get(position));
                                        dataList.remove(position);
                                        listview.setAdapter(arrayAdapter);
                                    }
                                })

                                .setNegativeButton("No",null)
                                .create()
                                .show();

                    }
                });*/
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                        removeGrocery(dataList.get(position),position);
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
                    String[] ddmmyyyyArray = foodexppair.get(1).split("-");
                    ArrayList<String> ddmmyyyyList = new ArrayList<String>(Arrays.asList(ddmmyyyyArray));
                    if (Integer.parseInt(ddmmyyyyList.get(2)) < Integer.parseInt(ddmmyyyyTodaysList.get(2))
                            || Integer.parseInt(ddmmyyyyList.get(2)) == Integer.parseInt(ddmmyyyyTodaysList.get(2))
                            && Integer.parseInt(ddmmyyyyList.get(1)) < Integer.parseInt(ddmmyyyyTodaysList.get(1))
                            || Integer.parseInt(ddmmyyyyList.get(2)) == Integer.parseInt(ddmmyyyyTodaysList.get(2))
                            && Integer.parseInt(ddmmyyyyList.get(1)) == Integer.parseInt(ddmmyyyyTodaysList.get(1))
                            && Integer.parseInt(ddmmyyyyList.get(0)) < Integer.parseInt(ddmmyyyyTodaysList.get(0))) {
                        foodExpiredList.add(foodexppair);
                        //mark red?
                    }

                    if (Integer.parseInt(ddmmyyyyList.get(2)) == Integer.parseInt(ddmmyyyyTodaysList.get(2))
                            && Integer.parseInt(ddmmyyyyList.get(1)) == Integer.parseInt(ddmmyyyyTodaysList.get(1))
                            && Integer.parseInt(ddmmyyyyList.get(0)) > Integer.parseInt(ddmmyyyyTodaysList.get(0))
                            && (Integer.parseInt(ddmmyyyyList.get(0)) - Integer.parseInt(ddmmyyyyTodaysList.get(0))) <= 2) {
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

    public void removeGrocery(String groceryanddate, final int pos) {

        String user = getUsername();
        String grocery;
        String date;
        groceryanddate = groceryanddate.replace(" ", "");
        String[] groceryanddate2 = groceryanddate.split(":");
        grocery = groceryanddate2[0];
        date = groceryanddate2[1];
        final Boolean booleanValue = false;
        readFood(new MyCallback2() {
            @Override
            public void onCallback2(Boolean value, final String groc) {
                if (value) {
                    TextView myMsg = new TextView(getActivity());
                    myMsg.setText("Are you sure you want to delete "+ groc +"?");
                    myMsg.setTextSize(20);

                    new AlertDialog.Builder(getActivity())
                            .setCustomTitle(myMsg)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mDatabase.child("users").child(getUsername()).child("food items").child(groc).removeValue();
                                    dataList.remove(pos);
                                    listview.setAdapter(arrayAdapter);
                                }
                            })

                            .setNegativeButton("No",null)
                            .create()
                            .show();


                } else {
                    Toast.makeText(getActivity(), "This does not belong to you", Toast.LENGTH_SHORT).show();
                    Log.d("tag123", "This is not in your fridge");
                }
            }
        }, grocery,date);

    }

    public void readData(final MyCallback myCallback) {

        String user = getUsername();
        shareList = getUsernameList();

        shareList.add(user);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        for (String username : shareList) {
            final String thisuser = username;
            String path = "/users/"+username+"/food items";
            DatabaseReference myRef = database.getReference(path);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String value = dataSnapshot.getValue().toString();
                        myCallback.onCallback(value);
                    } else {
                        if (shareList.contains(thisuser) && shareList.size() == 1) {
                            Toast.makeText(getActivity(), "Your fridge is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    public void readFood(final MyCallback2 myCallback, final String grocery, final String date) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final Boolean[] exist = {false};

        String path = "/users/"+getUsername()+"/food items";
        DatabaseReference myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groc = grocery;
                String grocDate = date;
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                        if (foodSnapshot.getKey().equals(groc) && foodSnapshot.getValue().equals(grocDate)) {
                            //mDatabase.child("users").child(thisuser).child("food items").child(groc).removeValue();
                            exist[0] = true;
                        }
                    }

                } myCallback.onCallback2(exist[0], groc);
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
        void onCallback2(Boolean value, String groc);
    }

    public String getUsername() {
        sharedpreferences = this.getActivity().getSharedPreferences("autoLogin", getActivity().getApplicationContext().MODE_PRIVATE);
        String j = sharedpreferences.getString("key",null);
        return j;
    }

    public ArrayList<String> getUsernameList() {
        sharedpreferences = this.getActivity().getSharedPreferences("ShareFridge", getActivity().getApplicationContext().MODE_PRIVATE);
        String j = sharedpreferences.getString(getUsername(), null);
        if (j == null) {
            return new ArrayList<>();
        } else {
            String[] TempUsernames = j.replace("[", "").replace("]", "").split(",");
            ArrayList<String> usernames = new ArrayList<>(Arrays.asList(TempUsernames));
            return usernames;
        }
    }

}