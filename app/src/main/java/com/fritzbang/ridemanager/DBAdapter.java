package com.fritzbang.ridemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_MONTH = "month";
	public static final String KEY_DAY = "day";
	public static final String KEY_YEAR = "year";
	public static final String KEY_AVE = "ave";
	public static final String KEY_DIS = "dis";
	public static final String KEY_MAX = "max";
	public static final String KEY_TIME = "time";
	public static final String KEY_GOAL = "goal";
	public static final String KEY_TRAINER = "trainer";

	public static final String KEY_DATE_TIME = "date_time";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_ALTITUDE = "altitude";
	public static final String KEY_GPSTIME = "gpstime";
	public static final String KEY_ACCURACY = "accuracy";

	public static final String TAG = "DBAdapter";
	public static final String DATABASE_NAME = "rides";

	// table names
	public static final String DATABASE_TABLE_RIDE = "ride";
	public static final String DATABASE_TABLE_GOAL = "goal";
	public static final String DATABASE_TABLE_TRACK = "track";
	public static final String DATABASE_TABLE_RIDEID = "rideid";
	public static final String DATABASE_TABLE_TEMP_TRACK = "temp_track";

	public static final int DATABASE_VERSION = 1;

	// Table creation statements
	public static final String DATABASE_CREATE_RIDE = "create table ride (_id integer primary key autoincrement, month int not null,day int not null,year int not null, ave real not null, dis real not null, max real not null, time real not null,trainer boolean not null);";
	public static final String DATABASE_CREATE_GOAL = "create table goal (_id integer primary key autoincrement, year int not null unique,goal real not null);";
	public static final String DATABASE_CREATE_TRACK = "create table track (rideid int not null, gpstime int not null,latitude real not null,longitude real not null,altitude real not null,accuracy real not null);";
	public static final String DATABASE_CREATE_TEMP_TRACK = "create table temp_track (date_time string not null, gpstime int not null,latitude real not null,longitude real not null,altitude real not null,accuracy real not null);";

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	// TODO: Change statements so I don't get the warnings and i make sure that
	// everything is entered
	// TODO: When a ride is entered into the database make sure none of the
	// values are null or empty strings

	// TODO create a table in the database for the gps points connected.
	// TODO add the helper functions for insertion of gps points into the points
	// table

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_RIDE);
			db.execSQL(DATABASE_CREATE_GOAL);
			db.execSQL(DATABASE_CREATE_TRACK);
			db.execSQL(DATABASE_CREATE_TEMP_TRACK);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + " ,Which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS ride");
			db.execSQL("DROP TABLE IF EXISTS goal");
			db.execSQL("DROP TABLE IF EXISTS track");
			db.execSQL("DROP TABLE IF EXISTS temp_track");
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public boolean isOpen() {
		return db.isOpen();
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert a ride into the database---
	public long insertRide(String month, String day, String year, String ave,
			String dis, String max, String time, String trainer) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MONTH, month);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_YEAR, year);
		initialValues.put(KEY_AVE, ave);
		initialValues.put(KEY_DIS, dis);
		initialValues.put(KEY_MAX, max);
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_TRAINER, trainer);

		return db.insert(DATABASE_TABLE_RIDE, null, initialValues);
	}

	// ---deletes a particular ride---
	public boolean deleteRide(long rowId) {
		return db.delete(DATABASE_TABLE_RIDE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// ---deletes a particular ride---
	public boolean deleteRide(String condition) {
		return db.delete(DATABASE_TABLE_RIDE, condition, null) > 0;
	}

	// ---retrieves all the rides---
	public Cursor getAllRides() {
		return db.query(DATABASE_TABLE_RIDE, new String[] { KEY_ROWID,
				KEY_MONTH, KEY_DAY, KEY_YEAR, KEY_AVE, KEY_DIS, KEY_MAX,
				KEY_TIME, KEY_TRAINER }, null, null, null, null, KEY_MONTH
				+ "," + KEY_DAY);
	}

	// ---retrieves all the rides that meet the condition---
	public Cursor getAllRides(String condition) {
		return db.query(DATABASE_TABLE_RIDE, new String[] { KEY_ROWID,
				KEY_MONTH, KEY_DAY, KEY_YEAR, KEY_AVE, KEY_DIS, KEY_MAX,
				KEY_TIME, KEY_TRAINER }, condition, null, null, null, KEY_MONTH
				+ "," + KEY_DAY);
	}

	// ---retrieves a particular ride---
	public Cursor getRide(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE_RIDE, new String[] {
				KEY_ROWID, KEY_MONTH, KEY_DAY, KEY_YEAR, KEY_AVE, KEY_DIS,
				KEY_MAX, KEY_TIME, KEY_TRAINER }, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---retrieves a particular ride---
	public Cursor getRide(String month, String day, String year)
			throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE_RIDE, new String[] {
				KEY_ROWID, KEY_MONTH, KEY_DAY, KEY_YEAR, KEY_AVE, KEY_DIS,
				KEY_MAX, KEY_TIME, KEY_TRAINER }, KEY_MONTH + "=" + month
				+ " and " + KEY_DAY + "=" + day + " and " + KEY_YEAR + "="
				+ year, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a ride---
	public boolean updateRide(long rowId, String month, String day,
			String year, String ave, String dis, String max, String time,
			String trainer) {
		ContentValues args = new ContentValues();
		args.put(KEY_MONTH, month);
		args.put(KEY_DAY, day);
		args.put(KEY_YEAR, year);
		args.put(KEY_AVE, ave);
		args.put(KEY_DIS, dis);
		args.put(KEY_MAX, max);
		args.put(KEY_TIME, time);
		args.put(KEY_TRAINER, trainer);
		return db.update(DATABASE_TABLE_RIDE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public float getTotalValue(String column) throws SQLException {
		float value = 0;
		Cursor disCursor = db.rawQuery("select sum(" + column + ") from ride",
				null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			value = disCursor.getFloat(0);
		}
		return value;
	}

	public float getTotalValue(String column, String condition)
			throws SQLException {
		float value = 0;
		Cursor disCursor = db.rawQuery("select sum(" + column
				+ ") from ride where " + condition, null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			value = disCursor.getFloat(0);
		}
		return value;
	}

	public float getTotalTime(String condition) throws SQLException {
		float value = 0;
		Cursor c = db.rawQuery("select " + KEY_TIME + " from ride where "
				+ condition, null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			String tmp1 = c.getString(0);
			String[] tmp2 = tmp1.split(":");
			value += Float.parseFloat(tmp2[0]) + Float.parseFloat(tmp2[1]) / 60
					+ Float.parseFloat(tmp2[2]) / 3600;

			while (c.moveToNext()) {
				tmp1 = c.getString(0);
				tmp2 = tmp1.split(":");
				value += Float.parseFloat(tmp2[0]) + Float.parseFloat(tmp2[1])
						/ 60 + Float.parseFloat(tmp2[2]) / 3600;
			}
		}
		return value;
	}

	public float getMax(String column) throws SQLException {
		float value = 0;
		Cursor disCursor = db.rawQuery("select max(" + column + ") from ride",
				null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			value = disCursor.getFloat(0);
		}
		return value;
	}

	public float getMax(String column, String condition) throws SQLException {
		float value = 0;
		Cursor disCursor = db.rawQuery("select max(" + column
				+ ") from ride where " + condition, null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			value = disCursor.getFloat(0);
		}
		return value;
	}

	public float getMaxMonth() throws SQLException {
		float value = 0;
		Cursor disCursor = db.rawQuery(
				"select sum(dis) from ride group by month", null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			while (disCursor.isAfterLast() == false) {

				if (disCursor.getFloat(0) > value)
					value = disCursor.getFloat(0);
				disCursor.moveToNext();
			}
		}
		return value;
	}

	public int getCount() throws SQLException {
		int value = 0;
		Cursor disCursor = db.rawQuery("select count(*) from ride", null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			value = disCursor.getInt(0);
		}
		return value;
	}

	public int getCount(String condition) throws SQLException {
		int value = 0;
		Cursor disCursor = db.rawQuery("select count(*) from ride where "
				+ condition, null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			value = disCursor.getInt(0);
		}
		return value;
	}

	public Cursor getUnique(String column) throws SQLException {
		Cursor retcursor = db.query(true, DATABASE_TABLE_RIDE, new String[] {
				KEY_ROWID, column }, null, null, column, null, null, null);
		return retcursor;
	}

	public Cursor getUnique(String column, String condition)
			throws SQLException {
		Cursor retcursor = db.query(true, DATABASE_TABLE_RIDE, new String[] {
				KEY_ROWID, column }, condition, null, column, null, null, null);
		return retcursor;
	}

	public Cursor getAllGoals() {
		return db.query(DATABASE_TABLE_GOAL, new String[] { KEY_ROWID,
				KEY_YEAR, KEY_GOAL }, null, null, null, null, null);
	}

	public float getGoal(int year) throws SQLException {
		float value = 0;
		Cursor mCursor = db.query(true, DATABASE_TABLE_GOAL, new String[] {
				KEY_ROWID, KEY_YEAR, KEY_GOAL }, KEY_YEAR + "=" + year, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			value = mCursor.getFloat(2);
		}
		return value;
	}

	public long insertGoal(String year, String goal) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_YEAR, year);
		initialValues.put(KEY_GOAL, goal);

		return db.insert(DATABASE_TABLE_GOAL, null, initialValues);
	}

	public boolean updateGoal(long rowId, String year, String goal) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_YEAR, year);
		initialValues.put(KEY_GOAL, goal);
		return db.update(DATABASE_TABLE_GOAL, initialValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean rideExists(String month, String day, String year) {
		Cursor mCursor = db.query(true, DATABASE_TABLE_RIDE,
				new String[] { KEY_ROWID },
				KEY_MONTH + "=" + month + " and " + KEY_DAY + "=" + day
						+ " and " + KEY_YEAR + "=" + year, null, null, null,
				null, null);
		if (mCursor.getCount() > 0) {
			return true;
		}
		return false;

	}

	// removes any entry that has an empty field
	public void cleanUp() {
		String condition = DBAdapter.KEY_DAY + " = '' or "
				+ DBAdapter.KEY_MONTH + " = '' or " + DBAdapter.KEY_YEAR
				+ " = '' or " + DBAdapter.KEY_AVE + " = '' or "
				+ DBAdapter.KEY_DIS + " = '' or " + DBAdapter.KEY_MAX
				+ " = '' or " + DBAdapter.KEY_TIME + " = ''";
		deleteRide(condition);
	}

	// These functions are for the track db

	public long insertTrackPoint(String dateTime, long gpsTime,
			double latitude, double longitude, double altitude, float accuracy) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATE_TIME, dateTime);
		initialValues.put(KEY_GPSTIME, gpsTime);
		initialValues.put(KEY_LATITUDE, latitude);
		initialValues.put(KEY_LONGITUDE, longitude);
		initialValues.put(KEY_ALTITUDE, altitude);
		initialValues.put(KEY_ACCURACY, accuracy);

		return db.insert(DATABASE_TABLE_TEMP_TRACK, null, initialValues);

	}

	public boolean clearTempTrack() {
		return db.delete(DATABASE_TABLE_TEMP_TRACK, null, null) > 0;
	}

	public Cursor getTempTrack() {
		return db.query(DATABASE_TABLE_TEMP_TRACK, new String[] {
				KEY_DATE_TIME, KEY_GPSTIME, KEY_LATITUDE, KEY_LONGITUDE,
				KEY_ALTITUDE, KEY_ACCURACY }, null, null, null, null, null);
	}

	public int getNumberOfPointsInTempTrack() {
		int value = 0;
		Cursor disCursor = db.rawQuery("select count(*) from "
				+ DATABASE_TABLE_TEMP_TRACK, null);
		if (disCursor != null) {
			disCursor.moveToFirst();
			value = disCursor.getInt(0);
		}
		return value;
	}

	public void toJSON(Cursor mCursor) {
		// TODO add funcitonality to convert the retrieved query to JSON to send
		// to remote server.

	}

}
