package com.tbldevelopment.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

	public static String DATABASE_NAME = "bmny";
	public static int DATABASE_VERSION = 1;
	public static String TABLE_REGISTER = "register";
	public static String TABLE_FRIEND = "friend";
	public static String TABLE_MESSAGE = "message";
	public static String TABLE_GET_MESSAGE = "getmessage";

	/** register Table **/
	public static String USER_ID = "userId";
	public static String USER_NAME = "userName";
	public static String USER_EMAIL = "userEmail";
	public static String PASSWORD = "password";
	public static String SERVER_USER_ID = "serverUserId";
	

	/** friend **/
	public static String LOGIN_USER_ID = "loginUserId";
	public static String FRIEND_ID = "friendid";
	public static String FRIEND_NAME = "friendName";
	public static String SENDER_ID = "senderId";
	public static String RECIEVER_ID = "recieverId";
	public static String FRIEND_STATUS = "friendStatus";
	public static String FRIEND_EMAIL = "friendEmail";
	public static String FRIEND_PASSWORD = "friendPassword";
	public static String FRIEND_MOBILE = "friendMobile";
	public static String FRIEND_DATE = "friendDate";
	public static String WEBSITE = "website";

	/** message **/
	public static String MESSAGE_ID = "messageId";
	public static String PHOTO_DATA = "photoData";
	
	/** get message **/
	public static String GET_MESSAGE_ID = "getMessageId";
	public static String GET_MESSAGE_DATA = "getMessageData";
	public static String GET_MESSAGE_USER_ID = "userIdGetMessage";
	
	String CREATE_TABLE_REGISTER, CREATE_TABLE_FRIEND, CREATE_TABLE_MESSAGE,CREATE_TABLE_GET_MESSAGE;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub

		CREATE_TABLE_REGISTER = "CREATE TABLE " + TABLE_REGISTER + "("
				+ USER_ID + " INTEGER PRIMARY KEY autoincrement ," + USER_EMAIL
				+ " TEXT," + USER_NAME
				+ " TEXT," + PASSWORD + " TEXT," + SERVER_USER_ID + " TEXT);";

		CREATE_TABLE_FRIEND = "CREATE TABLE " + TABLE_FRIEND + "("+ LOGIN_USER_ID
				+ " TEXT ,"+FRIEND_NAME + " TEXT ,"+FRIEND_EMAIL + " TEXT ,"+FRIEND_PASSWORD + " TEXT,"
				+ FRIEND_MOBILE + " TEXT ,"+FRIEND_DATE + " TEXT ,"
				+FRIEND_STATUS + " TEXT ,"+WEBSITE + " TEXT ,"+ FRIEND_ID + " TEXT,"+SENDER_ID + " TEXT ,"+RECIEVER_ID + " TEXT);";

		CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE + "("
				+ MESSAGE_ID + " INTEGER PRIMARY KEY autoincrement ,"
				+ PHOTO_DATA + " TEXT);";

		CREATE_TABLE_GET_MESSAGE = "CREATE TABLE " + TABLE_GET_MESSAGE + "(" + GET_MESSAGE_ID
				+ " INTEGER PRIMARY KEY autoincrement ," + GET_MESSAGE_USER_ID + " TEXT,"+GET_MESSAGE_DATA + " TEXT);";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_FRIEND);
		db.execSQL(CREATE_TABLE_MESSAGE);
		db.execSQL(CREATE_TABLE_REGISTER);
		db.execSQL(CREATE_TABLE_GET_MESSAGE);
	}

	public long insertRegister(String email,String name, String password,String serverUserId) {
		long rowid = 0;
		ContentValues intialvalue = new ContentValues();
		intialvalue.put(USER_NAME, email);
		intialvalue.put(USER_NAME, name);
		intialvalue.put(PASSWORD, password);
		intialvalue.put(SERVER_USER_ID, serverUserId);
		rowid = getWritableDatabase().insert(TABLE_REGISTER, null, intialvalue);
		return rowid;
	}

	public long Login(String username, String password) throws SQLException {
		long id = 0;
		Cursor mCursor = getReadableDatabase().rawQuery(
				"select * from register where userName = ? And password = ?",
				new String[] { username, password });
		if (mCursor != null) {

			if (mCursor.getCount() > 0) {
				if (mCursor.moveToFirst()) {
					id = mCursor.getInt(mCursor.getColumnIndex(USER_ID));
				}
				mCursor.close();
				return id;

			}
		}
		return -1;
	}

	public long getuserid(String username) throws SQLException {
		long id = 0;
		Cursor mCursor = getReadableDatabase().rawQuery(
				"select * from register where userName = ? ",
				new String[] { username });
		if (mCursor != null) {

			if (mCursor.getCount() > 0) {
				if (mCursor.moveToFirst()) {
					id = mCursor.getInt(mCursor.getColumnIndex(USER_ID));
				}
				mCursor.close();
				return id;

			}
		}
		return -1;
	}

	public boolean isExist(String username) throws SQLException {

		Cursor mCursor = getReadableDatabase().rawQuery(
				"select * from register where userName = ?",
				new String[] { username });
		if (mCursor != null) {

			if (mCursor.getCount() > 0) {
				mCursor.close();
				return true;

			}
		}
		mCursor.close();
		return false;
	}

	public int getUserCount() throws SQLException {

		int count = 0;
		Cursor mCursor = getReadableDatabase().rawQuery(
				"select * from register", null);
		if (mCursor != null) {
			count = mCursor.getCount();
			mCursor.close();
		}
		return count;
	}

	public int getFriendCount() throws SQLException {

		int count = 0;
		Cursor mCursor = getReadableDatabase().rawQuery("select * from friend",
				null);
		if (mCursor != null) {
			count = mCursor.getCount();
			mCursor.close();
		}
		return count;
	}

	public void addFriend(String loginUserId,String username,String email,String password,String mobile
			,String date,String status,String website,String friendid,String senderid,String recieverid) {
		SQLiteDatabase sqldb=this.getWritableDatabase();
		ContentValues intialvalue = new ContentValues();
		intialvalue.put(LOGIN_USER_ID, loginUserId);
		intialvalue.put(FRIEND_NAME, username);
		intialvalue.put(FRIEND_EMAIL, email);
		intialvalue.put(FRIEND_PASSWORD, password);
		intialvalue.put(FRIEND_MOBILE, mobile);
		intialvalue.put(FRIEND_DATE, date);
		intialvalue.put(FRIEND_STATUS,status);
		intialvalue.put(WEBSITE, website);
		intialvalue.put(FRIEND_ID, friendid);
		intialvalue.put(SENDER_ID, senderid);
		intialvalue.put(RECIEVER_ID, recieverid);
		sqldb.insert(TABLE_FRIEND, null, intialvalue);
		sqldb.close();
	}
	
	
	public long createGetMassage(String userId,String getMessageData) {
		long rowid = 0;
		ContentValues intialvalue = new ContentValues();
		intialvalue.put(GET_MESSAGE_DATA, getMessageData);
		intialvalue.put(GET_MESSAGE_USER_ID, userId);
		rowid = getWritableDatabase().insert(TABLE_GET_MESSAGE, null, intialvalue);
		return rowid;
	}
	
	public void updateGetMessage(String userId,String getMessage)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(GET_MESSAGE_DATA, getMessage);
		values.put(GET_MESSAGE_USER_ID, userId);
		sqldb.update("getmessage", values, "userIdGetMessage=?",new String[]{userId});
		sqldb.close();
	}

	
	public Cursor getFriend(String userId) {
		
		SQLiteDatabase sqldb = this.getWritableDatabase();
		String query = "select * from friend where loginUserId=?";
		Cursor c = sqldb.rawQuery(query,new String[]{userId});
		if (c.getCount() != 0) {
			return c;
		}
		return null;
	}
	
	public Cursor getMessages(String userId) {

		SQLiteDatabase sqldb = this.getWritableDatabase();
		String query = "select * from getmessage where userIdGetMessage=?";
		Cursor c = sqldb.rawQuery(query,new String[]{userId});
		if (c.getCount() != 0) {
			return c;
		}
		return null;
	}

	public void blockFrind(String loginUserId,String friendid)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("friendStatus", "0");
		sqldb.update("friend", values, "loginUserId=? AND friendid=?", new String[]{loginUserId,friendid});
		sqldb.close();
	}
	
	public void unblockFrind(String loginUserId,String friendid)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("friendStatus", "10");
		sqldb.update("friend", values, "loginUserId=? AND friendid=?", new String[]{loginUserId,friendid});
		sqldb.close();
	}
	
	public void approveFriendRequest(String friendid)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("friendStatus", "10");
		sqldb.update("friend", values, "friendid=?", new String[]{friendid});
		sqldb.close();
	}
	
	public void deleteFriend(String friendid)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		sqldb.delete("friend", "friendid=?", new String[] {friendid});
		sqldb.close();
	}
	
	public void deleteAllFriend(String userId)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		sqldb.delete("friend","loginUserId=?",new String[]{userId});
		sqldb.close();
	}
	
	public void deleteAllMessages(String userId)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		sqldb.delete("getmessage","userIdGetMessage=?",new String[]{userId});
		sqldb.close();
	}
	
	public void rejectFriendRequest(String friendid)
	{
		SQLiteDatabase sqldb=this.getWritableDatabase();
		sqldb.delete("friend", "friendid=?", new String[] {friendid});
		sqldb.close();
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
