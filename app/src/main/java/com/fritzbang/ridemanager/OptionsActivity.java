package com.fritzbang.ridemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class OptionsActivity extends Activity {

	String url = "192.168.56.101";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.options_activity);

		// TODO decide which options to add

		// TODO pull the URL for the database from the URL box and use it to
		// connect to the database.

		// TODO add the functionality to compare the local database with the
		// remote database.
		// TODO Sending the database information to the server
		// TODO Have the server decide which things are in the the database
		// TODO Receive the data that is not in the database and insert it into
		// the local database.

		// TODO add db sync
		Button dbSyncButton = (Button) findViewById(R.id.dbSyncButton);
		dbSyncButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Syncing with DB",
						Toast.LENGTH_SHORT).show();
				String result = HTTPAdapter.syncData(getApplicationContext());
				// Toast.makeText(getApplicationContext(), result,
				// Toast.LENGTH_LONG).show();
			}
		});

		Button csvExportButton = (Button) findViewById(R.id.csvButton);
		csvExportButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Exporting to CSV",
						Toast.LENGTH_SHORT).show();
				String filename = CSVHandler.createCSV(getApplicationContext());
				Toast.makeText(getApplicationContext(),
						"CSV Created: " + filename, Toast.LENGTH_LONG).show();

			}
		});
		// TODO add email export - creates a csv file and calls an email
		// application to send the attachment.

		Button emailButton = (Button) findViewById(R.id.emailButton);
		emailButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),
						"Exporting to CSV and emailing", Toast.LENGTH_SHORT)
						.show();
				String filename = CSVHandler.createCSV(getApplicationContext());
				Toast.makeText(getApplicationContext(),
						"CSV Created: " + filename, Toast.LENGTH_LONG).show();
				// TODO attach file to email and send
			}
		});

		// TODO fix all text items in xml files

	}

}
