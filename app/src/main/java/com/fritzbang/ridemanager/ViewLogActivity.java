package com.fritzbang.ridemanager;

import java.util.Calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ViewLogActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_log);

		// TODO create Icons for each of the table views

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int index = 0;
		month = this.getIntent().getIntExtra("Month", month);
		year = this.getIntent().getIntExtra("Year", year);
		index = this.getIntent().getIntExtra("Tab", index);

		TabSpec dayTabSpec = tabHost.newTabSpec("tid1");
		TabSpec weekTabSpec = tabHost.newTabSpec("tid1");
		TabSpec monthTabSpec = tabHost.newTabSpec("tid1");
		TabSpec yearTabSpec = tabHost.newTabSpec("tid1");

		this.setTitle("Ride Manager " + year + "/" + month);

		Intent yearIntent = new Intent(this, YearTab.class);
		yearIntent.putExtra("Year", year);

		Intent monthIntent = new Intent(this, MonthTab.class);
		monthIntent.putExtra("Month", month);
		monthIntent.putExtra("Year", year);

		dayTabSpec.setIndicator("Day").setContent(
				new Intent(this, DayTab.class));
		weekTabSpec.setIndicator("Week").setContent(
				new Intent(this, WeekTab.class));
		monthTabSpec.setIndicator("Month").setContent(monthIntent);
		yearTabSpec.setIndicator("Year").setContent(yearIntent);

		tabHost.addTab(yearTabSpec);
		tabHost.addTab(monthTabSpec);
		tabHost.addTab(weekTabSpec);
		tabHost.addTab(dayTabSpec);

		tabHost.setCurrentTab(index);
	}
}
