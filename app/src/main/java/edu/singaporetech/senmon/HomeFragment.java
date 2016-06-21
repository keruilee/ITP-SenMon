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

    //hardcode array for testing computeMachine()
    public ArrayList<String> arrayN = new ArrayList(Arrays.asList(
            "Machine A",
            "Machine B",
            "Machine C",
            "Machine D",
            "Machine E",
            "Machine F",
            "Machine G",
            "Machine H"));

    public ArrayList<Double> arrayV = new ArrayList(Arrays.asList(
            15.00,
            13.122,
            25.55,
            37.21,
            40.57,
            59.00,
            5.22,
            21.2222));

    public ArrayList<Double> arrayT = new ArrayList(Arrays.asList(
            24.00,
            14.00,
            28.55,
            33.212,
            48.57,
            52.00,
            26.33,
            5.223));

    //hardcode array for testing checkpriority()
    public ArrayList<String> arrayTestN = new ArrayList(Arrays.asList(
            "Machine A",
            "Machine H"));

        public ArrayList<String> arrayTestW = new ArrayList(Arrays.asList(
            "Machine C",
            "Machine F",
            "Machine G",
            "Machine E"));
//    ArrayList<String> arrayTestW = new ArrayList<String>();

    public ArrayList<String> arrayTestC = new ArrayList(Arrays.asList(
            "Machine B",
            "Machine D",
            "Machine E"));
//ArrayList<String> arrayTestC = new ArrayList<String>();

    public ArrayList<Double> arrayNH = new ArrayList(Arrays.asList(
            25.56,
            5.229));

    public ArrayList<Double> arrayWH = new ArrayList(Arrays.asList(
            21.00,
            100.202,
            59.00,
            -5.2));

    public ArrayList<Double> arrayCH = new ArrayList(Arrays.asList(
            255.55,
            32.212,
            48.57));


    //Declare variables
    public ArrayList<String> normHArray = new ArrayList<String>();
    public ArrayList<String> warnHArray = new ArrayList<String>();
    public ArrayList<String> critHArray = new ArrayList<String>();
    public ArrayList<String> allHArray = new ArrayList<String>();
    String hmachineName="";

    //Declare variables
    String TAG = "Home Fragment";
    private TextView tvCrit, tvWarn, tvNorm, tvAll;
    private TextView tvCritLbl, tvWarnLbl, tvNormLbl, critBtn, warnBtn, normBtn, allBtn;
    View v;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

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
        hmachineName = checkPriority();

        //Button onClick to redirect to info fragment
        tvCritLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle=new Bundle();
                bundle.putString("name",hmachineName);
                details.setArguments(bundle); //data being send to DetailsFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.commit();
            }

        });

        tvWarnLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle=new Bundle();
                bundle.putString("name",hmachineName);
                details.setArguments(bundle); //data being send to DetailsFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.commit();
            }

        });

        //Button onClick to redirect to machinelist fragment
        critBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                MachineListFragment list = new MachineListFragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.relativelayoutfor_fragment, list);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("name",critHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.commit();
            }

        });

        warnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("name",warnHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.commit();
            }

        });

        normBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("name",normHArray);
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.commit();
            }

        });

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                ListFragment list = new ListFragment();
                //using Bundle to send data
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("name",allHArray);
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
        int i, m, n, o;
        int y, j, k;

        int totalMachine = arrayN.size();

        Log.i(TAG + " Total Machine ", String.valueOf(totalMachine));

        for (i = 0; i < totalMachine; i++) {
            if ((arrayT.get(i) >= 0 & arrayT.get(i) < 21) || (arrayV.get(i) >= 0 & arrayV.get(i) < 21)) {
                normArray.add(arrayN.get(i));
                noOfNorm++;

            }
            if ((arrayT.get(i) >= 21 & arrayT.get(i) < 31) || (arrayV.get(i) >= 21 & arrayV.get(i) < 31)) {

                for (m = 0; m < normArray.size(); m++) {
                    if (normArray.get(m).equals(arrayN.get(i))) {
                        normArray.remove(m);
                        noOfNorm--;
                    }
                }

                warnArray.add(arrayN.get(i));
                noOfWarn++;
            }
            if (arrayT.get(i) >= 31 || arrayV.get(i) >= 31) {

                for (n = 0; n < normArray.size(); n++) {
                    if (normArray.get(n).equals(arrayN.get(i))) {
                        normArray.remove(n);
                        noOfNorm--;
                    }
                }

                for (o = 0; o < warnArray.size(); o++) {
                    if (warnArray.get(o).equals(arrayN.get(i))) {
                        warnArray.remove(o);
                        noOfCrit--;
                    }
                }

                critArray.add(arrayN.get(i));
                noOfCrit++;
            }
        }

        normHArray = normArray;
        warnHArray = warnArray;
        critHArray = critArray;

        allHArray.addAll(normArray);
        allHArray.addAll(warnArray);
        allHArray.addAll(critArray);


        tvCrit.setText(noOfCrit + "/" + totalMachine + " Machines");
        tvWarn.setText(noOfWarn + "/" + totalMachine + " Machines");
        tvNorm.setText(noOfNorm + "/" + totalMachine + " Machines");
        tvAll.setText(totalMachine + " Machines");

