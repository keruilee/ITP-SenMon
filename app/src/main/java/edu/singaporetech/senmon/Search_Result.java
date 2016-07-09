package edu.singaporetech.senmon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Search_Result extends AppCompatActivity {
    DatabaseHelper db;
    TextView textViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        textViewResult = (TextView) findViewById(R.id.result);
        textViewResult.setVisibility(View.VISIBLE);
        /////// Retrive the search ///////////
        Bundle bundle = getIntent().getExtras();
        String query = bundle.getString("SearchQuery");
        Log.i("Search", query);


        db = new DatabaseHelper(getApplication());
        if (db.findMachine(query) ==true )
        {
            Log.i("FINDMACHINE", "hi");
            textViewResult.setText(query + "FOUND");

        }
        else
        {
            Log.i("FINDMACHINE", "bye");
            textViewResult.setText(query + "NOT FOUND");

        }

    }
}
