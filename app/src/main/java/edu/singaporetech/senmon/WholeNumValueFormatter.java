package edu.singaporetech.senmon;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Meixi on 1/7/2016.
 */
public class WholeNumValueFormatter implements ValueFormatter {
    private DecimalFormat mFormat;

    /**
     * determine the format to display values
     */
    public WholeNumValueFormatter() {
        mFormat = new DecimalFormat("#########0"); // only whole numbers, no decimal
    }

    /**
     * return the values in the selected format
     * @param value
     * @param entry
     * @param dataSetIndex
     * @param viewPortHandler
     * @return
     */
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if(value == 0)
            return "";                      // do not display anything if value is 0
        return mFormat.format(value);
    }
}
