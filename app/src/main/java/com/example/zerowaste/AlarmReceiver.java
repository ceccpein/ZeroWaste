package com.example.zerowaste;

import android.app.Activity;
import android.app.AliasActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class AlarmReceiver extends BroadcastReceiver {

    public String InAlarmReceiver = "In alarmreceiver";
    SharedPreferences sharedPreferences;
    String user = "";

    MyFridgeFragment fridgeFragment = new MyFridgeFragment();
    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("autoLogin", context.MODE_PRIVATE);
        user = sharedPreferences.getString("key", null);

        Log.d(InAlarmReceiver, "Beginning of alarmreceiver, 1");

        final ArrayList<ArrayList<String>> foodExpiredList = new ArrayList<ArrayList<String>>();
        final ArrayList<ArrayList<String>> foodExpiresList = new ArrayList<ArrayList<String>>();
        final ArrayList<ArrayList<String>> foodExpList = new ArrayList<ArrayList<String>>();


        Intent notificationIntent = new Intent(context, MainActivity.class);
        Log.d("tag1234", "In AlarmReceiver");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        Log.d("tag1234", "after stackbuilder");
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        final Notification notificationsd = builder.setContentTitle("Food is about to or has expired!")
                .setContentText("One or more food items is about to expire or has expired.")
                .setTicker("")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        final Notification notifications = builder.setContentTitle("Food is about to expire!")
                .setContentText("One or more food items is about to expire.")
                .setTicker("")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        final Notification notificationd = builder.setContentTitle("Food has expired!")
                .setContentText("One or more food items has expired.")
                .setTicker("")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        final NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);


        readData(new MyCallback2() {
            @Override
            public void onCallback2(String value) {
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
                }
                Log.d(InAlarmReceiver, "in readdata");
                findExpiringFood(foodExpList);

                if (foodExpiredList.size() > 0 && foodExpiresList.size() > 0) {
                    notificationManager.notify(0, notificationsd);
                }
                else if (foodExpiredList.size() > 0) {
                    notificationManager.notify(0, notificationd);
                }
                else if (foodExpiresList.size() > 0) {
                    notificationManager.notify(0, notifications);
                }

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

    }


    public void readData(final MyCallback2 myCallback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String path = "/users/"+user+"/food items/";
        DatabaseReference myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String value = dataSnapshot.getValue().toString();
                    myCallback.onCallback2(value);
                } else {
                    Log.d("tag123", "empty datasnapshot");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public interface MyCallback2 {
        void onCallback2(String value);
        ArrayList<String> findExpiringFood(ArrayList<ArrayList<String>> foodlist);
    }


}