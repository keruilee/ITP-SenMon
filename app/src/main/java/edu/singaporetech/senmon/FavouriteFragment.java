package edu.singaporetech.senmon;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment implements WebService.OnAsyncRequestComplete {
    ListView listViewListing ;
    //private FavouriteDatabaseHelper databaseHelper;
    public DatabaseHelper mydatabaseHelper ;

    public Context context;
    SharedPreferences DateTimeSharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    String dateTime;
    int numberOfFavInAlert = 0;

    public static final String MyPREFERENCES = "MyPrefs" ;

    public static final String NumberOfFavourite = "numOfFav";

    public static final String DETAILS_FRAG_TAG = "DETAILS_FRAGMENT";

    CustomAdapter adapter;

    ArrayList<Machine> myFavouriteMachineList = new ArrayList<Machine>();
    ArrayList<Machine> myTempoMachineList = new ArrayList<Machine>();
    private static final String TAG_RESULTS="result";
    ProgressDialog progressDialog;
    JSONArray serverSQLRecords = null;
    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";
    public String[] latestRecords;
    public String[] allSQLRecords;

    // for swipe
    public SwipeRefreshLayout mSwipeRefreshLayout = null;

    // for date time
    TextView updateDateTime;


    public FavouriteFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //myFavouriteMachineList.clear();
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        context = getContext();
        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        updateDateTime= (TextView) rootView.findViewById(R.id.textViewUpdateDateTime);
        listViewListing = (ListView) rootView.findViewById(R.id.ListView);


        /////////////////retrieve range values ////////////
        RangeSharedPreferences = context.getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display it
        tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_warning_value))));
        tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_critical_value))));
        veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_warning_value))));
        veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_critical_value))));


        mydatabaseHelper = new DatabaseHelper(getActivity());
        myFavouriteMachineList = mydatabaseHelper.returnFavourite();


        DateTimeSharedPreferences = context.getSharedPreferences("DT_PREFS_NAME", Context.MODE_PRIVATE);
        dateTime = DateTimeSharedPreferences.getString("DT_PREFS_KEY", null);
        if (myFavouriteMachineList.isEmpty())
        {
            updateDateTime.setText("No results found");
        }
        else
        {
            updateDateTime.setText("Updated on :"+dateTime);
        }
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
                transaction.replace(R.id.relativelayoutfor_fragment, details, DETAILS_FRAG_TAG);
                transaction.addToBackStack(DETAILS_FRAG_TAG);
                transaction.commit();
            }
        });
        //////////////////swipe///////////////
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("REFRESH", "onRefresh called from SwipeRefreshLayout");
                        progressDialog = new ProgressDialog(getActivity());

                        if(isNetworkEnabled()){
                            getSQLData();
                        }
                        else{
                            mSwipeRefreshLayout.setRefreshing(false);
                            // Use the Builder class for convenient dialog construction
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Network Connectivity");
                            builder.setMessage("No network detected! Data will not be updated!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // You don't have to do anything here if you just want it dismissed when clicked
                                }
                            });
                            AlertDialog networkDialog = builder.create();
                            networkDialog.show();
                        }

            }
        });


        return rootView;
        // / return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of FAV");
        super.onResume();
    }

    ////////////////////////update the list///////////////////////////

    public void getSQLData() {
        WebService webServiceTask = new WebService(context, this);
        webServiceTask.execute();
    }

    // async task of getting SQL records from server completed
    @Override
    public void asyncResponse(JSONObject response) {

        getSQLRecords(response);
        // display list with sorted values
        progressDialog.dismiss();
        computeMachineState();
        // for swipe refresh to dismiss the loading icon
        mSwipeRefreshLayout.setRefreshing(false);
        // display the date time
    }

    //Get the server CSV records
    public void getSQLRecords(JSONObject jsonObj) {
        try {
            serverSQLRecords = jsonObj.getJSONArray(TAG_RESULTS);
            myFavouriteMachineList.clear();

            String cleanupLatestRecords;
            //remove all unwanted symbols and text
            cleanupLatestRecords = serverSQLRecords.toString().replaceAll(",false]]", "").replace("[[", "").replace("[", "").replace("]]", "").replace("\"", "").replace("]", "");
            //split different csv records, the ending of each csv record list is machineID.csv
            allSQLRecords = cleanupLatestRecords.split("split,");
            //loop through each csv and get the latest records and split each field
            for (String record : allSQLRecords) {
                latestRecords = record.split(",");
                Log.e("latestRecords", record);
                Machine machine = new Machine(latestRecords[0],latestRecords[1],latestRecords[2],latestRecords[3],latestRecords[4],latestRecords[5],
                        latestRecords[6],latestRecords[7],latestRecords[8],latestRecords[9],"0","","");

                myTempoMachineList.add(machine);
                //Change database
                mydatabaseHelper.changeDatabase(latestRecords[0], latestRecords[1], latestRecords[2], latestRecords[3], latestRecords[4], latestRecords[5],
                        latestRecords[6], latestRecords[7], latestRecords[8], latestRecords[9]);
                //mydatabaseHelper.updateMachineDateTime(latestRecords[0], DateFormat.getDateTimeInstance().format(new Date()));


            }

            Log.d("cleanupLatestRecords: ", cleanupLatestRecords);
            Log.d("CSVRecords2: ", allSQLRecords[1]);
            Log.d("LatestRecords: ", latestRecords[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        myFavouriteMachineList.clear();
        Cursor c = FavouriteList();
        String statusForFavo;

        editor = DateTimeSharedPreferences.edit();
        editor.putString("DT_PREFS_KEY", DateFormat.getDateTimeInstance().format(new Date()));

        editor.commit();
        dateTime = DateTimeSharedPreferences.getString("DT_PREFS_KEY", null);
        updateDateTime.setText("Updated on :" + dateTime);


        if (c.moveToFirst()) {
            do {
                statusForFavo = c.getString(c.getColumnIndex("machineFavouriteStatus"));
                if (statusForFavo != null) {
                    Log.i("stats", statusForFavo);
                    Machine machineFavourite = new Machine(c.getString(1), c.getString(2), c.getString(3),
                            c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                            c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
                    Log.i("FAVOURITE,STATUES" , " " + c.getString(12));
                    Log.i("FAVOURITE,STATUES" , " " + c.getString(13));
                    myFavouriteMachineList.add(machineFavourite);

                }
            } while (c.moveToNext());
        }
        updateMachineState(myFavouriteMachineList);
    }

    public Cursor FavouriteList() {
        // to return all records in the form of a Cursor object
        SQLiteDatabase db = mydatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + mydatabaseHelper.getTableName(), null);

        return cursor;

    }
    //Computation of machines in each state
    private void computeMachineState() {
        editor = DateTimeSharedPreferences.edit();
        editor.putString("DT_PREFS_KEY", DateFormat.getDateTimeInstance().format(new Date()));

        editor.commit();
        dateTime = DateTimeSharedPreferences.getString("DT_PREFS_KEY", null);
        updateDateTime.setText("Updated on :"+dateTime);
        Log.d(" computeMachine", "testing");
        updateMachineState(myTempoMachineList);

    }
    public void updateMachineState(ArrayList<Machine> list){
        Double machineTemp, machineVelo;
        for(Machine machine : list)
        {
            machineTemp = Double.parseDouble(machine.getmachineTemp());
            machineVelo = Double.parseDouble(machine.getmachineVelo());
            //normal machine
            if(machineTemp < Double.parseDouble(tempWarningValue) && machineVelo < Double.parseDouble(veloWarningValue))
            {
                // both temp and velo is less than warning value = machine is in normal status
                mydatabaseHelper.updateMachineState(machine.getMachineID(), getString(R.string.status_normal));
            }
            else if (machineTemp >= Double.parseDouble(tempCriticalValue) || machineVelo >= Double.parseDouble(veloCriticalValue))
            {
                // either temp/velo is in critical range = machine is in critical status
                mydatabaseHelper.updateMachineState(machine.getMachineID(), getString(R.string.status_critical));
            }
            else
            {
                // machine is not in normal or critical status, so machine is in warning status
                mydatabaseHelper.updateMachineState(machine.getMachineID(), getString(R.string.status_warning));
            }
        }
    }


    public boolean isNetworkEnabled(){
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            //Network available
            return true;
        }
        else {
            return false;
        }
    }
}