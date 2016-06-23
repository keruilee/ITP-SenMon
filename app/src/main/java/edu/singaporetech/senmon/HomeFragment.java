package edu.singaporetech.senmon;


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
        Machine machine = new Machine("SDK001-M001-01-0001a", "0.3", "36.11", "50");
        Machine machine2 = new Machine("SDK221-M001-01-0001a", "0.2244", "10.11", "33");
        Machine machine3 = new Machine("SDK331-M001-01-0001a", "0.293", "20.11", "53");
        Machine machine4 = new Machine("ADK444-M001-01-0001a", "0.922", "30.11", "900");
        Machine machine5 = new Machine("SDK555-M001-01-0001a", "0.312", "40.11", "6");
        Machine machine6 = new Machine("SDK166-M001-01-0001a", "0.9222", "5.11", "3");

        myMachineList.add(machine);
        myMachineList.add(machine2);
        myMachineList.add(machine3);
        myMachineList.add(machine4);
        myMachineList.add(machine5);
        myMachineList.add(machine6);

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

        //call computeMachine method
        computeMachine();

        //call checkPriority method
        hmachineID = checkPriority();

        //Button onClick to redirect to info fragment
        tvCritLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", hmachineID);
                details.setArguments(bundle); //data being send to DetailsFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.commit();
            }

        });

        tvWarnLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putString("name", hmachineID);
                details.setArguments(bundle); //data being send to DetailsFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.commit();
            }

        });

        //Button onClick to redirect to machinelist fragment
        critBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("name", critHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.commit();
            }

        });

        warnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("name", warnHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.commit();
            }

        });

        normBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("name", normHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.commit();
            }

        });

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("name", allHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.commit();
            }

        });

        return v;
    }


    //Computation of machines in each state
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
            if ((Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(getString(R.string.min_normal_temp)) & Double.parseDouble(machine.getmachineTemp()) <= Double.parseDouble(getString(R.string.max_normal_temp))) | (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(getString(R.string.min_normal_velo)) & Double.parseDouble(machine.getmachineVelo()) <=Double.parseDouble(getString(R.string.max_normal_velo)))) {
                normArray.add(machine.getMachineID());
                noOfNorm++;

            }
            if ((Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(getString(R.string.min_warning_temp)) & Double.parseDouble(machine.getmachineTemp()) <= Double.parseDouble(getString(R.string.max_warning_temp))) | (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(getString(R.string.min_warning_velo)) & Double.parseDouble(machine.getmachineVelo()) <= Double.parseDouble(getString(R.string.max_warning_velo)))) {

                for (m = 0; m < normArray.size(); m++) {
                    if (normArray.get(m).equals(machine.getMachineID())) {
                        normArray.remove(m);
                        noOfNorm--;
                    }
                }

                warnArray.add(machine.getMachineID());
                noOfWarn++;
            }
            if (Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(getString(R.string.min_critical_temp)) | Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(getString(R.string.min_critical_velo))) {

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


        //store machine name in each array state
        normHArray = normArray;
        warnHArray = warnArray;
        critHArray = critArray;

        allHArray.addAll(normArray);
        allHArray.addAll(warnArray);
        allHArray.addAll(critArray);

        //Set to display on button textview
        tvCrit.setText(noOfCrit + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvWarn.setText(noOfWarn + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvNorm.setText(noOfNorm + "/" + totalMachine + " " + getString(R.string.machine_name));
        tvAll.setText(totalMachine + " " + getString(R.string.machine_name));

    }

    //Check highest priority machine in critical state
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


    //Compute longest machine hour
    private Double computeHour(ArrayList<Double> array) {
        double name = 0.00;
        double highest = array.get(0);
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > highest)
                highest = array.get(i);
            name = highest;
        }

        return name;
    }
}
