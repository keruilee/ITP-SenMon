package edu.singaporetech.senmon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinyu on 14/6/2016.
 */
public class CustomAdapter extends ArrayAdapter<Machine> {


    public double tempvalue;
    public double velovalue;

    Context context;
   // int layoutResourceId;
    //Machine data[] =null;


    public CustomAdapter(Activity context, int textViewResourceId, ArrayList <Machine> myMachineList) {
        super(context,textViewResourceId, myMachineList );
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = this.getContext();
        Log.i("CustomAdapter", "test1");
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View rowView = inflater.inflate(R.layout.custom_row, null, true);

        TextView textViewMachineid = (TextView) rowView.findViewById(R.id.textViewmachineid);
        TextView textViewTemp = (TextView) rowView.findViewById(R.id.textViewTemp);
        TextView textViewVelo = (TextView) rowView.findViewById(R.id.textViewVelocity);

        Machine myMachine = getItem(position) ;

        textViewMachineid.setText(myMachine.getMachineID());
        textViewTemp.setText(myMachine.getmachineTemp());
        textViewVelo.setText(myMachine.getmachineVelo());


        Log.i("CustomAdapter", "test2");
        tempvalue = Double.parseDouble(myMachine.getmachineTemp());
        velovalue = Double.parseDouble(myMachine.getmachineVelo());

        // changing colors for temp and velo
        if (tempvalue >= 31)
        {
            textViewTemp.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
        }
        if ((tempvalue >= 21 )&& (tempvalue <=30))
        {
            textViewTemp.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
        }
        if (velovalue >= 0.01)
        {

            textViewVelo.setTextColor(ContextCompat.getColor(context, R.color.colorCritical));
        }
        if ((velovalue>=0.21 && (velovalue<=0.30)))
        {
            textViewVelo.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
        }
        return rowView;
    }

}