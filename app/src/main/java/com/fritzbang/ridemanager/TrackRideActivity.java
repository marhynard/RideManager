package com.fritzbang.ridemanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrackRideActivity extends Activity {

	Button startStopButton;
	Button resetButton;
	DBAdapter db;
	double totalDistance = 0;
	double previousLat = -999;
	double previousLon = -999;
	double previousAlt = -1;
	protected long previousTime = -1;
	double currentLat = -999;
	double currentLon = -999;
	double currentAlt = -1;
	float currentAcu;

	long totalTime = 0;

	private boolean running = false;

	private LocationManager locationManager = null;
	private LocationListener listener = null;
	protected float currentSpeed;
	protected float currentPace;
	protected long currentTime;
	private TextView curSpeed;
	private TextView timeLabel;
	TextView curLatitude;
	TextView curLongitude;
	TextView curHeight;
	TextView curDis;
	TextView curPace;
	TextView avePace;
	TextView aveSpeed;
	TextView curAccuracy;
	private Context curContext;
	String date_key = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.track_ride);
		curContext = getApplicationContext();
		startStopButton = (Button) findViewById(R.id.buttonStartStop);
		resetButton = (Button) findViewById(R.id.buttonReset);
		timeLabel = (TextView) findViewById(R.id.textViewCurTime);
		curLatitude = (TextView) findViewById(R.id.textViewCurLatitude);
		curLongitude = (TextView) findViewById(R.id.textViewCurLongitude);
		curHeight = (TextView) findViewById(R.id.textViewCurHeight);
		curDis = (TextView) findViewById(R.id.textViewCurDistance);
		curSpeed = (TextView) findViewById(R.id.textViewCurSpeed);
		curPace = (TextView) findViewById(R.id.textViewCurPace);
		avePace = (TextView) findViewById(R.id.textViewAvePace);
		aveSpeed = (TextView) findViewById(R.id.textViewCurAveSpeed);
		curAccuracy = (TextView) findViewById(R.id.textViewCurAccuracy);
		startStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (listener == null)
					listener = new MyLocationListener();
				if (!running) {
					if (locationManager == null) {
						locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						if (!locationManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							showSettingsAlert();
						} else {

							startStopButton.setText("Pause");
							running = true;

							locationManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER, 0, 0,
									listener);
						}

					} else {
						if (!locationManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							showSettingsAlert();
						} else {
							startStopButton.setText("Pause");
							running = true;
							locationManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER, 0, 0,
									listener);
						}

					}

				} else {
					// TODO fix the start Pause button to update correctly with
					// the alert
					showSaveAlert();
					startStopButton.setText("Start");
					locationManager.removeUpdates(listener);
					running = false;
					clearPrevious();
				}
			}
		});

		resetButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				clearPrevious();
				clearTotals();
			}
		});

		// TODO set the start text to be the same as the Reset text

		// TODO change time to system time
		// TODO make time a continuous timer

		// TODO add a ride tracker function to go along with the GPS feature

		// TODO sync internal information with and external DB

		// TODO add mapping capabilities investigate google maps api.

		// TODO verify restore state is correct
		// TODO test the activity for accuracy

		// TODO add a popup when pressing pause
		// TODO add options to pop up to save discard or resume the tracking
		// TODO add funcitonality to save discard or resume the track.

		// TODO if saving a track add to permanent track table then remove from
		// temp track table

		// TODO create permanent and temp track table
		// TODO create sync for premanent tracks in web db

		// TODO clear temp track table at beginning of tracking
		// TODO add the datetime key to the database
		// TODO put stats on a scroll out bar
	}

	protected void clearTotals() {
		totalTime = 0;
		totalDistance = 0;
		curDis.setText(String.format("%4.2f", totalDistance));
		timeLabel.setText(totalTime + "");
		avePace.setText("0.0 Min/Mile");
		aveSpeed.setText("0.0 MpH");

	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			if (location != null) {
				db = new DBAdapter(curContext);
				db.open();
				currentLat = location.getLatitude();
				currentLon = location.getLongitude();
				currentAlt = location.getAltitude();
				currentAcu = location.getAccuracy();
				currentSpeed = location.getSpeed() * 2.23694f;
				currentTime = location.getTime();
				curLatitude.setText(ddToDMS((float) currentLat));
				curLongitude.setText(ddToDMS((float) currentLon));
				curHeight.setText((float) currentAlt + "");
				totalDistance += calculateDistance(previousLat, previousLon,
						previousAlt, currentLat, currentLon, currentAlt);
				totalTime += calculateTime();
				curDis.setText(String.format("%4.2f M", totalDistance));
				curSpeed.setText(String.format("%4.2f MpH", currentSpeed));
				currentPace = 60 / currentSpeed;
				curPace.setText(String.format("%4.2f Min/Mile", currentPace));
				timeLabel.setText(formatTime(totalTime));
				double averagePace = ((double) totalTime / 60000)
						/ totalDistance;
				avePace.setText(String.format("%4.2f Min/Mile", averagePace));
				double averageSpeed = totalDistance
						/ ((double) totalTime / 3600000);
				aveSpeed.setText(String.format("%4.2f MpH", averageSpeed));
				curAccuracy.setText(String.format("%4.2f ft",
						location.getAccuracy() * 3.28084));
				previousLat = currentLat;
				previousLon = currentLon;
				previousAlt = currentAlt;
				previousTime = currentTime;

				if (date_key.equals("")) {
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyyMMddHHmmss", Locale.US);
					Calendar cal = Calendar.getInstance();
					date_key = dateFormat.format(cal.getTime());
				}
				db.insertTrackPoint(date_key, currentTime, currentLat,
						currentLon, currentAlt, currentAcu);
				db.close();
			}
		}

		public void onProviderDisabled(String arg0) {
		}

		public void onProviderEnabled(String arg0) {
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}

	}

	public long calculateTime() {
		long time = 0;
		if (previousTime < 0)
			return 0;
		time = currentTime - previousTime;
		return time;
	}

	public String formatTime(long time) {
		long hours = time / (60 * 60 * 1000);
		long minutes = (time - (hours * (60 * 60 * 1000))) / (60 * 1000);
		long seconds = (time - ((hours * (60 * 60 * 1000)) + (minutes * (60 * 1000)))) / 1000;

		return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
				seconds);
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("GPS is settings");
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings Menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				});
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	public void showSaveAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Finished?");
		alertDialog.setMessage("Is this the end? Save,Resume, or Discard?");

		alertDialog.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClassName("com.fritzbang.ridemanager",
								"com.fritzbang.ridemanager.SaveTrackActivity");
						intent.putExtra("dataTime", date_key);
						startActivity(intent);
					}
				});
		alertDialog.setNeutralButton("Resume",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.setNegativeButton("Discard",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						discardTrack();

					}
				});

		alertDialog.show();
	}

	protected void discardTrack() {

		if (db == null) {
			db = new DBAdapter(curContext);
		}
		if (!db.isOpen()) {
			db.open();
		}
		db.clearTempTrack();
		clearTotals();
	}

	public double calculateDistance(double lat1, double lon1, double alt1,
			double lat2, double lon2, double alt2) {
		// double distance = 0;

		if (lat1 < -360 && lon1 < -360)
			return 0;

		// instantiate the calculator
		GeodeticCalculator geoCalc = new GeodeticCalculator();
		//
		// select a reference elllipsoid
		Ellipsoid reference = Ellipsoid.WGS84;

		GlobalPosition position1;
		position1 = new GlobalPosition(lat1, lon1, alt1);
		GlobalPosition position2;
		position2 = new GlobalPosition(lat2, lon2, alt2);

		// calculate the geodetic measurement
		GeodeticMeasurement geoMeasurement;
		double p2pKilometers;
		double p2pMiles;
		double elevChangeMeters;
		double elevChangeFeet;

		geoMeasurement = geoCalc.calculateGeodeticMeasurement(reference,
				position1, position2);
		p2pKilometers = geoMeasurement.getPointToPointDistance() / 1000.0;
		p2pMiles = p2pKilometers * 0.621371192;
		elevChangeMeters = geoMeasurement.getElevationChange();
		elevChangeFeet = elevChangeMeters * 3.2808399;

		return p2pMiles;

	}

	public String ddToDMS(float value) {
		String returnValue = "";
		int deg = (int) value;
		float min_d = (Math.abs(value - deg) * 60);
		int min = (int) min_d;
		float sec = (Math.abs(min_d - min) * 60);
		returnValue = String.format(Locale.US, "%d:%2d:%4.2f", deg, min, sec);
		return returnValue;
	}

	public void clearPrevious() {
		previousLat = -999;
		previousLon = -999;
		previousAlt = -1;
		previousTime = -1;
	}

	protected void onSaveInstanceState(Bundle savedState) {

		savedState.putLong("totalTime", totalTime);
		savedState.putDouble("totalDistance", totalDistance);
		savedState.putBoolean("running", running);
		savedState.putDouble("prevLat", previousLat);
		savedState.putDouble("prevLon", previousLon);
		savedState.putDouble("prevAlt", previousAlt);
		savedState.putLong("prevTime", previousTime);
		savedState.putString("date_key", date_key);
		super.onSaveInstanceState(savedState);
	}

	protected void onRestoreInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		totalTime = savedState.getLong("totalTime", totalTime);
		totalDistance = savedState.getDouble("totalDistance", totalDistance);
		running = savedState.getBoolean("running", running);
		previousLat = savedState.getDouble("prevLat", previousLat);
		previousLon = savedState.getDouble("prevLon", previousLon);
		previousAlt = savedState.getDouble("prevAlt", previousAlt);
		previousTime = savedState.getLong("prevTime", previousTime);
		date_key = savedState.getString("date_key");

		timeLabel.setText(formatTime(totalTime));
		curDis.setText(String.format("%4.2f", totalDistance));

		double averagePace = ((double) totalTime / 60000) / totalDistance;
		avePace.setText(String.format("%4.2f", averagePace));
		double averageSpeed = totalDistance / ((double) totalTime / 3600000);
		aveSpeed.setText(String.format("%4.2f", averageSpeed));

		if (running) {
			startStopButton.setText("Stop");
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			listener = new MyLocationListener();
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, listener);
		}

	}

}
