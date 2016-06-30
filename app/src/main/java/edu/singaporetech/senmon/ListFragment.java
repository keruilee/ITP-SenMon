package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;

import static java.lang.String.CASE_INSENSITIVE_ORDER;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    ListView listViewListing;
    public ArrayList<Machine> myMachineList = new ArrayList<Machine>();
    public ArrayList<String> machineArray = new ArrayList<String>();
    String TAG = "List Fragment";
    String value = "";

    ProgressDialog progressDialog;
    private static final String TAG_RESULTS="result";
    JSONArray serverCSVrecords = null;
    public String[] latestRecords;
    public String[] allCSVRecords;


    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        getCSVData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Hardcode array
//        Machine machine = new Machine("SDK001-M001-01-0001a", "0.3", "36.11", "50");
//        Machine machine2 = new Machine("SDK221-M001-01-0001a", "0.2244", "10.11", "33");
//        Machine machine3 = new Machine("SDK331-M001-01-0001a", "0.293", "20.11", "53");
//        Machine machine4 = new Machine("ADK444-M001-01-0001a", "0.922", "30.11", "900");
//        Machine machine5 = new Machine("SDK555-M001-01-0001a", "0.312", "40.11", "6");
//        Machine machine6 = new Machine("SDK166-M001-01-0001a", "0.9222", "5.11", "3");

//        myMachineList.add(machine);
//        myMachineList.add(machine2);
//        myMachineList.add(machine3);
//        myMachineList.add(machine4);
//        myMachineList.add(machine5);
//        myMachineList.add(machine6);


        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        final Button sortMachineButton = (Button) rootView.findViewById(R.id.buttonMachineID);
        final Button sortTempButton = (Button) rootView.findViewById(R.id.buttonTemperature);
        final Button sortVeloButton = (Button) rootView.findViewById(R.id.buttonVelocity);

        //set default color and sort
        sortVeloButton.setBackgroundColor(Color.parseColor("#94949C"));
        sortTempButton.setBackgroundResource(R.drawable.button_border);
        sortMachineButton.setBackgroundResource(R.drawable.button_border);

        //retrieving data using bundle
        Bundle bundle = getArguments();

        if (bundle != null) {
            //Log.i(TAG + " Machine Name ", String.valueOf(bundle.getStringArrayList("name")));
            machineArray.add(String.valueOf(bundle.getStringArrayList("name")));
        }

        //remove unwanted data
        //(Kerui: Whats the use of the Iterator to remove unwanted data? This block of code will cause app to crash when back from details fragment)
//        Iterator<Machine> i = myMachineList.iterator();
//        while (i.hasNext()) {
//            Machine s = i.next();
//            for (int q = 0; q < machineArray.size(); q++) {
//                if (!(machineArray.get(q).contains(s.getMachineID()))) {
//                    i.remove();
//                }
//            }
//        }

        // temperature button on click
        sortTempButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortTempButton.setBackgroundColor(Color.parseColor("#94949C"));
                sortVeloButton.setBackgroundResource(R.drawable.button_border);
                sortMachineButton.setBackgroundResource(R.drawable.button_border);


                Log.i("Sort", "Sorting temp");
                Collections.sort(myMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return Double.compare(Double.parseDouble(m2.getmachineTemp()), Double.parseDouble(m1.getmachineTemp()));
                    }
                });
                CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.fragment_list, myMachineList);
                listViewListing.setAdapter(adapter);
                //sortTempButton.setTextColor(Color.parseColor("#ff0000"));
            }
        });

        // Velo button click
        sortVeloButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortVeloButton.setBackgroundColor(Color.parseColor("#94949C"));
                sortMachineButton.setBackgroundResource(R.drawable.button_border);
                sortTempButton.setBackgroundResource(R.drawable.button_border);

                Log.i("Sort", "Sorting velo");
                Collections.sort(myMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return Double.compare(Double.parseDouble(m2.getmachineVelo()), Double.parseDouble(m1.getmachineVelo()));
                    }
                });
                CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.fragment_list, myMachineList);
                listViewListing.setAdapter(adapter);
            }
        });

        // favourite button on click
        sortMachineButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sortMachineButton.setBackgroundColor(Color.parseColor("#94949C"));
                sortVeloButton.setBackgroundResource(R.drawable.button_border);
                sortTempButton.setBackgroundResource(R.drawable.button_border);

                Log.i("Sort", "Sorting letter");
                Collections.sort(myMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return m1.getMachineID().compareTo(m2.getMachineID());
                    }
                });
                CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.fragment_list, myMachineList);
                listViewListing.setAdapter(adapter);

            }
        });


        // list view
        listViewListing = (ListView) rootView.findViewById(R.id.ListView);
        CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.fragment_list, myMachineList);
        listViewListing.setAdapter(adapter);

        // when click on the item
        listViewListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup viewgrp = (ViewGroup) view;
                TextView intentMachineID = (TextView) viewgrp.findViewById(R.id.textViewmachineid);
                TextView intentTemp = (TextView)viewgrp.findViewById(R.id.textViewTemp);
                TextView intentVelo = (TextView)viewgrp.findViewById(R.id.textViewVelocity);

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


        return rootView;
        // / return inflater.inflate(R.layout.fragment_list, container, false);
    }


    //Added by Kerui
    public void getCSVData(){
        class GetCSVDataJSON extends AsyncTask<Void, Void, JSONObject>{

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

                Machine machine = new Machine(latestRecords[9].replace(".csv",""),latestRecords[0],latestRecords[1],latestRecords[2],latestRecords[3],latestRecords[4],
                        latestRecords[5],latestRecords[6],latestRecords[7],latestRecords[8],"22");

                myMachineList.add(machine);

            }

            Log.d("cleanupLatestRecords: ", cleanupLatestRecords);
            Log.d("CSVRecords2: ", allCSVRecords[1]);
            Log.d("LatestRecords: ", latestRecords[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
