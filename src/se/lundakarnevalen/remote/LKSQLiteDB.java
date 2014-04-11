package se.lundakarnevalen.remote;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.lundakarnevalen.widget.LKInboxArrayAdapter.LKMenuListItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LKSQLiteDB extends SQLiteOpenHelper{
	
	private static final String LOG_TAG = "DB";
	
	public LKSQLiteDB(Context context) {
		super(context, LKSQLiteDBContract.DATABASE_NAME, null, LKSQLiteDBContract.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(LKSQLiteDBContract.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(LKSQLiteDBContract.DELETE_ENTRIES);
		onCreate(db);
	}
	
	public float addItem(LKMenuListItem item){
		SQLiteDatabase db = getWritableDatabase();
		/*if(messageExistsInDb(item.id)){
			return -1;
		}*/
		ContentValues values = new ContentValues();
		values.put(LKSQLiteDBContract.COLUMN_NAME_TITLE, item.title);
		values.put(LKSQLiteDBContract.COLUMN_NAME_MESSAGE, item.message);
		values.put(LKSQLiteDBContract.COLUMN_NAME_DATE, item.date);
		values.put(LKSQLiteDBContract.COLUMN_NAME_UNREAD, item.unread ? 1 : 0);
		values.put(LKSQLiteDBContract.COLUMN_NAME_ENTRY_ID, item.id);
		values.put(LKSQLiteDBContract.COLUMN_NAME_RECIPIENTS_ID, item.recipients);
		float f = db.insert(LKSQLiteDBContract.TABLE_NAME, null, values);
		//db.close();
		return f;
	}
	
	public int update(LKMenuListItem item) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LKSQLiteDBContract.COLUMN_NAME_TITLE, item.title);
		values.put(LKSQLiteDBContract.COLUMN_NAME_MESSAGE, item.message);
		values.put(LKSQLiteDBContract.COLUMN_NAME_DATE, item.date);
		values.put(LKSQLiteDBContract.COLUMN_NAME_UNREAD, item.unread ? 1 : 0);
		values.put(LKSQLiteDBContract.COLUMN_NAME_RECIPIENTS_ID, item.recipients);
		Log.d("LKSQLiteDB", "item.id = "+item.id);
		return db.update(LKSQLiteDBContract.TABLE_NAME, values, LKSQLiteDBContract.COLUMN_NAME_ENTRY_ID+" = "+item.id, null);
	}
	
	public List<LKMenuListItem> getMessages(){
		List<LKMenuListItem> data = new LinkedList<LKMenuListItem>();
		SQLiteDatabase dbRead = getReadableDatabase();
		String[] dataProjection = {LKSQLiteDBContract.COLUMN_NAME_ENTRY_ID, LKSQLiteDBContract.COLUMN_NAME_TITLE,
									LKSQLiteDBContract.COLUMN_NAME_MESSAGE, LKSQLiteDBContract.COLUMN_NAME_DATE,
									LKSQLiteDBContract.COLUMN_NAME_UNREAD, LKSQLiteDBContract.COLUMN_NAME_RECIPIENTS_ID
									};
		String sort = "DATE DESC";
		Cursor cursor = dbRead.query(LKSQLiteDBContract.TABLE_NAME, dataProjection, null, null, null, null, sort);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			boolean unread = cursor.getInt(4) == 1;
			LKMenuListItem item = new LKMenuListItem(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(5), cursor.getInt(0), unread);
			data.add(item);
			cursor.moveToNext();
		}
		dbRead.close();
		return data;
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
		onUpgrade(db, oldVersion, newVersion);
	}
	
	public int numberOfUnreadMessages() {
		SQLiteDatabase db = getReadableDatabase();
		String[] dataProjection = {LKSQLiteDBContract.COLUMN_NAME_UNREAD};
		String sort = "DATE DESC";
		Cursor cursor = db.query(LKSQLiteDBContract.TABLE_NAME, dataProjection, null, null, null, null, sort);
		cursor.moveToFirst();
		int count = 0;
		while(!cursor.isAfterLast()) {
			if(cursor.getInt(0) == 1) {
				count++;
			}
			cursor.moveToNext();
		}
		db.close();
		return count;
	}
	SQLiteDatabase db;
	public boolean messageExistsInDb(int id) {
		Log.d(LOG_TAG, "start");

		SQLiteDatabase db = getReadableDatabase();
		
		String[] dataProjection = {LKSQLiteDBContract.COLUMN_NAME_ENTRY_ID};
		String sort = "DATE DESC";
		Cursor cursor = db.query(LKSQLiteDBContract.TABLE_NAME, dataProjection, null, null, null, null, sort);
		cursor.moveToFirst();
		
		Log.d(LOG_TAG, "before loop");
		while(!cursor.isAfterLast()) {
			Log.d(LOG_TAG, "loop step");
			if(Integer.parseInt(cursor.getString(0)) == id) {
				return true;
			}
			cursor.moveToNext();
		}
		db.close();
		Log.d("LKSQLiteDB", "Completed messageExistsInDb");
		return false;
	}
	
	public ArrayList<Integer> existingMessageIds() {
		SQLiteDatabase db = getReadableDatabase();
		String[] dataProjection = {LKSQLiteDBContract.COLUMN_NAME_ENTRY_ID};
		String sort = "DATE DESC";
		Cursor cursor = db.query(LKSQLiteDBContract.TABLE_NAME, dataProjection, null, null, null, null, sort);
		cursor.moveToFirst();
		ArrayList<Integer> idList = new ArrayList<Integer>();
		while(!cursor.isAfterLast()) {
			idList.add(Integer.parseInt(cursor.getString(0)));
			cursor.moveToNext();
		}
		db.close();
		return idList;
	}
	
	public int heighestMessageId() {
		SQLiteDatabase db = getReadableDatabase();
		String[] dataProjection = {LKSQLiteDBContract.COLUMN_NAME_ENTRY_ID};
		String sort = "DATE DESC";
		Cursor cursor = db.query(LKSQLiteDBContract.TABLE_NAME, dataProjection, null, null, null, null, sort);
		cursor.moveToFirst();
		int tmp, max = 0;
		while(!cursor.isAfterLast()) {
			tmp = Integer.parseInt(cursor.getString(0));
			if(tmp > max) {
				max = tmp;
			}
			cursor.moveToNext();
		}
		db.close();
		return max;
	}
	
	public static final class LKSQLiteDBContract{
		public final static String DATABASE_NAME = "LK2014";
		public final static int DATABASE_VERSION = 2;
		
		public final static String TABLE_NAME = "LKMessageTable";
		public final static String SECTION_TABLE_NAME = "LKSectionTable";
		public final static String COLUMN_NAME_ENTRY_ID = "entryid";
		public final static String COLUMN_NAME_TITLE = "title";
		public final static String COLUMN_NAME_MESSAGE = "message";
		public final static String COLUMN_NAME_DATE = "date";
		public final static String COLUMN_NAME_IMG = "image";
		public final static String COLUMN_NAME_UNREAD = "unread";
		public final static String COLUMN_NAME_RECIPIENTS_ID = "recipients";
		public final static String COLUMN_NAME_SECTION_NAME = "sectionname";
		
		public final static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+" ID INTEGER PRIMARY KEY,"+
												  COLUMN_NAME_ENTRY_ID + " TEXT," +
												  COLUMN_NAME_TITLE + " TEXT,"+
												  COLUMN_NAME_MESSAGE + " TEXT,"+
												  COLUMN_NAME_DATE + " TEXT,"+
												  COLUMN_NAME_RECIPIENTS_ID + " INTEGER,"+
												  COLUMN_NAME_UNREAD + " INTEGER)";
		public final static String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
		
	}
}