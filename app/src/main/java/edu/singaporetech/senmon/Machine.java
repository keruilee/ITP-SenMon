package edu.singaporetech.senmon;

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


    public Machine(String machineID, String machineDate, String machineTime, String machineVx, String machineVy,
                   String machineVz, String machineVelo, String machineTemp, String machineTS, String machineHud, String machineHour)
    {
        //super();
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
}
