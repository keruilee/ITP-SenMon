package edu.singaporetech.senmon;

/**
 * Created by jinyu on 10/6/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class FavouriteDatabaseHelper extends SQLiteOpenHelper {
    private final Context mContext;
    final static String TAG = "wishhelp";
    //creating Database
    final static String TABLE_NAME = "FavouriteTable";
    final static String MACHINEID = "machine";
    final static String _ID = "_id";
    final static String[] columns = { _ID, MACHINEID};

    final private static String DBNAME = "FavouriteDB";
    final private static Integer VERSION = 1;
    final private static String  CREATE_CMD = "CREATE TABLE " + TABLE_NAME + " (" +_ID + " INTEGER PRIMARY KEY," + columns[1] + " STRING);" ;

    public FavouriteDatabaseHelper(Context context) {
        // logic to create database

        super(context,DBNAME,null,VERSION);
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
    public void deleteDatabase(){
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
    public void clearRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.close();
    }
    // Delete the row by using machindid //
    public void removeMachineID(String machineID)
    {    SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, " machine= '" + machineID + "'", null);
        db.close();

    }
    public long getRowsCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }
    // Get column
    public static String[] getColumns()
    {
        return columns;
    }
    // getMachineid
    public  static String getMachineid()
    {
        return MACHINEID;
    }
    //get id
    public static String getId()
    {
        return _ID;
    }
    //get table name
    public  static String getTableName()
    {
        return TABLE_NAME;
    }

}