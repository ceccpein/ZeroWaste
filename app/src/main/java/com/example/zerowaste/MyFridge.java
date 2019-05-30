package com.example.zerowaste;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;
import android.app.AlertDialog;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.util.Pair;





public class MyFridge extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ValueEventListener postListener;
    public String foods = "empty";
    ArrayAdapter<String> arrayAdapter;
    String TAG = "tag12346";

    static final ArrayList<ArrayList<String>> foodExpiredList = new ArrayList<ArrayList<String>>();
    final ArrayList<ArrayList<String>> foodExpiresList = new ArrayList<ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);

        /*
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,5);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(this,100, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);*/






        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("users").child("ingvild").child("matvarer").child("bananas").setValue("10 mai");
        //goes into users, ingvild, matvarer, bananas og setter bananas med 10 mai
        //mDatabase.child("posts").child("author").setValue(new MainActivity.Post("ingvilds2","nilsson2"));
        //addGrocery("ost","15 mai");

        //foods = readDB();

        final List<String> dataList = new ArrayList<String>();
        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);

        final ArrayList<ArrayList<String>> foodExpList = new ArrayList<ArrayList<String>>();

        String pattern = "dd-M-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        Log.d(TAG,"here is the date: " + date);




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


                        new AlertDialog.Builder(MyFridge.this)
                                .setTitle("Are you sure you want to delete"+ dataList.get(position) +"?")
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

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(this,100, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        int intToSend = foodExpiredList.size() + foodExpiresList.size();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60000, broadcast);
        Log.d("START123","hereherehere");

    }


    public void removeGrocery(String groceryanddate) {
        String user = "ingvild"; //getUsername();
        String grocery;
        groceryanddate = groceryanddate.replace(" ", "");
        String[] groceryanddate2 = groceryanddate.split(":");
        grocery = groceryanddate2[0];
        Log.d(TAG,"raw input " + groceryanddate + ", split "+ groceryanddate2 + ", grocery " + grocery);
        DatabaseReference remove = mDatabase.child("users").child(user).child("matvarer").child(grocery);
        remove.removeValue();
    }



    public void addGrocery(String grocery, String date) {
        String user = "ingvild"; //getUsername();
        mDatabase.child("users").child(user).child("matvarer").child(grocery).setValue(date);
    }




    public String readDB() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users/ingvild/matvarer");
        final String s = "unitialized";
        // Attach a listener to read the data at our posts reference
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                    //Log.d(TAG, dataSnapshot.getKey().toString());
                    String s = dataSnapshot.getValue().toString();
                    foods = s;
                    Log.d(TAG,"here is foods2"+s);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    return s;
    }


    public void readData(final MyCallback myCallback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users/ingvild/matvarer");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                myCallback.onCallback(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public void notifier() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("Notifications NEW!!!!!")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this,MyFridge.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }

    public void alarm() {
    }


    Boolean checkLists() {

        if (foodExpiredList.size() != 0 || foodExpiresList.size() != 0) {
            Log.d(TAG,"set of notification");
            //notifier();
            return true;
        }
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        /*AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,10);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(this,100, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        int intToSend = foodExpiredList.size() + foodExpiresList.size();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);*/

        //put in oncreate

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public interface MyCallback {
        void onCallback(String value);
        ArrayList<String> findExpiringFood(ArrayList<ArrayList<String>> foodlist);
    }

}




