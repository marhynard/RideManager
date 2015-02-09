package com.fritzbang.ridemanager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

public class CSVHandler {

	public static String createCSV(Context applicationContext) {
		// TODO have CSVHandler dump all tables from the database including the
		// tracks.
		String output = "";
		DBAdapter db = new DBAdapter(applicationContext);
		db.open();
		Cursor allRides = db.getAllRides();

		Toast.makeText(applicationContext, "parsing db", Toast.LENGTH_SHORT)
				.show();
		while (allRides.moveToNext()) {
			String line = allRides.getString(0) + "," + allRides.getString(1)
					+ "," + allRides.getString(2) + "," + allRides.getString(3)
					+ "," + allRides.getString(4) + "," + allRides.getString(5)
					+ "," + allRides.getString(6) + "," + allRides.getString(7)
					+ "," + allRides.getString(8) + "\n";
			output += line;
		}
		db.close();
		// YYYYMMDD-HHMMSS-RideBackup.csv
		String filename = "";

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss",
				Locale.US);
		Calendar cal = Calendar.getInstance();
		String selectedDate = dateFormat.format(cal.getTime());
		filename = selectedDate + "-RideBackup.csv";

		Toast.makeText(applicationContext, filename, Toast.LENGTH_SHORT).show();
		try {

			FileOutputStream fo = applicationContext.openFileOutput(filename,
					Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fo);

			// Write the string to the file
			osw.write(output);

			/*
			 * ensure that everything is really written out and close
			 */
			osw.flush();
			osw.close();

		} catch (IOException e) {
			Toast.makeText(applicationContext, "error with csv",
					Toast.LENGTH_SHORT).show();
		}
		return filename;
	}

}
