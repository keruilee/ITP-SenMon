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

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class RangeFragment extends Fragment {

    //Declare variables\
    Boolean validateWarnTemp = true;
    Boolean validateCritTemp = true;
    Boolean validateWarnVelo = true;
    Boolean validateCritVelo = true;
    View v;
    TempRangeSeekBar seekBarTemp;
    VeloRangeSeekBar seekBarVelo;
    EditText warnTempEdit, critTempEdit, warnVeloEdit, critVeloEdit;
    Button save, resetBtn;

    SharedPreferences RangeSharedPreferences;
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";
    String warningTemp ;
    String criticalTemp ;
    String warningVelo ;
    String criticalVelo;

    DecimalFormat twoDP = new DecimalFormat("#0.00");

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
        resetBtn = (Button) v.findViewById(R.id.resetButton);

        //Set default range for rangeseekbar
        seekBarTemp.setRangeValues(Double.parseDouble(getString(R.string.default_temp_min_range)), Double.parseDouble(getString(R.string.default_temp_max_range)));
        seekBarVelo.setRangeValues(Double.parseDouble(getString(R.string.default_velo_min_range)), Double.parseDouble(getString(R.string.default_velo_max_range)));

        //get the shared preferences mode
        RangeSharedPreferences = getContext().getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        //reload the value from the shared preferences and display on edittext
        warningTemp = RangeSharedPreferences.getString(WarningTemperature, getString(R.string.temp_warning_value));
        criticalTemp = RangeSharedPreferences.getString(CriticalTemperature, getString(R.string.temp_critical_value));
        warningVelo = RangeSharedPreferences.getString(WarningVelocity, getString(R.string.velo_warning_value));
        criticalVelo = RangeSharedPreferences.getString(CriticalVelocity, getString(R.string.velo_critical_value));

        warnTempEdit.setText(twoDP.format(Double.parseDouble(warningTemp)));
        critTempEdit.setText(twoDP.format(Double.parseDouble(criticalTemp)));
        warnVeloEdit.setText(twoDP.format(Double.parseDouble(warningVelo)));
        critVeloEdit.setText(twoDP.format(Double.parseDouble(criticalVelo)));

        seekBarTemp.setSelectedWarningValue(Double.parseDouble(warningTemp));
        seekBarTemp.setSelectedCriticalValue(Double.parseDouble(criticalTemp));
        seekBarVelo.setSelectedWarningValue(Double.parseDouble(warningVelo));
        seekBarVelo.setSelectedCriticalValue(Double.parseDouble(criticalVelo));

        //Temp seekbar action
        seekBarTemp.setOnRangeSeekBarChangeListener(new TempRangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

            public void onRangeSeekBarValuesChanged(TempRangeSeekBar<?> bar, Double warnValue, Double critValue) {
                Log.d("tempvalue", warnValue + "  " + critValue);
                warnTempEdit.setText(twoDP.format(seekBarTemp.getSelectedWarningValue()));
                critTempEdit.setText(twoDP.format(seekBarTemp.getSelectedCriticalValue()));
            }
        });

        //Velo seekbar action
        seekBarVelo.setOnRangeSeekBarChangeListener(new VeloRangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

            public void onRangeSeekBarValuesChanged(VeloRangeSeekBar<?> bar, Double warnValue, Double critValue) {
                Log.d("velovalue", warnValue + "  " + critValue);
                warnVeloEdit.setText(twoDP.format(seekBarVelo.getSelectedWarningValue()));
                critVeloEdit.setText(twoDP.format(seekBarVelo.getSelectedCriticalValue()));
            }
        });

        //warning temperature edit text
        warnTempEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (!s.toString().trim().equals("")) {
                    //met the range criteria
                    if ((Double.valueOf(s.toString()) >= Double.parseDouble(getString(R.string.default_temp_min_range))) && (Double.valueOf(s.toString()) <= Double.parseDouble(getString(R.string.default_temp_max_range)))) {
                        seekBarTemp.setSelectedWarningValue(Double.valueOf(s.toString()));
                        validateWarnTemp = true;
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), getString(R.string.invalid_range_temp), Toast.LENGTH_SHORT).show();
                        validateWarnTemp = false;
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_blank_value), Toast.LENGTH_SHORT).show();
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
                        validateCritTemp = true;
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), getString(R.string.invalid_range_temp), Toast.LENGTH_SHORT).show();
                        validateCritTemp = false;
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_blank_value), Toast.LENGTH_SHORT).show();
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
                        validateWarnVelo = true;
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), getString(R.string.invalid_range_velo), Toast.LENGTH_SHORT).show();
                        validateWarnVelo = false;
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_blank_value), Toast.LENGTH_SHORT).show();
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
                        validateCritVelo = true;
                    }
                    //dont meet range criteria
                    else {
                        Toast.makeText(getActivity(), getString(R.string.invalid_range_velo), Toast.LENGTH_SHORT).show();
                        validateCritVelo = false;
                    }
                    //null input value
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_blank_value), Toast.LENGTH_SHORT).show();
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

                //validate for empty inputs
                if ((validateEmpty(warnTempEdit)) && (validateEmpty(critTempEdit))
                        && (validateEmpty(warnVeloEdit)) && (validateEmpty(critVeloEdit))) {

                    //Check for invalid range input
                    if  ((validateWarnTemp) && (validateCritTemp) &&
                            (validateWarnVelo) && (validateCritVelo))
                    {
                        //Check for not logical warning/critical input
                        if ((validateLogical(warnTempEdit,critTempEdit)) &&
                                (validateLogical(warnVeloEdit,critVeloEdit)))
                        {
                            //No empty input or invalid input
                            //Call the method and Store the number into a variable
                             warningTemp = seekBarTemp.getSelectedWarningValue().toString();
                             criticalTemp = seekBarTemp.getSelectedCriticalValue().toString();
                             warningVelo = seekBarVelo.getSelectedWarningValue().toString();
                             criticalVelo = seekBarVelo.getSelectedCriticalValue().toString();

                            SharedPreferences.Editor rangeEditor = RangeSharedPreferences.edit();

                            //store to warning and critical temperature,velocity
                            rangeEditor.putString(WarningTemperature, warningTemp);
                            rangeEditor.putString(CriticalTemperature, criticalTemp);
                            rangeEditor.putString(WarningVelocity, warningVelo);
                            rangeEditor.putString(CriticalVelocity, criticalVelo);
                            rangeEditor.commit();

                            computeMachine();
                            Toast.makeText(getActivity(), getString(R.string.range_saved_success), Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(getActivity(), getString(R.string.invalid_range), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                seekBarTemp.setSelectedWarningValue(Double.parseDouble(getString(R.string.temp_warning_value)));
                seekBarTemp.setSelectedCriticalValue(Double.parseDouble(getString(R.string.temp_critical_value)));
                seekBarVelo.setSelectedWarningValue(Double.parseDouble(getString(R.string.velo_warning_value)));
                seekBarVelo.setSelectedCriticalValue(Double.parseDouble(getString(R.string.velo_critical_value)));
                warnTempEdit.setText(getString(R.string.temp_warning_value));
                critTempEdit.setText(getString(R.string.temp_critical_value));
                warnVeloEdit.setText(getString(R.string.velo_warning_value));
                critVeloEdit.setText(getString(R.string.velo_critical_value));
            }});

        return v;
    }

    public boolean validateEmpty(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.no_blank_value), Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }

    public boolean validateLogical(EditText editTextW, EditText editTextC) {
        if (Double.valueOf(editTextW.getText().toString()) >= Double.valueOf(editTextC.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.warning_must_be_higher), Toast.LENGTH_SHORT).show();
            return false;

        }
        else{
            return true;
        }
    }

    //Computation of machines in each state
    private void computeMachine() {
        DatabaseHelper mydatabaseHelper = new DatabaseHelper(getActivity());
        mydatabaseHelper.updateAllMachineStates();
    }
}
