package se.lundakarnevalen.remote;

import java.util.LinkedList;
import java.util.List;

import se.lundakarnevalen.widget.LKInboxArrayAdapter.LKMenuListItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LKSQLiteDB extends SQLiteOpenHelper{
	
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
		ContentValues values = new ContentValues();
		values.put(LKSQLiteDBContract.COLUMN_NAME_TITLE, item.title);
		values.put(LKSQLiteDBContract.COLUMN_NAME_MESSAGE, item.message);
		values.put(LKSQLiteDBContract.COLUMN_NAME_DATE, item.date);
		values.put(LKSQLiteDBContract.COLUMN_NAME_IMG, "img path");
		values.put(LKSQLiteDBContract.COLUMN_NAME_UNREAD, item.unread ? 1 : 0);
		return db.insert(LKSQLiteDBContract.TABLE_NAME, null, values);
	}
	
	public List<LKMenuListItem> getMessages(){
		List<LKMenuListItem> data = new LinkedList<LKMenuListItem>();
		SQLiteDatabase dbRead = getReadableDatabase();
		String[] dataProjection = {LKSQLiteDBContract.COLUMN_NAME_ENTRY_ID, LKSQLiteDBContract.COLUMN_NAME_TITLE, LKSQLiteDBContract.COLUMN_NAME_MESSAGE, LKSQLiteDBContract.COLUMN_NAME_DATE, LKSQLiteDBContract.COLUMN_NAME_IMG, LKSQLiteDBContract.COLUMN_NAME_UNREAD};
		String sort = "DATE DESC";
		Cursor cursor = dbRead.query(LKSQLiteDBContract.TABLE_NAME, dataProjection, null, null, null, null, sort);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			boolean unread = cursor.getInt(5) == 1;
			LKMenuListItem item = new LKMenuListItem(cursor.getString(1), cursor.getString(2), cursor.getString(3), unread, null);
			data.add(item);
			cursor.moveToNext();
		}
		return data;
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
		onUpgrade(db, oldVersion, newVersion);
	}
	
	public static final class LKSQLiteDBContract{
		public final static String DATABASE_NAME = "LK2014";
		public final static int DATABASE_VERSION = 1;
		
		public final static String TABLE_NAME = "LKMessageTable";
		public final static String COLUMN_NAME_ENTRY_ID = "entryid";
		public final static String COLUMN_NAME_TITLE = "title";
		public final static String COLUMN_NAME_MESSAGE = "message";
		public final static String COLUMN_NAME_DATE = "date";
		public final static String COLUMN_NAME_IMG = "image";
		public final static String COLUMN_NAME_UNREAD = "unread";
		
		public final static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+" ID INTEGER PRIMARY KEY,"+
												  COLUMN_NAME_ENTRY_ID + " TEXT," +
												  COLUMN_NAME_TITLE + " TEXT,"+
												  COLUMN_NAME_MESSAGE + " TEXT,"+
												  COLUMN_NAME_DATE + " TEXT,"+
												  COLUMN_NAME_IMG + " TEXT,"+
												  COLUMN_NAME_UNREAD + " INTEGER)";
		public final static String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
		
	}
}