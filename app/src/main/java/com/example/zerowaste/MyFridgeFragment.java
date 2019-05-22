package com.example.zerowaste;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import android.app.AlertDialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyFridgeFragment extends Fragment {
    private DatabaseReference mDatabase;
    private ValueEventListener postListener;
    public String foods = "empty";
    ArrayAdapter<String> arrayAdapter;
    String TAG = "tag12346";

    public MyFridgeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();

        //may have to remove these lines?
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("users").child("ingvild").child("matvarer").child("bananas").setValue("10 mai");
        //goes into users, ingvild, matvarer, bananas og setter bananas med 10 mai
        //mDatabase.child("posts").child("author").setValue(new MainActivity.Post("ingvilds2","nilsson2"));
        //addGrocery("ost","15 mai");

        //foods = readDB();

        final List<String> dataList = new ArrayList<String>();
        final ListView listview = (ListView) getView().findViewById(R.id.listview);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, dataList);




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
                    dataList.add(food + ":  " + exp);
                }


                listview.setAdapter(arrayAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {


                        new AlertDialog.Builder(getActivity().getApplicationContext())
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
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_myfridge, container, false);

        return rootView;
    }

    public void removeGrocery(String groceryanddate) {
        String user = "ingvild"; //getUsername();
        String grocery;
        groceryanddate = groceryanddate.replace(" ", "");
        String[] groceryanddate2 = groceryanddate.split(":");
        grocery = groceryanddate2[0];
        //Log.d(TAG,"raw input " + groceryanddate + ", split "+ groceryanddate2 + ", grocery " + grocery);
        DatabaseReference remove = mDatabase.child("users").child(user).child("matvarer").child(grocery);
        remove.removeValue();
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