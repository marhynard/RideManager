package com.fritzbang.ridemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RideManagerActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_manager);

		// TODO create the button Icons for RideManager main page figure out how
		// to design and make it look good.

		// TODO fix the layout so it will work for both tablets and for phones

		// TODO add statistics and goal tracking
		// TODO clean up code and add comments

		// TODO add a method to export data through file or through email
		// TODO fix the strings on the buttons
		// TODO add charts and graphs
		// TODO enlarge font on ride list

		// TODO add the ability to sync with an outside database
		// TODO create an import function

		// TODO add the ability to estimate values

		// TODO create a menu for easier navigation through the app

		// TODO create a reloadable settings file or something

		// DBAdapter db = new DBAdapter(this);
		// db.open();
		// db.cleanUp();
		// db.close();

		// Displays the page for adding a new entry
		Button newRideEntry = (Button) findViewById(R.id.buttonNewEntry);
		newRideEntry.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "New Entry",
						Toast.LENGTH_SHORT).show();
				Intent newentryintent = new Intent();
				newentryintent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.RideEntryActivity");
				startActivity(newentryintent);

			}
		});

		Button buttonCharts = (Button) findViewById(R.id.buttonCharts);
		buttonCharts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Charts",
						Toast.LENGTH_SHORT).show();
				Intent chartIntent = new Intent();
				chartIntent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.ChartActivity");
				startActivity(chartIntent);
			}
		});
		Button buttonLogs = (Button) findViewById(R.id.buttonViewLog);
		buttonLogs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Logs",
						Toast.LENGTH_SHORT).show();
				Intent newentryintent = new Intent();
				newentryintent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.ViewLogActivity");
				startActivity(newentryintent);
			}
		});

		Button buttonGoals = (Button) findViewById(R.id.buttonGoals);
		buttonGoals.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Goals",
						Toast.LENGTH_SHORT).show();
				Intent goalIntent = new Intent();
				goalIntent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.GoalActivity");
				startActivity(goalIntent);
			}
		});

		Button buttonTrackRide = (Button) findViewById(R.id.buttonTrackRide);
		buttonTrackRide.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Track Ride",
						Toast.LENGTH_SHORT).show();
				Intent trackRideIntent = new Intent();
				trackRideIntent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.TrackRideActivity");
				startActivity(trackRideIntent);
			}
		});
		// TODO add options activity
		Button buttonOptions = (Button) findViewById(R.id.buttonOptions);
		buttonOptions.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Options",
						Toast.LENGTH_SHORT).show();
				Intent optionsIntent = new Intent();
				optionsIntent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.OptionsActivity");
				startActivity(optionsIntent);
			}
		});

		// Exits the program
		Button buttonExit = (Button) findViewById(R.id.buttonExit);
		buttonExit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_ride_manager, menu);
		return true;
	}

}
