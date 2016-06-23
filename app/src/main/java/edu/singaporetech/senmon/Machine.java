package edu.singaporetech.senmon;

/**
 * Created by jinyu on 15/6/2016.
 */
public class Machine {
    private String machineID;
    private String machineTemp;
    private String machineVelo;
    private String machineHour;
//    private String machineDate;
//    private String machineTime;

    public Machine(String machineID,String machineVelo, String machineTemp, String machineHour)
    {
        //super();
        this.machineID =machineID;
        this.machineTemp=machineTemp;
        this.machineVelo=machineVelo;
        this.machineHour=machineHour;
//        this.machineDate=machineDate;
//        this.machineTime=machineTime;
    }

    public String getMachineID()
    {return machineID;}
    public String getmachineTemp()
    {return machineTemp;}
    public String getmachineVelo()
    {return machineVelo;}
    public String getMachineHour()
    {return machineHour;}
//    public String getMachineDate()
//    {return machineDate;}
//    public String getMachineTime()
//    {return machineTime;}

}
