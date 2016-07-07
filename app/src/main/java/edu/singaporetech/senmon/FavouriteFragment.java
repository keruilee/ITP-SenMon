package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {
    ListView listViewListing ;
    private FavouriteDatabaseHelper databaseHelper;
    public DatabaseHelper mydatabaseHelper ;


    CustomAdapter adapter;

    ArrayList<Machine> myFavouriteMachineList = new ArrayList<Machine>();


    private static final String TAG_RESULTS="result";
    ProgressDialog progressDialog;
    JSONArray serverCSVrecords = null;

    public String[] latestRecords;
    public String[] allCSVRecords;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFavouriteMachineList.clear();
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);


        mydatabaseHelper = new DatabaseHelper(getActivity());
        Cursor c = FavouriteList();
        String statusForFavo;

        if (c.moveToFirst()) {
            do {
                statusForFavo = c.getString(c.getColumnIndex("machineFavouriteStatus"));
                if (statusForFavo != null) {
                    Log.i("stats", statusForFavo);
                    Machine machineFavourite = new Machine(c.getString(1), c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                            c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
                    myFavouriteMachineList.add(machineFavourite);
                }
            } while (c.moveToNext());

        }c.close();


        listViewListing = (ListView) rootView.findViewById(R.id.ListView);
        adapter = new CustomAdapter(getActivity(),R.layout.fragment_favourite,myFavouriteMachineList);
        listViewListing.setAdapter(adapter);

        ////// when click on the item   //////////////////////
        listViewListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup viewgrp = (ViewGroup) view;
                TextView intentMachineID = (TextView) viewgrp.findViewById(R.id.textViewmachineid);
                TextView intentTemp = (TextView) viewgrp.findViewById(R.id.textViewTemp);
                TextView intentVelo = (TextView) viewgrp.findViewById(R.id.textViewVelocity);

                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle2 = new Bundle();
                bundle2.putString("name", intentMachineID.getText().toString());
                bundle2.putString("temp", intentTemp.getText().toString());
                bundle2.putString("velo", intentVelo.getText().toString());
                details.setArguments(bundle2); //data being send to MachineListFragment
                //Edited by kerui
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
        // / return inflater.inflate(R.layout.fragment_list, container, false);
    }
    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();
        myFavouriteMachineList.clear();
        Cursor c = FavouriteList();
        String statusForFavo;

        if (c.moveToFirst()) {
            do {
                statusForFavo = c.getString(c.getColumnIndex("machineFavouriteStatus"));
                if (statusForFavo != null) {
                    Log.i("stats", statusForFavo);
                    Machine machineFavourite = new Machine(c.getString(1), c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                            c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
                    myFavouriteMachineList.add(machineFavourite);
                }
            } while (c.moveToNext());

        }c.close();


    }
    public Cursor FavouriteList() {
        // to return all records in the form of a Cursor object
        SQLiteDatabase db = mydatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + mydatabaseHelper.getTableName(), null);

        return cursor;
    }
}