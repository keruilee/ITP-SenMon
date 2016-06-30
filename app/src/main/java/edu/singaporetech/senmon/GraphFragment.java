package edu.singaporetech.senmon;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.animation.Easing;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    Context context;

    private LineChart lineChart ;
    public static final String TAB_POSITION = "TAB_POSITION";

    private float criticalLine, warningLine;
    private String dataSetName;

    ArrayList<String> xVals = new ArrayList<String>();
    ArrayList<Entry> yVals = new ArrayList<Entry>();

    public static GraphFragment newInstance(int n) {
        Bundle args = new Bundle();
        args.putInt(TAB_POSITION, n);
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

        initialSetup(getArguments().getInt(TAB_POSITION));

        lineChart = (LineChart) v.findViewById(R.id.chart1);

        setupLineChart();

        return v;

    }

    private void initialSetup(int tabNo) {
        // clear previous values
        xVals.clear();
        yVals.clear();

        int count;              // number of points, used for looping when creating data

        switch(tabNo)
        {
            case 0:             // graph at 1st tab
                // name of dataset
                dataSetName = "GRAPH 1";

                // where the 2 lines will be
                criticalLine = 35f;
                warningLine = 20f;

                // plotting of data
                count = 10;                 // number of points, used for looping when creating data
                for(int i = 0; i < count; i++)
                {
                    xVals.add("a" +i);                      // x-axis values, can rename to anything
                    yVals.add(new Entry(i*5, i));           // plotting of data on graph; new Entry(y value, x value).
                    // x value in Entry() just go accordingly by 0, 1, 2, 3, ...
                }
                break;
            case 1:             // graph at 2nd tab
                // name of dataset
                dataSetName = "GRAPH 2";

                // where the 2 lines will be
                criticalLine = 60f;
                warningLine = 40f;

                // plotting of data
                count = 15;                 // number of points, used for looping when creating data
                for(int i = 0; i < count; i++)
                {
                    xVals.add("b" +i);                      // x-axis values, can rename to anything
                    yVals.add(new Entry(i*5, i));           // plotting of data on graph; new Entry(y value, x value).
                    // x value in Entry() just go accordingly by 0, 1, 2, 3, ...
                }
                break;
            case 2:             // graph at 3rd tab
                // name of dataset
                dataSetName = "GRAPH 3";

                // where the 2 lines will be
                criticalLine = 80f;
                warningLine = 50f;

                // plotting of data
                count = 20;                 // number of points, used for looping when creating data
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
    }
    private void setupLineChart() {
        lineChart.setDrawGridBackground(false);

        // no description text
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

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

        lineChart.getAxisRight().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(this.getContext(), R.layout.custom_marker_view);

        // set the marker to the chart
        lineChart.setMarkerView(mv);

        setLineData();

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
        }
    }
}
