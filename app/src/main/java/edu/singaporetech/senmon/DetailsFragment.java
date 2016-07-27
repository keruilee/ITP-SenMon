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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {

    //Declare variables
    String TAG = "Details Fragment";
    private TextView tvDMachineID, tvDTemperature, tvDVelocity, tvDHour, tvNoData;
    private ImageView tvDFavourite, tvDShare;
    String machineID = "";
    String tempValue, veloValue;
    View v;
    Context context;
    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";
    public static final String NumberOfFavourite = "numOfFav";
    int count = 0;
    private DatabaseHelper favDatabasehelper;

    View content;

    ProgressDialog progressDialog;
    private static final String TAG_RESULTS = "result";

    private TabLayout tabLayout;

    private RelativeLayout lineChartLayout;
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
    private static final String TEMP_NAME = "TEMPERATURE", VELO_NAME = "VELOCITY";
    MyMarkerView mv;

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
        tvNoData = (TextView) v.findViewById(R.id.tvNoData);

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
        favDatabasehelper = new DatabaseHelper((getContext()));

        //retrieve range values
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display it
        tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_warning_value))));
        tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, String.valueOf(Double.parseDouble(getString(R.string.temp_critical_value))));
        veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_warning_value))));
        veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, String.valueOf(Double.parseDouble(getString(R.string.velo_critical_value))));


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
            SQLiteDatabase testDb = favDatabasehelper.getWritableDatabase();
            values.put(favDatabasehelper.MACHINEID, machineID); // KR do take note might need to change as update
            values.put(favDatabasehelper.MACHINEFAVOURITESTATUS, "no");
            testDb.insert(favDatabasehelper.TABLE_NAME, null, values);


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
                if(tabLayout.getSelectedTabPosition() == 0) {
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
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });

        progressDialog = new ProgressDialog(getActivity());

        // Give the TabLayout the ViewPage
        tabLayout = (TabLayout) v.findViewById(R.id.graph_tabs);

        //retrieve data
        getCSVData();

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
                    SQLiteDatabase testDb = favDatabasehelper.getWritableDatabase();
                    // KR do take note might need to change as update
                    testDb.execSQL("UPDATE DatabaseTable SET machineFavouriteStatus = NULL WHERE machineID = '" + machineID + "'");
                    checkEventForDataBaseHelperFavourite(machineID);
                    tvDFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_unfavourite));
                    int count = favDatabasehelper.checkNumberOfFavouriteMachineInAlert();
                    sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt(NumberOfFavourite, count);
                    editor.apply();
                    Log.d("FAAVOURITE COUNT", count + "");
                    Toast.makeText(getActivity(), "Removed from Favourite List", Toast.LENGTH_SHORT).show();
                }
                else                                // machine id not in fav, add to fav
                {
                    ContentValues values = new ContentValues();
                    SQLiteDatabase testDb = favDatabasehelper.getWritableDatabase();
                    // KR do take note might need to change as update
                    testDb.execSQL("UPDATE DatabaseTable SET machineFavouriteStatus = 'yes' WHERE machineID = '" + machineID + "'");
                    checkEventForDataBaseHelperFavourite(machineID);
                    //tvDFavourite.setText("Click to unfavourite");
                    tvDFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_favourite));


                    count = favDatabasehelper.checkNumberOfFavouriteMachineInAlert();
                    sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt(NumberOfFavourite, count);
                    editor.apply();
                    Log.d("FAAVOURITE COUNT", count + "");
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
        } else if (Double.parseDouble(tempValue) < Double.parseDouble(tempCriticalValue)) {
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
        } else if (Double.parseDouble(veloValue) < Double.parseDouble(veloCriticalValue)) {
            //Warning state text color
            tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
        } else {
            //Critical state text color
            tvDVelocity.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
        }
    }

    public String checkEventForDataBaseHelperFavourite(String machineID) {
        SQLiteDatabase db = favDatabasehelper.getWritableDatabase();
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

    //Added by Kerui
    public void getCSVData() {
        class GetCSVDataJSON extends AsyncTask<Void, Void, JSONObject> {

            String data;
            URL encodedUrl;
            HttpURLConnection urlConnection = null;

            String url = "http://itpsenmon.net23.net/readAllSQLRecords.php";

            JSONObject responseObj;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Loading Records...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
                try
                {
                    data = URLEncoder.encode("machine", "UTF-8")
                            + "=" + URLEncoder.encode(machineID, "UTF-8");
                }
                catch(Exception e)
                {
                }
                lineChart.setNoDataText("Loading graph...");
                stackedChart.setNoDataText("Loading graph...");
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {

                    encodedUrl = new URL(url);
                    urlConnection = (HttpURLConnection) encodedUrl.openConnection();
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
                progressDialog.dismiss();
                setupLineChart();
                setupStackedChart();
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
            int i = 0, numOfOff = 0, numOfNormal = 0, numOfWarning = 0, numOfCritical = 0, opHours = 0;
            String[] currentRecord;
            int numOfRecords = serverCSVrecords.length();
            boolean withDate = false, beforeNoon = false;


            // got at least 1 record
            if(serverCSVrecords.length() > 0) {
                // load last record first
                String object = serverCSVrecords.get(numOfRecords - 1).toString();
                object = object.replace("\r", "").replace("\n", "").replace("\"","");
                currentRecord = object.split(",");
                tempValue = currentRecord[7];
                veloValue = currentRecord[6];
                tvDTemperature.setText(tempValue);
                tvDVelocity.setText(veloValue);
                detailColor();

                SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

                Date recordDate, recordTime;
                String recordDateString, recordTimeString;

                // load other records for graph
                while (i < serverCSVrecords.length()) {
                    object = serverCSVrecords.get(i).toString();
                    object = object.replace("\r", "").replace("\n", "").replace("\"","").replace("\\","");
                    currentRecord = object.split(",");
                    recordDate = dateFormatter.parse(currentRecord[1]);
                    recordTime = timeFormatter.parse(currentRecord[2]);
                    recordDateString = dateFormatter.format(recordDate);
                    recordTimeString = timeFormatter.format(recordTime);

                    if (i == 0)
                    {
                        stackedXVals.add(recordDateString);
                        if(recordTime.before(timeFormatter.parse("12:00")))          // time is past 12 noon (12:00 onwards)
                            beforeNoon = true;
                    }
                    // determine whether to add x-label as date+time or time only
                    // first time after 12am and 12pm will be the label with full date+time
                    // the other labels in between will only have time
                    if(!withDate && beforeNoon && recordTime.before(timeFormatter.parse("12:00")))          // first label after 12am, date+time
                    {
                        lineXVals.add(recordDateString +" " +recordTimeString);
                        beforeNoon = false;
                        withDate = true;
                    }
                    else if(!withDate && !beforeNoon && !recordTime.before(timeFormatter.parse("12:00")))   // first label after 12pm, date+time
                    {
                        lineXVals.add(recordDateString +" " +recordTimeString);
                        beforeNoon = true;
                        withDate = true;
                    }
                    else            // time only
                    {
                        lineXVals.add(recordTimeString);
                        withDate = false;
                    }

                    float tempValue, velValue;

                    // for line charts
                    try {
                        recordDate = dateTimeFormatter.parse(currentRecord[1] + " " + currentRecord[2]);
                    } catch (Exception e) {
                        continue;   // date in wrong format; skip current record
                    }
                    recordDateString = dateTimeFormatter.format(recordDate);
                    tempValue = Float.parseFloat(currentRecord[7]);
                    velValue = Float.parseFloat(currentRecord[6]);
                    tempYVals.add(new Entry(tempValue, i));
                    veloYVals.add(new Entry(velValue, i));

                    // here onwards: for stacked bar chart
                    recordDateString = dateFormatter.format(recordDate);

                    // new date = new x label; add date to x-axis and reset states variable to zero
                    if (!recordDateString.equals(stackedXVals.get(stackedXVals.size() - 1))) {
                        stackedYVals.add(new BarEntry(new float[]{numOfOff, numOfNormal, numOfWarning, numOfCritical}, stackedXVals.size() - 1));
                        numOfOff = 0;
                        numOfNormal = 0;
                        numOfWarning = 0;
                        numOfCritical = 0;
                        stackedXVals.add(recordDateString);
                    }

                    // determine state of machine
                    if (velValue >= Float.parseFloat(veloCriticalValue) || tempValue >= Float.parseFloat(tempCriticalValue))
                        numOfCritical++;
                    else if (velValue >= Float.parseFloat(veloWarningValue) || tempValue >= Float.parseFloat(veloWarningValue))
                        numOfWarning++;
                    else if(velValue > 0 || tempValue > 0)
                        numOfNormal++;
                    else
                        numOfOff++;

                    if(velValue == 0)           // check if current row indicates machine is off
                        opHours = 0;
                    else
                        opHours++;
                    i++;
                }
                stackedYVals.add(new BarEntry(new float[] { numOfOff, numOfNormal, numOfWarning, numOfCritical }, stackedXVals.size()-1));
                tvDHour.setText(Integer.toString(opHours));
            }
            else {      // no records found for machine
                lineChart.setNoDataText("No records found");
                stackedChart.setNoDataText("No records found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // set up line chart with preferred settings
    private void setupLineChart() {
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        // no description textelse
        lineChart.setDescription("");
        lineChart.setNoDataText("Loading graph...");

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
        xAxis.setDrawGridLines(true);

        //xAxis.setLabelsToSkip(6);
        leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinValue(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setValueFormatter(new TempYAxisValueFormatter(context));
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextColor(Color.parseColor("#2c3e50"));
        leftAxis.setSpaceTop(50);

        rightAxis = lineChart.getAxisRight();
        rightAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        rightAxis.setAxisMinValue(0f);
        rightAxis.enableGridDashedLine(10f, 10f, 0f);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setValueFormatter(new VelYAxisValueFormatter(context));
        rightAxis.setTextColor(Color.parseColor("#3498db"));
        rightAxis.setDrawGridLines(false);

        mv = new MyMarkerView(this.getContext(), R.layout.custom_marker_view);

        // set the marker to the chart
        lineChart.setMarkerView(mv);

        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);

        lineChart.setOnChartValueSelectedListener(this);
        insertLineData();
    }

    // put data into line chart
    private void insertLineData() {
        // create a dataset and give it a type
        tempSet = new LineDataSet(tempYVals, TEMP_NAME);
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


        veloSet = new LineDataSet(veloYVals, VELO_NAME);
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
        //
        // set data
        lineChart.setData(data);

        // set viewport
        lineChart.zoom((lineXVals.size() / 10), 0, lineXVals.size()-1, tempYVals.size()-1);
        lineChart.moveViewToX(lineXVals.size() - 1);
    }

    // set up stacked chart with preferred settings
    private void setupStackedChart() {
        insertStackedData();

        //stackedChart.setOnChartValueSelectedListener(this);

        stackedChart.setNoDataText("Loading graph...");
        stackedChart.setDescription("");
        // scaling can now only be done on x- and y-axis separately
        stackedChart.setScaleXEnabled(true);
        stackedChart.setScaleYEnabled(false);
        stackedChart.setDrawGridBackground(false);
        stackedChart.setDrawBarShadow(false);
        stackedChart.setVisibleXRangeMaximum(7);
        stackedChart.setDrawValueAboveBar(false);

        // change the position of the y-labels
        YAxis leftAxis = stackedChart.getAxisLeft();
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        stackedChart.getAxisRight().setEnabled(false);

        XAxis xLabels = stackedChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);

        Legend l = stackedChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
    }

    // put data into stacked chart
    private void insertStackedData() {
        BarDataSet set1;

        set1 = new BarDataSet(stackedYVals, "| Machine States Count / Day");
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"Off", "Safe", "Warning", "Critical"});

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(stackedXVals, dataSets);
        data.setValueFormatter(new WholeNumValueFormatter());
        stackedChart.setData(data);

        if(stackedXVals.size() > 7)
            stackedChart.moveViewToX(stackedXVals.size() - 7);

        stackedChart.invalidate();
    }

    // set up colours for stacked chart
    private int[] getColors() {

        int stacksize = 4;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        colors[0] = getResources().getColor(R.color.colorPrimary);
        colors[1] = getResources().getColor(R.color.colorNormal);
        colors[2] = getResources().getColor(R.color.colorWarning);
        colors[3] = getResources().getColor(R.color.colorCritical);

        return colors;
    }

    // update line chart when checkboxes are pressed
    private void updateLineChart(String datasetSelected, boolean added)
    {
        LineData data = lineChart.getData();
        if(added){
            if(data.getDataSetCount() <= 0)
            {
                lineChart.setVisibility(View.VISIBLE);
                tvNoData.setVisibility(View.INVISIBLE);
                cbLines.setEnabled(true);
            }
            if(datasetSelected.equals(TEMP_NAME))
            {
                data.addDataSet(tempSet);
                leftAxis.setDrawLabels(true);
                leftAxis.setEnabled(true);
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
                rightAxis.setEnabled(true);
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
            if(datasetSelected.equals(TEMP_NAME))
            {
                leftAxis.setDrawLabels(false);
                leftAxis.setEnabled(false);
                leftAxis.removeAllLimitLines();
            }
            else
            {
                rightAxis.setDrawLabels(false);
                rightAxis.setEnabled(false);
                rightAxis.removeAllLimitLines();
            }
            if(data.getDataSetCount() <= 0)
            {
                lineChart.setVisibility(View.INVISIBLE);
                tvNoData.setVisibility(View.VISIBLE);
                cbLines.setEnabled(false);
            }
        }
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    // when an entry is selected, pass the full datetime and correct unit to markerview to display
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        ArrayList<ILineDataSet> dataSets = (ArrayList) lineChart.getData().getDataSets();
        if(dataSets.get(dataSetIndex).getLabel().equals(TEMP_NAME))
            mv.setUnit(getString(R.string.temp_unit));
        else
            mv.setUnit(getString(R.string.velo_unit));

        // get date+time of selected entry
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
        int currentIndex = h.getXIndex();
        Date currentDate;
        try {
            currentDate = dateTimeFormatter.parse(lineXVals.get(currentIndex)); // x-label is a valid datetime
            mv.setDate(dateTimeFormatter.format(currentDate));
            return;
        } catch (ParseException ex) {
            //ex.printStackTrace();
        }
        // x-label is not a valid datetime (only have time don't have date)
        // iterate through previous values to get the date of current entry
        int newIndex = currentIndex;
        while(true) {
            newIndex--;
            try {
                currentDate = dateFormatter.parse(lineXVals.get(newIndex));
                mv.setDate(dateFormatter.format(currentDate) +" " +lineXVals.get(currentIndex));
                return;
            } catch (ParseException ex) {
                //ex.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected() {
    }
}
