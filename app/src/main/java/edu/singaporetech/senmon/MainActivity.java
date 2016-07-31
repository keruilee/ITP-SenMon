package edu.singaporetech.senmon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NotificationsEnabled = "ntfnEnabledKey";
    public static final String WarningEnabled = "warningEnabledKey";
    public static final String CriticalEnabled = "criticalEnabledKey";
    public static final String FavNtfnOnly = "favNtfnOnlyKey";

    public static final String HOME_FRAG_TAG = "HOME_FRAGMENT";
    public static final String FAV_FRAG_TAG = "FAV_FRAGMENT";
    public static final String LIST_FRAG_CRIT_TAG = "LIST_FRAGMENT_CRITICAL";
    public static final String LIST_FRAG_WARN_TAG = "LIST_FRAGMENT_WARNING";
    public static final String LIST_FRAG_NORM_TAG = "LIST_FRAGMENT_NORMAL";
    public static final String LIST_FRAG_ALL_TAG = "LIST_FRAGMENT_ALL";
    public static final String SETTINGS_FRAG_TAG = "SETTINGS_FRAGMENT";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseHelper mydatabaseHelper;
    public Context context;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu menu;

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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
                {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        computeMachine();
                        super.onDrawerOpened(drawerView);
                    }
                };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        menu.getItem(0).setChecked(true);
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

        getSupportFragmentManager().addOnBackStackChangedListener(this);
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

 /*   @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        computeMachine();

        Log.i("drawer","compute");
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //https://guides.codepath.com/android/Using-the-App-ToolBar



        getMenuInflater().inflate(R.menu.menu_main, menu);

        //testing menu item

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        Log.i("Search", "searchView ");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                SearchFragment searchfragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("SearchQuery", s);
                searchfragment.setArguments(bundle); //data being send to MachineListFragment
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.relativelayoutfor_fragment, searchfragment, "SEARCH_FRAGMENT")
                        .addToBackStack("SEARCH_FRAGMENT")
                        .commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

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

        if (id == R.id.nav_home) {

            HomeFragment homeFragment = new HomeFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.relativelayoutfor_fragment, homeFragment, HOME_FRAG_TAG)
                    .addToBackStack(HOME_FRAG_TAG)
                    .commit();
        }
        //link to favourite machine
        else if (id == R.id.nav_favourite) {
            FavouriteFragment myFavouriteFragment = new FavouriteFragment();
            FragmentManager myManager = getSupportFragmentManager();
            myManager.beginTransaction()
                    .replace(R.id.relativelayoutfor_fragment, myFavouriteFragment, FAV_FRAG_TAG)
                    .addToBackStack(FAV_FRAG_TAG)
                    .commit();
        }
        //link to critical machine
        else if (id == R.id.nav_critical) {

            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "Critical");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_CRIT_TAG)
                    .addToBackStack(LIST_FRAG_CRIT_TAG)
                    .commit();

        }
        //link to warning machine
        else if (id == R.id.nav_warning) {

            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "Warning");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_WARN_TAG)
                    .addToBackStack(LIST_FRAG_WARN_TAG)
                    .commit();

        }
        //link to normal machine
        else if (id == R.id.nav_normal) {
            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "Normal");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_NORM_TAG)
                    .addToBackStack(LIST_FRAG_NORM_TAG)
                    .commit();

        }
        //link to all machine
        else if (id == R.id.nav_all) {
            ListFragment list = new ListFragment();
            //using Bundle to send data
            Bundle bundle = new Bundle();
            bundle.putString("name", "All");
            list.setArguments(bundle); //data being send to MachineListFragment
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutfor_fragment, list, LIST_FRAG_ALL_TAG)
                    .addToBackStack(LIST_FRAG_ALL_TAG)
                    .commit();
        }
        //link to setting
        else if (id == R.id.nav_settings) {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.relativelayoutfor_fragment, settingsFragment, SETTINGS_FRAG_TAG)
                    .addToBackStack(SETTINGS_FRAG_TAG)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void computeMachine() {
        mydatabaseHelper = new DatabaseHelper(this);
        if (mydatabaseHelper.getRowsCount() != 0) {

            if (mydatabaseHelper.returnStringMachineStateString("Warning").isEmpty())
            {menu.findItem(R.id.nav_warning).setEnabled(false);}
            else
            {menu.findItem(R.id.nav_warning).setEnabled(true);}

            if (mydatabaseHelper.returnStringMachineStateString("Critical").isEmpty())
            {menu.findItem(R.id.nav_critical).setEnabled(false);}
            else
            {menu.findItem(R.id.nav_critical).setEnabled(true);}

            if (mydatabaseHelper.returnStringMachineStateString("Normal").isEmpty())
            {menu.findItem(R.id.nav_normal).setEnabled(false);}
            else
            {menu.findItem(R.id.nav_normal).setEnabled(true);}
        }
    }

    /**
     * update navigation drawer selected item when things are added / removed from back stack
     * selected item in drawer corresponds to current fragment
     */
    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null)
        {
            //HomeFragment currFrag = (HomeFragment) manager.findFragmentById(R.id.nav_home);
            int backstackCount = manager.getBackStackEntryCount();
            if(backstackCount > 0) {
                FragmentManager.BackStackEntry backEntry = manager.getBackStackEntryAt(backstackCount - 1);
                String str = backEntry.getName();
                switch(str)
                {
                    case HOME_FRAG_TAG:
                        menu.getItem(0).setChecked(true);
                        break;
                    case FAV_FRAG_TAG:
                        menu.getItem(1).setChecked(true);
                        break;
                    case LIST_FRAG_CRIT_TAG:
                        menu.getItem(2).setChecked(true);
                        break;
                    case LIST_FRAG_WARN_TAG:
                        menu.getItem(3).setChecked(true);
                        break;
                    case LIST_FRAG_NORM_TAG:
                        menu.getItem(4).setChecked(true);
                        break;
                    case LIST_FRAG_ALL_TAG:
                        menu.getItem(5).setChecked(true);
                        break;
                    case SETTINGS_FRAG_TAG:
                        menu.getItem(6).setChecked(true);
                        break;
                    default:
                        uncheckAllMenuItems();
                        break;
                }
            }
            else
            {
                // nothing in backstack; home fragment displayed on screen
                menu.getItem(0).setChecked(true);
            }
        }
    }

    private void uncheckAllMenuItems() {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                SubMenu subMenu = item.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setChecked(false);
                }
            } else {
                item.setChecked(false);
            }
        }
    }
}