package edu.singaporetech.senmon;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
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

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Hardcode array
        Machine machine = new Machine("SDK001-M001-01-0001a", "0.3", "36.11", "50");
        Machine machine2 = new Machine("SDK221-M001-01-0001a", "0.2244", "10.11", "33");
        Machine machine3 = new Machine("SDK331-M001-01-0001a", "0.293", "20.11", "53");
        Machine machine4 = new Machine("ADK444-M001-01-0001a", "0.922", "30.11", "900");
        Machine machine5 = new Machine("SDK555-M001-01-0001a", "0.312", "40.11", "6");
        Machine machine6 = new Machine("SDK166-M001-01-0001a", "0.9222", "5.11", "3");

        myMachineList.add(machine);
        myMachineList.add(machine2);
        myMachineList.add(machine3);
        myMachineList.add(machine4);
        myMachineList.add(machine5);
        myMachineList.add(machine6);


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
        Iterator<Machine> i = myMachineList.iterator();
        while (i.hasNext()) {
            Machine s = i.next();
            for (int q = 0; q < machineArray.size(); q++) {
                if (!(machineArray.get(q).contains(s.getMachineID()))) {
                    i.remove();
                }
            }
        }

        // temperature button on click
        sortTempButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortTempButton.setBackgroundColor(Color.parseColor("#94949C"));
                sortVeloButton.setBackgroundResource(R.drawable.button_border);
                sortMachineButton.setBackgroundResource(R.drawable.button_border);


                Log.i("Sort", "Sorting temp");
                Collections.sort(myMachineList, new Comparator<Machine>() {
                    public int compare(Machine m1, Machine m2) {
                        return m2.getmachineTemp().compareTo(m1.getmachineTemp());
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
                        return m2.getmachineVelo().compareTo(m1.getmachineVelo());
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

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle2 = new Bundle();
                bundle2.putString("name", intentMachineID.getText().toString());
                details.setArguments(bundle2); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.commit();
            }
        });


        return rootView;
        // / return inflater.inflate(R.layout.fragment_list, container, false);
    }
}
