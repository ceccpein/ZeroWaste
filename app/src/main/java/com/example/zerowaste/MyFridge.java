package com.example.zerowaste;

import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;




public class MyFridge extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ValueEventListener postListener;
    public String foods = "empty";
    ArrayAdapter<String> arrayAdapter;
    String TAG = "tag12346";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);


        //may have to remove these lines?
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("users").child("ingvild").child("matvarer").child("bananas").setValue("10 mai");
        //goes into users, ingvild, matvarer, bananas og setter bananas med 10 mai
        //mDatabase.child("posts").child("author").setValue(new MainActivity.Post("ingvilds2","nilsson2"));
        //addGrocery("ost","15 mai");

        //foods = readDB();

        final List<String> dataList = new ArrayList<String>();
        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);

        addGrocery("melk","10 juni");
        addGrocery("melk1","10 juni");
        addGrocery("melk2","10 juni");
        addGrocery("melk3","10 juni");
        addGrocery("melk4","10 juni");
        addGrocery("mel5","10 juni");
        addGrocery("mel6","10 juni");
        addGrocery("melk7","10 juni");
        addGrocery("melk8","10 juni");



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
                    dataList.add(food + "  :  " + exp);
                }


                listview.setAdapter(arrayAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listview.removeViewAt(position);
                    }
                });
            }
        });


    }


    public ArrayList<String> addFoodToListView(String list) {
        ArrayList<String> returnlist = null;
        String[] allItems1 = list.split(",");
        ArrayList<String> allItems = new ArrayList<String>(Arrays.asList(allItems1));
        for (String item : allItems) {
            String[] foodexp = item.split("=");
            String food = foodexp[0];
            String exp = foodexp[1];
            returnlist.add(food + "  :  " + exp);

        }
        return returnlist;
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



    public interface MyCallback {
        void onCallback(String value);
    }

}




