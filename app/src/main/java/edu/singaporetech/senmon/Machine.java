package edu.singaporetech.senmon;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jinyu on 15/6/2016.
 */
public class Machine {
    private String machineID;
    private String machineDate;
    private String machineTime;
    private String machineVx;
    private String machineVy;
    private String machineVz;
    private String machineVelo;//Vtotal
    private String machineTemp;//T_C
    private String machineTS;//TS
    private String machineHud;
    private String machineHour;
    private String machineStatus;
    private String machineFavouriteStatus;

    // to get temp/velo warning/crit values from shared preference
    public static final String MyRangePREFERENCES = "MyRangePrefs";
    public static final String WarningTemperature = "warnTempKey";
    public static final String CriticalTemperature = "critTempKey";
    public static final String WarningVelocity = "warnVeloKey";
    public static final String CriticalVelocity = "critVeloKey";
    SharedPreferences RangeSharedPreferences;

    private Context mContext;

    public Machine(Context context,
                   String machineID, String machineDate, String machineTime, String machineVx, String machineVy,
                   String machineVz, String machineVelo, String machineTemp, String machineTS, String machineHud, String machineHour)
    {
        this.mContext = context;

        this.machineID = machineID;
        this.machineDate = machineDate;
        this.machineTime = machineTime;
        this.machineVx = machineVx;
        this.machineVy = machineVy;
        this.machineVz = machineVz;
        this.machineVelo = machineVelo;
        this.machineTemp = machineTemp;
        this.machineTS = machineTS;
        this.machineHud = machineHud;
        this.machineHour = machineHour;
        this.machineStatus = determineState(Double.parseDouble(machineTemp), Double.parseDouble(machineVelo));
        this.machineFavouriteStatus = "no";
    }

    public Machine(Context context, String machineID, String machineDate, String machineTime, String machineVx, String machineVy,
                   String machineVz, String machineVelo, String machineTemp, String machineTS, String machineHud, String machineHour,
                   String machineStatus, String machineFavouriteStatus)
    {
        //super();
        this.mContext = context.getApplicationContext();

        this.machineID = machineID;
        this.machineDate = machineDate;
        this.machineTime = machineTime;
        this.machineVx = machineVx;
        this.machineVy = machineVy;
        this.machineVz = machineVz;
        this.machineVelo = machineVelo;
        this.machineTemp = machineTemp;
        this.machineTS = machineTS;
        this.machineHud = machineHud;
        this.machineHour = machineHour;
        this.machineStatus = determineState(Double.parseDouble(machineTemp), Double.parseDouble(machineVelo));
        this.machineFavouriteStatus = machineFavouriteStatus;
    }

    public String getMachineID()
    {return machineID;}
    public String getMachineDate()
    {return machineDate;}
    public String getMachineTime()
    {return machineTime;}
    public String getMachineVx()
    {return machineVx;}
    public String getMachineVy()
    {return machineVy;}
    public String getMachineVz()
    {return machineVz;}
    public String getmachineVelo()
    {return machineVelo;}
    public String getmachineTemp()
    {return machineTemp;}
    public String getMachineTS()
    {return machineTS;}
    public String getMachineHud()
    {return machineHud;}
    public String getMachineHour()
    {return machineHour;}
    public Double getMachineHourDouble()
    {return Double.parseDouble(machineHour);}
    public String getMachineStatus()
    {return machineStatus;}
    public String getMachineFavouriteStatus()
    {return machineFavouriteStatus;}

    /**
     * check what status machine is in depending on the warning/crit threshold
     * @param machineTemp - temperature of the machine
     * @param machineVelo - velocity of the machine
     * @return state of the machine "Critical", "Warning" or "Normal"
     */
    private String determineState(double machineTemp, double machineVelo)
    {
        RangeSharedPreferences = mContext.getSharedPreferences(MyRangePREFERENCES, Context.MODE_PRIVATE);

        String tempWarningValue = RangeSharedPreferences.getString(WarningTemperature, String.valueOf(Double.parseDouble(mContext.getString(R.string.temp_warning_value))));
        String tempCriticalValue = RangeSharedPreferences.getString(CriticalTemperature, String.valueOf(Double.parseDouble(mContext.getString(R.string.temp_critical_value))));
        String veloWarningValue = RangeSharedPreferences.getString(WarningVelocity, String.valueOf(Double.parseDouble(mContext.getString(R.string.velo_warning_value))));
        String veloCriticalValue = RangeSharedPreferences.getString(CriticalVelocity, String.valueOf(Double.parseDouble(mContext.getString(R.string.velo_critical_value))));

        if(machineTemp < Double.parseDouble(tempWarningValue) && machineVelo < Double.parseDouble(veloWarningValue))
        {
            // both temp and velo is less than warning value = machine is in normal status
            return mContext.getString(R.string.status_normal);
        }
        else if (machineTemp >= Double.parseDouble(tempCriticalValue) || machineVelo >= Double.parseDouble(veloCriticalValue))
        {
            // either temp/velo is in critical range = machine is in critical status
            return mContext.getString(R.string.status_critical);
        }
        else
        {
            // machine is not in normal or critical status, so machine is in warning status
            return mContext.getString(R.string.status_warning);
        }
    }
}
