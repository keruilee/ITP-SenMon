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
public class WholeNumValueFormatter implements ValueFormatter {
    private DecimalFormat mFormat;

    public WholeNumValueFormatter() {
        mFormat = new DecimalFormat("#########0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        // access the YAxis object to get more information
        return mFormat.format(value); // e.g. append a dollar-sign
    }
}
