//package edu.singaporetech.senmon;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//
///**
// * Created by Kerui on 27/7/2016.
// */
//public class WebService {
//    private Context mContext = null;
//    private static final String TAG_RESULTS = "result";
//    JSONArray serverSQLrecords = null;
//
//    public WebService(Context context){
//        mContext = context;
//    }
//
//
//    public void getSQLData() {
//        class GetSQLDataJSON extends AsyncTask<Void, Void, JSONObject> {
//
//            URL encodedUrl;
//            HttpURLConnection urlConnection = null;
//
//            String url = "http://itpsenmon.net23.net/readFromSQL.php";
//
//            JSONObject responseObj;
//            ProgressDialog progressDialog = new ProgressDialog(mContext);
//
//            @Override
//            protected void onPreExecute() {
//                progressDialog.setMessage("Loading Records...");
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.setIndeterminate(false);
//                progressDialog.show();
//            }
//
//            @Override
//            protected JSONObject doInBackground(Void... params) {
//                try {
//                    encodedUrl = new URL(url);
//                    urlConnection = (HttpURLConnection) encodedUrl.openConnection();
//                    urlConnection.setDoInput(true);
//                    urlConnection.setDoOutput(true);
//                    urlConnection.setUseCaches(false);
//                    urlConnection.setRequestProperty("Content-Type", "application/json");
//                    urlConnection.connect();
//
//                    InputStream input = urlConnection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                    }
//                    Log.d("doInBackground(Resp)", result.toString());
//                    responseObj = new JSONObject(result.toString());
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } finally {
//                    urlConnection.disconnect();
//                }
//                return responseObj;
//            }
//
//            @Override
//            protected void onPostExecute(JSONObject result) {
//                super.onPostExecute(result);
//                getSQLRecords(result);
//                getRecords();
//                progressDialog.dismiss();
//
//            }
//        }
//        GetSQLDataJSON g = new GetSQLDataJSON();
//        g.execute();
//    }
//
//    //Get the server CSV records
//    public void getSQLRecords(JSONObject jsonObj) {
//        try {
//            serverSQLrecords = jsonObj.getJSONArray(TAG_RESULTS);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public JSONArray getRecords(){
//        return serverSQLrecords;
//    }
//
//}
