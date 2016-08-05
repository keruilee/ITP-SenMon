package edu.singaporetech.senmon;

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
    //final static String MACHINEUPDATEDATETIME = "machineUpdateDateTime"; // yes, no , use this as a standard

    final static String _ID = "_id";

    final static String[] columns = {_ID,MACHINEID,MACHINEDATE,MACHINETIME,MACHINEVX,MACHINEVY,MACHINEVZ,MACHINEVELO,
            MACHINETEMP,MACHINETS,MACHINEHUD,MACHINEHOUR,MACHINESTATUS,MACHINEFAVOURITESTATUS};

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
            columns[11] + " STRING DEFAULT '0', " +         // set default of machine hours to 0
            columns[12] + " STRING, " +
            columns[13] + " STRING DEFAULT 'no')";

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

    // find machine //
    public Machine getMachineDetails (String findMachineID) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, columns,
                MACHINEID + " = ?",
                new String[] { findMachineID }, null,null,null);

        if (c.moveToFirst()) {
            Machine machineStringObj = new Machine(mContext, c.getString(1), c.getString(2), c.getString(3),
                    c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                    c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
            c.close();
            return machineStringObj;
        }
        c.close();
        return null;
    }

    // find machine //
    public boolean findMachine (String findMachineID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, columns,
                MACHINEID + " = ?",
                new String[] { findMachineID }, null,null,null);

        boolean machineExist = cursor.getCount() > 0;
        cursor.close();
        return machineExist;
    }

    ////////// To search a list of machine //////////////
    public ArrayList <String> SearchMachine (String findMachineID) {
        ArrayList <String> resultsString = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME +" WHERE machineID LIKE " + "'%" +findMachineID +"%' " ;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Log.d(TAG, c.getString(1) + "" + c.getString(12));
                Machine machineStringObj  = new Machine(mContext, c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                        c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
                resultsString.add(c.getString(1));

                Log.i(TAG+"search", machineStringObj.getMachineID());
            } while (c.moveToNext());
        }

        c.close();
        return resultsString;
    }

    // Insert/update database //
    /**
     * update machines in database
     * if it's a new machine, add a new row
     * else if it already exist in database, update its value
     * @param machine the machine to be added/updated in database
     */
    public void updateDatabase(Machine machine){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MACHINEDATE, machine.getMachineDate());
        values.put(MACHINETIME, machine.getMachineTime());
        values.put(MACHINEVX, machine.getMachineVx());
        values.put(MACHINEVY, machine.getMachineVy());
        values.put(MACHINEVZ, machine.getMachineVz());
        values.put(MACHINEVELO, machine.getmachineVelo());
        values.put(MACHINETEMP, machine.getmachineTemp());
        values.put(MACHINETS, machine.getMachineTS());
        values.put(MACHINEHUD, machine.getMachineHud());
        values.put(MACHINESTATUS, machine.getMachineStatus());
        // op hours is not updated/added here; only in updateOpHours() function

        if (findMachine(machine.getMachineID()))        // machine exists in database, just update it
        {
            db.update(TABLE_NAME, values, MACHINEID + "= ?", new String[] {machine.getMachineID()});
        }
        else                                            // machine does not exist in database, add new machine
        {
            values.put(MACHINEID, machine.getMachineID());
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }

    /**
     * update machine operating hours in database
     * @param machineID the ID of machine to be updated
     * @param opHours the operating hours to be updated
     */
    public void updateOpHours(String machineID, String opHours)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MACHINEHOUR, opHours);
        db.update(TABLE_NAME, values, MACHINEID + "= ?", new String[] {machineID});
        db.close();
    }

    /**
     * updates all machine states in database when warning/critical value is adjusted
     */
    public void updateAllMachineStates() {
        SQLiteDatabase dbWrite = this.getWritableDatabase();

        // Select All Query
        SQLiteDatabase dbRead = this.getReadableDatabase();
        Cursor cursor = dbRead.query(TABLE_NAME, columns, null, null, null, null, null);

        // looping through all rows and updating status
        if (cursor.moveToFirst()) {
            do {
                Machine machineStringObj = new Machine(mContext, cursor.getString(1),cursor.getString(2),cursor.getString(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),
                        cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13));
                ContentValues cv = new ContentValues();
                cv.put(MACHINESTATUS, machineStringObj.getMachineStatus());
                dbWrite.update(TABLE_NAME, cv, MACHINEID + "= ?", new String[] {cursor.getString(1)});
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbRead.close();
        dbWrite.close();
    }

    // Find machine that is favourite //
    public ArrayList <Machine> returnFavourite() {
        Log.d("TEST FAV", "enter");
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList <Machine> machineString = new ArrayList<>();
        Machine machineStringObj;
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE machineFavouriteStatus == 'yes' ";
        Cursor c = db.rawQuery(selectQuery, null);

        //Cursor c = db.query(TABLE_NAME, columns,MACHINESTATUS + " = ?",new String[]{""}, null, null, null);
        // looping through if exist
        if (c.moveToFirst()) {
            do {
                Log.d("Favourite", c.getString(1) + "" + c.getString(12));
                 machineStringObj  = new Machine(mContext, c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                        c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
                machineString.add(machineStringObj);

            } while (c.moveToNext());
        }

        c.close();
        return machineString;

    }

    public boolean favouriteExist(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE machineFavouriteStatus == 'yes' ";
        Cursor c = db.rawQuery(selectQuery, null);
        int count = c.getCount();
        c.close();
        if (count > 0){
            return true;
        }else
            return false;
    }

    /**
     * check if given machine is in favourite list
     * @param machineID the ID of machine to be checked
     * @return true if machine is in fav list
     */
    public boolean isInFavourite(String machineID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {MACHINEFAVOURITESTATUS},
                MACHINEID + " = ? AND " +MACHINEFAVOURITESTATUS + " = ?",
                new String[] { machineID, "yes" }, null,null,null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        if(count > 0)
            return true;

        return false;
    }

    /**
     * add a machine to favourite list by updating favstatus in database to "yes"
     * @param machineID the ID of machine to be added from fav list
     */
    public void addToFavourite(String machineID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MACHINEFAVOURITESTATUS, "yes");
        // op hours is not updated/added here; only in updateOpHours() function

        db.update(TABLE_NAME, values, MACHINEID + "= ?", new String[] {machineID});
        db.close();
    }

    /**
     * remove a machine from favourite list by updating favstatus in database to "no"
     * @param machineID the ID of machine to be removed from fav list
     */
    public void removeFromFavourite(String machineID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MACHINEFAVOURITESTATUS, "no");
        // op hours is not updated/added here; only in updateOpHours() function

        db.update(TABLE_NAME, values, MACHINEID + "= ?", new String[] {machineID});
        db.close();
    }

    // Find all machine in the database //
    public ArrayList <Machine> returnStringMachineAllString() {
        ArrayList <Machine> machineString = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to mydatabaselist
        if (cursor.moveToFirst()) {
            do {
                Machine machineStringObj = new Machine(mContext, cursor.getString(1),cursor.getString(2),cursor.getString(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),
                        cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13));
                machineString.add(machineStringObj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return machineString;
    }

    // Find machine in a particular state in database //
    public ArrayList <Machine> returnStringMachineStateString(String machineStatus) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList <Machine> machineString = new ArrayList<>();

        Cursor c = db.query(TABLE_NAME, columns,
                MACHINESTATUS + " = ?",
                new String[]{machineStatus}, null, null, null);
        // looping through if exist
        if (c.moveToFirst()) {
            do {
                Log.d(TAG, c.getString(1) + "" + c.getString(12));
                Machine machineStringObj  = new Machine(mContext, c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                        c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
                machineString.add(machineStringObj);

            } while (c.moveToNext());
        }
        c.close();
        return machineString;
    }

    /**
     * queries the database to find the number of machines in favourite list
     * @return number of machine in favourite list
     */
    public int checkNumberOfFavouriteMachineInAlert (){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns,
                MACHINEFAVOURITESTATUS + " = ? AND " + MACHINESTATUS + " != ?",
                new String[]{"yes", "Normal"}, null, null, null);

        Log.d("CHECK FAV FUNCTION", cursor.getCount()+" FAVOURITE");
        int totalMachine = cursor.getCount();
        cursor.close();
        return totalMachine;

    }

    /**
     * queries the database to find the number of machines in the given status
     * @param status either critical, warning or normal
     * @return the number of machine in the given status
     */
    public int getNumOfMachinesByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns,
                MACHINESTATUS + " = ?",
                new String[]{status}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
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
    /*public String getMachineupdatedatetime()
    {return MACHINEUPDATEDATETIME;}*/

}