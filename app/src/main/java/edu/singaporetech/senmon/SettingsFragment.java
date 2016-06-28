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
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements android.widget.CompoundButton.OnCheckedChangeListener {

    private Context context;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NotificationsEnabled = "ntfnEnabledKey";
    public static final String WarningEnabled = "warningEnabledKey";
    public static final String CriticalEnabled = "criticalEnabledKey";
    public static final String FavNtfnOnly = "favNtfnOnlyKey";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Switch ntfnEnableSw, ntfnWarningSw, ntfnCriticalSw, ntfnFavSw;
    boolean favExists;

    NotificationManager notificationManager;
    int WarnNotificID = 111;
    int CritNotificID = 222;
    int FavNotificID = 333;

    boolean isWarnNotificActive = false;
    boolean isCritNotificActive = false;
    boolean isFavNotificActive = false;

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

        ntfnEnableSw.setOnCheckedChangeListener(SettingsFragment.this);
        ntfnWarningSw.setOnCheckedChangeListener(SettingsFragment.this);
        ntfnCriticalSw.setOnCheckedChangeListener(SettingsFragment.this);
        ntfnFavSw.setOnCheckedChangeListener(SettingsFragment.this);

        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        favExists = favouriteExists();
        if(!favExists)                      // by default fav notifications switch is enabled
            ntfnFavSw.setEnabled(false);

        loadSavedPrefs();

        return settingsView;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId())
        {
            case R.id.ntfnEnableSw:                 // enable notifications switch
                editor = sharedPreferences.edit();
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
                editor.commit();
                break;
            case R.id.ntfnWarningSw:                // warning notifications switch
                editor = sharedPreferences.edit();
                if(buttonView.isChecked()) {        // warning notifications enabled
                    editor.putBoolean(WarningEnabled, true);
                    //TODO build notifications for warning machines
                    NotificationCompat.Builder WarnNotificBuilder = (NotificationCompat.Builder) new
                            NotificationCompat.Builder(context).setContentTitle("Attention!")
                            .setContentText("Machine is in Warning State!")
                            .setTicker("Machine needs attention!")
                            .setSmallIcon(R.drawable.ic_warning_white_48dp)
                            .setAutoCancel(true);
                    Intent openIntent = new Intent(context, MainActivity.class);

                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                    taskStackBuilder.addParentStack(MainActivity.class);
                    taskStackBuilder.addNextIntent(openIntent);

                    PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    //TODO fire off the notification for warning machines *temp holding place 
                    WarnNotificBuilder.setContentIntent(pendingIntent);

                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(WarnNotificID, WarnNotificBuilder.build());
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1000);

                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isWarnNotificActive = true;
                }
                else {
                    //TODO if the switch is off then cancel notifications
                    editor.putBoolean(WarningEnabled, false);
                    if(isWarnNotificActive){
                        notificationManager.cancel(WarnNotificID);
                    }
                }
                editor.commit();
                break;
            case R.id.ntfnCriticalSw:               // critical notifications switch
                editor = sharedPreferences.edit();
                if(buttonView.isChecked()) {        // critical notifications enabled
                    editor.putBoolean(CriticalEnabled, true);
                    //TODO build notifications for critical machines
                    NotificationCompat.Builder CritNotificBuilder = (NotificationCompat.Builder) new
                            NotificationCompat.Builder(context).setContentTitle("Attention!")
                            .setContentText("Machine is in Critical State!")
                            .setTicker("Machine needs attention!")
                            .setSmallIcon(R.drawable.ic_cancel_white_48dp)
                            .setAutoCancel(true);
                    Intent openIntent = new Intent(context, MainActivity.class);

                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                    taskStackBuilder.addParentStack(MainActivity.class);
                    taskStackBuilder.addNextIntent(openIntent);

                    PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    //TODO fire off the critical notification
                    CritNotificBuilder.setContentIntent(pendingIntent);

                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(CritNotificID, CritNotificBuilder.build());
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1000);

                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isCritNotificActive = true;
                }
                else {
                    editor.putBoolean(CriticalEnabled, false);
                    //TODO if the switch is off then cancel notifications
                    if(isCritNotificActive){
                        notificationManager.cancel(CritNotificID);
                    }
                }
                editor.commit();
                break;
            case R.id.ntfnFavSw:                    // fav only notification switch
                editor = sharedPreferences.edit();
                if(buttonView.isChecked()) {        // notifications for fav only
                    editor.putBoolean(FavNtfnOnly, true);
                }
                else {                              // notifications for all machines
                    editor.putBoolean(FavNtfnOnly, false);
                }
                editor.commit();
                break;
        }
    }

    public boolean favouriteExists() {
        // to return all records in the form of a Cursor object
        FavouriteDatabaseHelper favDatabase = new FavouriteDatabaseHelper(getActivity());;
        int count = (int) favDatabase.getRowsCount();
        if(count == 0) {            // no fav machine
            editor = sharedPreferences.edit();
            editor.putBoolean(FavNtfnOnly, false);          // update shared preference to disable alert fav only
            editor.commit();
            return false;
        }
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
                ntfnWarningSw.setChecked(sharedPreferences.getBoolean(WarningEnabled, true));
                ntfnCriticalSw.setChecked(sharedPreferences.getBoolean(CriticalEnabled, true));
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
