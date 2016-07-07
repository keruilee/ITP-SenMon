package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {
    ListView listViewListing ;
    private FavouriteDatabaseHelper databaseHelper;
    CustomAdapter adapter;
    ArrayList<Machine> myMachineList = new ArrayList<Machine>();
    ArrayList<Machine> myFavouriteMachineList = new ArrayList<Machine>();

    ArrayList <String> favouriteList=  new ArrayList<String>();

    private static final String TAG_RESULTS="result";
    ProgressDialog progressDialog;
    JSONArray serverCSVrecords = null;

    public String[] latestRecords;
    public String[] allCSVRecords;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        databaseHelper = new FavouriteDatabaseHelper(getActivity());

            listViewListing = (ListView) rootView.findViewById(R.id.ListView);
            adapter = new CustomAdapter(getActivity(),R.layout.fragment_favourite,myFavouriteMachineList);
            listViewListing.setAdapter(adapter);

            ////// when click on the item   //////////////////////
            listViewListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewGroup viewgrp = (ViewGroup) view;
                    TextView intentMachineID = (TextView) viewgrp.findViewById(R.id.textViewmachineid);
                    TextView intentTemp = (TextView) viewgrp.findViewById(R.id.textViewTemp);
                    TextView intentVelo = (TextView) viewgrp.findViewById(R.id.textViewVelocity);

                    DetailsFragment details = new DetailsFragment();
                    //using Bundle to send data
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("name", intentMachineID.getText().toString());
                    bundle2.putString("temp", intentTemp.getText().toString());
                    bundle2.putString("velo", intentVelo.getText().toString());
                    details.setArguments(bundle2); //data being send to MachineListFragment
                    //Edited by kerui
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.relativelayoutfor_fragment, details);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });


        progressDialog = new ProgressDialog(getActivity());
        getCSVData();

            return rootView;
            // / return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");

        super.onResume();
        myFavouriteMachineList.clear();
        Cursor c = FavouriteList();
        if (c.moveToFirst()) {
            do {
                Machine machineFavourite = new Machine (c.getString(1),c.getString(2),c.getString(3),
                        c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getString(8),c.getString(9),c.getString(10),
                        c.getString(11),c.getString(12),c.getString(13));
                myFavouriteMachineList.add(machineFavourite);
            } while (c.moveToNext());

        }c.close();

        CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.fragment_favourite,myFavouriteMachineList);
        listViewListing.setAdapter(adapter);


    }

    public Cursor FavouriteList() {
        // to return all records in the form of a Cursor object
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + databaseHelper.getTableName(), null);

        return cursor;
    }
    public boolean checkEvent()
    {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor c = db.rawQuery("Select * from " + databaseHelper.getTableName(), null);

        if(c.getCount() > 0){
            Log.i("CHECK", "true , found in the database" +c.getCount() );
            return true;
        }
        else{
            Log.i("CHECK", "false, not found in the databse");
            return false;
        }

    }



    //Added by Kerui
    public void getCSVData(){
        class GetCSVDataJSON extends AsyncTask<Void, Void, JSONObject> {

            URL encodedUrl;
            HttpURLConnection urlConnection = null;

            String url = "http://itpsenmon.net23.net/readFromCSV.php";

            JSONObject responseObj;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Loading Records...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    encodedUrl = new URL(url);
                    urlConnection = (HttpURLConnection) encodedUrl.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.connect();

                    InputStream input = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("doInBackground(Resp)", result.toString());
                    responseObj = new JSONObject(result.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    urlConnection.disconnect();
                }

                return responseObj;
            }

            @Override
            protected void onPostExecute(JSONObject result){
                super.onPostExecute(result);
                getCSVRecords(result);
                progressDialog.dismiss();


            }
        }
        GetCSVDataJSON g = new GetCSVDataJSON();
        g.execute();
    }

    //Get the server CSV records
    public void getCSVRecords(JSONObject jsonObj)
    {
        try {
            serverCSVrecords = jsonObj.getJSONArray(TAG_RESULTS);

            String cleanupLatestRecords;

            //remove all unwanted symbols and text
            cleanupLatestRecords = serverCSVrecords.toString().replaceAll(",false]]", "").replace("[[", "").replace("[", "").replace("]]", "").replace("\"","").replace("]","");
            //split different csv records, the ending of each csv record list is machineID.csv
            allCSVRecords = cleanupLatestRecords.split(".csv,");
            //loop through each csv and get the latest records and split each field
            for(String record : allCSVRecords)
            {
                latestRecords = record.split(",");

                    Machine machine = new Machine(latestRecords[9].replace(".csv", ""), latestRecords[0], latestRecords[1], latestRecords[2], latestRecords[3], latestRecords[4],
                            latestRecords[5], latestRecords[6], latestRecords[7], latestRecords[8], "22","","");

                    myMachineList.add(machine);
            }

            Log.d("cleanupLatestRecords: ", cleanupLatestRecords);
            Log.d("CSVRecords2: ", allCSVRecords[1]);
            Log.d("LatestRecords: 1", latestRecords[0]);
            Log.d("LatestRecords: 2", latestRecords[1]);
            Log.d("LatestRecords: 6 V", latestRecords[6]);
            Log.d("LatestRecords: 7 T", latestRecords[7]);
            Log.d("LatestRecords: 9 ", latestRecords[9]);
            Log.d("LatestRecords: last ", latestRecords[9].replace(".csv", ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
