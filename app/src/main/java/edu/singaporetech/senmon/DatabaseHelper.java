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


    final static String _ID = "_id";


    final static String[] columns = {_ID,MACHINEID,MACHINEDATE,MACHINETIME,MACHINEVX,MACHINEVY,MACHINEVZ,MACHINEVELO,
                                      MACHINETEMP,MACHINETS,MACHINEHUD,MACHINEHOUR,MACHINESTATUS,MACHINEFAVOURITESTATUS};

    final private static String DBNAME = "FavouriteDB";
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
                                        columns[13] + " STRING );";

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
    public void addmachineID(String addmachineID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MACHINEID, addmachineID);
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
    // Clear rows //
    public void clearRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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

}