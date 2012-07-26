package br.com.foxtracker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FoxTrackerSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_TRACK = "track";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_COMMENT = "jsontext";

	private static final String DATABASE_NAME = "fox.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_TRACK
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_COMMENT + " text not null);";

	public FoxTrackerSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(FoxTrackerSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
		onCreate(db);
	}

	public void addTrackData(TrackData d) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_COMMENT, JsonProvider.toJson(d));
		db.insert(TABLE_TRACK, COLUMN_COMMENT, cv);
		db.close();
	}

	public List<String> getAllTracks() {
		List<String> resp = new ArrayList<String>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_TRACK, null);
		cur.moveToFirst();
		while (cur.isAfterLast() == false) {
			String json = cur.getString(cur.getColumnIndex(COLUMN_COMMENT));
			TrackData d = JsonProvider.fromJson(json);
			if(d.getStatus().equals(TrackDataStatus.NOT_SENT_YET.toString())){
				resp.add(json);
			}
			cur.moveToNext();
		}
		cur.close();
		db.close();
		return resp;
	}
	
	public void deleteAllTracks(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRACK, null, null);
		db.close();
	}

}
