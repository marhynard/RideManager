package com.fritzbang.ridemanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class SaveTrackActivity extends Activity {
	String dateTime = "";

	Button saveButton;
	TextView timeLabel;
	DBAdapter db;

	// TODO create functions to save the ride information in the more permanent
	// track table
	// TODO create layout for SaveRideActivity
	// TODO get the date_time from previous activity in order to store the data

	// TODO calculate the time,distance,max, and Ave speeds to store in the ride
	// table

	// TODO insert ride information into the ride table

	// TODO add a save button

	// TODO return to main menu after save
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.save_ride);
		dateTime = this.getIntent().getStringExtra("dateTime");

		saveButton = (Button) findViewById(R.id.buttonSave);
		timeLabel = (TextView) findViewById(R.id.textViewSaveTime);

		db = new DBAdapter(this);
		db.open();

		Cursor tmpTrack = db.getTempTrack();
		// TODO scroll through cursor calculate max ave dis time
		if (tmpTrack != null) {
			boolean isEnd = tmpTrack.moveToFirst();
			while (isEnd) {
				double lat = tmpTrack.getDouble(2);
				double lon = tmpTrack.getDouble(3);
			}

		}
	}

}
