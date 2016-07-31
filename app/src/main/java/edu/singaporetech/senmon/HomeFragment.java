package edu.singaporetech.senmon;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

    SharedPreferences sharedPreferences;
    SharedPreferences DateTimeSharedPreferences;

    SharedPreferences.Editor editor;

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

    private DatabaseHelper DbHelper;

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

        DbHelper = new DatabaseHelper(context);
        progressDialog = new ProgressDialog(context);

        //retrieve data
        if(isNetworkEnabled()){
            getSQLData();
        }
        else{
            //call compute machine method
            computeMachine();

            if (DbHelper.getRowsCount() > 0)
            {
                //check priority method
                hmachineID = checkPriority();
            }

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    /**
     * start async WebService task to retrieve records from server's database
     */
    public void getSQLData(){
        WebService webServiceTask = new WebService(context, this);
        webServiceTask.execute();
    }

    // async task completed
    @Override
    public void asyncResponse(JSONObject response) {
        getSQLRecords(response);

        //call compute machine method
        if(!isAdded())          // if fragment is not attached to activity, dont continue
            return;

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

                Machine machine = new Machine(context, latestRecords[0],latestRecords[1],latestRecords[2],latestRecords[3],latestRecords[4],latestRecords[5],
                        latestRecords[6],latestRecords[7],latestRecords[8],latestRecords[9], "0");

                //Change database
                DbHelper.updateDatabase(machine);
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
        // get num of machines in each state from database
        int noOfCrit = DbHelper.getNumOfMachinesByStatus(getString(R.string.status_critical));
        int noOfWarn = DbHelper.getNumOfMachinesByStatus(getString(R.string.status_warning));
        int noOfNorm = DbHelper.getNumOfMachinesByStatus(getString(R.string.status_normal));
        long totalMachine = DbHelper.getRowsCount();

        Log.d(" Total Machine ", String.valueOf(totalMachine));

        //Set to display number of machine for each button
        tvCrit.setText(noOfCrit + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvWarn.setText(noOfWarn + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvNorm.setText(noOfNorm + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvAll.setText(totalMachine + " " + getString(R.string.machine_name));

        //Set disabled of button
        disableButton(noOfCrit, noOfWarn, noOfNorm);
    }

    /////Check highest priority machine
    private String checkPriority() {
        //declare variables
        String machineID = "";

        //Display different alert bar depending on state
        ArrayList<Machine> myMachineList = DbHelper.returnStringMachineStateString(getString(R.string.status_critical));
        if (myMachineList.isEmpty())
        {
            //show normal alert bar
            myMachineList = DbHelper.returnStringMachineStateString(getString(R.string.status_warning));
            if (myMachineList.isEmpty())
            {
                tvNormLbl.setVisibility(View.VISIBLE);
            }
            //show warning alert bar
            else
            {
                Machine mostCriticalMachine = getMostCriticalMachine(myMachineList);
                machineID = mostCriticalMachine.getMachineID();
                tvWarnLbl.setText(mostCriticalMachine.getMachineID() + " " + getString(R.string.warning_lbl));
                tvWarnLbl.setVisibility(View.VISIBLE);
            }
        }
        //show critical alert bar
        else
        {
            Machine mostCriticalMachine = getMostCriticalMachine(myMachineList);
            machineID = mostCriticalMachine.getMachineID();
            tvCritLbl.setText(mostCriticalMachine.getMachineID() + " " + getString(R.string.critical_lbl));
            tvCritLbl.setVisibility(View.VISIBLE);
        }

        Log.d(" Machine Name ", machineID);
        return machineID;
    }

    //////Compute longest machine hour
    private Machine getMostCriticalMachine(ArrayList<Machine> arrayMachines) {
        double highestOpHour = arrayMachines.get(0).getMachineHourDouble();
        Machine mostCritMachine = arrayMachines.get(0);
        int size = arrayMachines.size();

        if(size > 1)
        {
            for (int i = 1; i < arrayMachines.size(); i++) {
                if (arrayMachines.get(i).getMachineHourDouble() > highestOpHour)
                {
                    mostCritMachine = arrayMachines.get(i);
                    highestOpHour = arrayMachines.get(i).getMachineHourDouble();
                }
            }
        }

        return mostCritMachine;
    }

    //set disable of button
    private void disableButton(int critNum, int warnNum, int normNum)
    {
        int totalNum = critNum + warnNum + normNum;

        //critical button disabled
        if (critNum == 0)
        {
            critBtn.setEnabled(false);
            critBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterCritical), PorterDuff.Mode.SRC_ATOP);
            critBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_critical_disabled, 0, 0);
        }
        //warning button disabled
        if (warnNum == 0)
        {
            warnBtn.setEnabled(false);
            warnBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterWarning), PorterDuff.Mode.SRC_ATOP);
            warnBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_warning_disabled, 0, 0);
        }
        //normal button disabled
        if (normNum == 0)
        {
            normBtn.setEnabled(false);
            normBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterNormal), PorterDuff.Mode.SRC_ATOP);
            normBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_normal_disabled, 0, 0);
        }
        //all button disabled
        if (totalNum == 0)
        {
            allBtn.setEnabled(false);
            allBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterAll), PorterDuff.Mode.SRC_ATOP);
            allBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_all_disabled, 0, 0);
        }

    }

    public boolean isNetworkEnabled(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
