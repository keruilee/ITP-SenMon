package edu.singaporetech.senmon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


//*
// * A simple {@link Fragment} subclass.


public class SettingsFragment extends Fragment implements android.widget.CompoundButton.OnCheckedChangeListener {

    private Context context;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NotificationsEnabled = "ntfnEnabledKey";
    public static final String WarningEnabled = "warningEnabledKey";
    public static final String CriticalEnabled = "criticalEnabledKey";
    public static final String FavNtfnOnly = "favNtfnOnlyKey";
    public static final String NumberOfFavourite = "numOfFav";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Switch ntfnEnableSw, ntfnWarningSw, ntfnCriticalSw, ntfnFavSw;
    boolean favExists;
    Button editRangeBtn;

    NotificationManager notificationManager;


    public SettingsFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        context = getContext();

        ntfnEnableSw= (Switch)settingsView.findViewById(R.id.ntfnEnableSw);
        ntfnWarningSw= (Switch)settingsView.findViewById(R.id.ntfnWarningSw);
        ntfnCriticalSw= (Switch)settingsView.findViewById(R.id.ntfnCriticalSw);
        ntfnFavSw= (Switch)settingsView.findViewById(R.id.ntfnFavSw);
        editRangeBtn = (Button) settingsView.findViewById(R.id.goRangeButton);

        ntfnEnableSw.setOnCheckedChangeListener(SettingsFragment.this);
        ntfnWarningSw.setOnCheckedChangeListener(SettingsFragment.this);
        ntfnCriticalSw.setOnCheckedChangeListener(SettingsFragment.this);
        ntfnFavSw.setOnCheckedChangeListener(SettingsFragment.this);

        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        int noOfFave = sharedPreferences.getInt(NumberOfFavourite,0);
//        Log.d("SHARED NUMBER OF FAV", ""+noOfFave);
        favExists = favouriteExists();
        if(!favExists)                      // by default fav notifications switch is enabled
            ntfnFavSw.setEnabled(false);

        loadSavedPrefs();

        //edit range button
        editRangeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                RangeFragment range = new RangeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.relativelayoutfor_fragment, range);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return settingsView;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor = sharedPreferences.edit();
        switch(buttonView.getId())
        {
            case R.id.ntfnEnableSw:                 // enable notifications switch

                if(buttonView.isChecked()) {        // notifications enabled
                    // show all other switches with default settings
                    ntfnWarningSw.setVisibility(View.VISIBLE);
                    ntfnCriticalSw.setVisibility(View.VISIBLE);
                    ntfnFavSw.setVisibility(View.VISIBLE);

                    ntfnWarningSw.setChecked(true);
                    ntfnCriticalSw.setChecked(true);
                    ntfnFavSw.setChecked(false);

                    ntfnWarningSw.setEnabled(true);
                    ntfnCriticalSw.setEnabled(true);
                    if(!favExists)
                        ntfnFavSw.setEnabled(false);
                    else
                        ntfnFavSw.setEnabled(true);

                    // update shared preferences with default settings
                    editor.putBoolean(NotificationsEnabled, true);
                    editor.putBoolean(WarningEnabled, true);
                    editor.putBoolean(CriticalEnabled, true);
                    editor.putBoolean(FavNtfnOnly, false);
                }
                else {                              // notifications disabled
                    // hide all other switches
                    ntfnWarningSw.setVisibility(View.INVISIBLE);
                    ntfnCriticalSw.setVisibility(View.INVISIBLE);
                    ntfnFavSw.setVisibility(View.INVISIBLE);

                    // update shared preferences
                    editor.putBoolean(NotificationsEnabled, false);
                }

                break;
            case R.id.ntfnWarningSw:                // warning notifications switch

                if(buttonView.isChecked()) {        // warning notifications enabled
                    editor.putBoolean(WarningEnabled, true);
                }else{
                    editor.putBoolean(WarningEnabled,false);
                }

                break;
            case R.id.ntfnCriticalSw:               // critical notifications switch

                if(buttonView.isChecked()) {        // critical notifications enabled
                    editor.putBoolean(CriticalEnabled, true);

                }
                else {
                    editor.putBoolean(CriticalEnabled, false);
                }

                break;
            case R.id.ntfnFavSw:                    // fav only notification switch

                if(buttonView.isChecked()) {        // notifications for fav only
                    editor.putBoolean(FavNtfnOnly, true);
                }
                else {                              // notifications for all machines
                    editor.putBoolean(FavNtfnOnly, false);
                }

                break;
        }
        editor.commit();
    }

    public boolean favouriteExists() {
        // to return all records in the form of a Cursor object
        FavouriteDatabaseHelper favDatabase = new FavouriteDatabaseHelper(getActivity());
        int count = (int) favDatabase.getRowsCount();
        if(count == 0) {            // no fav machine
            Log.d("FAAVOURITE COUNT", count+"");
            editor = sharedPreferences.edit();
            editor.putBoolean(FavNtfnOnly, false);          // update shared preference to disable alert fav only
            editor.commit();
            return false;
        }
        Log.d("FAAVOURITE COUNT", count+"");
        return true;
    }

    public void loadSavedPrefs() {
        if(sharedPreferences.contains(NotificationsEnabled))                    // load saved settings
        {
            if(!sharedPreferences.getBoolean(NotificationsEnabled, true))       // disabled notifications
            {
                ntfnEnableSw.setChecked(false);
                return;
            }
            else            // notifications enabled, set all to saved settings
            {
                //ntfnWarningSw.setChecked(sharedPreferences.getBoolean(WarningEnabled, true));
                //ntfnCriticalSw.setChecked(sharedPreferences.getBoolean(CriticalEnabled, true));
                if(favExists)
                    ntfnFavSw.setChecked(sharedPreferences.getBoolean(FavNtfnOnly, false));
            }
        }
        else {          // save default settings
            editor = sharedPreferences.edit();
            editor.putBoolean(NotificationsEnabled, true);
            editor.putBoolean(WarningEnabled, true);
            editor.putBoolean(CriticalEnabled, true);
            editor.putBoolean(FavNtfnOnly, false);
            editor.commit();
        }
    }

}
