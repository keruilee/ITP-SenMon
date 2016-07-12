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
public class TempYAxisValueFormatter implements YAxisValueFormatter {
    private DecimalFormat mFormat;
    private Context context;

    public TempYAxisValueFormatter (Context c) {
        mFormat = new DecimalFormat("########0.0"); // use one decimal
        context = c;
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) + context.getString(R.string.temp_unit); // append temperature unit to value
    }
}
