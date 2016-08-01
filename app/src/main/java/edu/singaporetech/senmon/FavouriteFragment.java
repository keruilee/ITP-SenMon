package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment implements WebService.OnAsyncRequestComplete {
    ListView listViewListing ;
    //private FavouriteDatabaseHelper databaseHelper;
    public DatabaseHelper mydatabaseHelper ;

    public Context context;
    SharedPreferences DateTimeSharedPreferences;
    String dateTime;
    int numberOfFavInAlert = 0;

    public static final String MyPREFERENCES = "MyPrefs" ;

    public static final String NumberOfFavourite = "numOfFav";

    public static final String DETAILS_FRAG_TAG = "DETAILS_FRAGMENT";

    CustomAdapter adapter;

    ArrayList<Machine> myFavouriteMachineList = new ArrayList<Machine>(), tempMachineList = new ArrayList<>();
    private static final String TAG_RESULTS="result";
    ProgressDialog progressDialog;

    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";

    // for swipe
    public SwipeRefreshLayout mSwipeRefreshLayout = null;

    // for date time
    TextView updateDateTime;

    IntentFilter inF = new IntentFilter("database_updated");

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

        DateTimeSharedPreferences = context.getSharedPreferences("DT_PREFS_NAME", Context.MODE_PRIVATE);

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

                        getSQLData();
                        mSwipeRefreshLayout.setRefreshing(false);

            }
        });

        updateList();

        return rootView;
        // / return inflater.inflate(R.layout.fragment_list, container, false);
    }

    ////////////////////////update the list///////////////////////////
    /**
     * start async WebService task to retrieve records from server's database
     */
    public void getSQLData() {
        WebService webServiceTask = new WebService(context, this);
        webServiceTask.execute();
    }

    // async task completed
    @Override
    public void asyncResponse() {
        updateList();

        // for swipe refresh to dismiss the loading icon
        mSwipeRefreshLayout.setRefreshing(false);
        // display the date time
    }

    // update datetime text
    private void setUpdateDateTime() {
        if (myFavouriteMachineList.isEmpty())
        {
            updateDateTime.setText("No results found");
        }
        else
        {
            dateTime = DateTimeSharedPreferences.getString("DT_PREFS_KEY", null);
            updateDateTime.setText("Updated on "+dateTime);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //unregister the receiver
        LocalBroadcastManager.getInstance(context).unregisterReceiver(dataChangeReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //register the receiver

        LocalBroadcastManager.getInstance(context).registerReceiver(dataChangeReceiver, inF);
    }

    private BroadcastReceiver dataChangeReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update your listview
            Log.d("BROADCAST FAV", "YES!");

            updateList();

            // for swipe refresh to dismiss the loading icon
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    /**
     * update list displayed on screen with new data
     */
    private void updateList() {
        //get the data from database
        myFavouriteMachineList = mydatabaseHelper.returnFavourite();

        tempMachineList = myFavouriteMachineList;
        adapter.clear();
        adapter.addAll(tempMachineList);
        myFavouriteMachineList = tempMachineList;

        setUpdateDateTime();
    }
}