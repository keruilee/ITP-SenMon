package edu.singaporetech.senmon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    public WebService(Context a, String m, List<String> p) {
        caller = (OnAsyncRequestComplete) a;
        this.mContext = a;
        method = m;
        parameters = p;
    }

    public WebService(Context a, String m) {
        caller = (OnAsyncRequestComplete) a;
        this.mContext = a;
        method = m;
    }


    public WebService(Context a, OnAsyncRequestComplete listener) {
        //caller = (OnAsyncRequestComplete) a;
        this.mContext = a;
        this.caller = listener;
    }

    // Interface to be implemented by calling activity
    public interface OnAsyncRequestComplete {
        public void asyncResponse(JSONObject response);
    }

    public JSONObject doInBackground(Void... params) {
        // get url pointing to entry point of API
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
        } finally {
            urlConnection.disconnect();
        }
        return responseObj;
    }

    public void onPreExecute() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading Records...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
        }
        if(!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void onProgressUpdate(Integer... progress) {
        // you can implement some progressBar and update it in this record
        // setProgressPercent(progress[0]);
    }

    public void onPostExecute(JSONObject result) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onPostExecute(result);
        getSQLRecords(result);
        //progressDialog.dismiss();             // dismissing dialogs in each fragments instead
        caller.asyncResponse(result);           // return SQL records back to fragment | see asyncResponse function in fragment
    }

    //Get the server CSV records
    public void getSQLRecords(JSONObject jsonObj) {
        try {
            serverSQLrecords = jsonObj.getJSONArray(TAG_RESULTS);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONArray getRecords(){
        return serverSQLrecords;
    }

}
