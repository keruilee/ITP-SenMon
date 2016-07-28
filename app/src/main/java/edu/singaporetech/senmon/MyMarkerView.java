
package edu.singaporetech.senmon;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent, tvDate;
    private String date, unit;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
        tvDate = (TextView) findViewById(R.id.tvDate);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvDate.setText(date);
        tvContent.setText(e.getVal() +unit);
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }

    /**
     * set the date to display in the marker
     * @param selectedDate - date of selected point
     */
    public void setDate(String selectedDate) {
        date = selectedDate;
    }

    /**
     * set the unit to display in the marker
     * °C for temperature
     * mm/s for velocity
     * @param selectedUnit - unit of selected point depending on whether its temperature or velocity data
     */
    public void setUnit(String selectedUnit) {
        unit = selectedUnit;
    }
}
