package edu.singaporetech.senmon;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    Context context;

    private String graphNames[] = new String[] { "TEMPERATURE", "VELOCITY", "GRAPH 3" };

    private LineChart lineChart ;
    public static final String TAB_POSITION = "TAB_POSITION";
    public static final String MACHINE_NAME = "MACHINE_NAME";
    private int tabNo;

    private float criticalLine, warningLine;
    private String dataSetName;

    ArrayList<String> xVals = new ArrayList<String>();
    ArrayList<Entry> yVals = new ArrayList<Entry>();

    private static final String TAG_RESULTS="result";
    public String[][] allCSVRecords;
    private String machineName;

    public static GraphFragment newInstance(int n, String id) {
        Bundle args = new Bundle();
        args.putInt(TAB_POSITION, n);
        args.putString(MACHINE_NAME, id);
        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GraphFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graphs, container, false);

        tabNo = getArguments().getInt(TAB_POSITION);
        machineName = getArguments().getString(MACHINE_NAME);
        lineChart = (LineChart) v.findViewById(R.id.chart1);

        if(tabNo != 2)
            getCSVData();
        else
            initialSetup();

        return v;

    }

    private void initialSetup() {
        dataSetName = graphNames[tabNo];

        // clear previous values
        xVals.clear();
        yVals.clear();

        switch(tabNo)
        {
            case 0:             // graph at 1st tab
                // where the 2 lines will be
                criticalLine = 35f;
                warningLine = 20f;

                // plotting of data
                for(int i = 0; i < allCSVRecords.length; i++)
                {
                    xVals.add(allCSVRecords[i][0] +" " +allCSVRecords[i][1]);                      // x-axis values, can rename to anything
                    yVals.add(new Entry(new Float(allCSVRecords[i][6]), i));           // plotting of data on graph; new Entry(y value, x value).
                    // x value in Entry() just go accordingly by 0, 1, 2, 3, ...
                }
                break;
            case 1:             // graph at 2nd tab
                // where the 2 lines will be
                criticalLine = 60f;
                warningLine = 40f;

                // plotting of data
                for(int i = 0; i < allCSVRecords.length; i++)
                {
                    xVals.add(allCSVRecords[i][0] +" " +allCSVRecords[i][1]);                       // x-axis values, can rename to anything
                    yVals.add(new Entry(new Float(allCSVRecords[i][5]), i));           // plotting of data on graph; new Entry(y value, x value).
                    // x value in Entry() just go accordingly by 0, 1, 2, 3, ...
                }
                break;
            case 2:             // graph at 3rd tab
                // where the 2 lines will be
                criticalLine = 80f;
                warningLine = 50f;

                // plotting of data
                int count = 20;                 // number of points, used for looping when creating data
                for(int i = 0; i < count; i++)
                {
                    xVals.add("c" +i);                      // x-axis values, can rename to anything
                    yVals.add(new Entry(i*5, i));           // plotting of data on graph; new Entry(y value, x value).
                    // x value in Entry() just go accordingly by 0, 1, 2, 3, ...
                }
                break;
            default:
                break;
        }
        setupLineChart();
    }

    private void setupLineChart() {
        setLineData();

        lineChart.setDrawGridBackground(false);

        // no description text
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

        // limit lines
        LimitLine ll1 = new LimitLine(criticalLine, "Critical");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setLineColor(ContextCompat.getColor(context, R.color.colorCritical));
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(warningLine, "Warning");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setLineColor(ContextCompat.getColor(context, R.color.colorWarning));
        ll2.setTextSize(10f);

        // axes
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //xAxis.setLabelsToSkip(6);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        //leftAxis.setAxisMaxValue(100f);
        //leftAxis.setAxisMinValue(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        if(tabNo == 0)
            leftAxis.setValueFormatter(new TempValueFormatter());
        else if(tabNo == 1)
            leftAxis.setValueFormatter(new VelValueFormatter());

        lineChart.getAxisRight().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(this.getContext(), R.layout.custom_marker_view);

        // set the marker to the chart
        lineChart.setMarkerView(mv);

        //lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setLineData() {

        LineDataSet set1;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
            set1.setYVals(yVals);
            lineChart.getData().setXVals(xVals);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, dataSetName);

            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            set1.setDrawValues(false);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setHighlightLineWidth(1f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            // set data
            lineChart.setData(data);
            lineChart.notifyDataSetChanged();
        }
        lineChart.invalidate();
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
                            + "=" + URLEncoder.encode(machineName, "UTF-8");
                }
                catch(Exception e)
                {
                }
                lineChart.setNoDataTextDescription("Loading graph...");
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
            allCSVRecords = new String[serverCSVrecords.length()][];
            for(int i = 0; i<serverCSVrecords.length() ; i++){
                String object = serverCSVrecords.get(i).toString();
                object = object.replace("\r", "").replace("\n", "");
                String[] currentRecord = object.split(",");
                allCSVRecords[i] = currentRecord;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
