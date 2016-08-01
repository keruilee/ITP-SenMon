package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements WebService.OnAsyncRequestComplete {

    //Declare variables
    String TAG = "Home Fragment";
    String hmachineID = "";

    public static final String LIST_FRAG_CRIT_TAG = "LIST_FRAGMENT_CRITICAL";
    public static final String LIST_FRAG_WARN_TAG = "LIST_FRAGMENT_WARNING";
    public static final String LIST_FRAG_NORM_TAG = "LIST_FRAGMENT_NORMAL";
    public static final String LIST_FRAG_ALL_TAG = "LIST_FRAGMENT_ALL";
    public static final String DETAILS_FRAG_TAG = "DETAILS_FRAGMENT";

    ProgressDialog progressDialog;

    private static final String TAG_RESULTS="result";

    private DatabaseHelper DbHelper;

    private TextView tvCrit, tvWarn, tvNorm, tvAll;
    private TextView tvCritLbl, tvWarnLbl, tvNormLbl, critBtn, warnBtn, normBtn, allBtn;
    private SwipeRefreshLayout swipeContainer;
    public Context context;
    View v;

    IntentFilter inF = new IntentFilter("database_updated");

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

        getMachinesCountByStatus();
        if (DbHelper.getRowsCount() > 0)
        {
            //check priority method
            hmachineID = checkPriority();
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
                getSQLData();
            }
        });

        //register the receiver

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
    public void asyncResponse() {
        getMachinesCountByStatus();

        //check priority method
        hmachineID = checkPriority();

        swipeContainer.setRefreshing(false);
    }

    //what to do when it receives the broadcast from the backgroundservice
    private BroadcastReceiver dataChangeReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update your listview
            Log.d("BROADCAST HOME", "YES!");

            getMachinesCountByStatus();

            //check priority method
            hmachineID = checkPriority();
        }
    };

    //Computation of machines in each state
    private void getMachinesCountByStatus() {
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

        //Set state of button
        updateButtons(noOfCrit, noOfWarn, noOfNorm);
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

    //update state of button
    private void updateButtons(int critNum, int warnNum, int normNum)
    {
        int totalNum = critNum + warnNum + normNum;

        if (critNum == 0)   //critical button disabled
        {
            critBtn.setEnabled(false);
            critBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterCritical), PorterDuff.Mode.SRC_ATOP);
            critBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_critical_disabled, 0, 0);
        } else {
            critBtn.setEnabled(true);
            critBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorCritical), PorterDuff.Mode.SRC_ATOP);
            critBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_critical, 0, 0);
        }

        if (warnNum == 0)   //warning button disabled
        {
            warnBtn.setEnabled(false);
            warnBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterWarning), PorterDuff.Mode.SRC_ATOP);
            warnBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_warning_disabled, 0, 0);
        } else {
            warnBtn.setEnabled(true);
            warnBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorWarning), PorterDuff.Mode.SRC_ATOP);
            warnBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_warning, 0, 0);
        }

        if (normNum == 0)   //normal button disabled
        {
            normBtn.setEnabled(false);
            normBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterNormal), PorterDuff.Mode.SRC_ATOP);
            normBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_normal_disabled, 0, 0);
        } else {
            normBtn.setEnabled(true);
            normBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorNormal), PorterDuff.Mode.SRC_ATOP);
            normBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_normal, 0, 0);
        }
        //all button disabled
        if (totalNum == 0)
        {
            allBtn.setEnabled(false);
            allBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorLighterAll), PorterDuff.Mode.SRC_ATOP);
            allBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_all_disabled, 0, 0);
        } else {
            allBtn.setEnabled(true);
            allBtn.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorAll), PorterDuff.Mode.SRC_ATOP);
            allBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_all, 0, 0);
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
}
