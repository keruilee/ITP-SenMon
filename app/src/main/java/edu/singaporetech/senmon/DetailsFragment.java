package edu.singaporetech.senmon;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    //hardcode variables for testing
    public ArrayList<String> nameArray = new ArrayList(Arrays.asList(
            "Machine A",
            "Machine B",
            "Machine C",
            "Machine D",
            "Machine E",
            "Machine F",
            "Machine G",
            "Machine H"));

    public ArrayList<Double> veloArray = new ArrayList(Arrays.asList(
            15.00,
            21.122,
            25.55,
            37.21,
            40.57,
            59.00,
            5.22,
            21.2222));

    public ArrayList<Double> tempArray = new ArrayList(Arrays.asList(
            24.00,
            32.00,
            28.55,
            33.212,
            48.57,
            52.00,
            26.33,
            5.223));

    public ArrayList<String> hourArray = new ArrayList(Arrays.asList(
            "200h 10min",
            "2h 4min",
            "5h 1min",
            "33h 12min",
            "24h 100min",
            "4h 94min",
            "23h 49min",
            "66h 49min"));

    String startDate = "04/18/2012 09:29:58";
    String endDate = "04/20/2012 15:42:41";
    String time = "";



    //Declare variables
    String TAG = "Details Fragment";
    private TextView tvDMachineName, tvDTemperature, tvDVelocity, tvDHour, tvDFavourite, tvDNoFavourite;
    String machineName ="";
    String machineID = "";
    View v;
    private FavouriteDatabaseHelper databaseHelper;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);

        //Set variables
        tvDMachineName = (TextView) v.findViewById(R.id.tvMachineName);
        tvDTemperature = (TextView) v.findViewById(R.id.tvTemperatureField);
        tvDVelocity = (TextView) v.findViewById(R.id.tvVelocityField);
        tvDHour = (TextView) v.findViewById(R.id.tvHourField);
        tvDFavourite = (TextView) v.findViewById(R.id.btnfavourite);
        tvDNoFavourite = (TextView) v.findViewById(R.id.btnnofavourite);

        //retrieving data using bundle
        Bundle bundle = getArguments();
        Bundle bundle2 = getArguments();
        Bundle bundle3 = getArguments();

        if(bundle != null) {
            tvDMachineName.setText(String.valueOf(bundle.getString("name")));
            machineName = bundle.getString("name");
        }

        if(bundle2 != null) {
            tvDMachineName.setText(machineID);
            machineID = bundle2.getString("name");
        }

        if(bundle3 != null) {
            tvDMachineName.setText(machineID);
            machineID = bundle3.getString("name");
        }


        // intent the machine id and display to on textview
//        textViewMachineId.setText(machine);
//        Bundle extras = getIntent().getExtras();
//        machineID = extras.getString("MachineID");
//        textViewMachineId.setText(machineID);

        //database helper
        databaseHelper = new FavouriteDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        //call compute time
        //time = computeTime(startDate,endDate);

        //call machine details
        machineDetails(machineName);

        if (checkEvent(machineID) == false)
        {
            //tvDFavourite.setText("Click to favourite");
            tvDNoFavourite.setVisibility(View.VISIBLE);


        }
        else
        {
            //tvDFavourite.setText("Click to unfavourite");
            tvDFavourite.setVisibility(View.VISIBLE);
        }


        tvDFavourite.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (checkEvent(machineID) == false)
                {

                    databaseHelper.addmachineID(machineID);
                   /* ContentValues values = new ContentValues();
                    values.put(databaseHelper.MACHINEID, machineID);
                    db.insert(databaseHelper.TABLE_NAME, null, values);*/
                    checkEvent(machineID);
                    //tvDFavourite.setText("Click to unfavourite");
                    tvDFavourite.setVisibility(View.VISIBLE);


                }
                else if (checkEvent(machineID) == true)

                {
                    databaseHelper.removeMachineID(machineID);
                    checkEvent(machineID);
                    //tvDFavourite.setText("Click to favourite");
                    tvDNoFavourite.setVisibility(View.VISIBLE);
                }
            }});

        return v;
    }


    //Retrieve machine details
    private void machineDetails(String machineName) {

        for (int i=0; i<nameArray.size();i++)
        {
            if(machineName.equals(nameArray.get(i)))
            {
                //check temperature value range
                if (tempArray.get(i) >= 0 & tempArray.get(i) < 21) {
                    //Normal state text color
                    tvDTemperature.setTextColor(Color.parseColor("#0B610B"));
                }
                else if (tempArray.get(i) >= 21 & tempArray.get(i) < 31) {
                    //Warning state text color
                    tvDTemperature.setTextColor(Color.parseColor("#8A4B08"));
                }
                else {
                    //Critical state text color
                    tvDTemperature.setTextColor(Color.parseColor("#FE2E2E"));
                }

                //check velocity value range
                if (veloArray.get(i) >= 0 & veloArray.get(i) < 21) {
                    //Normal state text color
                    tvDVelocity.setTextColor(Color.parseColor("#0B610B"));
                }
                else if (veloArray.get(i) >= 21 & veloArray.get(i) < 31) {
                    //Warning state text color
                    tvDVelocity.setTextColor(Color.parseColor("#8A4B08"));
                }
                else {
                    //Critical state text color
                    tvDVelocity.setTextColor(Color.parseColor("#FE2E2E"));
                }

                tvDTemperature.setText(String.valueOf(tempArray.get(i)));
                tvDVelocity.setText(String.valueOf(veloArray.get(i)));
                tvDHour.setText(String.valueOf(hourArray.get(i)));
                //tvDHour.setText(time);
            }
        }

    }

    //Calculate time differences once detect machine is in "off" state
    private String computeTime (String startDate, String endDate) {

        //Declare variables
        String timediff = "";
        long diff = 0L, diffSeconds = 0L, diffMinutes = 0L, diffHours = 0L, diffDays = 0L;

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        java.util.Date d1 = null;
        java.util.Date d2 = null;

        try {
            d1 = format.parse(startDate);
            d2 = format.parse(endDate);

            //in milliseconds
            diff = d2.getTime() - d1.getTime();

            diffSeconds = diff / 1000 % 60;
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        timediff = Long.toString(diffDays) + " Day " + Long.toString(diffHours) + " Hours " + Long.toString(diffMinutes)
                + " Min " + Long.toString(diffSeconds) + " Sec ";

        Log.i(TAG + " Day ", String.valueOf(Long.toString(diffDays)));
        Log.i(TAG + " Hour ", String.valueOf(Long.toString(diffHours)));
        Log.i(TAG + " Minute ", String.valueOf(Long.toString(diffMinutes)));
        Log.i(TAG + " Second ", String.valueOf(Long.toString(diffSeconds)));
        Log.i(TAG + " Total ", String.valueOf(Long.toString(diffDays) + " " + Long.toString(diffHours) + " " + Long.toString(diffMinutes)
                + " " + Long.toString(diffSeconds)));


        return timediff;
    }

    // to check if the machineID has already stored in the databasehelper
    public boolean checkEvent(String machineID)
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String queryString = "SELECT * FROM FavouriteTable WHERE machine = '"+machineID+"'";
        Cursor c = db.rawQuery(queryString, null);
        if(c.getCount() > 0){
            Log.i("CHECK", "true , found in the database");
            return true;
        }
        else{
            Log.i("CHECK", "false, not found in the databse");
            return false;
        }

    }

}
