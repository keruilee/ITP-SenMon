package edu.singaporetech.senmon;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


//*//**
// * A simple {@link Fragment} subclass.
// *//*
public class SearchFragment extends Fragment {

    DatabaseHelper db;
    TextView textViewResult;
    ListView listView;
    String query = "";

    public static final String DETAILS_FRAG_TAG = "DETAILS_FRAGMENT";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        textViewResult = (TextView) rootView.findViewById(R.id.result);

        ArrayList<String> resultsString = new ArrayList<String>();
        listView = (ListView) rootView.findViewById(R.id.listView);


        /////////// Retrive the search ///////////
        Bundle bundle = getArguments();
        if (bundle != null) {

            query = String.valueOf(bundle.getString("SearchQuery"));
            Log.i("Search ??", query);
        }
        // check from database helper
        db = new DatabaseHelper(getActivity());
        resultsString = db.SearchMachine(query);

        if (resultsString == null || resultsString.isEmpty()) {
            textViewResult.setText(getString(R.string.you_have_entered) +" '" + query + "'. " +getString(R.string.no_results_found));
        } else {
            textViewResult.setText(getString(R.string.you_have_entered) +" '" + query + "'");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, resultsString);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String itemValue = (String) listView.getItemAtPosition(position);

                    Log.i("itemPosition", "itemPosition" + itemPosition);
                    Log.i("itemValue", itemValue);
                    DetailsFragment details = new DetailsFragment();
                    //using Bundle to send data
                    Bundle bundle = new Bundle();
                    bundle.putString("name", itemValue);
                    details.setArguments(bundle); //data being send to MachineListFragment
                    //Edited by kerui
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.relativelayoutfor_fragment, details, DETAILS_FRAG_TAG);
                    transaction.addToBackStack(DETAILS_FRAG_TAG);
                    transaction.commit();

                }
            });

        }
        return rootView;

    }

}
