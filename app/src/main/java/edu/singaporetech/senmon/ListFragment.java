package edu.singaporetech.senmon;


import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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
import java.util.List;

import javax.crypto.Mac;

import static java.lang.String.CASE_INSENSITIVE_ORDER;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {
    ListView listViewListing ;
    final ArrayList<Machine> myMachineList = new ArrayList<Machine>();


    //String[] machineeee = {"SDK001-M001-01-0001a" ,"SDK221-M001-01-0001a", "SDK33-M001-01-0001a", "B1133-M001-01-0001a", "SDK33-M001-01-0001a", "AAA222-M001-01-0001a"};
   // String[] velo = {"0.641","0.321","0.641","0.421","0.222","0.221"};
    //String[] temp = {"36.11","15.11","22.11","40.11","33.11","10.11"};

    public ListFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Machine machine =  new Machine("SDK001-M001-01-0001a", "0.641", "36.11");
        Machine machine2 = new Machine("SDK221-M001-01-0001a", "0.321", "10.11");
        Machine machine3 = new Machine("SDK331-M001-01-0001a", "0.441", "20.11");
        Machine machine4 = new Machine("ADK444-M001-01-0001a", "0.221", "30.11");
        Machine machine5 = new Machine("SDK555-M001-01-0001a", "0.121", "40.11");
        Machine machine6 = new Machine("SDK166-M001-01-0001a", "0.551", "50.11");

        myMachineList.add(machine);
        myMachineList.add(machine2);
        myMachineList.add(machine3);
        myMachineList.add(machine4);
        myMachineList.add(machine5);
        myMachineList.add(machine6);



       /* Machine machine_data[] = new Machine[]
                {
                        new Machine("SDK001-M001-01-0001a", "0.641", "36.11"),
                        new Machine("SDK221-M001-01-0001a", "0.321", "10.11"),
                        new Machine("SDK331-M001-01-0001a", "0.441", "20.11"),
                        new Machine("SDK444-M001-01-0001a", "0.221", "30.11"),
                        new Machine("SDK555-M001-01-0001a", "0.121", "40.11"),
                        new Machine("SDK666-M001-01-0001a", "0.551", "50.11"),
                        new Machine("SDK777-M001-01-0001a", "0.331", "15.11")

        };*/
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        Button SortMachineButton = (Button)rootView.findViewById(R.id.buttonMachineID);
        // favourite button on click
        SortMachineButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i("Sort", "Sorting");
                Collections.sort(myMachineList, new Comparator<Machine>() {
                    @Override
                    public int compare(Machine lhs, Machine rhs) {
                        Log.i("Sort","Sorting?");
                        Log.i("Sort",lhs+"");
                        Log.i("Sort",rhs+"");

                        return 0;
                    }
                });
                CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.fragment_list , myMachineList);
                listViewListing.setAdapter(adapter);

            }});


        // list view
        listViewListing = (ListView)rootView.findViewById(R.id.ListView);
        CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.fragment_list , myMachineList);
        listViewListing.setAdapter(adapter);



        // when click on the item
        listViewListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup viewgrp = (ViewGroup) view;
                TextView intentMachineID = (TextView) viewgrp.findViewById(R.id.textViewmachineid);

               // intent to the detail page
//                Intent intent = new Intent(getActivity(), DetailsFragment.class);
//                intent.putExtra("MachineID", intentMachineID.getText().toString());
//                startActivity(intent);

                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle2=new Bundle();
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
