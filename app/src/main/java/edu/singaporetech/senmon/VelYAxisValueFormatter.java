package edu.singaporetech.senmon;

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

    public VelYAxisValueFormatter () {
        mFormat = new DecimalFormat("########0.0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) + "mm/s"; // append velocity unit to value
    }
}
