package com.fritzbang.ridemanager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class HTTPAdapter {

	// TODO create the functions that handle the http posting interactions and
	// the database server calls
	// TODO create a more permanent database and put the URL in here
	// TODO add ssl or a more secure connection to the server and the server
	// connection
	private static String dbURL = "http://192.168.56.101/rideserver.php";

	public static String selectData() {

		String result = "";
		// the year data to send
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// nameValuePairs.add(new BasicNameValuePair("year","1980"));
		nameValuePairs.add(new BasicNameValuePair("year", "2003"));
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(dbURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();

			// Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		// TODO correct the json parsing to handle the ridemanager data.
		// parse json data
		try {
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				Log.i("log_tag",
						"id: " + json_data.getInt("id") + ", name: "
								+ json_data.getString("name") + ", sex: "
								+ json_data.getInt("sex") + ", birthyear: "
								+ json_data.getInt("birthyear"));
			}

		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		return result;
	}// end selectData

	public static String syncData(Context applicationContext) {
		String missing = "All values are up to date";

		// Parse cursor values and put them in valuePair to send to server
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("actionType", "sync"));
		DBAdapter db = new DBAdapter(applicationContext);
		db.open();
		Cursor allRides = db.getAllRides();
		while (allRides.moveToNext()) {
			String line = allRides.getString(0) + "," + allRides.getString(1)
					+ "," + allRides.getString(2) + "," + allRides.getString(3)
					+ "," + allRides.getString(4) + "," + allRides.getString(5)
					+ "," + allRides.getString(6) + "," + allRides.getString(7)
					+ "," + allRides.getString(8);
			nameValuePairs.add(new BasicNameValuePair("row", line));
		}
		// db.close();

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://192.168.56.101/rideserver.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();

			// Receive the missing values
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			missing = sb.toString();
			Toast.makeText(applicationContext, missing, Toast.LENGTH_LONG)
					.show();
			// TODO fix the empty query
			if (missing.equals("emptyquery")) {
				Toast.makeText(applicationContext, "Already up to date",
						Toast.LENGTH_LONG).show();
			} else {
				JSONArray jArray = parseJSONArray(applicationContext, missing);
				if (jArray == null) {
					Toast.makeText(applicationContext, "Already up to date",
							Toast.LENGTH_LONG).show();
				} else {
					for (int x = 0; x < jArray.length(); x++) {
						JSONObject jobj = jArray.getJSONObject(x);
						db.insertRide(jobj.getString("MONTH"),
								jobj.getString("DAY"), jobj.getString("YEAR"),
								jobj.getString("AVE"), jobj.getString("DIS"),
								jobj.getString("MAX"), jobj.getString("HOUR")
										+ ":" + jobj.getString("MIN") + ":"
										+ jobj.getString("SEC"),
								jobj.getString("TRAINER"));
					}
					db.close();
					Toast.makeText(applicationContext, "Done inserting",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		// TODO parse the "missing" result values

		// TODO insert the missing values into the local database
		// TODO verify the right data is being returned and it is not
		// duplicating results.

		return missing;
	}

	private static JSONArray parseJSONArray(Context applicationContext,
			String jsonString) {

		JSONArray jArray = null;
		try {
			jArray = new JSONArray(jsonString);

			// for (int x = 0; x < jArray.length(); x++) {
			// JSONObject jobj = jArray.getJSONObject(x);
			// db.insertRide(jobj.getString("MONTH"), jobj.getString("DAY"),
			// jobj.getString("YEAR"), jobj.getString("AVE"),
			// jobj.getString("DIS"), jobj.getString("MAX"),
			// jobj.getString("HOUR") + ":" + jobj.getString("MIN")
			// + ":" + jobj.getString("SEC"),
			// jobj.getString("TRAINER"));
			// }
			// db.close();
			// Toast.makeText(applicationContext, "Done inserting",
			// Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			Toast.makeText(applicationContext, "JSON parse failed",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (NullPointerException ex) {
			Toast.makeText(applicationContext, "String is Null",
					Toast.LENGTH_LONG).show();
		}
		return jArray;
	}
}
