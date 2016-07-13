package edu.singaporetech.senmon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by User on 29/6/2016.
 */
public class BackgroundService extends Service{

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NotificationsEnabled = "ntfnEnabledKey";
    public static final String WarningEnabled = "warningEnabledKey";
    public static final String CriticalEnabled = "criticalEnabledKey";
    public static final String FavNtfnOnly = "favNtfnOnlyKey";

    public static final String NumberOfCritical = "numOfCrit";
    public static final String NumberOfWarning = "numOfWarn";
    public static final String NumberOfFavourite = "numOfFav";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    NotificationManager notificationManager;
    int WarnNotificID = 111;
    int CritNotificID = 222;
    boolean isWarnNotificActive = false;
    boolean isCritNotificActive = false;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(mTask);

    }

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            //TODO do something here
            Log.d("LOG", "BACKGROUND IS RUNNING");
            sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            //send a broadcast when the notification activates


            //Declare variables

            int noOfCrit = sharedPreferences.getInt(NumberOfCritical, 0);
            int noOfWarn = sharedPreferences.getInt(NumberOfWarning, 0);

            //if there is any machines in the warning state

            Log.d("NUMBER OF CRITICALS", "" + noOfCrit);
            Log.d("NUMBER OF WARNINGS", "" + noOfWarn);
            if (sharedPreferences.getBoolean(NotificationsEnabled, true)) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed"));
                if (noOfWarn > 0) {
                    Log.d("LOL", "SERVICE RECEIVED THE VARIABLE");
                    if (sharedPreferences.getBoolean(WarningEnabled, true)) {
                        Log.d("LOL", "SERVICE RECEIVED THE WARNING ENABLED VARIABLE");
                        callNotification("Attention!", "warning", noOfWarn, R.drawable.ic_warning_white_48dp);

                    }
                }

                if (noOfCrit > 0) {
                    //notifications for critical states
////            //TODO build notifications for critical machines
                    if (sharedPreferences.getBoolean(CriticalEnabled, true)) {
                        Log.d("SHAREPREFERENCE", "SERVICE RECEIVED THE CRITICAL ENABLED VARIABLE");
                        callNotification("URGENT", "critical", noOfCrit, R.drawable.ic_cancel_white_48dp);

                    }
                }
            }
            if(sharedPreferences.getBoolean(FavNtfnOnly,true)){
                Log.d("Favourite Notific", "ENABLED");
            }
        }
    };

    public void callNotification(String contentTitle, String state, int noOfMachine, int icon){
        //TODO Build notifications from the parameters retreived
        NotificationCompat.Builder NotificBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(context).setContentTitle(contentTitle)
                .setContentText("There are " + noOfMachine + " Machine(s) in the " +state+" State!")
                .setSmallIcon(icon)
                .setAutoCancel(true);
        Intent openIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(openIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);


            //TODO fire off the critical notification
            NotificBuilder.setContentIntent(pendingIntent);
            if(state == "critical"){
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(CritNotificID, NotificBuilder.build());
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

                //call the destroy method
                stopSelf();
            }
            if(state == "warning"){
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(WarnNotificID, NotificBuilder.build());
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(800);

                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isWarnNotificActive = true;

                //call the destroy method
                stopSelf();
            }

    }

    //if is not running then destroy
    @Override
    public void onDestroy(){
        this.isRunning = false;
    }

    //the alarmreceiver activates this and starts running this class
    @Override
    public  int onStartCommand(Intent intent, int flags, int startID){
        if(!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return  START_STICKY;
    }

}
