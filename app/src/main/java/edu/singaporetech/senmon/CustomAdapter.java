package edu.singaporetech.senmon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jinyu on 14/6/2016.
 */
public class CustomAdapter extends ArrayAdapter<Machine> {


    String tempWarningValue, tempCriticalValue, veloWarningValue, veloCriticalValue;
    SharedPreferences RangeSharedPreferences;
    Context context;
    public static final String MyRangePREFERENCES = "MyRangePrefs" ;
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";

    public double tempvalue;
    public double velovalue;

    public CustomAdapter(Activity context, int textViewResourceId, ArrayList <Machine> myMachineList) {
        super(context,textViewResourceId, myMachineList );
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = this.getContext();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View rowView = inflater.inflate(R.layout.custom_row, null, true);

        TextView textViewMachineid = (TextView) rowView.findViewById(R.id.textViewmachineid);
        TextView textViewTemp = (TextView) rowView.findViewById(R.id.textViewTemp);
        TextView textViewVelo = (TextView) rowView.findViewById(R.id.textViewVelocity);

        Machine myMachine = getItem(position) ;

        textViewMachineid.setText(myMachine.getMachineID());
        textViewTemp.setText(myMachine.getmachineTemp());
        textViewVelo.setText(myMachine.getmachineVelo());



        tempvalue = Double.parseDouble(myMachine.getmachineTemp());
        velovalue = Double.parseDouble(myMachine.getmachineVelo());

        //retrieve range values
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display it
        tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, context.getString(R.string.temp_warning_value));
        tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, context.getString(R.string.temp_critical_value));
        veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, context.getString(R.string.velo_warning_value));
        veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, context.getString(R.string.velo_critical_value));

        //temp normal state
        if (tempvalue < Double.parseDouble(tempWarningValue))
        {
            textViewTemp.setTextColor(ContextCompat.getColor(context, R.color.colorNormal));
        }
        //temp warning state
        else if(tempvalue < Double.parseDouble(tempCriticalValue))
        {
            textViewTemp.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
        }
        //temp critical state
        else
        {
            textViewTemp.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
        }


        //velo normal state
        if (velovalue < Double.parseDouble(veloWarningValue))
        {
            textViewVelo.setTextColor(ContextCompat.getColor(context, R.color.colorNormal));
        }
        //velo warning state
        else if(velovalue < Double.parseDouble(veloCriticalValue))
        {
            textViewVelo.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
        }
        //velo critical state
        else
        {
            textViewVelo.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
        }

        return rowView;
    }

}