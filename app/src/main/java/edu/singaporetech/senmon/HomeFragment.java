package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //Declare variables
    String TAG = "Home Fragment";
    private TextView tvCrit, tvWarn, tvNorm, tvAll;
    private TextView tvCritLbl, tvWarnLbl, tvNormLbl, critBtn, warnBtn, normBtn, allBtn;
    View v;
    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    final String CRITICAL = "Critical";
    final String WARNING = "Warning";
    final String NORMAL = "Normal";
    String hmachineID = "";
    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs" ;
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";

    ProgressDialog progressDialog;
    JSONArray serverCSVrecords = null;
    private static final String TAG_RESULTS="result";
    public String[] latestRecords;
    public String[] allCSVRecords;

    final ArrayList<Machine> myMachineList = new ArrayList<Machine>();
    private DatabaseHelper DbHelper;

    //for testing
    final ArrayList<Machine> myDatabaseList = new ArrayList<Machine>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        DbHelper = new DatabaseHelper(this.getActivity());

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

        progressDialog = new ProgressDialog(getActivity());

        //retrieve data
        getCSVData();


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
                bundle.putString("name", "critical");
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
                bundle.putString("name", "warning");
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
                bundle.putString("name", "normal");
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
                bundle.putString("name", "all");
                list.setArguments(bundle); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, list);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });

        return v;
    }

    public void getCSVData(){
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
                }finally {
                    urlConnection.disconnect();
                }

                return responseObj;
            }

            @Override
            protected void onPostExecute(JSONObject result){
                super.onPostExecute(result);

                getCSVRecords(result);

                //call compute machine method
                computeMachine();

                //check priority method
                hmachineID = checkPriority();


                //For testing
                //checkDatabaseRecord();
                //seeValue();


                progressDialog.dismiss();
            }
        }
        GetCSVDataJSON g = new GetCSVDataJSON();
        g.execute();
    }

    //Get the server CSV records
    public void getCSVRecords(JSONObject jsonObj)
    {
        try {
            serverCSVrecords = jsonObj.getJSONArray(TAG_RESULTS);

            if (!(myMachineList.isEmpty())) {
                myMachineList.clear();
            }

            String cleanupLatestRecords;

            //remove all unwanted symbols and text
            cleanupLatestRecords = serverCSVrecords.toString().replaceAll(",false]]", "").replace("[[", "").replace("[", "").replace("]]", "").replace("\"","").replace("]","");
            //split different csv records, the ending of each csv record list is machineID.csv
            allCSVRecords = cleanupLatestRecords.split(".csv,");
            //loop through each csv and get the latest records and split each field
            for(String record : allCSVRecords)
            {
                latestRecords = record.split(",");

                Machine machine = new Machine(latestRecords[9].replace(".csv",""),latestRecords[0],latestRecords[1],latestRecords[2],latestRecords[3],latestRecords[4],
                        latestRecords[5],latestRecords[6],latestRecords[7],latestRecords[8],"22","","");

                myMachineList.add(machine);

                //Change database
                DbHelper.changeDatabase(latestRecords[9].replace(".csv", ""), latestRecords[0], latestRecords[1], latestRecords[2], latestRecords[3], latestRecords[4],
                        latestRecords[5], latestRecords[6], latestRecords[7], latestRecords[8], "22");
                DbHelper.updateMachineDateTime(latestRecords[9].replace(".csv", ""), DateFormat.getDateTimeInstance().format(new Date()));
                Log.d("cleanupLatestRecords: ", DbHelper.toString());
            }

            Log.d("cleanupLatestRecords: ", cleanupLatestRecords);
            Log.d("CSVRecords2: ", allCSVRecords[1]);
            Log.d("LatestRecords: ", latestRecords[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Computation of machines in each state
    private void computeMachine() {
        //Declare variables
        int noOfCrit = 0;
        int noOfWarn = 0;
        int noOfNorm = 0;

        int totalMachine = myMachineList.size();
        Log.d(TAG + " Total Machine ", String.valueOf(totalMachine));

        for(Machine machine : myMachineList)
        {
            //number of normal machine
            if (Double.parseDouble(machine.getmachineTemp()) < Double.parseDouble(tempWarningValue)
                    | (Double.parseDouble(machine.getmachineVelo()) < Double.parseDouble(veloWarningValue))) {

                //update normal state on the row
                DbHelper.updateMachineState(machine.getMachineID(), NORMAL);
                noOfNorm++;

            }
            //number of warning machine
            if ((Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(tempWarningValue)
                    & Double.parseDouble(machine.getmachineTemp()) < Double.parseDouble(tempCriticalValue))
                    | (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(veloWarningValue)
                    & Double.parseDouble(machine.getmachineVelo()) <= Double.parseDouble(veloCriticalValue))) {


                ///check whether particular machine have normal state
                if (DbHelper.checkMachineState(machine.getMachineID(), NORMAL) == true)
                {
                    noOfNorm--;
                }

                //update warning state on the row
                DbHelper.updateMachineState(machine.getMachineID(), WARNING);
                noOfWarn++;
            }
            //number of critical machine
            if (Double.parseDouble(machine.getmachineTemp()) >= Double.parseDouble(tempCriticalValue)
                    | (Double.parseDouble(machine.getmachineVelo()) >= Double.parseDouble(veloCriticalValue))) {

                ///check whether particular machine have normal state
                if (DbHelper.checkMachineState(machine.getMachineID(), NORMAL) == true)
                {
                    noOfNorm--;
                }

                ///check whether particular machine have warning state
                if (DbHelper.checkMachineState(machine.getMachineID(), WARNING) == true)
                {
                    noOfWarn--;
                }


                //update critical state on the row
                DbHelper.updateMachineState(machine.getMachineID(), CRITICAL);
                noOfCrit++;
            }
        }


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

        Log.d(TAG + " Machine Name ", machineID);
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

    //For testing
    private void checkDatabaseRecord()
    {
        if (!(myDatabaseList.isEmpty()))
        {
            myDatabaseList.clear();
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
                myDatabaseList.add(dbMachine);
            } while (cursor.moveToNext());
        }
    }

    private void seeValue() {
        int i = 1;
        int j = 1;
        for(Machine seeMachine : myMachineList){

            Log.i(TAG + "Server Array", String.valueOf(i) + " " + seeMachine.getMachineID() + " " + seeMachine.getmachineTemp() + " " + seeMachine.getmachineVelo() + " " + seeMachine.getMachineHour());
            i++;

        }

        for(Machine seedbMachine :  myDatabaseList){

            Log.i(TAG + "Database Array", String.valueOf(j) + " " + seedbMachine.getMachineID() + " " + seedbMachine.getmachineTemp() + " " + seedbMachine.getmachineVelo() + " " + seedbMachine.getMachineStatus());
            j++;


        }
        Log.d("Number of row in db ", String.valueOf(DbHelper.getRowsCount()));

    }



}
