package com.fritzbang.ridemanager;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ViewLog
 * 
 * @author mrhynard this contains the activities that allow the user to view
 *         each of the logs by year, month, week, or day. each day will be
 *         displayed on a seperate line. totals will be displayed in a line at
 *         the bottom of the page.
 */
public class MonthTab extends Activity {
	int month;
	int year;
	String tab = "month";
	String entryValue = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_view);

		// TODO add a bar at the top that tells what month the user is looking
		// at.(Running Log(2012-01) Total Data)

		// TODO create a menu bar that allows the selection of the specific
		// month,year,etc... or scrolls to previous or next

		// TODO fix the spacing on the list view

		ListView listView = (ListView) findViewById(R.id.loglist);// ListView
																	// listView
																	// = new
																	// ListView(this);

		Cursor rides = null;
		DBAdapter db = new DBAdapter(this);
		db.open();

		Calendar cal = Calendar.getInstance();
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);

		month = this.getIntent().getIntExtra("Month", month);
		year = this.getIntent().getIntExtra("Year", year);

		String condition = DBAdapter.KEY_MONTH + " = " + (month + 1) + " and "
				+ DBAdapter.KEY_YEAR + " = " + year;
		rides = db.getAllRides(condition);

		startManagingCursor(rides);
		String[] columns = new String[] { DBAdapter.KEY_MONTH,
				DBAdapter.KEY_DAY, DBAdapter.KEY_YEAR, DBAdapter.KEY_DIS,
				DBAdapter.KEY_TIME, DBAdapter.KEY_AVE, DBAdapter.KEY_MAX };
		int[] to = new int[] { R.id.month_entry, R.id.day_entry,
				R.id.year_entry, R.id.dis_entry, R.id.time_entry,
				R.id.ave_entry, R.id.max_entry };
		ListAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.list_entry, rides, columns, to);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						"CLick ListItemNumber " + position + " " + id,
						Toast.LENGTH_LONG).show();
				Intent changeActivity = new Intent();
				changeActivity.putExtra("rowid", id);
				changeActivity.putExtra("edit", true);
				// changeActivity.putExtra("entryValue", entryValue);
				changeActivity.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.RideEntryActivity");
				startActivity(changeActivity);

			}

		});

		TextView disTotalView = (TextView) findViewById(R.id.distance_total);
		float totalDistance = db.getTotalValue(DBAdapter.KEY_DIS, condition);
		disTotalView.setText(totalDistance + "");

		TextView timeTotalView = (TextView) findViewById(R.id.time_total);
		float totalTime = db.getTotalTime(condition);
		timeTotalView.setText(formatTime(totalTime));

		db.close();

		ImageButton previous = (ImageButton) findViewById(R.id.previousButton);
		previous.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Previous",
						Toast.LENGTH_SHORT).show();
				Intent prevMonthIntent = new Intent();
				// TODO fix the previous month so it will check for year
				// rollover
				prevMonthIntent.putExtra("Month", month - 1);
				prevMonthIntent.putExtra("Year", year);
				prevMonthIntent.putExtra("Tab", 1);
				prevMonthIntent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.ViewLogActivity");
				startActivity(prevMonthIntent);
			}
		});
		Button selectButton = (Button) findViewById(R.id.selectButton);
		selectButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "select",
						Toast.LENGTH_SHORT).show();
				// Intent newentryintent = new Intent();
				// newentryintent.setClassName("com.fritzbang.ridemanager","com.fritzbang.ridemanager.RideManagerActivity");
				// startActivity(newentryintent);
				// prevMonthIntent.putExtra("Tab",tab);
			}
		});

		Button nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "next",
						Toast.LENGTH_SHORT).show();
				Intent nextMonthIntent = new Intent();
				// TODO fix the previous month so it will check for year
				// rollover
				nextMonthIntent.putExtra("Month", month + 1);
				nextMonthIntent.putExtra("Year", year);
				nextMonthIntent.putExtra("Tab", 1);
				nextMonthIntent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.ViewLogActivity");
				startActivity(nextMonthIntent);
			}
		});

		// Toast.makeText(getApplicationContext(), "Month:  " + month + ":"+year
		// + " " + numrides, Toast.LENGTH_SHORT).show();
	}

	String formatTime(float inTime) {
		int hrs = (int) inTime;
		float mns_float = (inTime - hrs) * 60;
		int mns = (int) mns_float;
		float sec = (mns_float - mns) * 60;
		String output = hrs + ":" + mns + ":" + Math.round(sec);

		return output;
	}
}
