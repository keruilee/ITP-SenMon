package edu.singaporetech.senmon;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class detail extends AppCompatActivity {
    private static final String TAG = "detail" ;
    String machineID;
    TextView textViewMachineId;
    Button btnfavourite;
    private FavouriteDatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // intent the machine id and display to on textview
        Bundle extras = getIntent().getExtras();
        machineID = extras.getString("MachineID");
        textViewMachineId = (TextView) findViewById(R.id.textViewmachineid);
        textViewMachineId.setText(machineID);

        //database helper
        databaseHelper = new FavouriteDatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();


        // set the button text
        btnfavourite =(Button) findViewById(R.id.buttonFavourite);
        if (checkEvent(machineID) == false)
        {
            btnfavourite.setText("Click to favourite");
        }
        else
        {
            btnfavourite.setText("Click to unfavourite");
        }


        // favourite button on click
        btnfavourite.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (checkEvent(machineID) == false)
                {

                    databaseHelper.addmachineID(machineID);
                   /* ContentValues values = new ContentValues();
                    values.put(databaseHelper.MACHINEID, machineID);
                    db.insert(databaseHelper.TABLE_NAME, null, values);*/
                    checkEvent(machineID);
                    btnfavourite.setText("Click to unfavourite");
                }
                else if (checkEvent(machineID) == true)

                {
                    databaseHelper.removeMachineID(machineID);
                    checkEvent(machineID);
                    btnfavourite.setText("Click to favourite");
                }
            }});


    }


    // to check if the machineID has already stored in the databasehelper
    public boolean checkEvent(String machineID)
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String queryString = "SELECT * FROM FavouriteTable WHERE machine = '"+machineID+"'";
        Cursor c = db.rawQuery(queryString, null);
        if(c.getCount() > 0){
            Log.i("CHECK", "true , found in the database");
            return true;
        }
        else{
            Log.i("CHECK", "false, not found in the databse");
            return false;
        }

    }

}
