package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements View.OnClickListener {

    Context context;

    //test computation of datetime
    String startDate = "04/18/2012 09:29:58";
    String endDate = "04/20/2012 15:42:41";
    String time = "";


    //Declare variables
    String TAG = "Details Fragment";
    private TextView tvDMachineID, tvDTemperature, tvDVelocity, tvDHour;
    private ImageView tvDFavourite, tvDShare;
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
   // private FavouriteDatabaseHelper databaseHelper;

    private DatabaseHelper testDatabasehelper;

    View content;

    ProgressDialog progressDialog;
    JSONArray serverCSVrecords = null;
    private static final String TAG_RESULTS = "result";
    public String[] latestRecords;
    public String[] allCSVRecords;

    private TabLayout tabLayout;
    private RelativeLayout lineChartLayout;
    public String[][] allCSVRecords2;
    private CheckBox cbTemp, cbVelo, cbLines;
    private LineChart lineChart ;
    private YAxis leftAxis, rightAxis;
    private XAxis xAxis;
    private LimitLine tempCritLine, tempWarningLine, veloCriticalLine, veloWarningLine;
    private BarChart stackedChart;
    private ArrayList<String> lineXVals = new ArrayList<>();
    private ArrayList<String> stackedXVals = new ArrayList<>();
    private ArrayList<Entry> tempYVals = new ArrayList<>();
    private ArrayList<Entry> veloYVals = new ArrayList<>();
    private ArrayList<BarEntry> stackedYVals = new ArrayList<>();
    private LineDataSet tempSet, veloSet;

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

        //Set variables
        tvDMachineID = (TextView) v.findViewById(R.id.tvMachineName);
        tvDTemperature = (TextView) v.findViewById(R.id.tvTemperatureField);
        tvDVelocity = (TextView) v.findViewById(R.id.tvVelocityField);
        tvDHour = (TextView) v.findViewById(R.id.tvHourField);
        tvDFavourite = (ImageView) v.findViewById(R.id.btnfavourite);
        tvDShare = (ImageView) v.findViewById(R.id.shareBtn);

        lineChart = (LineChart) v.findViewById(R.id.lineChart);
        stackedChart = (BarChart) v.findViewById(R.id.barChart);

        tabLayout = (TabLayout) v.findViewById(R.id.graph_tabs);
        lineChartLayout = (RelativeLayout) v.findViewById(R.id.lineChartLayout);

        cbTemp = (CheckBox) v.findViewById(R.id.checkbox_temp);
        cbVelo = (CheckBox) v.findViewById(R.id.checkbox_velo);
        cbLines = (CheckBox) v.findViewById(R.id.checkbox_lines);

        //retrieving data using bundle
        Bundle bundle = getArguments();

        //For home/list/favorite fragment
        if (bundle != null) {
            tvDMachineID.setText(String.valueOf(bundle.getString("name")));
            machineID = bundle.getString("name");
            Log.i("WHAT IS in the bundle?" ,machineID );
        }

        // test for the new database helper
        testDatabasehelper = new DatabaseHelper((getContext()));

        //retrieve range values
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display it
        tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_warning_value))));
        tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_critical_value))));
        veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_warning_value))));
        veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_critical_value))));

        //call compute time
        //time = computeTime(startDate,endDate);

        String check = checkEventForDataBaseHelperFavourite(machineID);

        // to checked what is the favourite status , yes or no , if yes , set the star as higlighted
        if (check.equalsIgnoreCase("yes"))
        {
            //tvDFavourite.setText("Click to unfavourite");
            tvDFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_favourite));
            Log.i("status yes?", checkEventForDataBaseHelperFavourite(machineID));

        }
        else if(check.equalsIgnoreCase("not found"))
        {
            Log.i("status no found?", checkEventForDataBaseHelperFavourite(machineID));

            ContentValues values = new ContentValues();
            SQLiteDatabase testDb = testDatabasehelper.getWritableDatabase();
            values.put(testDatabasehelper.MACHINEID, machineID); // KR do take note might need to change as update
            values.put(testDatabasehelper.MACHINEFAVOURITESTATUS, "no");
            testDb.insert(testDatabasehelper.TABLE_NAME, null, values);
        }

        // set on click listeners for all clickable items
        tvDFavourite.setOnClickListener(this);
        tvDShare.setOnClickListener(this);
        cbTemp.setOnClickListener(this);
        cbVelo.setOnClickListener(this);
        cbLines.setOnClickListener(this);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TAG", "on tab selected");
                if(tabLayout.getSelectedTabPosition() < 1) {
                    lineChartLayout.setVisibility(View.VISIBLE);
                    stackedChart.setVisibility(View.INVISIBLE);
                }
                else
                {
                    lineChartLayout.setVisibility(View.INVISIBLE);
                    stackedChart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        progressDialog = new ProgressDialog(getActivity());

        //retrieve data
        getCSVDataHX();

        // Give the TabLayout the ViewPage
        tabLayout = (TabLayout) v.findViewById(R.id.graph_tabs);

        loadGraph();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnfavourite:
                if (checkEventForDataBaseHelperFavourite(machineID).equalsIgnoreCase("yes"))          // machine id exists in fav, remove from fav
                {
                    SQLiteDatabase testDb = testDatabasehelper.getWritableDatabase();
                    // KR do take note might need to change as update
                    testDb.execSQL("UPDATE DatabaseTable SET machineFavouriteStatus = NULL WHERE machineID = '" + machineID + "'");
                    checkEventForDataBaseHelperFavourite(machineID);
                    tvDFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_unfavourite));
                    Toast.makeText(getActivity(), "Removed from Favourite List", Toast.LENGTH_SHORT).show();
                }
                else                                // machine id not in fav, add to fav
                {
                    ContentValues values = new ContentValues();
                    SQLiteDatabase testDb = testDatabasehelper.getWritableDatabase();
                    // KR do take note might need to change as update
                    testDb.execSQL("UPDATE DatabaseTable SET machineFavouriteStatus = 'yes' WHERE machineID = '" + machineID + "'");
                    checkEventForDataBaseHelperFavourite(machineID);
                    //tvDFavourite.setText("Click to unfavourite");
                    tvDFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_favourite));
                    Toast.makeText(getActivity(), "Added into Favourite List", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.shareBtn:         //share a screenshot of the machine details
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
                break;
            case R.id.checkbox_temp:         //share a screenshot of the machine details
                updateLineChart(cbTemp.getText().toString(), cbTemp.isChecked());
                break;
            case R.id.checkbox_velo:         //share a screenshot of the machine details
                updateLineChart(cbVelo.getText().toString(), cbVelo.isChecked());
                break;
            case R.id.checkbox_lines:         //share a screenshot of the machine details
                if(cbLines.isChecked())
                {
                    if(cbTemp.isChecked())
                    {
                        leftAxis.addLimitLine(tempCritLine);
                        leftAxis.addLimitLine(tempWarningLine);
                    }
                    if(cbVelo.isChecked())
                    {
                        rightAxis.addLimitLine(veloCriticalLine);
                        rightAxis.addLimitLine(veloWarningLine);
                    }
                }
                else
                {
                    leftAxis.removeAllLimitLines();
                    rightAxis.removeAllLimitLines();
                }
                lineChart.invalidate();
                break;
            default:
                break;
        }
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


    public void getCSVDataHX() {
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
                getCSVRecordsHX(result);

                //set machine detail color
                detailColor();

                progressDialog.dismiss();
            }
        }
        GetCSVDataJSON g = new GetCSVDataJSON();
        g.execute();
    }

    //Get the server CSV records
    public void getCSVRecordsHX(JSONObject jsonObj) {
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


    public String checkEventForDataBaseHelperFavourite(String machineID) {
        SQLiteDatabase db = testDatabasehelper.getWritableDatabase();
        String statusForFavo;
        int index;

        String queryString = "SELECT * FROM DatabaseTable WHERE machineID = '" + machineID + "' ";
        Cursor c = db.rawQuery(queryString, null);
        if (c.getCount() > 0) {
            c.moveToFirst();

            statusForFavo = c.getString(c.getColumnIndex("machineFavouriteStatus"));
            Log.i("checkDataBaseHelper", statusForFavo + "");
            Log.i("c.getCount", c.getCount() + "");
            if (statusForFavo == null || statusForFavo.isEmpty()) {
                Log.i("dbHelper null?", statusForFavo + "");
                return "no";
            } else {
                return "yes";
            }

        } else {
            Log.i("checkDataBaseHelper", "false, not found in the database");
            return "not found";
        }
    }

    public void loadGraph() {
        setupLineChart();
        getCSVData();
    }

    //Added by Kerui
    public void getCSVData() {
        class GetCSVDataJSON extends AsyncTask<Void, Void, JSONObject> {

            String data;
            URL encodedUrl;
            HttpURLConnection urlConnection = null;

            String url = "http://itpsenmon.net23.net/readAllRecords.php";

            JSONObject responseObj;

            @Override
            protected void onPreExecute() {
                try
                {
                    data = URLEncoder.encode("machine", "UTF-8")
                            + "=" + URLEncoder.encode(machineID, "UTF-8");
                }
                catch(Exception e)
                {
                }
                lineChart.setNoDataTextDescription("Loading graph...");
                stackedChart.setNoDataTextDescription("Loading graph...");
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {

                    encodedUrl = new URL(url);
                    urlConnection = (HttpURLConnection) encodedUrl.openConnection();
                    //urlConnection.setUseCaches(false);
                    //urlConnection.setRequestProperty("Content-Type", "application/json");
                    //urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                    wr.write( data );
                    wr.flush();
                    //urlConnection.connect();

                    InputStream input = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
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
                initialSetup();                     // done loading, set up line chart
            }
        }
        GetCSVDataJSON g = new GetCSVDataJSON();
        g.execute();
    }

    //Get the server CSV records
    public void getCSVRecords(JSONObject jsonObj)
    {
        try {
            JSONArray serverCSVrecords = jsonObj.getJSONArray(TAG_RESULTS);
            allCSVRecords2 = new String[serverCSVrecords.length()][];
            for(int i = 0; i<serverCSVrecords.length() ; i++){
                String object = serverCSVrecords.get(i).toString();
                object = object.replace("\r", "").replace("\n", "");
                String[] currentRecord = object.split(",");
                allCSVRecords2[i] = currentRecord;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialSetup() {

        // clear previous values
        int i = 0, numOfNormal = 0, numOfWarning = 0, numOfCritical = 0;

        stackedXVals.add(allCSVRecords2[i][0]);
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
        float tempValue, velValue;
        Date recordDate;
        String recordDateString;
        while (i < allCSVRecords2.length)
        {
            // for line charts
            try {
                recordDate = dateTimeFormatter.parse(allCSVRecords2[i][0] + " " + allCSVRecords2[i][1]);
            } catch(Exception e) {
                continue;   // date in wrong format; skip current record
            }
            recordDateString = dateTimeFormatter.format(recordDate);
            tempValue = Float.parseFloat(allCSVRecords2[i][6]);
            velValue = Float.parseFloat(allCSVRecords2[i][5]);
            lineXVals.add(recordDateString);
            tempYVals.add(new Entry(tempValue, i));
            veloYVals.add(new Entry(velValue, i));

            // for stacked bar chart
            recordDateString = dateFormatter.format(recordDate);
            if(!recordDateString.equals(stackedXVals.get(stackedXVals.size()-1)))
            {
                stackedYVals.add(new BarEntry(new float[] { numOfNormal, numOfWarning, numOfCritical }, stackedXVals.size()-1));
                numOfNormal = 0;
                numOfWarning = 0;
                numOfCritical = 0;
                try {
                    stackedXVals.add(recordDateString);
                } catch(Exception e) {
                    stackedXVals.add(allCSVRecords2[i][0]);
                }
            }

            if(velValue >= Float.parseFloat(veloCriticalValue) || tempValue >= Float.parseFloat(tempCriticalValue))
                numOfCritical++;
            else if(velValue >= Float.parseFloat(veloWarningValue) || tempValue >= Float.parseFloat(veloWarningValue))
                numOfWarning++;
            else
                numOfNormal++;

            i++;
        }

        stackedYVals.add(new BarEntry(new float[] { numOfNormal, numOfWarning, numOfCritical }, stackedXVals.size()-1));

        setLineData();
        setupStackedChart();
    }

    private void setupLineChart() {
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        // no description textelse
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        //         limit lines
        tempCritLine = new LimitLine(Float.parseFloat(tempCriticalValue), "Critical");
        tempCritLine.setLineWidth(1f);
        tempCritLine.enableDashedLine(10f, 15f, 0f);
        tempCritLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        tempCritLine.setLineColor(Color.parseColor("#2c3e50"));
        tempCritLine.setTextSize(10f);
        tempCritLine.setTextColor(Color.parseColor("#2c3e50"));

        tempWarningLine = new LimitLine(Float.parseFloat(tempWarningValue), "Warning");
        tempWarningLine.setLineWidth(1f);
        tempWarningLine.enableDashedLine(10f, 15f, 0f);
        tempWarningLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        tempWarningLine.setLineColor(Color.parseColor("#2c3e50"));
        tempWarningLine.setTextSize(10f);
        tempWarningLine.setTextColor(Color.parseColor("#2c3e50"));

        veloCriticalLine = new LimitLine(Float.parseFloat(veloCriticalValue), "Critical");
        veloCriticalLine.setLineWidth(1f);
        veloCriticalLine.enableDashedLine(10f, 15f, 0f);
        veloCriticalLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        veloCriticalLine.setLineColor(Color.parseColor("#3498db"));
        veloCriticalLine.setTextSize(10f);
        veloCriticalLine.setTextColor(Color.parseColor("#3498db"));

        veloWarningLine = new LimitLine(Float.parseFloat(veloWarningValue), "Warning");
        veloWarningLine.setLineWidth(1f);
        veloWarningLine.enableDashedLine(10f, 15f, 0f);
        veloWarningLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        veloWarningLine.setLineColor(Color.parseColor("#3498db"));
        veloWarningLine.setTextSize(10f);
        veloWarningLine.setTextColor(Color.parseColor("#3498db"));

        // axes
        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);

        //xAxis.setLabelsToSkip(6);
        leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinValue(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setValueFormatter(new TempValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextColor(Color.parseColor("#2c3e50"));
        leftAxis.setSpaceTop(50);

        rightAxis = lineChart.getAxisRight();
        rightAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        rightAxis.setAxisMinValue(0f);
        rightAxis.enableGridDashedLine(10f, 10f, 0f);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setValueFormatter(new VelValueFormatter());
        rightAxis.setTextColor(Color.parseColor("#3498db"));
        rightAxis.setDrawGridLines(false);

        MyMarkerView mv = new MyMarkerView(this.getContext(), R.layout.custom_marker_view);

        // set the marker to the chart
        lineChart.setMarkerView(mv);

        if(lineXVals.size() > 30)
            lineChart.zoom(8, 0, lineXVals.size()-1, tempYVals.size()-1);
        lineChart.moveViewToX(lineXVals.size()-1);

        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
    }

    private void setLineData() {
        // create a dataset and give it a type
        tempSet = new LineDataSet(tempYVals, "TEMPERATURE");
        tempSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        // set the line to be drawn like this "- - - - - -"
        tempSet.setDrawValues(false);
        tempSet.enableDashedHighlightLine(10f, 5f, 0f);
        tempSet.setHighlightLineWidth(1f);
        tempSet.setColor(Color.parseColor("#2c3e50"));
        tempSet.setCircleColor(Color.parseColor("#2c3e50"));
        tempSet.setLineWidth(1f);
        tempSet.setCircleRadius(3f);
        tempSet.setDrawCircleHole(false);
        tempSet.setValueTextSize(9f);
        tempSet.setDrawFilled(false);

        veloSet = new LineDataSet(veloYVals, "VELOCITY");
        veloSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        // set the line to be drawn like this "- - - - - -"
        veloSet.setDrawValues(false);
        veloSet.enableDashedHighlightLine(10f, 5f, 0f);
        veloSet.setHighlightLineWidth(1f);
        veloSet.setColor(Color.parseColor("#3498db"));
        veloSet.setCircleColor(Color.parseColor("#3498db"));
        veloSet.setLineWidth(1f);
        veloSet.setCircleRadius(3f);
        veloSet.setDrawCircleHole(false);
        veloSet.setValueTextSize(9f);
        veloSet.setDrawFilled(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        if(cbTemp.isChecked())
            dataSets.add(tempSet); // add the datasets
        if(cbVelo.isChecked())
            dataSets.add(veloSet);

        // create a data object with the datasets
        LineData data = new LineData(lineXVals, dataSets);

        // set data
        lineChart.setData(data);
        lineChart.invalidate();         // refresh the graph
    }

    private void setupStackedChart() {
        setStackedData();
        //stackedChart.setOnChartValueSelectedListener(this);

        stackedChart.setDescription("");

        // scaling can now only be done on x- and y-axis separately
        stackedChart.setScaleXEnabled(true);
        stackedChart.setScaleYEnabled(false);
        stackedChart.setDrawGridBackground(false);
        stackedChart.setDrawBarShadow(false);
        stackedChart.setVisibleXRangeMaximum(7);
        if(stackedXVals.size() > 7)
            stackedChart.moveViewToX(stackedXVals.size() - 7);

        stackedChart.setDrawValueAboveBar(false);

        // change the position of the y-labels
        YAxis leftAxis = stackedChart.getAxisLeft();
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        stackedChart.getAxisRight().setEnabled(false);

        XAxis xLabels = stackedChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);

        Legend l = stackedChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
    }

    private void setStackedData() {
        BarDataSet set1;

        if (stackedChart.getData() != null &&
                stackedChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)stackedChart.getData().getDataSetByIndex(0);
            set1.setYVals(stackedYVals);
            stackedChart.getData().setXVals(stackedXVals);
            stackedChart.getData().notifyDataChanged();
            stackedChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(stackedYVals, "");
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{"Safe", "Warning", "Critical"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(stackedXVals, dataSets);

            stackedChart.setData(data);
        }

        stackedChart.invalidate();  // refresh the graph
    }

    private int[] getColors() {

        int stacksize = 3;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        colors[0] = getResources().getColor(R.color.colorNormal);
        colors[1] = getResources().getColor(R.color.colorWarning);
        colors[2] = getResources().getColor(R.color.colorCritical);

        return colors;
    }

    private void updateLineChart(String datasetSelected, boolean added)
    {
        LineData data = lineChart.getData();
        if(added){
            if(data.getDataSetCount() <= 0)
                xAxis.setDrawLabels(true);
            if(datasetSelected.equals("TEMPERATURE"))
            {
                data.addDataSet(tempSet);
                leftAxis.setDrawLabels(true);
                if(cbLines.isChecked())
                {
                    leftAxis.addLimitLine(tempCritLine);
                    leftAxis.addLimitLine(tempWarningLine);
                }
            }
            else
            {
                data.addDataSet(veloSet);
                rightAxis.setDrawLabels(true);
                if(cbLines.isChecked())
                {
                    rightAxis.addLimitLine(veloCriticalLine);
                    rightAxis.addLimitLine(veloWarningLine);
                }
            }
        }
        else if(data != null && data.getDataSetCount() > 0)
        {
            data.removeDataSet(data.getDataSetByLabel(datasetSelected, false));
            if(datasetSelected.equals("TEMPERATURE"))
            {
                leftAxis.setDrawLabels(false);
                leftAxis.removeAllLimitLines();
            }
            else
            {
                rightAxis.setDrawLabels(false);
                rightAxis.removeAllLimitLines();
            }
            if(data.getDataSetCount() <= 0)
                xAxis.setDrawLabels(false);
        }
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
}
