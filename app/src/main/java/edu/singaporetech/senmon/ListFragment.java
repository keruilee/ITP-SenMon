package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;

import static java.lang.String.CASE_INSENSITIVE_ORDER;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    String TAG = "List Fragment";
    Context context;
    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    SharedPreferences DateTimeSharedPreferences;
    SharedPreferences.Editor editor;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";


    private static final String TAG_RESULTS = "result";

    ListView listViewListing;
    CustomAdapter adapter;

    public ArrayList<Machine> myMachineList = new ArrayList<Machine>();
    public ArrayList<Machine> myTempoMachineList = new ArrayList<Machine>();
    //public ArrayList<String> machineArray = new ArrayList<String>();
    public String status = "";

    ProgressDialog progressDialog;
    JSONArray serverCSVrecords = null;

    public String[] latestRecords;
    public String[] allCSVRecords;

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

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Give the TabLayout the ViewPage
        tabLayout = (TabLayout) rootView.findViewById(R.id.list_tabs);

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
        }

        /////////////////retrieve range values ////////////
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display it
        tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_warning_value))));
        tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_critical_value))));
        veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_warning_value))));
        veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_critical_value))));

        ////////////////////////Retrived datatime sharef pref///////////////////////

        DateTimeSharedPreferences = getContext().getSharedPreferences("DT_PREFS_NAME", Context.MODE_PRIVATE);
        dateTime = DateTimeSharedPreferences.getString("DT_PREFS_KEY", null);
        updateDateTime.setText("Updated on :"+dateTime);


        /////////////////// take out the data from databasehelper///////////////////////
        mydatabaseHelper = new DatabaseHelper(getActivity());
        myMachineList.clear();
        if (status.equalsIgnoreCase("all")) {
            Log.d("LF All", "all");
            myMachineList = mydatabaseHelper.returnStringMachineAllString();
        } else {
            Log.d("All", "not all");
            myMachineList = mydatabaseHelper.returnStringMachineStateString(status);
        }

        for (Machine m : myMachineList) {
            Log.d("hahaah?", m.getMachineID());
        }

        // set up list with listadapter
        listViewListing = (ListView) rootView.findViewById(R.id.ListView);
        adapter = new CustomAdapter(getActivity(), R.layout.fragment_list, myMachineList);
        listViewListing.setAdapter(adapter);

        //////////////////swipe///////////////
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("REFRESH", "onRefresh called from SwipeRefreshLayout");
                Log.i("REFRESH", "what bundle?" + status);
                progressDialog = new ProgressDialog(getActivity());
                getCSVData();
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
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TAG", "on tab selected");
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


        return rootView;
    }


    ///////// To update the tab when the user select///////////////
    public void updateList() {
        switch (selectedTab) {
            case 0:
                Log.i("SWITCH", "0 , Sort machine id");
                Collections.sort(myMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return m1.getMachineID().compareTo(m2.getMachineID());
                    }
                });
                break;

            case 1:
                Log.i("SWITCH", "1 , Sort temp");
                Collections.sort(myMachineList, new Comparator<Machine>() {
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
                Collections.sort(myMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return Double.compare(Double.parseDouble(m2.getmachineVelo()), Double.parseDouble(m1.getmachineVelo()));
                    }
                });
                break;

            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }


    ////////////////////////update the list///////////////////////////

    public void getCSVData() {
        class GetCSVDataJSON extends AsyncTask<Void, Void, JSONObject> {

            URL encodedUrl;
            HttpURLConnection urlConnection = null;

            String url = "http://itpsenmon.net23.net/readFromCSV.php";

            JSONObject responseObj;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Loading Records...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    encodedUrl = new URL(url);
                    urlConnection = (HttpURLConnection) encodedUrl.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.connect();

                    InputStream input = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("doInBackground(Resp)", result.toString());
                    responseObj = new JSONObject(result.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return responseObj;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                getCSVRecords(result);
                computeMachineState();
                updateList();                   // display list with sorted values
                progressDialog.dismiss();

                // for swipe refresh to dismiss the loading icon
                mSwipeRefreshLayout.setRefreshing(false);
                // display the date time
            }
        }
        GetCSVDataJSON g = new GetCSVDataJSON();
        g.execute();
    }

    //Get the server CSV records
    public void getCSVRecords(JSONObject jsonObj) {
        try {
            serverCSVrecords = jsonObj.getJSONArray(TAG_RESULTS);
            myTempoMachineList.clear();


            String cleanupLatestRecords;
            //remove all unwanted symbols and text
            cleanupLatestRecords = serverCSVrecords.toString().replaceAll(",false]]", "").replace("[[", "").replace("[", "").replace("]]", "").replace("\"", "").replace("]", "");
            //split different csv records, the ending of each csv record list is machineID.csv
            allCSVRecords = cleanupLatestRecords.split(".csv,");
            //loop through each csv and get the latest records and split each field
            for (String record : allCSVRecords) {
                latestRecords = record.split(",");
                //Change database
                Machine machine = new Machine(latestRecords[9].replace(".csv",""),latestRecords[0],latestRecords[1],latestRecords[2],latestRecords[3],latestRecords[4],
                        latestRecords[5],latestRecords[6],latestRecords[7],latestRecords[8],"22","","");

                myTempoMachineList.add(machine);
                mydatabaseHelper.changeDatabase(latestRecords[9].replace(".csv", ""), latestRecords[0], latestRecords[1], latestRecords[2], latestRecords[3], latestRecords[4],
                        latestRecords[5], latestRecords[6], latestRecords[7], latestRecords[8], "22");

                Log.i("VELO",latestRecords[8]);
                Log.i("TEMP" , latestRecords[7]);
                mydatabaseHelper.updateMachineDateTime(latestRecords[9].replace(".csv", ""), DateFormat.getDateTimeInstance().format(new Date()));

            }
            Log.i("REFRESH", "kr bundle?" + status);
            Log.d("cleanupLatestRecords: ", cleanupLatestRecords);
            Log.d("CSVRecords2: ", allCSVRecords[1]);
            Log.d("LatestRecords: ", latestRecords[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //Computation of machines in each state
    private void computeMachineState() {
        editor = DateTimeSharedPreferences.edit();
        editor.putString("DT_PREFS_KEY", DateFormat.getDateTimeInstance().format(new Date()));

        editor.commit();
        dateTime = DateTimeSharedPreferences.getString("DT_PREFS_KEY", null);
        updateDateTime.setText("Updated on :"+dateTime);

        Log.d(" computeMachine", "testing");
        for(Machine machine : myTempoMachineList)
        {
        //normal machine
        if (Double.parseDouble(machine.getmachineTemp()) < Double.parseDouble(tempWarningValue)) {
            if (Double.parseDouble(machine.getmachineVelo()) < Double.parseDouble(veloWarningValue)) {
                mydatabaseHelper.updateMachineState(machine.getMachineID(), "Normal");
            }
            else if (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(veloCriticalValue)) {
                mydatabaseHelper.updateMachineState(machine.getMachineID(), "Critical");
            }
            else {
                mydatabaseHelper.updateMachineState(machine.getMachineID(), "Warning");
            }
        }
        else if (Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(tempWarningValue) & Double.parseDouble(machine.getmachineTemp()) < Double.parseDouble(tempCriticalValue)) {
            if (Double.parseDouble(machine.getmachineVelo()) < Double.parseDouble(veloCriticalValue)) {
                mydatabaseHelper.updateMachineState(machine.getMachineID(), "Critical");
            }
            else if (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(veloCriticalValue)) {
                mydatabaseHelper.updateMachineState(machine.getMachineID(), "Warning");
            }
        }
        else if (Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(tempCriticalValue)){
            mydatabaseHelper.updateMachineState(machine.getMachineID(), "Critical");

    }
        }}
}
