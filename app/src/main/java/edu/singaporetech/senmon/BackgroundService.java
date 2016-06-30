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
import android.preference.PreferenceManager;
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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    NotificationManager notificationManager;
    int WarnNotificID = 111;
    boolean isWarnNotificActive = false;

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

            if(sharedPreferences.getBoolean(NotificationsEnabled,true)) {
                Log.d("LOL", "SERVICE RECEIVED THE VARIABLE");
                if (sharedPreferences.getBoolean(WarningEnabled, true)) {
                    Log.d("LOL", "SERVICE RECEIVED THE WARNING ENABLED VARIABLE");
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
//                    WarnNotificBuilder.setContentIntent(pendingIntent);
//
//                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.notify(WarnNotificID, WarnNotificBuilder.build());
//                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                    v.vibrate(1000);
//
//                    try {
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
//                        r.play();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    isWarnNotificActive = true;
                }
            }
////            //TODO build notifications for critical machines
//            NotificationCompat.Builder CritNotificBuilder = (NotificationCompat.Builder) new
//                    NotificationCompat.Builder(context).setContentTitle("Attention!")
//                    .setContentText("Machine is in Critical State!")
//                    .setTicker("Machine needs attention!")
//                    .setSmallIcon(R.drawable.ic_cancel_white_48dp)
//                    .setAutoCancel(true);
//            Intent openIntent = new Intent(context, MainActivity.class);
//
//            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
//            taskStackBuilder.addParentStack(MainActivity.class);
//            taskStackBuilder.addNextIntent(openIntent);
//
//            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//
//            //TODO fire off the critical notification
//            CritNotificBuilder.setContentIntent(pendingIntent);
//
//            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(CritNotificID, CritNotificBuilder.build());
//            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//            v.vibrate(1000);
//
//            try {
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
//                r.play();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            isCritNotificActive = true;

            //call the destroy method
            stopSelf();
        }
    };

    //if is not running then destroy
    @Override
    public void onDestroy(){
        this.isRunning = false;
    }

    //the alarmreceiver activates this
    @Override
    public  int onStartCommand(Intent intent, int flags, int startID){
        if(!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return  START_STICKY;
    }

}
