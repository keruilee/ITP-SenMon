package edu.singaporetech.senmon;

/**
 * Created by jinyu on 15/6/2016.
 */
public class Machine {
    private String machineID;
    private String machineTemp;
    private String machineVelo;

    public Machine(String machineID,String machineVelo, String machineTemp )
    {
        //super();
        this.machineID =machineID;
        this.machineTemp=machineTemp;
        this.machineVelo=machineVelo;
    }

    public String getMachineID()
    {return machineID;}
    public String getmachineTemp()
    {return machineTemp;}
    public String getmachineVelo()
    {return machineVelo;}

}