//        for(y=0;y<normArray.size();y++)
//        {
//            Log.i(TAG + " Normal Machine ", String.valueOf(normArray.get(y)));
//        }
//
//        for(j=0;j<warnArray.size();j++)
//        {
//            Log.i(TAG + " Warning Machine ", String.valueOf(warnArray.get(j)));
//        }
//
//        for(k=0;k<critArray.size();k++)
//        {
//            Log.i(TAG + " Critical Machine ", String.valueOf(critArray.get(k)));
//        }

    }


    //    //Check highest priority machine in critical state
//    private void checkPriority(){
//        //declare variables
//        Double machineName = 0.00;
//
//        //Display different alert depending on states
//        if (critHArray.isEmpty()) {
//            //show normal alert
//            if (warnHArray.isEmpty()) {
//                tvNormLbl.setVisibility(View.VISIBLE);
//            }
//            //show warning alert
//            else {
//                machineName = computeHour(arrayH);
//                tvWarnLbl.setText(Double.toString(machineName) + " is in warning state!");
//                tvWarnLbl.setVisibility(View.VISIBLE);
//            }
//
//        }
//        //show critical alert
//        else {
//            machineName = computeHour(arrayH);
//            tvCritLbl.setText(Double.toString(machineName) + " requires immediate action!");
//            tvCritLbl.setVisibility(View.VISIBLE);
//        }
//    }
//Testing Check highest priority machine in critical state
    private String checkPriority() {
        //declare variables
        String machineName = "";
        Double machineValue = 0.00;

        //Display different alert depending on states
        if (arrayTestC.isEmpty()) {
            //show normal alert
            if (arrayTestW.isEmpty()) {
                tvNormLbl.setVisibility(View.VISIBLE);
            }
            //show warning alert
            else {
                machineValue = computeHour(arrayWH);

                for (int t = 0; t < arrayWH.size(); t++) {
                    if (machineValue.equals(arrayWH.get(t))) {
                        machineName = arrayTestW.get(t);
                    }
                }
                tvWarnLbl.setText(machineName + " is in warning state!");
                tvWarnLbl.setVisibility(View.VISIBLE);
            }

        }
        //show critical alert
        else {
            machineValue = computeHour(arrayCH);

            for (int u = 0; u < arrayCH.size(); u++) {
                if (machineValue.equals(arrayCH.get(u))) {
                    machineName = arrayTestC.get(u);
                }
            }
            tvCritLbl.setText(machineName + " requires immediate action!");
            tvCritLbl.setVisibility(View.VISIBLE);
        }
        Log.i(TAG + " Machine Name ", machineName);
        return machineName;
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
