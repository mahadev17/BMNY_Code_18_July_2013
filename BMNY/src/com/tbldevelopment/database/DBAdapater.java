package com.tbldevelopment.database;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DBAdapater {
	public static final String KEY_ROWID = "id";
	public static final String KEY_NAME = "username";
	public static final String KEY_EMAIL = "useremail";
	public static final String USERID = "userid";
	public static final String FRIENDID = "friendid";
	public static final String STATUS = "status";
	public static final String ISBLOCK = "isblock";
	public static final String DATE = "date";
	public static final String ISSEND = "SEND";
	public static final String IMAGE = "image";
	public static final String TIME = "time";
	public static final String TYPE = "type";
	public static final String KEY_PASSWORD = "password";
	public static final String DATABASE_NAME = "BMNY";
	public static final String DATABASE_TABLE = "FriendsList";
	public static final String DATABASE_TABLE2 = "Photohistory";
	public static final String DATABASE_REG_TABLE = "Register";

	public static final int DATABASE_VERSION = 1;
	public static final String TAG = "DBADAPTER";
	public static final String KEY_ID = "id";
	public static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE
			+ "(id integer primary key autoincrement, "
			+ "username text not null, userid text not null, status text not null, isblock text not null, type text not null, friendid text not null);";

	public static final String DATABASE_REGISTER = "create table Register (id integer primary key autoincrement, "
			+ "useremail text not null,username text not null, password text not null);";
	
	private static final String CREATE_SIGHTING_TABLE = "create table Photohistory ("
			+ KEY_ID
			+ " integer primary key autoincrement, "
			+ USERID
			+ " text not null, username text not null, date text not null, image BLOB, time text not null, type text not null);";

	public Context context;
	public DatabaseHelper dbhelper;
	public SQLiteDatabase db;
	long rowid = 0;

	public DBAdapater(Context ctx) {
		context = ctx;
		dbhelper = new DatabaseHelper(context);
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try {
				db.execSQL(DATABASE_CREATE);
				db.execSQL(CREATE_SIGHTING_TABLE);
				db.execSQL(DATABASE_REGISTER);

			} catch (SQLException e) {
				e.printStackTrace();

			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "upgrading database from version" + "oldversion"
					+ " to " + "newversion "
					+ "' which will destroy all old data ");
		}
	}

	public DBAdapater open() throws SQLException {
		db = dbhelper.getWritableDatabase();

		return this;

	}

	public void close() {
		dbhelper.close();

	}

	public long addFriend(String name, String id, String status,
			String isblock, String type,String friendid) {
		long rowid = 0;
		ContentValues intialvalue = new ContentValues();
		intialvalue.put(KEY_NAME, name);
		intialvalue.put(USERID, id);
		intialvalue.put(STATUS, status);
		intialvalue.put(ISBLOCK, isblock);
		intialvalue.put(TYPE, type);
		intialvalue.put(FRIENDID, friendid);
		rowid = db.insert(DATABASE_TABLE, null, intialvalue);
		return rowid;
	}

	public long createMessageEntry(String userid, String name, String time,
			String date, String type, Bitmap b) {
		db = dbhelper.getWritableDatabase();
		Bitmap photo = b;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] bArray = bos.toByteArray();

		ContentValues cv = new ContentValues();
		cv.put(USERID, userid);
		cv.put(KEY_NAME, name);
		cv.put(TIME, time);
		cv.put(TYPE, type);
		cv.put(DATE, date);
		cv.put(IMAGE, bArray);
		System.out.println("after innserting data in on purchase entry");
		long rowid = db.insert(DATABASE_TABLE2, null, cv);
		return rowid;
	}
	
	public long insertRegister(String name, String password) {
		long rowid = 0;
		ContentValues intialvalue = new ContentValues();
		//intialvalue.put(KEY_EMAIL, name);
		intialvalue.put(KEY_NAME, name);
		intialvalue.put(KEY_PASSWORD, password);
		rowid = db.insert(DATABASE_REG_TABLE, null, intialvalue);
		return rowid;
	}

	/*
	 * public Sighting getSightings() throws SQLException {
	 * 
	 * Cursor cur = db.query(true, PURCHASE_TABLE, new String[] { KEY_POP,
	 * CATEGORY, LOCATION, DATE, DESCRIPTION, IMAGE }, null, null, null, null,
	 * null, null);
	 * 
	 * if (cur.moveToFirst()) { do { byte[] blob =
	 * cur.getBlob(cur.getColumnIndex(IMAGE));
	 * 
	 * String name = cur.getString(cur.getColumnIndex(KEY_POP)); String category
	 * = cur.getString(cur.getColumnIndex(CATEGORY)); String location =
	 * cur.getString(cur.getColumnIndex(LOCATION)); String date =
	 * cur.getString(cur.getColumnIndex(DATE)); String description =
	 * cur.getString(cur .getColumnIndex(DESCRIPTION)); Bitmap bm =
	 * BitmapFactory.decodeByteArray(blob, 0, blob.length); cur.close(); return
	 * new Sighting(name, location, category, date, description); } while
	 * (cur.moveToNext()); }
	 * 
	 * return null; }
	 */

	/*
	 * public Sighting getMySightings(String userId) throws SQLException {
	 * 
	 * 
	 * Cursor cur = db.query(true, PURCHASE_TABLE, new String[] { KEY_POP,
	 * CATEGORY, LOCATION, DATE, DESCRIPTION, IMAGE }, null, null, null, null,
	 * null, null);
	 * 
	 * 
	 * Cursor cur = db.rawQuery("SELECT * FROM " + PURCHASE_TABLE +
	 * " WHERE userid=?", new String[] { userId });
	 * 
	 * if (cur.moveToFirst()) { do {
	 * 
	 * String name = cur.getString(cur.getColumnIndex(KEY_POP)); String category
	 * = cur.getString(cur.getColumnIndex(CATEGORY)); String location =
	 * cur.getString(cur.getColumnIndex(LOCATION)); String date =
	 * cur.getString(cur.getColumnIndex(DATE)); String description =
	 * cur.getString(cur .getColumnIndex(DESCRIPTION)); byte[] blob =
	 * cur.getBlob(cur.getColumnIndex(IMAGE)); Bitmap bm =
	 * BitmapFactory.decodeByteArray(blob, 0, blob.length); cur.close(); return
	 * new Sighting(name, location, category, date, description); } while
	 * (cur.moveToNext()); }
	 * 
	 * return null; }
	 */

	public ArrayList<HashMap<String, String>> getPopEnteries() {
		int i = 0;
		db = dbhelper.getWritableDatabase();
		Cursor cursor = db.query(true, DATABASE_TABLE2, new String[] { KEY_NAME,
				TYPE, TIME, DATE, USERID,IMAGE }, null, null, null,
				null, null, null);
		ArrayList<HashMap<String, String>> list_name = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		if (cursor.moveToFirst()) {
			do {

				String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
				map.put(KEY_NAME + i, name);
				String category = cursor.getString(cursor
						.getColumnIndex(TYPE));
				map.put(TYPE, category);
				String location = cursor.getString(cursor
						.getColumnIndex(TIME));
				map.put(TIME, location);
				String date = cursor.getString(cursor.getColumnIndex(DATE));
				map.put(DATE + i, date);
				String description = cursor.getString(cursor
						.getColumnIndex(USERID));
				map.put(USERID, description);
				// System.out.println("value of name list" + name);
				list_name.add(map);
				// System.out.println("value of counter in get name i " + i);
			} while (cursor.moveToNext());
			cursor.close();
		}
		if (list_name.size() != 0) {

			return list_name;

		} else {
			// System.out
			// .println("names are not added to list in get purchse function");
			return list_name;
		}

	}

	String[] strarray;

	public ArrayList<HashMap<String, String>> getPopEnteriesbyUser(String userid) {
		db = dbhelper.getWritableDatabase();
		int j = 0;

		ArrayList<HashMap<String, String>> list_name = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		Log.i(TAG, "catArra " + userid);
		String q = "SELECT * FROM Photohistory WHERE userid = '" + userid + "'";
		System.out.println("query " + q);
		Cursor cursor = db.rawQuery(q, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String name = cursor.getString(cursor
							.getColumnIndex(KEY_NAME));
					System.out.println("KEY_POP  " + name);
					map.put(KEY_NAME, name);
					String category2 = cursor.getString(cursor
							.getColumnIndex(TYPE));
					;
					map.put(TYPE, category2);
					String location = cursor.getString(cursor
							.getColumnIndex(TIME));
				
					map.put(TIME, location);
					String date = cursor.getString(cursor.getColumnIndex(DATE));
					map.put(DATE + j, date);
					String description = cursor.getString(cursor
							.getColumnIndex(USERID));
					map.put(USERID, description);
					list_name.add(map);
				} while (cursor.moveToNext());
				cursor.close();
			} else {
				cursor.close();
			}

			// db.close();
		}

		return list_name;

	}

	public ArrayList<Bitmap> getPopImages() {
		db = dbhelper.getWritableDatabase();
		Cursor cursor = db.query(true, DATABASE_TABLE2, new String[] { IMAGE },
				null, null, null, null, null, null);
		ArrayList<Bitmap> list_name = new ArrayList<Bitmap>();
		if (cursor.moveToFirst()) {
			do {

				byte[] blob = cursor.getBlob(cursor.getColumnIndex(IMAGE));
				Bitmap bm = BitmapFactory.decodeByteArray(blob, 0, blob.length);

				// System.out.println("value of name list" + name);

				list_name.add(bm);
				// System.out.println("value of counter in get name i " + i);
			} while (cursor.moveToNext());
			cursor.close();
		}
		if (list_name.size() != 0) {

			return list_name;

		} else {
			// System.out
			// .println("names are not added to list in get purchse function");
			return list_name;
		}

	}

	public ArrayList<HashMap<String, String>> getUserList(String userid) {
		int i = 0;
		db = dbhelper.getWritableDatabase();
		
		String q = "SELECT * FROM FriendsList WHERE userid = '" + userid + "'";
		
		Cursor cursor = db.query(DATABASE_REG_TABLE, null,
				"userid=?",
				new String[] { userid }, null, null, null);
		//Cursor cursor = db.rawQuery(q, null);
		
		ArrayList<HashMap<String, String>> list_name = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		if (cursor.moveToFirst()) {
			do {

				String name = cursor.getString(cursor
						.getColumnIndex("username"));
				map.put(KEY_NAME, name);
				String id = cursor.getString(cursor
						.getColumnIndex(FRIENDID));
				map.put(FRIENDID, id);
				String status = cursor.getString(cursor
						.getColumnIndex(STATUS));
				map.put(STATUS, status);
				String type = cursor.getString(cursor
						.getColumnIndex(TYPE));
				map.put(TYPE, type);
				// System.out.println("value of name list" + name);

				list_name.add(map);
				// System.out.println("value of counter in get name i " + i);
			} while (cursor.moveToNext());
			cursor.close();
			db.close();
		}
		if (list_name.size() != 0) {

			return list_name;

		} else {
			// System.out
			// .println("names are not added to list in get purchse function");
			return list_name;
		}

	}

	

	public boolean Login(String username, String password) throws SQLException {
		System.out.println("value of usernamne is " + username + "password is"

		+ password);
		db = dbhelper.getWritableDatabase();

		Cursor mCursor = db.query(DATABASE_REG_TABLE, null,
				"username=? AND password=?",
				new String[] { username, password }, null, null, null);
		if (mCursor != null) {

			if (mCursor.getCount() > 0) {
				mCursor.close();
				return true;

			}
		}
		return false;
	}

	public int getId(String username, String password) throws SQLException {
		System.out.println("value of usernamne is " + username + "password is"
				+ password);
		db = dbhelper.getWritableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE
				+ " WHERE username=? AND password=?", new String[] { username,
				password });

		if (mCursor.moveToFirst()) {
			do {

				int id = mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID));
				System.out.println("value of id in get id is " + id);
				mCursor.close();
				return id;
			} while (mCursor.moveToNext());

		}
		return 0;
	}

}
