package edu.singaporetech.senmon;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    Context context;
    //hardcode array
    final ArrayList<Machine> myMachineList = new ArrayList<Machine>();

    //test computation of datetime
    String startDate = "04/18/2012 09:29:58";
    String endDate = "04/20/2012 15:42:41";
    String time = "";


    //Declare variables
    String TAG = "Details Fragment";
    private TextView tvDMachineName, tvDTemperature, tvDVelocity, tvDHour, tvDFavourite, tvDNoFavourite;
    String machineName ="";
    String machineID = "";
    View v;
    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs" ;
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";
    private FavouriteDatabaseHelper databaseHelper;
    private TabLayout tabLayout;
    ViewPager viewPager;
    ViewPageAdapter viewPagerAdapter;
    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);

        //Hardcode array
//        Machine machine = new Machine("SDK001-M001-01-0001a", "0.3", "36.11", "50");
//        Machine machine2 = new Machine("SDK221-M001-01-0001a", "0.2244", "10.11", "33");
//        Machine machine3 = new Machine("SDK331-M001-01-0001a", "0.293", "20.11", "53");
//        Machine machine4 = new Machine("ADK444-M001-01-0001a", "0.922", "30.11", "900");
//        Machine machine5 = new Machine("SDK555-M001-01-0001a", "0.312", "40.11", "6");
//        Machine machine6 = new Machine("SDK166-M001-01-0001a", "0.9222", "5.11", "3");
//
//        myMachineList.add(machine);
//        myMachineList.add(machine2);
//        myMachineList.add(machine3);
//        myMachineList.add(machine4);
//        myMachineList.add(machine5);
//        myMachineList.add(machine6);

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

        //For home fragment
        if(bundle != null) {
            tvDMachineName.setText(String.valueOf(bundle.getString("name")));
            machineName = bundle.getString("name");
        }

        //For list fragment
        if(bundle2 != null) {
            tvDMachineName.setText(String.valueOf(bundle2.getString("name")));
            machineID = bundle2.getString("name");
            tvDTemperature.setText(String.valueOf(bundle2.getString("temp")));
            tvDVelocity.setText(String.valueOf(bundle2.getString("velo")));
        }

        //For favorite fragment
        if(bundle3 != null) {
            tvDMachineName.setText(machineID);
            machineID = bundle3.getString("name");
        }

        //database helper
        databaseHelper = new FavouriteDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        //retrieve range values
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display it
        tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_warning_value))));
        tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_critical_value))));
        veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_warning_value))));
        veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_critical_value))));

        //call compute time
        //time = computeTime(startDate,endDate);

        //call machine details
        machineDetails(machineName);

        if (checkEvent(machineID) == false)
        {
            //tvDFavourite.setText("Click to favourite");
            tvDNoFavourite.setVisibility(View.VISIBLE);
            tvDFavourite.setVisibility(View.INVISIBLE);
        }
        else
        {
            //tvDFavourite.setText("Click to unfavourite");
            tvDFavourite.setVisibility(View.VISIBLE);
            tvDNoFavourite.setVisibility(View.INVISIBLE);
        }


        tvDNoFavourite.setOnClickListener(new View.OnClickListener() {

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
                    tvDNoFavourite.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Added into Favourite List", Toast.LENGTH_SHORT).show();

                }

            }});

        tvDFavourite.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (checkEvent(machineID) == true)

                {
                    databaseHelper.removeMachineID(machineID);
                    checkEvent(machineID);
                    //tvDFavourite.setText("Click to favourite");
                    tvDNoFavourite.setVisibility(View.VISIBLE);
                    tvDFavourite.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Removed from Favourite List", Toast.LENGTH_SHORT).show();
                }
            }});

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPageAdapter(getActivity().getSupportFragmentManager(),
                this.getContext(), machineID);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        // Give the TabLayout the ViewPage
        tabLayout = (TabLayout) v.findViewById(R.id.graph_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return v;
    }


    //Retrieve machine details
    private void machineDetails(String machineName) {

        Iterator<Machine> i = myMachineList.iterator();
        while (i.hasNext()) {
            Machine s = i.next();
            if (machineName.contains(s.getMachineID()))
            {
                //check temperature value range
                if (Double.parseDouble(s.getmachineTemp()) < Double.parseDouble(tempWarningValue)) {
                    //Normal state text color
                    tvDTemperature.setTextColor(ContextCompat.getColor(context, R.color.colorNormal));
                }
                else if ((Double.parseDouble(s.getmachineTemp()) >= Double.parseDouble(tempWarningValue)
                        & Double.parseDouble(s.getmachineTemp()) < Double.parseDouble(tempCriticalValue))) {
                    //Warning state text color
                    tvDTemperature.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
                }
                else {
                    //Critical state text color
                    tvDTemperature.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
                }

                //check velocity value range
                if (Double.parseDouble(s.getmachineVelo()) < Double.parseDouble(veloWarningValue)) {
                    //Normal state text color
                    tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorNormal));
                }
                else if (Double.parseDouble(s.getmachineVelo()) >= Double.parseDouble(veloWarningValue)
                        & Double.parseDouble(s.getmachineVelo()) <= Double.parseDouble(veloCriticalValue)) {
                    //Warning state text color
                    tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
                }
                else {
                    //Critical state text color
                    tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
                }

                tvDTemperature.setText(String.valueOf(Double.parseDouble(s.getmachineTemp())));
                tvDVelocity.setText(String.valueOf(Double.parseDouble(s.getmachineVelo())));
                tvDHour.setText(String.valueOf(Double.parseDouble(s.getMachineHour())));
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
            Log.i("CHECK", "false, not found in the database");
            return false;
        }

    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        viewPager.invalidate();
        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(null);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        viewPager.setAdapter(viewPagerAdapter);
        super.onResume();
    }
}
