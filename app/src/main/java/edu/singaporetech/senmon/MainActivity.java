package edu.singaporetech.senmon;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NotificationsEnabled = "ntfnEnabledKey";
    public static final String WarningEnabled = "warningEnabledKey";
    public static final String CriticalEnabled = "criticalEnabledKey";
    public static final String FavNtfnOnly = "favNtfnOnlyKey";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;

        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(sharedPreferences == null) {
            editor = sharedPreferences.edit();
            editor.putBoolean(NotificationsEnabled, true);
            editor.putBoolean(WarningEnabled, true);
            editor.putBoolean(CriticalEnabled, true);
            editor.putBoolean(FavNtfnOnly, false);
            editor.commit();
        }
        if(sharedPreferences.getBoolean(NotificationsEnabled, true)){
            Log.d("msg", "notification is true");
        };
        //start nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        //start home fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.relativelayoutfor_fragment, new HomeFragment()).commit();
        }


        Intent alarm = new Intent(this.context, AlarmReceiver.class);
        //check if the alarmservice has already started
        boolean isAlarmRunning = (PendingIntent.getBroadcast(this.context,0,alarm, PendingIntent.FLAG_NO_CREATE) != null);
        //if alarmsservice has not start then start it
        if(isAlarmRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0 , alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 15000, pendingIntent);

        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //link to home
        if (id == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, homeFragment).addToBackStack(null).commit();
        }
        //link to favourite machine
        else if (id == R.id.nav_favourite) {

            Context context = getApplicationContext();
            CharSequence text = "Favourite List";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            FavouriteFragment myFavouriteFragment = new FavouriteFragment();
            FragmentManager myManager = getSupportFragmentManager();
            myManager.beginTransaction().replace(R.id.relativelayoutfor_fragment, myFavouriteFragment).addToBackStack(null).commit();
        }
        //link to critical machine
        else if (id == R.id.nav_critical) {

            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "Critical");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, list).addToBackStack(null).commit();

        }
        //link to warning machine
        else if (id == R.id.nav_warning) {

            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "Warning");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, list).addToBackStack(null).commit();

        }
        //link to normal machine
        else if (id == R.id.nav_normal) {
            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "Normal");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, list).addToBackStack(null).commit();

        }
        //link to all machine
        else if (id == R.id.nav_all) {
            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "All");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, list).addToBackStack(null).commit();
        }
        //link to setting
        else if (id == R.id.nav_settings) {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, settingsFragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        item.setChecked(true);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
