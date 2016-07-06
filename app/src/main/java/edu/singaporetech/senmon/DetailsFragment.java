package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    Context context;

    //test computation of datetime
    String startDate = "04/18/2012 09:29:58";
    String endDate = "04/20/2012 15:42:41";
    String time = "";


    //Declare variables
    String TAG = "Details Fragment";
    private TextView tvDMachineID, tvDTemperature, tvDVelocity, tvDHour, tvDFavourite, tvDNoFavourite, tvDShare;
    String machineID = "";
    String tempValue, veloValue;
    View v;
    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";
    private FavouriteDatabaseHelper databaseHelper;
    private TabLayout tabLayout;
    ViewPager viewPager;
    ViewPageAdapter viewPagerAdapter;
    View content;

    ProgressDialog progressDialog;
    JSONArray serverCSVrecords = null;
    private static final String TAG_RESULTS = "result";
    public String[] latestRecords;
    public String[] allCSVRecords;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        setHasOptionsMenu(true);

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
        tvDMachineID = (TextView) v.findViewById(R.id.tvMachineName);
        tvDTemperature = (TextView) v.findViewById(R.id.tvTemperatureField);
        tvDVelocity = (TextView) v.findViewById(R.id.tvVelocityField);
        tvDHour = (TextView) v.findViewById(R.id.tvHourField);
        tvDFavourite = (TextView) v.findViewById(R.id.btnfavourite);
        tvDNoFavourite = (TextView) v.findViewById(R.id.btnnofavourite);
        tvDShare = (TextView) v.findViewById(R.id.shareBtn);

        //retrieving data using bundle
        Bundle bundle = getArguments();

        //For home/list/favorite fragment
        if (bundle != null) {
            tvDMachineID.setText(String.valueOf(bundle.getString("name")));
            machineID = bundle.getString("name");
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


        if (checkEvent(machineID) == false) {
            //tvDFavourite.setText("Click to favourite");
            tvDNoFavourite.setVisibility(View.VISIBLE);
            tvDFavourite.setVisibility(View.INVISIBLE);
        } else {
            //tvDFavourite.setText("Click to unfavourite");
            tvDFavourite.setVisibility(View.VISIBLE);
            tvDNoFavourite.setVisibility(View.INVISIBLE);
        }


        tvDNoFavourite.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (checkEvent(machineID) == false) {

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

            }
        });

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
            }
        });

        //share a screenshot of the machine details
        tvDShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = screenShot(getView().getRootView());
                File file = saveBitmap(bm, machineID + "_details.png");
                Log.i("chase", "filepath: " + file.getAbsolutePath());
                Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, machineID);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share Via"));
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        //retrieve data
        getCSVData();

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

    //function to take a screenshot of the current page
    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

//        view.setDrawingCacheEnabled(true);
//        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
//        view.buildDrawingCache();
//
//        if(view.getDrawingCache() == null) return null;
//
//        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
//        view.setDrawingCacheEnabled(false);
//        view.destroyDrawingCache();
//
//        return snapshot;
//
//        View rootView = getView();
//        rootView.setDrawingCacheEnabled(true);
//        return rootView.getDrawingCache();
    }

    //save the image
    private static File saveBitmap(Bitmap bm, String fileName) {
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //Set detail color
    private void detailColor() {
        //check temperature value range color
        if (Double.parseDouble(tempValue) < Double.parseDouble(tempWarningValue)) {
            //Normal state text color
            tvDTemperature.setTextColor(ContextCompat.getColor(context, R.color.colorNormal));
        } else if ((Double.parseDouble(tempValue) >= Double.parseDouble(tempWarningValue)
                & Double.parseDouble(tempValue) < Double.parseDouble(tempCriticalValue))) {
            //Warning state text color
            tvDTemperature.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
        } else {
            //Critical state text color
            tvDTemperature.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
        }

        //check velocity value range color
        if (Double.parseDouble(veloValue) < Double.parseDouble(veloWarningValue)) {
            //Normal state text color
            tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorNormal));
        } else if (Double.parseDouble(veloValue) >= Double.parseDouble(veloWarningValue)
                & Double.parseDouble(veloValue) <= Double.parseDouble(veloCriticalValue)) {
            //Warning state text color
            tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
        } else {
            //Critical state text color
            tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
        }

    }


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

                //set machine detail color
                detailColor();

                progressDialog.dismiss();
            }
        }
        GetCSVDataJSON g = new GetCSVDataJSON();
        g.execute();
    }

    //Get the server CSV records
    public void getCSVRecords(JSONObject jsonObj) {
        try {
            serverCSVrecords = jsonObj.getJSONArray(TAG_RESULTS);

            String cleanupLatestRecords;

            //remove all unwanted symbols and text
            cleanupLatestRecords = serverCSVrecords.toString().replaceAll(",false]]", "").replace("[[", "").replace("[", "").replace("]]", "").replace("\"", "").replace("]", "");
            //split different csv records, the ending of each csv record list is machineID.csv
            allCSVRecords = cleanupLatestRecords.split(".csv,");
            //loop through each csv and get the latest records and split each field
            for (String record : allCSVRecords) {
                latestRecords = record.split(",");

                if (machineID.equals(latestRecords[9].replace(".csv", ""))) {
                    tempValue = latestRecords[6];
                    veloValue = latestRecords[5];
                    tvDTemperature.setText(tempValue);
                    tvDVelocity.setText(veloValue);
                    tvDHour.setText("22");
                }
            }

            Log.d("cleanupLatestRecords: ", cleanupLatestRecords);
            Log.d("CSVRecords2: ", allCSVRecords[1]);
            Log.d("LatestRecords: ", latestRecords[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Calculate time differences of machine
    private String computeTime(String startDate, String endDate) {

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
        } catch (Exception e) {
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
    public boolean checkEvent(String machineID) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String queryString = "SELECT * FROM FavouriteTable WHERE machine = '" + machineID + "'";
        Cursor c = db.rawQuery(queryString, null);
        if (c.getCount() > 0) {
            Log.i("CHECK", "true , found in the database");
            return true;
        } else {
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
