package edu.singaporetech.senmon;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.TempRangeSeekBar;
import org.florescu.android.rangeseekbar.VeloRangeSeekBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class RangeFragment extends Fragment {

    //Declare variables
    View v;
    TempRangeSeekBar seekBarTemp;
    VeloRangeSeekBar seekBarVelo;
    EditText warnTempEdit, critTempEdit, warnVeloEdit, critVeloEdit;
    Button save;

    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";


    public RangeFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_range, container, false);

        //Set variables
        seekBarTemp = (TempRangeSeekBar) v.findViewById(R.id.seekbarTemp);
        warnTempEdit = (EditText) v.findViewById(R.id.warnTempEditText);
        critTempEdit = (EditText) v.findViewById(R.id.critTempEditText);
        seekBarVelo = (VeloRangeSeekBar) v.findViewById(R.id.seekbarVelo);
        warnVeloEdit = (EditText) v.findViewById(R.id.warnVeloEditText);
        critVeloEdit = (EditText) v.findViewById(R.id.critVeloEditText);
        save = (Button) v.findViewById(R.id.saveButton);

        //get the shared preferences mode
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display on edittext
        String displayWarnTemp = RangeSharedPreferences.getString(WarningTemperature, null);
        String displayCritTemp = RangeSharedPreferences.getString(CriticalTemperature, null);
        String displayWarnVelo = RangeSharedPreferences.getString(WarningVelocity, null);
        String displayCritVelo = RangeSharedPreferences.getString(CriticalVelocity, null);

        warnTempEdit.setText(displayWarnTemp);
        critTempEdit.setText(displayCritTemp);
        warnVeloEdit.setText(displayWarnVelo);
        critVeloEdit.setText(displayCritVelo);

        //Set selected range on the temp rangeseekbar
        if (displayWarnTemp != null) {
            seekBarTemp.setSelectedWarningValue(Double.valueOf(displayWarnTemp));
        }
        if (displayCritTemp != null) {
            seekBarTemp.setSelectedCriticalValue(Double.valueOf(displayCritTemp));
        }
        if (displayWarnTemp == null && displayCritTemp == null) {
            seekBarTemp.setSelectedWarningValue(Double.parseDouble(getString(R.string.temp_warning_value)));
            seekBarTemp.setSelectedCriticalValue(Double.parseDouble(getString(R.string.temp_critical_value)));
            warnTempEdit.setText(seekBarTemp.getSelectedWarningValue().toString());
            critTempEdit.setText(seekBarTemp.getSelectedCriticalValue().toString());
            Log.e("tempwarn", seekBarTemp.getSelectedWarningValue().toString());
            Log.e("tempcrit", seekBarTemp.getSelectedCriticalValue().toString());
        }

        //Set selected range on the velo rangeseekbar
        if (displayWarnVelo != null) {
            seekBarVelo.setSelectedWarningValue(Double.valueOf(displayWarnVelo));
        }
        if (displayCritVelo != null) {
            seekBarVelo.setSelectedCriticalValue(Double.valueOf(displayCritVelo));
        }
        if (displayWarnVelo == null && displayCritVelo == null) {
            seekBarVelo.setSelectedWarningValue(Double.parseDouble(getString(R.string.velo_warning_value)));
            seekBarVelo.setSelectedCriticalValue(Double.parseDouble(getString(R.string.velo_critical_value)));
            warnVeloEdit.setText(seekBarVelo.getSelectedWarningValue().toString());
            critVeloEdit.setText(seekBarVelo.getSelectedCriticalValue().toString());
            Log.e("velowarn", seekBarVelo.getSelectedWarningValue().toString());
            Log.e("velocrit", seekBarVelo.getSelectedCriticalValue().toString());
        }

        //Set default range for rangeseekbar
        seekBarTemp.setRangeValues(Double.parseDouble(getString(R.string.default_temp_min_range)), Double.parseDouble(getString(R.string.default_temp_max_range)));
        seekBarVelo.setRangeValues(Double.parseDouble(getString(R.string.default_velo_min_range)), Double.parseDouble(getString(R.string.default_velo_max_range)));


        //Temp seekbar action
        seekBarTemp.setOnRangeSeekBarChangeListener(new TempRangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

            public void onRangeSeekBarValuesChanged(TempRangeSeekBar<?> bar, Double warnValue, Double critValue) {
                Log.e("tempvalue", warnValue + "  " + critValue);
                warnTempEdit.setText(seekBarTemp.getSelectedWarningValue().toString());
                critTempEdit.setText(seekBarTemp.getSelectedCriticalValue().toString());
            }

        });

        //Temp seekbar action
        seekBarTemp.setOnRangeSeekBarChangeListener(new TempRangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

            public void onRangeSeekBarValuesChanged(TempRangeSeekBar<?> bar, Double warnValue, Double critValue) {
                Log.e("tempvalue", warnValue + "  " + critValue);
                warnTempEdit.setText(seekBarTemp.getSelectedWarningValue().toString());
                critTempEdit.setText(seekBarTemp.getSelectedCriticalValue().toString());
            }
        });

        //Velo seekbar action
        seekBarVelo.setOnRangeSeekBarChangeListener(new VeloRangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

            public void onRangeSeekBarValuesChanged(VeloRangeSeekBar<?> bar, Double warnValue, Double critValue) {
                Log.e("velovalue", warnValue + "  " + critValue);
                warnVeloEdit.setText(seekBarVelo.getSelectedWarningValue().toString());
                critVeloEdit.setText(seekBarVelo.getSelectedCriticalValue().toString());
            }
        });

        //warning temperature edit text
        warnTempEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (!s.toString().trim().equals("")) {
                    //met the range criteria
                    if ((Double.valueOf(s.toString()) >= Double.parseDouble(getString(R.string.default_temp_min_range))) && (Double.valueOf(s.toString()) <= Double.parseDouble(getString(R.string.default_temp_max_range)))) {
                        seekBarTemp.setSelectedWarningValue(Double.valueOf(s.toString()));
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), "Invalid input value range", Toast.LENGTH_SHORT).show();
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), "You cannot leave a blank input value", Toast.LENGTH_SHORT).show();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        //critical temperature edit text
        critTempEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (!s.toString().trim().equals("")) {
                    //met the range criteria
                    if ((Double.valueOf(s.toString()) >= Double.parseDouble(getString(R.string.default_temp_min_range))) && (Double.valueOf(s.toString()) <= Double.parseDouble(getString(R.string.default_temp_max_range)))) {
                        seekBarTemp.setSelectedCriticalValue(Double.valueOf(s.toString()));
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), "Invalid input value range", Toast.LENGTH_SHORT).show();
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), "You cannot leave a blank input value", Toast.LENGTH_SHORT).show();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        //warning velocity edit text
        warnVeloEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (!s.toString().trim().equals("")) {
                    //met the range criteria
                    if ((Double.valueOf(s.toString()) >= Double.parseDouble(getString(R.string.default_velo_min_range))) && (Double.valueOf(s.toString()) <= Double.parseDouble(getString(R.string.default_velo_max_range)))) {
                        seekBarVelo.setSelectedWarningValue(Double.valueOf(s.toString()));
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), "Invalid input value range", Toast.LENGTH_SHORT).show();
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), "You cannot leave a blank input value", Toast.LENGTH_SHORT).show();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        //critical velocity edit text
        critVeloEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (!s.toString().trim().equals("")) {
                    //met the range criteria
                    if ((Double.valueOf(s.toString()) >= Double.parseDouble(getString(R.string.default_velo_min_range))) && (Double.valueOf(s.toString()) <= Double.parseDouble(getString(R.string.default_velo_max_range)))) {
                        seekBarVelo.setSelectedCriticalValue(Double.valueOf(s.toString()));
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), "Invalid input value range", Toast.LENGTH_SHORT).show();
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), "You cannot leave a blank input value", Toast.LENGTH_SHORT).show();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        //save button
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Call the method and Store the number into a variable
                String warningTemp = seekBarTemp.getSelectedWarningValue().toString();
                String criticalTemp = seekBarTemp.getSelectedCriticalValue().toString();
                String warningVelo = seekBarVelo.getSelectedWarningValue().toString();
                String criticalVelo = seekBarVelo.getSelectedCriticalValue().toString();

                SharedPreferences.Editor rangeEditor = RangeSharedPreferences.edit();

                //store to warning and critical temperature,velocity
                rangeEditor.putString(WarningTemperature, warningTemp);
                rangeEditor.putString(CriticalTemperature, criticalTemp);
                rangeEditor.putString(WarningVelocity, warningVelo);
                rangeEditor.putString(CriticalVelocity, criticalVelo);
                rangeEditor.commit();
                Toast.makeText(getActivity(), "Changes Saved", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }


}
