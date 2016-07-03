package edu.singaporetech.senmon;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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

    public ArrayList<Machine> myMachineList = new ArrayList<Machine>();
    public ArrayList<String> machineArray = new ArrayList<String>();

    String TAG = "List Fragment";
    Context context;

    //For the sorting tab
    private TabLayout tabLayout;
    ViewPager viewPager;
    ViewTabPageAdapter viewTabPagerAdapter;

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewTabPagerAdapter = new ViewTabPageAdapter(getActivity().getSupportFragmentManager(),
                this.getContext());
        viewPager.setAdapter(viewTabPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        // Give the TabLayout the ViewPage
        tabLayout = (TabLayout) rootView.findViewById(R.id.list_tabs);
        tabLayout.setupWithViewPager(viewPager);


        return rootView;
    }


    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        viewPager.invalidate();
        viewTabPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(null);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        viewPager.setAdapter(viewTabPagerAdapter);
        super.onResume();
    }
}
