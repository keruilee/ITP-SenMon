package edu.singaporetech.senmon;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jinyu on 1/7/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context mContext;
    final static String TAG = "DATABASE";
    //creating Database
    final static String TABLE_NAME      = "DatabaseTable";
    final static String MACHINEID       = "machineID";
    final static String MACHINEDATE     = "machineDate";
    final static String MACHINETIME     = "machineTime";
    final static String MACHINEVX       = "machineVx";
    final static String MACHINEVY       = "machineVy";
    final static String MACHINEVZ       = "machineVz";
    final static String MACHINEVELO     = "machineVelo";//Vtotal
    final static String MACHINETEMP     = "machineTemp";//T_C
    final static String MACHINETS       = "machineTS";//TS
    final static String MACHINEHUD      = "machineHud";
    final static String MACHINEHOUR     = "machineHour";
    final static String MACHINESTATUS   = "machineStatus"; // Critical , Warning , Normal , use this as a standard
    final static String MACHINEFAVOURITESTATUS = "machineFavouriteStatus"; // yes, no , use this as a standard
    final static String MACHINEUPDATEDATETIME = "machineUpdateDateTime"; // yes, no , use this as a standard


    final static String _ID = "_id";


    final static String[] columns = {_ID,MACHINEID,MACHINEDATE,MACHINETIME,MACHINEVX,MACHINEVY,MACHINEVZ,MACHINEVELO,
            MACHINETEMP,MACHINETS,MACHINEHUD,MACHINEHOUR,MACHINESTATUS,MACHINEFAVOURITESTATUS,MACHINEUPDATEDATETIME};

    final private static String DBNAME = "DBNAME";
    final private static Integer VERSION = 1;
    final private static String CREATE_CMD = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            columns[1] + " STRING, " +
            columns[2] + " STRING, " +
            columns[3] + " STRING, " +
            columns[4] + " STRING, " +
            columns[5] + " STRING, " +
            columns[6] + " STRING, " +
            columns[7] + " STRING, " +
            columns[8] + " STRING, " +
            columns[9] + " STRING, " +
            columns[10] + " STRING, " +
            columns[11] + " STRING, " +
            columns[12] + " STRING, " +
            columns[13] + " STRING, " +
            columns[14] + " STRING )";

    public DatabaseHelper(Context context) {
        // logic to create database
        super(context, DBNAME, null, VERSION);
        mContext = context;
    }

    // onCreate database//
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    // onUpgrade database//
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    // Delete database //
    public void deleteDatabase() {
        mContext.deleteDatabase(DBNAME);
    }


    // Add  data into database //
    public void addmachine(String addmachineID, String addmachineDate, String addmachineTime,
                           String addmachineVx, String addmachineVy, String addmachineVz,
                           String addmachineVelo, String addmachineTemp, String addmachineTS,
                           String addmachineHud, String addmachineHour)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MACHINEID, addmachineID);
        values.put(MACHINEDATE, addmachineDate);
        values.put(MACHINETIME, addmachineTime);
        values.put(MACHINEVX, addmachineVx);
        values.put(MACHINEVY, addmachineVy);
        values.put(MACHINEVZ, addmachineVz);
        values.put(MACHINEVELO, addmachineVelo);
        values.put(MACHINETEMP, addmachineTemp);
        values.put(MACHINETS, addmachineTS);
        values.put(MACHINEHUD, addmachineHud);
        values.put(MACHINEHOUR, addmachineHour);


        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Clear rows //
    public void clearRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(CREATE_CMD);
        db.close();
    }

    // Delete the row by using machindid //
    public void removeMachineID(String machineID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, " machine= '" + machineID + "'", null);
        db.close();

    }
    public long getRowsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }

    // Update machine detail in database //
    public void updateMachineDetail(String addmachineID, String addmachineDate, String addmachineTime,
                                    String addmachineVx, String addmachineVy, String addmachineVz,
                                    String addmachineVelo, String addmachineTemp, String addmachineTS,
                                    String addmachineHud, String addmachineHour)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MACHINEDATE, addmachineDate);
        values.put(MACHINETIME, addmachineTime);
        values.put(MACHINEVX, addmachineVx);
        values.put(MACHINEVY, addmachineVy);
        values.put(MACHINEVZ, addmachineVz);
        values.put(MACHINEVELO, addmachineVelo);
        values.put(MACHINETEMP, addmachineTemp);
        values.put(MACHINETS, addmachineTS);
        values.put(MACHINEHUD, addmachineHud);
        values.put(MACHINEHOUR, addmachineHour);
        values.put(MACHINESTATUS, "");
        db.update(TABLE_NAME, values, MACHINEID + "= ?", new String[] {addmachineID});
        db.close();
    }

    // find machine //
    public boolean findMachine (String findMachineID) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns,
                MACHINEID + " = ?",
                new String[] { findMachineID }, null,null,null);

        if (cursor.moveToFirst()) {
            do {
                return true;
            } while (cursor.moveToNext());
        }
        return false;
    }

    // Update machine state in database //
    public void updateMachineState(String machineID, String machineStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MACHINESTATUS, machineStatus);
        db.update(TABLE_NAME, cv, MACHINEID + "= ?", new String[] {machineID});
        db.close();
    }


    // Check current machine state in database //
    public boolean checkMachineState(String machineID, String machineStatus) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns,
                MACHINEID + " = ? AND " + MACHINESTATUS + " = ?",
                new String[] { machineID, machineStatus }, null,null,null);
        // looping through if exist
        if (cursor.moveToFirst()) {
            do {
                return true;
            } while (cursor.moveToNext());
        }
        else
        {
            return false;
        }
    }


    // Check number of machine in particular state in database //
    public ArrayList<Double> checkMachineInParticularState (String machineStatus) {
        ArrayList<Double> arrayHour = new ArrayList<Double>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns,
                MACHINESTATUS + " = ?",
                new String[] { machineStatus }, null,null,null);

        if (cursor.moveToFirst()) {
            do {
                arrayHour.add(Double.parseDouble(cursor.getString(11)));
            } while (cursor.moveToNext());
        }
        return arrayHour;
    }

    // Get machine ID based on hour database //
    public String machineUsingHour(String machineState, String machineHour) {
        String value = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns,
                MACHINEHOUR + " = ? AND " + MACHINESTATUS + " = ?",
                new String[] { machineHour, machineState }, null,null,null);
        // looping through if exist
        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(1);

            } while (cursor.moveToNext());
        }
        return value;
    }

    public void changeDatabase(String changemachineID, String changemachineDate, String changemachineTime,
                               String changemachineVx, String changemachineVy, String changemachineVz,
                               String changemachineVelo, String changemachineTemp, String changemachineTS,
                               String changemachineHud, String changemachineHour){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        //Database is not empty
        if (cursor.getCount() > 0)
        {
            Log.d(TAG, "Database not empty");
            //Machine exist in database
            if (findMachine(changemachineID) == true)
            {
                updateMachineDetail(changemachineID,changemachineDate,changemachineTime,changemachineVx,changemachineVy,changemachineVz,
                        changemachineVelo,changemachineTemp,changemachineTS,changemachineHud,changemachineHour);
            }
            else
            {
                //Machine dont exist in database
                addmachine(changemachineID,changemachineDate,changemachineTime,changemachineVx,changemachineVy,changemachineVz,
                        changemachineVelo,changemachineTemp,changemachineTS,changemachineHud,changemachineHour);
            }
        }
        //Database is empty
        else
        {
            Log.d(TAG, "Database empty");
            addmachine(changemachineID,changemachineDate,changemachineTime,changemachineVx,changemachineVy,changemachineVz,
                    changemachineVelo,changemachineTemp,changemachineTS,changemachineHud,changemachineHour);
        }
    }

    // Get column
    public static String[] getColumns()
    { return columns;}

    //get id
    public static String getId()
    {return _ID;}

    //get table name
    public static String getTableName()
    {return TABLE_NAME;}

    public String getMachineID()
    {return MACHINEID;}
    public String getMachineDate()
    {return MACHINEDATE;}
    public String getMachineTime()
    {return MACHINETIME;}
    public String getMachineVx()
    {return MACHINEVX;}
    public String getMachineVy()
    {return MACHINEVY;}
    public String getMachineVz()
    {return MACHINEVZ;}
    public String getmachineVelo()
    {return MACHINEVELO;}
    public String getmachineTemp()
    {return MACHINETEMP;}
    public String getMachineTS()
    {return MACHINETS;}
    public String getMachineHud()
    {return MACHINEHUD;}
    public String getMachineHour()
    {return MACHINEHOUR;}
    public String getMachineStatus()
    {return MACHINESTATUS;}
    public String getMachineFavouriteStatus()
    {return MACHINEFAVOURITESTATUS;}
    public String getMachineupdatedatetime()
    {return MACHINEUPDATEDATETIME;}

}