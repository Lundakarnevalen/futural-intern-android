package se.lundakarnevalen.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.lundakarnevalen.widget.LKSectionsArrayAdapter.LKSectionsItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SectionSQLiteDB extends SQLiteOpenHelper{
	
	public SectionSQLiteDB(Context context) {
		super(context, SectionSQLiteDBContract.DATABASE_NAME, null, SectionSQLiteDBContract.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SectionSQLiteDBContract.CREATE_TABLE);
	}

	/**
	 * Will drop all the entries from the sections database
	 */
	public void dropEntiresInDatabase() {
		getWritableDatabase().execSQL(SectionSQLiteDBContract.DELETE_ENTRIES);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SectionSQLiteDBContract.DELETE_ENTRIES);
		onCreate(db);
	}
	/**
	 * 
	 * @param Will add an LKSecionsItem to the sections database
	 * @returns the row ID or -1 if an error occurred
	 */
	public long addItem(LKSectionsItem item){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SectionSQLiteDBContract.COLUMN_NAME_TITLE, item.title);
		values.put(SectionSQLiteDBContract.COLUMN_NAME_ICON, item.icon);
		values.put(SectionSQLiteDBContract.COLUMN_NAME_SLOGAN, item.slogan);
		values.put(SectionSQLiteDBContract.COLUMN_NAME_INFO, item.information);
		values.put(SectionSQLiteDBContract.COLUMN_NAME_LIKED, item.like ? 1 : 0);
		return db.insert(SectionSQLiteDBContract.TABLE_NAME, null, values);
	}
	
	/**
	 * Gets a random section from the database
	 * @return 
	 */
	public LKSectionsItem getRandomSection() {
		List<LKSectionsItem> listSections = getSections();
		
		Random rand = new Random();
		
		return listSections.get(rand.nextInt(listSections.size()));
	}
	
	public List<LKSectionsItem> getSections() {
		List<LKSectionsItem> data = new ArrayList<LKSectionsItem>();
		SQLiteDatabase dbRead = getReadableDatabase();
		String[] dataProjection = {SectionSQLiteDBContract.COLUMN_NAME_ENTRY_ID, SectionSQLiteDBContract.COLUMN_NAME_TITLE, SectionSQLiteDBContract.COLUMN_NAME_ICON, SectionSQLiteDBContract.COLUMN_NAME_SLOGAN, SectionSQLiteDBContract.COLUMN_NAME_INFO, SectionSQLiteDBContract.COLUMN_NAME_LIKED};
		String sort = "title ASC";
		Cursor cursor = dbRead.query(SectionSQLiteDBContract.TABLE_NAME, dataProjection, null, null, null, null, sort);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			LKSectionsItem item = new LKSectionsItem(cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), Boolean.getBoolean(cursor.getString(5)));
			data.add(item);
			cursor.moveToNext();
		}
		return data;
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
		onUpgrade(db, oldVersion, newVersion);
	}
	
	public static final class SectionSQLiteDBContract{
		public final static String DATABASE_NAME = "LKSection2014";
		public final static int DATABASE_VERSION = 1;
		
		public final static String TABLE_NAME = "LKMessageTable";
		public final static String COLUMN_NAME_ENTRY_ID = "entryid";
		public final static String COLUMN_NAME_TITLE = "title";
		public final static String COLUMN_NAME_ICON = "message";
		public final static String COLUMN_NAME_SLOGAN = "date";
		public final static String COLUMN_NAME_INFO = "image";
		public final static String COLUMN_NAME_LIKED = "unliked";
		
		public final static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+" ID INTEGER PRIMARY KEY,"+
												  COLUMN_NAME_ENTRY_ID + " TEXT," +
												  COLUMN_NAME_TITLE + " TEXT,"+
												  COLUMN_NAME_ICON + " IMAGE,"+
												  COLUMN_NAME_SLOGAN + " TEXT,"+
												  COLUMN_NAME_INFO + " TEXT,"+
												  COLUMN_NAME_LIKED + " INTEGER)";
		
		public final static String DELETE_ENTRIES = "DELETE FROM " + TABLE_NAME;
		
	}
}