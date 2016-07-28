package edu.singaporetech.senmon;

import android.content.Context;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Meixi on 1/7/2016.
 */
public class VelYAxisValueFormatter implements YAxisValueFormatter {
    private DecimalFormat mFormat;
    private Context context;

    /**
     * determine the format to display values
     * @param c
     */
    public VelYAxisValueFormatter (Context c) {
        mFormat = new DecimalFormat("########0.0"); // use one decimal
        context = c;
    }

    /**
     * return the values in the selected format
     * @param value - value to be displayed
     * @param yAxis
     * @return
     */
    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) + context.getString(R.string.velo_unit); // append velocity unit to value
    }
}
