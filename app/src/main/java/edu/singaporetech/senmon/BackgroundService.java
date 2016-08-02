package edu.singaporetech.senmon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
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
    int FavNotificID = 333;
    boolean isWarnNotificActive = false;
    boolean isCritNotificActive = false;
    boolean isFavNotificActive = false;

    private DatabaseHelper dbHelper;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(mTask);
        dbHelper = new DatabaseHelper(context);
    }

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            //TODO do something here
            Log.d("LOG", "BACKGROUND IS RUNNING");
            sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            //Declare variables

            // get number of machines in states from database, number of fav machines from shared pref
            int noOfCrit = dbHelper.getNumOfMachinesByStatus(getString(R.string.status_critical));
            int noOfWarn = dbHelper.getNumOfMachinesByStatus(getString(R.string.status_warning));
            int noOfFav = sharedPreferences.getInt(NumberOfFavourite, 0);

            //if there is any machines in the warning state

            Log.d("NUMBER OF CRITICALS", "" + noOfCrit);
            Log.d("NUMBER OF WARNINGS", "" + noOfWarn);
            if (sharedPreferences.getBoolean(NotificationsEnabled, true)) {
                //send a broadcast when the notification activates
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed"));
                //if there are any machine in warning state
                if (noOfWarn > 0) {
                    Log.d("LOL", "SERVICE RECEIVED THE VARIABLE");
                    if (sharedPreferences.getBoolean(WarningEnabled, true)) {
                        Log.d("LOL", "SERVICE RECEIVED THE WARNING ENABLED VARIABLE");
                        callNotification("Attention!", "warning", noOfWarn, R.drawable.ic_warning_white_48dp);

                    }
                }
                //if there are machines in critical states
                if (noOfCrit > 0) {
                    //notifications for critical states
////            //TODO build notifications for critical machines
                    if (sharedPreferences.getBoolean(CriticalEnabled, true)) {
                        Log.d("SHAREPREFERENCE", "SERVICE RECEIVED THE CRITICAL ENABLED VARIABLE");
                        callNotification("URGENT", "critical", noOfCrit, R.drawable.ic_cancel_white_48dp);

                    }
                }

                //if there are machines in the favourite that are in warning or critical state
                if (noOfFav > 0){
                    //TODO build notifications for favourite machines
                    if(sharedPreferences.getBoolean(FavNtfnOnly, true)){
                        Log.d("SHARED", "fAVOURITE RECEIVED");
                        callFavNotification("ATTENTION!", noOfFav, R.drawable.ic_grade_24dp);

                    }
                }

                killThread();
            } else {
                killThread();
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



            NotificBuilder.setContentIntent(pendingIntent);

        //TODO fire off the critical notification
            if(state == "critical"){
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(CritNotificID, NotificBuilder.build());
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(800);

                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isCritNotificActive = true;

                //call the destroy method
//                stopSelf();
                killThread();
            }
        //TODO fire of the warning notifications
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
//                stopSelf();
                killThread();
            }



    }

    public void callFavNotification(String contentTitle, int noOfMachine, int icon){
        //TODO Build notifications from the parameters retreived
        NotificationCompat.Builder NotificBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(context).setContentTitle(contentTitle)
                .setContentText( noOfMachine + " of the Machine(s) that you have noted needs attention")
                .setSmallIcon(icon)
                .setAutoCancel(true);
        Intent openIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(openIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);



        NotificBuilder.setContentIntent(pendingIntent);

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(FavNotificID, NotificBuilder.build());
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(800);

            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isFavNotificActive = true;

            //call the destroy method
//            stopSelf();

            killThread();
    }

    private void killThread() {
        this.backgroundThread.interrupt();
        this.isRunning = false;
        stopSelf();
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
