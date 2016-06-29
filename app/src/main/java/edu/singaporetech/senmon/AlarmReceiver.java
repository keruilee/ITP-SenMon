package edu.singaporetech.senmon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by User on 29/6/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Intent backgroundService = new Intent(context, BackgroundService.class);
        context.startService(backgroundService);
    }
}
