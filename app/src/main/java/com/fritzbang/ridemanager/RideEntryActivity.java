package com.fritzbang.ridemanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RideEntryActivity extends Activity {

	String a;
	int keyDel;
	final Context context = this;
	String selectedDate;
	DatePicker dp;
	EditText distanceText;
	EditText timeText;
	EditText maxText;
	EditText aveText;
	TextView dateView;
	CheckBox trainerCheckbox;
	long rowId;
	boolean isedit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		rowId = this.getIntent().getLongExtra("rowid", -1);
		isedit = this.getIntent().getBooleanExtra("edit", false);
		if (isedit) {
			setContentView(R.layout.edit_entry);
		} else
			setContentView(R.layout.new_entry);

		// TODO add all the hooks and logic for edit_entry
		// TODO add something to prevent addition of empty rides
		// TODO fix the empty rides

		distanceText = (EditText) findViewById(R.id.editTextDistance);
		timeText = (EditText) findViewById(R.id.editTextTime);
		timeText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				boolean flag = true;
				String eachBlock[] = timeText.getText().toString().split(":");
				for (int i = 0; i < eachBlock.length; i++) {
					if (eachBlock[i].length() > 2) {
						flag = false;
					}
				}
				if (flag) {
					timeText.setOnKeyListener(new OnKeyListener() {
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_DEL)
								keyDel = 1;
							return false;
						}
					});
					if (keyDel == 0) {
						if (((timeText.getText().length() + 1) % 3) == 0) {
							if (timeText.getText().toString().split(":").length <= 2) {
								timeText.setText(timeText.getText() + ":");
								timeText.setSelection(timeText.getText()
										.length());
							}
						}
						a = timeText.getText().toString();

					} else {
						a = timeText.getText().toString();
						keyDel = 0;
					}
				} else {
					timeText.setText(a);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});

		aveText = (EditText) findViewById(R.id.editTextAverage);
		maxText = (EditText) findViewById(R.id.editTextMaximum);

		dateView = (TextView) findViewById(R.id.textViewSelectedDate);

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
		Calendar cal = Calendar.getInstance();
		selectedDate = dateFormat.format(cal.getTime());
		dateView.setText(selectedDate);

		if (isedit) {
			DBAdapter db = new DBAdapter(this);
			db.open();
			Cursor cr = db.getRide(rowId);

			distanceText.setText(cr.getFloat(cr
					.getColumnIndex(DBAdapter.KEY_DIS)) + "");
			aveText.setText(cr.getFloat(cr.getColumnIndex(DBAdapter.KEY_AVE))
					+ "");
			maxText.setText(cr.getFloat(cr.getColumnIndex(DBAdapter.KEY_MAX))
					+ "");
			timeText.setText(cr.getString(cr.getColumnIndex(DBAdapter.KEY_TIME)));
			selectedDate = cr.getInt(cr.getColumnIndex(DBAdapter.KEY_YEAR))
					+ "/" + cr.getInt(cr.getColumnIndex(DBAdapter.KEY_MONTH))
					+ "/" + cr.getInt(cr.getColumnIndex(DBAdapter.KEY_DAY));
			dateView.setText(selectedDate);
			db.close();
		}

		Button dateButton = (Button) findViewById(R.id.buttonDate);
		dateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Creating a dialog to get the date
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.date_dialog);
				dialog.setTitle("Select Date...");

				dp = (DatePicker) dialog.findViewById(R.id.datePickerNewDate);
				Button selectDateButton = (Button) dialog
						.findViewById(R.id.buttonAddDate);
				// if button is clicked, close the custom dialog
				selectDateButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						selectedDate = dp.getYear() + "/" + (dp.getMonth() + 1)
								+ "/" + dp.getDayOfMonth();
						dateView.setText(selectedDate);
						dialog.dismiss();
					}
				});

				dialog.show();
			}
		});
		trainerCheckbox = (CheckBox) findViewById(R.id.checkBoxTrainer);

		Button back = (Button) findViewById(R.id.buttonBack);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Back",
						Toast.LENGTH_SHORT).show();
				Intent newentryintent = new Intent();
				newentryintent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.RideManagerActivity");
				startActivity(newentryintent);
			}
		});

		if (isedit) {
			Button deleteButton = (Button) findViewById(R.id.buttonDelete);
			deleteButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					DBAdapter db = new DBAdapter(context);
					db.open();
					db.deleteRide(rowId);
					db.close();
					Intent newentryintent = new Intent();
					newentryintent.setClassName("com.fritzbang.ridemanager",
							"com.fritzbang.ridemanager.ViewLogActivity");
					startActivity(newentryintent);
				}
			});
		}

		// TODO make sure values are not null when adding or updating ride
		Button save = (Button) findViewById(R.id.buttonSave);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String yr = dateView.getText().toString().split("/")[0];
				String mt = dateView.getText().toString().split("/")[1];
				String dy = dateView.getText().toString().split("/")[2];
				if (dp != null) {
					yr = dp.getYear() + "";
					mt = (dp.getMonth() + 1) + "";
					dy = dp.getDayOfMonth() + "";
				}
				String dt = distanceText.getText().toString();
				String tt = timeText.getText().toString();
				String xt = maxText.getText().toString();
				String at = aveText.getText().toString();
				String ck = trainerCheckbox.isChecked() + "";
				DBAdapter db = new DBAdapter(context);
				db.open();
				if (isedit)
					db.updateRide(rowId, mt, dy, yr, at, dt, xt, tt, ck);
				else
					db.insertRide(mt, dy, yr, at, dt, xt, tt, ck);
				db.close();
				Toast.makeText(getApplicationContext(), "saved",
						Toast.LENGTH_SHORT).show();
				Intent newentryintent = new Intent();
				newentryintent.setClassName("com.fritzbang.ridemanager",
						"com.fritzbang.ridemanager.RideManagerActivity");
				startActivity(newentryintent);
			}
		});

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
