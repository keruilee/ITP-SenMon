package edu.singaporetech.senmon;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements WebService.OnAsyncRequestComplete{

    String TAG = "List Fragment";
    Context context;

    SharedPreferences DateTimeSharedPreferences;
    TextView title ;

    public static final String DETAILS_FRAG_TAG = "DETAILS_FRAGMENT";
    private static final String TAG_RESULTS = "result";

    ListView listViewListing;
    CustomAdapter adapter;

    public ArrayList<Machine> myMachineList = new ArrayList<>(), tempMachineList = new ArrayList<>();

    public String status = "";

    private int selectedTab = 0;

    // for swipe
    public SwipeRefreshLayout mSwipeRefreshLayout = null;

    // for date time
    TextView updateDateTime;
    String dateTime;

    //For the sorting tab
    private TabLayout tabLayout;

    // for database helper
    public DatabaseHelper mydatabaseHelper;

    IntentFilter inF = new IntentFilter("database_updated");

    AlertDialog networkDialog;

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Give the TabLayout the ViewPage
        tabLayout = (TabLayout) rootView.findViewById(R.id.list_tabs);
        title =(TextView) rootView.findViewById(R.id.title);

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        updateDateTime = (TextView) rootView.findViewById(R.id.textViewUpdateDateTime);

        //retrieving data using bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            //Log.i(TAG + " Machine Name ", String.valueOf(bundle.getStringArrayList("name")));
            //machineArray.add(String.valueOf(bundle.getString("name")));
            status = String.valueOf(bundle.getString("name"));
            Log.i("TEST", status);
            title.setText("Machines (" +status +")");
        }

        ////////////////////////Retrived datatime sharef pref///////////////////////
        DateTimeSharedPreferences = context.getSharedPreferences("DT_PREFS_NAME", Context.MODE_PRIVATE);

        /////////////////// take out the data from databasehelper///////////////////////
        mydatabaseHelper = new DatabaseHelper(getActivity());

        // set up list with listadapter
        listViewListing = (ListView) rootView.findViewById(R.id.ListView);
        adapter = new CustomAdapter(getActivity(), R.layout.fragment_list, myMachineList);
        listViewListing.setAdapter(adapter);

        //////////////////swipe///////////////
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("REFRESH", "what bundle?" + status);
                //If there is network connection
                if(isNetworkEnabled())
                {
                    if(networkDialog != null && networkDialog.isShowing())
                        networkDialog.dismiss();

                    getSQLData();
                }
                else {
                    if(networkDialog != null && networkDialog.isShowing()) return;

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Network Connectivity");
                    builder.setMessage("No network detected! Data will not be updated!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // You don't have to do anything here if you just want it dismissed when clicked
                        }
                    });
                    networkDialog = builder.create();
                    networkDialog.show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                Log.i("REFRESH", "what bundle? After" + status);
            }
        });

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
                Bundle bundle = new Bundle();
                bundle.putString("name", intentMachineID.getText().toString());
                details.setArguments(bundle); //data being send to MachineListFragment
                //Edited by kerui
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.relativelayoutfor_fragment, details, DETAILS_FRAG_TAG);
                transaction.addToBackStack(DETAILS_FRAG_TAG);
                transaction.commit();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tabLayout.getSelectedTabPosition();
                updateList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        retrieveFromDatabase();
        updateList();

        return rootView;
    }

    ///////// To update the tab when the user select///////////////
    public void updateList() {
        tempMachineList = myMachineList;
        switch (selectedTab) {
            case 0:
                Log.i("SWITCH", "0 , Sort machine id");
                Collections.sort(tempMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return m1.getMachineID().compareTo(m2.getMachineID());
                    }
                });
                break;

            case 1:
                Log.i("SWITCH", "1 , Sort temp");
                Collections.sort(tempMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        Log.i("SWITCH", "1 , Sort temp m1" + m1.getmachineTemp());
                        Log.i("SWITCH", "1 , Sort temp m2" + m2.getmachineTemp());
                        return Double.compare(Double.parseDouble(m2.getmachineTemp()), Double.parseDouble(m1.getmachineTemp()));
                    }
                });
                Log.i("Sort", "Sorting temp done");
                break;

            case 2:
                Log.i("SWITCH", "2 , Sort velo");
                Collections.sort(tempMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return Double.compare(Double.parseDouble(m2.getmachineVelo()), Double.parseDouble(m1.getmachineVelo()));
                    }
                });
                break;

            default:
                break;
        }
        adapter.clear();
        adapter.addAll(tempMachineList);
        myMachineList = tempMachineList;
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

        //getSQLRecords(response);        // updates database with retrieved CSV records
        retrieveFromDatabase();
        updateList();                   // display list with sorted values

        // for swipe refresh to dismiss the loading icon
        mSwipeRefreshLayout.setRefreshing(false);
        // display the date time
    }

    //Computation of machines in each state
    private void setUpdateDateTime() {
        if (myMachineList.isEmpty())
        {
            updateDateTime.setText("No machine found in the list!");
        }
        else
        {
            dateTime = DateTimeSharedPreferences.getString("DT_PREFS_KEY", null);
            updateDateTime.setText("Updated on "+dateTime);
        }
    }

    private void retrieveFromDatabase() {
        if (status.equalsIgnoreCase("all")) {
            Log.d("LF All", "all");
            myMachineList = mydatabaseHelper.returnStringMachineAllString();
        } else {
            Log.d("All", "not all");
            myMachineList = mydatabaseHelper.returnStringMachineStateString(status);
        }
        setUpdateDateTime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            retrieveFromDatabase();
            updateList();                   // display list with sorted values

            Log.d("BROADCAST LIST", "YES!");
        }
    };

    public boolean isNetworkEnabled(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
