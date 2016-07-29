package edu.singaporetech.senmon;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements WebService.OnAsyncRequestComplete {

    //Declare variables
    String TAG = "Home Fragment";
    final String CRITICAL = "Critical";
    final String WARNING = "Warning";
    final String NORMAL = "Normal";
    String hmachineID = "";

    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    SharedPreferences sharedPreferences;
    SharedPreferences DateTimeSharedPreferences;

    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String MyRangePREFERENCES = "MyRangePrefs" ;
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";
    public static final String NumberOfCritical = "numOfCrit";
    public static final String NumberOfWarning = "numOfWarn";
    public static final String NumberOfNormal = "numOfNorm";
    public static final String NumberOfFavourite = "numOfFav";

    public static final String LIST_FRAG_CRIT_TAG = "LIST_FRAGMENT_CRITICAL";
    public static final String LIST_FRAG_WARN_TAG = "LIST_FRAGMENT_WARNING";
    public static final String LIST_FRAG_NORM_TAG = "LIST_FRAGMENT_NORMAL";
    public static final String LIST_FRAG_ALL_TAG = "LIST_FRAGMENT_ALL";
    public static final String DETAILS_FRAG_TAG = "DETAILS_FRAGMENT";

    ProgressDialog progressDialog;
    JSONArray serverSQLRecords = null;
    private static final String TAG_RESULTS="result";
    public String[] latestRecords;
    public String[] allSQLRecords;

    ArrayList<Machine> myMachineList = new ArrayList<Machine>();
    private DatabaseHelper DbHelper;
//    public WebService webService;

    private TextView tvCrit, tvWarn, tvNorm, tvAll;
    private TextView tvCritLbl, tvWarnLbl, tvNormLbl, critBtn, warnBtn, normBtn, allBtn;
    private SwipeRefreshLayout swipeContainer;
    public Context context;
    View v;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();

        //Set variables
        tvCrit = (TextView) v.findViewById(R.id.critTxt);
        tvWarn = (TextView) v.findViewById(R.id.warnTxt);
        tvNorm = (TextView) v.findViewById(R.id.normTxt);
        tvAll = (TextView) v.findViewById(R.id.allTxt);
        tvCritLbl = (TextView) v.findViewById(R.id.critmessageLbl);
        tvWarnLbl = (TextView) v.findViewById(R.id.warnmessageLbl);
        tvNormLbl = (TextView) v.findViewById(R.id.normmessageLbl);
        critBtn = (TextView) v.findViewById(R.id.criticalBtn);
        warnBtn = (TextView) v.findViewById(R.id.warningBtn);
        normBtn = (TextView) v.findViewById(R.id.normalBtn);
        allBtn = (TextView) v.findViewById(R.id.allBtn);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        DbHelper = new DatabaseHelper(this.getActivity());
//        webService = new WebService(getActivity());
        progressDialog = new ProgressDialog(this.getActivity());

        //retrieve range values
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display it
        tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_warning_value))));
        tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_critical_value))));
        veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_warning_value))));
        veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_critical_value))));

        //checking of range
        if (tempCriticalValue != null | tempWarningValue != null) {
            Log.e("result for range: ", tempWarningValue + " " + tempCriticalValue);
        }
        else {
            Log.e("default: ", "21.0 31.0");
        }

        if (veloCriticalValue != null | veloWarningValue != null) {
            Log.e("result for range: ", veloWarningValue + " " + veloCriticalValue);
        }
        else {
            Log.e("default: ", "21.0 31.0" + veloWarningValue + " " + veloCriticalValue);
            Log.e("default: ", "21.0 31.0" + tempWarningValue + " " + tempCriticalValue);
        }

        //retrieve data
        if(isNetworkEnabled()){
            getSQLData();
        }
        else{

            retrieveDatabaseRecord();

            //call compute machine method
            computeMachine();

            if (!(myMachineList.isEmpty()))
            {
                //check priority method
                hmachineID = checkPriority();
            }

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

        //Button onClick to redirect to info fragment
        tvCritLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DetailsFragment details = new DetailsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", hmachineID);
                details.setArguments(bundle); //data being send to DetailsFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details, DETAILS_FRAG_TAG);
                transaction.addToBackStack(DETAILS_FRAG_TAG);
                transaction.commit();
            }

        });

        tvWarnLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DetailsFragment details = new DetailsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", hmachineID);
                details.setArguments(bundle); //data being send to DetailsFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details, DETAILS_FRAG_TAG);
                transaction.addToBackStack(DETAILS_FRAG_TAG);
                transaction.commit();
            }

        });

        //Button onClick to redirect to machinelist fragment
        critBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListFragment list = new ListFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", "Critical");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_CRIT_TAG);
                transaction.addToBackStack(LIST_FRAG_CRIT_TAG);
                transaction.commit();
            }

        });

        warnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListFragment list = new ListFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", "Warning");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_WARN_TAG);
                transaction.addToBackStack(LIST_FRAG_WARN_TAG);
                transaction.commit();
            }

        });

        normBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListFragment list = new ListFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", "Normal");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_NORM_TAG);
                transaction.addToBackStack(LIST_FRAG_NORM_TAG);
                transaction.commit();
            }

        });

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListFragment list = new ListFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", "All");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_ALL_TAG);
                transaction.addToBackStack(LIST_FRAG_ALL_TAG);
                transaction.commit();
            }

        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //retrieve data
                if(isNetworkEnabled()){
                    getSQLData();
                }
                else{
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
                swipeContainer.setRefreshing(false);

            }
        });

        //register the receiver
        IntentFilter inF = new IntentFilter("data_changed");
        LocalBroadcastManager.getInstance(context).registerReceiver(dataChangeReceiver, inF);

        return v;
    }

    public void getSQLData(){
        WebService webServiceTask = new WebService(context, this);
        webServiceTask.execute();
    }

    // async task of getting SQL records from server completed
    @Override
    public void asyncResponse(JSONObject response) {

        getSQLRecords(response);
        //call compute machine method
        computeMachine();

        //check priority method
        hmachineID = checkPriority();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //Get the server CSV records
    public void getSQLRecords(JSONObject jsonObj)
    {
        try {
            serverSQLRecords = jsonObj.getJSONArray(TAG_RESULTS);

            if (!(myMachineList.isEmpty())) {
                myMachineList.clear();
            }

            Log.d("homefragment: ", serverSQLRecords.toString());

            String cleanupLatestRecords;

            //remove all unwanted symbols and text
            cleanupLatestRecords = serverSQLRecords.toString().replaceAll(",false]]", "").replace("[[", "").replace("[", "").replace("]]", "").replace("\"","").replace("]","");
            //split different csv records, the ending of each csv record list is machineID.csv
            allSQLRecords = cleanupLatestRecords.split("split,");
            //loop through each csv and get the latest records and split each field
            for(String record : allSQLRecords)
            {
                latestRecords = record.split(",");

                Machine machine = new Machine(latestRecords[0],latestRecords[1],latestRecords[2],latestRecords[3],latestRecords[4],latestRecords[5],
                        latestRecords[6],latestRecords[7],latestRecords[8],latestRecords[9],"0","","");

                myMachineList.add(machine);

                //Change database
                DbHelper.changeDatabase(latestRecords[0], latestRecords[1], latestRecords[2], latestRecords[3], latestRecords[4], latestRecords[5],
                        latestRecords[6], latestRecords[7], latestRecords[8], latestRecords[9]);
               // DbHelper.updateMachineDateTime(latestRecords[0], DateFormat.getDateTimeInstance().format(new Date()));

                Log.d("cleanupLatestRecords: ", DbHelper.toString());
            }

            DateTimeSharedPreferences = context.getSharedPreferences("DT_PREFS_NAME", Context.MODE_PRIVATE);
            editor = DateTimeSharedPreferences.edit();
            editor.putString("DT_PREFS_KEY", DateFormat.getDateTimeInstance().format(new Date()));
            editor.commit();


            Log.d("cleanupLatestRecords: ", cleanupLatestRecords);
            Log.d("SQLRecords2: ", allSQLRecords[1]);
            Log.d("LatestRecords: ", latestRecords[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


        //what to do when it receives the broadcast from the backgroundservice
    private BroadcastReceiver dataChangeReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update your listview
            Log.d("BROADCAST RECEIVED", "YES!");
            //get the data again from the csvdata()
            if(isNetworkEnabled()){
                getSQLData();
            }
        }
    };

    //Computation of machines in each state
    private void computeMachine() {
        //Declare variables
        int noOfCrit = 0;
        int noOfWarn = 0;
        int noOfNorm = 0;

        int totalMachine = myMachineList.size();
        Log.d(" Total Machine ", String.valueOf(totalMachine));

        double machineTemp, machineVelo, tempWarning, tempCritical, veloWarning, veloCritical;

        for(Machine machine : myMachineList)
        {
            machineTemp = Double.parseDouble(machine.getmachineTemp());
            machineVelo = Double.parseDouble(machine.getmachineVelo());
            tempWarning = Double.parseDouble(tempWarningValue);
            tempCritical = Double.parseDouble(tempCriticalValue);
            veloWarning = Double.parseDouble(veloWarningValue);
            veloCritical = Double.parseDouble(veloCriticalValue);

            // determine states of machine
            if(machineTemp >= tempCritical || machineVelo >= veloCritical)          // machine in critical state
            {
                DbHelper.updateMachineState(machine.getMachineID(), CRITICAL);
                noOfCrit++;
            }
            else if(machineTemp >= tempWarning || machineVelo >= veloWarning)       // machine in warning state
            {
                DbHelper.updateMachineState(machine.getMachineID(), WARNING);
                noOfWarn++;
            }
            else                                                                    // machine in off/normal state
            {
                DbHelper.updateMachineState(machine.getMachineID(), NORMAL);
                noOfNorm++;
            }
        }


        //Set to display number of machine for each button
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tvCrit.setText(noOfCrit + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvWarn.setText(noOfWarn + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvNorm.setText(noOfNorm + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvAll.setText(totalMachine + " " + getString(R.string.machine_name));

        //put number of crit and warning and normal into shared preference
        editor = sharedPreferences.edit();
        editor.putInt(NumberOfCritical, noOfCrit);
        editor.putInt(NumberOfWarning, noOfWarn);
        editor.putInt(NumberOfNormal, noOfNorm);
        editor.commit();

        //Set disabled of button
        disableButton(noOfCrit, noOfWarn, noOfNorm);
    }

    /////Check highest priority machine
    private String checkPriority() {
        //declare variables
        String machineID = "";
        Double machineHour = 0.00;
        String state = "";
        ArrayList<Double> harrayHour = new ArrayList<Double>();

        //Display different alert bar depending on state
        harrayHour = DbHelper.checkMachineInParticularState(CRITICAL);
        if (harrayHour.isEmpty())
        {
            //show normal alert bar
            harrayHour = DbHelper.checkMachineInParticularState(WARNING);
            if (harrayHour.isEmpty())
            {
                state = NORMAL;
                tvNormLbl.setVisibility(View.VISIBLE);
            }
            //show warning alert bar
            else
            {
                state = WARNING;
                machineHour = computeHour(harrayHour);
                machineID = DbHelper.machineUsingHour(state, machineHour.toString());

                tvWarnLbl.setText(machineID + " " + getString(R.string.warning_lbl));
                tvWarnLbl.setVisibility(View.VISIBLE);
            }
        }
        //show critical alert bar
        else
        {
            state = CRITICAL;
            machineHour = computeHour(harrayHour);
            machineID = DbHelper.machineUsingHour(state, machineHour.toString());

            tvCritLbl.setText(machineID + " " + getString(R.string.critical_lbl));
            tvCritLbl.setVisibility(View.VISIBLE);
        }

        Log.d(" Machine Name ", machineID);
        return machineID;
    }


    //////Compute longest machine hour
    private Double computeHour(ArrayList<Double> arrayHour) {
        double hour = 0.00;
        double highest = arrayHour.get(0);
        int size = arrayHour.size();

        if (size == 1)
        {
            hour = highest;
        }
        else {
            for (int i = 1; i < arrayHour.size(); i++) {
                if (arrayHour.get(i) > highest)
                    highest = arrayHour.get(i);
                hour = highest;
            }
        }

        return hour;
    }


    //set disable of button
    private void disableButton(int critNum, int warnNum, int normNum)
    {
        int totalNum = critNum + warnNum + normNum;

        //critical button disabled
        if (critNum == 0)
        {
            critBtn.setEnabled(false);
            critBtn.getBackground().setColorFilter(getResources().getColor(R.color.colorLighterCritical), PorterDuff.Mode.SRC_ATOP);
            critBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_critical_disabled, 0, 0);
        }
        //warning button disabled
        if (warnNum == 0)
        {
            warnBtn.setEnabled(false);
            warnBtn.getBackground().setColorFilter(getResources().getColor(R.color.colorLighterWarning), PorterDuff.Mode.SRC_ATOP);
            warnBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_warning_disabled, 0, 0);
        }
        //normal button disabled
        if (normNum == 0)
        {
            normBtn.setEnabled(false);
            normBtn.getBackground().setColorFilter(getResources().getColor(R.color.colorLighterNormal), PorterDuff.Mode.SRC_ATOP);
            normBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_normal_disabled, 0, 0);
        }
        //all button disabled
        if (totalNum == 0)
        {
            allBtn.setEnabled(false);
            allBtn.getBackground().setColorFilter(getResources().getColor(R.color.colorLighterAll), PorterDuff.Mode.SRC_ATOP);
            allBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_all_disabled, 0, 0);
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

    //Retrieve all database records
    private void retrieveDatabaseRecord()
    {
        if (!(myMachineList.isEmpty()))
        {
            myMachineList.clear();
        }
        // Select All Query
        String selectQuery = "SELECT * FROM " + DbHelper.TABLE_NAME;
        SQLiteDatabase db = DbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to mydatabaselist
        if (cursor.moveToFirst()) {
            do {
                Machine dbMachine = new Machine(cursor.getString(1),cursor.getString(2),cursor.getString(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),
                        cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13));
                myMachineList.add(dbMachine);
            } while (cursor.moveToNext());
        }
    }
}
