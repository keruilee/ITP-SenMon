package edu.singaporetech.senmon;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {
    ListView listViewListing ;
    private FavouriteDatabaseHelper databaseHelper;
    Machine machine_data[];

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<Machine> myMachineList = new ArrayList<Machine>();
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        listViewListing = (ListView)rootView.findViewById(R.id.ListView);
        databaseHelper = new FavouriteDatabaseHelper(getActivity());


        if (true ==checkEvent())
        {
            Cursor c = FavouriteList();
            if (c.moveToFirst()) {
                do {
                    Machine machine =  new Machine(c.getString(1), "0", "0");
                    myMachineList.add(machine);

                } while (c.moveToNext());

            }c.close();

        CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.fragment_favourite,myMachineList);
        listViewListing.setAdapter(adapter);

                // when click on the item
        listViewListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup viewgrp = (ViewGroup) view;
                TextView intentMachineID = (TextView) viewgrp.findViewById(R.id.textViewmachineid);

                // intent to the detail page
//                Intent intent = new Intent(getActivity(), detail.class);
//                intent.putExtra("MachineID", intentMachineID.getText().toString());
//                startActivity(intent);
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                DetailsFragment details = new DetailsFragment();
                //using Bundle to send data
                Bundle bundle3=new Bundle();
                bundle3.putString("name", intentMachineID.getText().toString());
                details.setArguments(bundle3); //data being send to MachineListFragment
                transaction.replace(R.id.relativelayoutfor_fragment, details);
                transaction.commit();
            }
        });

        return rootView;
        // / return inflater.inflate(R.layout.fragment_list, container, false);
        }
        else if (false == checkEvent())
        {
            Log.i("hii","nope");
        }
         return inflater.inflate(R.layout.fragment_favourite, container, false);


    }


    @Override
   public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");

        super.onResume();
        ArrayList<Machine> myMachineList = new ArrayList<Machine>();
            Cursor c = FavouriteList();
            if (c.moveToFirst()) {
                do {
                    Machine machine =  new Machine(c.getString(1), "0", "0");
                    myMachineList.add(machine);

                } while (c.moveToNext());
            }  c.close();

            CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.fragment_favourite,myMachineList);
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


}
