package edu.singaporetech.senmon;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //Declare variables
    public ArrayList<String> normHArray = new ArrayList<String>();
    public ArrayList<String> warnHArray = new ArrayList<String>();
    public ArrayList<String> critHArray = new ArrayList<String>();
    public ArrayList<String> allHArray = new ArrayList<String>();
    String hmachineID = "";

    //Declare variables
    String TAG = "Home Fragment";
    private TextView tvCrit, tvWarn, tvNorm, tvAll;
    private TextView tvCritLbl, tvWarnLbl, tvNormLbl, critBtn, warnBtn, normBtn, allBtn;
    View v;
    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs" ;
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";

    //hardcode array
    final ArrayList<Machine> myMachineList = new ArrayList<Machine>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        //Hardcode array
//        Machine machine = new Machine("SDK001-M001-01-0001a","","","","","",   "0.03", "36.11", "","","50");
//        Machine machine2 = new Machine("SDK221-M001-01-0001a","","","","","",  "2.44", "10.11", "","", "33");
//        Machine machine3 = new Machine("SDK331-M001-01-0001a","","","","","",  "0.293", "20.11", "","", "53");
//        Machine machine4 = new Machine("ADK444-M001-01-0001a","","","","","",  "9.22", "30.11", "","", "900");
//        Machine machine5 = new Machine("SDK555-M001-01-0001a","","","","","",  "0.312", "40.11", "","", "6");
//        Machine machine6 = new Machine("SDK166-M001-01-0001a","","","","","",  "0.922", "5.11", "","", "3");
//
//        myMachineList.add(machine);
//        myMachineList.add(machine2);
//        myMachineList.add(machine3);
//        myMachineList.add(machine4);
//        myMachineList.add(machine5);
//        myMachineList.add(machine6);

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

        //call computeMachine method
        computeMachine();

        //call checkPriority method
        hmachineID = checkPriority();

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
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.addToBackStack(null);
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
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.addToBackStack(null);
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
                bundle.putString("option", "critical");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.addToBackStack(null);
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
                bundle.putString("option", "warning");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.addToBackStack(null);
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
                bundle.putString("option", "normal");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.addToBackStack(null);
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
                bundle.putStringArrayList("name", allHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });

        return v;
    }


    /////Computation of machines in each state
    private void computeMachine() {
        //Declare variables
        ArrayList<String> normArray = new ArrayList<String>();
        ArrayList<String> warnArray = new ArrayList<String>();
        ArrayList<String> critArray = new ArrayList<String>();

        int noOfCrit = 0;
        int noOfWarn = 0;
        int noOfNorm = 0;
        int m, n, o;

        int totalMachine = myMachineList.size();
        Log.i(TAG + " Total Machine ", String.valueOf(totalMachine));

        for(Machine machine : myMachineList)
        {
            //number of normal machine
            if (Double.parseDouble(machine.getmachineTemp()) < Double.parseDouble(tempWarningValue)
                    | (Double.parseDouble(machine.getmachineVelo()) < Double.parseDouble(veloWarningValue))) {
                normArray.add(machine.getMachineID());
                noOfNorm++;

            }
            //number of warning machine
            if ((Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(tempWarningValue)
                    & Double.parseDouble(machine.getmachineTemp()) < Double.parseDouble(tempCriticalValue))
                    | (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(veloWarningValue)
                    & Double.parseDouble(machine.getmachineVelo()) <= Double.parseDouble(veloCriticalValue))) {

                for (m = 0; m < normArray.size(); m++) {
                    if (normArray.get(m).equals(machine.getMachineID())) {
                        normArray.remove(m);
                        noOfNorm--;
                    }
                }

                warnArray.add(machine.getMachineID());
                noOfWarn++;
            }
            //number of critical machine
            if (Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(tempCriticalValue)
                    | (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(veloCriticalValue))) {

                for (n = 0; n < normArray.size(); n++) {
                    if (normArray.get(n).equals(machine.getMachineID())) {
                        normArray.remove(n);
                        noOfNorm--;
                    }
                }

                for (o = 0; o < warnArray.size(); o++) {
                    if (warnArray.get(o).equals(machine.getMachineID())) {
                        warnArray.remove(o);
                        noOfWarn--;
                    }
                }

                critArray.add(machine.getMachineID());
                noOfCrit++;
            }
        }


        ////store machine name in each array state
        normHArray = normArray;
        warnHArray = warnArray;
        critHArray = critArray;

        allHArray.addAll(normArray);
        allHArray.addAll(warnArray);
        allHArray.addAll(critArray);

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
        Double machineValue = 0.00;
        ArrayList<Double> arrayWH = new ArrayList<Double>();
        ArrayList<Double> arrayCH = new ArrayList<Double>();


        //Display different alert depending on states
        if (critHArray.isEmpty()) {
            //show normal alert
            if (warnHArray.isEmpty()) {
                tvNormLbl.setVisibility(View.VISIBLE);
            }
            //show warning alert
            else {
                for (Machine machine1 : myMachineList) {
                    for (int i = 0; i < warnHArray.size(); i++) {
                        if (machine1.getMachineID().equals(warnHArray.get(i))) {
                            arrayWH.add(Double.parseDouble(machine1.getMachineHour()));
                        }
                    }
                }

                machineValue = computeHour(arrayWH);

                for (int t = 0; t < arrayWH.size(); t++) {
                    if (machineValue.equals(arrayWH.get(t))) {
                        machineID = warnHArray.get(t);
                    }
                }
                tvWarnLbl.setText(machineID + " " + getString(R.string.warning_lbl));
                tvWarnLbl.setVisibility(View.VISIBLE);
            }

        }
        //show critical alert
        else {
            for (Machine machine2 : myMachineList) {
                for (int p = 0; p < critHArray.size(); p++) {
                    if (machine2.getMachineID().equals(critHArray.get(p))) {
                        arrayCH.add(Double.parseDouble(machine2.getMachineHour()));
                    }
                }
            }

            machineValue = computeHour(arrayCH);

            for (int u = 0; u < arrayCH.size(); u++) {
                if (machineValue.equals(arrayCH.get(u))) {
                    machineID = critHArray.get(u);
                }
            }
            tvCritLbl.setText(machineID + " " + getString(R.string.critical_lbl));
            tvCritLbl.setVisibility(View.VISIBLE);
        }
        Log.i(TAG + " Machine Name ", machineID);
        return machineID;
    }


    //////Compute longest machine hour
    private Double computeHour(ArrayList<Double> array) {
        double hour = 0.00;
        double highest = array.get(0);
        int size = array.size();

        if (size == 1)
        {
            hour = highest;
        }
        else {
            for (int i = 1; i < array.size(); i++) {
                if (array.get(i) > highest)
                    highest = array.get(i);
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
        }
        //warning button disabled
        if (warnNum == 0)
        {
            warnBtn.setEnabled(false);
            warnBtn.getBackground().setColorFilter(getResources().getColor(R.color.colorLighterWarning), PorterDuff.Mode.SRC_ATOP);
        }
        //normal button disabled
        if (normNum == 0)
        {
            normBtn.setEnabled(false);
            normBtn.getBackground().setColorFilter(getResources().getColor(R.color.colorLighterNormal), PorterDuff.Mode.SRC_ATOP);
        }
        //all button disabled
        if (totalNum == 0)
        {
            allBtn.setEnabled(false);
            allBtn.getBackground().setColorFilter(getResources().getColor(R.color.colorLighterAll), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
