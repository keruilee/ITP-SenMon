package edu.singaporetech.senmon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//
//
///**
// * Created by Kerui on 27/7/2016.
// */
public class WebService extends AsyncTask<Void, Void, JSONObject> {
    OnAsyncRequestComplete caller;
    Context mContext;
    String method = "GET";
    List<String> parameters = null;
    ProgressDialog progressDialog = null;
    JSONArray serverSQLrecords = null;
    URL encodedUrl;
    HttpURLConnection urlConnection = null;
    private static final String TAG_RESULTS = "result";
    String url = "http://itpsenmon.net23.net/readFromSQL.php";

    JSONObject responseObj;

    public WebService(Context a, OnAsyncRequestComplete listener) {
        this.mContext = a;
        this.caller = listener;
    }

    // Interface to be implemented by calling activity
    public interface OnAsyncRequestComplete {
        public void asyncResponse();
    }

    public JSONObject doInBackground(Void... params) {
        // get url pointing to entry point of API
        int numOfTimeoutSec = 30;
        try {
            encodedUrl = new URL(url);
            urlConnection = (HttpURLConnection) encodedUrl.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setConnectTimeout(numOfTimeoutSec * 1000);
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

        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return responseObj;
    }

    /**
     * check if there is internet connection
     * if no internet connection, inform user via alertdialog and stop async task
     * if there's internet connection, continue async task to retrieve data from server
     */
    public void onPreExecute() {
        if(!isNetworkEnabled())
        {
            caller.asyncResponse();
            cancel(true);               // cancel current async task
        }
        else {
            if(progressDialog == null) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage(mContext.getString(R.string.dialog_loading));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
            }
            if(!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    public void onProgressUpdate(Integer... progress) {
        // you can implement some progressBar and update it in this record
        // setProgressPercent(progress[0]);
    }

    /**
     * data retrieved from server, add them to SQLite database
     * @param result data from server; null if data retrieval is unsuccessful
     */
    public void onPostExecute(JSONObject result) {

        super.onPostExecute(result);
        if(result != null)
            addToDatabase(result);

        caller.asyncResponse();           // return SQL records back to fragment | see asyncResponse function in fragment

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //Get the server CSV records
    public void addToDatabase(JSONObject jsonObj) {
        try {
            serverSQLrecords = jsonObj.getJSONArray(TAG_RESULTS);
            ArrayList<Machine> myMachineList = new ArrayList<>();

            //remove all unwanted symbols and text
            String cleanupLatestRecords = serverSQLrecords.toString().replaceAll(",false]]", "").replace("[[", "").replace("[", "").replace("]]", "").replace("\"", "").replace("]", "");;

            //split different csv records, the ending of each csv record list is machineID.csv
            String[] allSQLRecords = cleanupLatestRecords.split("split,");
            String[] latestRecords;

            DatabaseHelper mydatabaseHelper = new DatabaseHelper(mContext);

            //loop through each csv and get the latest records and split each field
            for (String record : allSQLRecords) {
                latestRecords = record.split(",");
                if(latestRecords.length < 10)
                    break;
                //Change database
                //last 3rd is work hours!!! remember to add in KR
                Machine machine = new Machine(mContext, latestRecords[0],latestRecords[1],latestRecords[2],latestRecords[3],latestRecords[4],latestRecords[5],
                        latestRecords[6],latestRecords[7],latestRecords[8],latestRecords[9],"0");

                mydatabaseHelper.updateDatabase(machine);
            }

            // update datetime in shared pref
            SharedPreferences.Editor editor;
            SharedPreferences DateTimeSharedPreferences = mContext.getSharedPreferences("DT_PREFS_NAME", Context.MODE_PRIVATE);
            editor = DateTimeSharedPreferences.edit();
            editor.putString("DT_PREFS_KEY", DateFormat.getDateTimeInstance().format(new Date()));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkEnabled(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            //Network available
            return true;
        }
        else {
            return false;
        }
    }
}
