package de.hauke_stieler.geonotes.notes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class NoteStore {
    private static final String NOTES_TABLE_NAME = "notes";
    private static final String NOTES_COL_ID = "id";
    private static final String NOTES_COL_LAT = "lat";
    private static final String NOTES_COL_LON = "lon";
    private static final String NOTES_COL_DESCRIPTION = "description";
    private static final String NOTES_COL_CREATED_AT = "created_at";

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY, %s DOUBLE NOT NULL, %s DOUBLE NOT NULL, %s VARCHAR NOT NULL, %s VARCHAR NOT NULL);",
                NOTES_TABLE_NAME,
                NOTES_COL_ID,
                NOTES_COL_LAT,
                NOTES_COL_LON,
                NOTES_COL_DESCRIPTION,
                NOTES_COL_CREATED_AT));
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            // Version 5: Column "created_at" added
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s VARCHAR NOT NULL DEFAULT '%s'", NOTES_TABLE_NAME, NOTES_COL_CREATED_AT, Note.getDateTimeString(GregorianCalendar.getInstance())));
        }

        Log.i("NoteStore", String.format("onUpgrade: from version %d to version %d", oldVersion, newVersion));
    }

    public long addNote(SQLiteDatabase db, String description, double lat, double lon) {
        ContentValues values = new ContentValues();
        values.put(NOTES_COL_LAT, lat);
        values.put(NOTES_COL_LON, lon);
        values.put(NOTES_COL_DESCRIPTION, description);
        values.put(NOTES_COL_CREATED_AT, Note.getDateTimeString(GregorianCalendar.getInstance()));

        return db.insert(NOTES_TABLE_NAME, null, values);
    }

    public void updateDescription(SQLiteDatabase db, long id, String newDescription) {
        ContentValues values = new ContentValues();
        values.put(NOTES_COL_ID, id);
        values.put(NOTES_COL_DESCRIPTION, newDescription);

        db.update(NOTES_TABLE_NAME, values, NOTES_COL_ID + " = ?", new String[]{"" + id});
    }

    public void updateLocation(SQLiteDatabase db, long id, GeoPoint location) {
        ContentValues values = new ContentValues();
        values.put(NOTES_COL_ID, id);
        values.put(NOTES_COL_LAT, location.getLatitude());
        values.put(NOTES_COL_LON, location.getLongitude());

        db.update(NOTES_TABLE_NAME, values, NOTES_COL_ID + " = ?", new String[]{"" + id});
    }

    public void removeNote(SQLiteDatabase db, long id) {
        db.delete(NOTES_TABLE_NAME, NOTES_COL_ID + " = ?", new String[]{"" + id});
    }

    public void removeAllNotes(SQLiteDatabase db) {
        db.delete(NOTES_TABLE_NAME, null, null);
    }

    public List<Note> getAllNotes(SQLiteDatabase db) {
        Cursor cursor = db.query(NOTES_TABLE_NAME, new String[]{NOTES_COL_ID, NOTES_COL_DESCRIPTION, NOTES_COL_LAT, NOTES_COL_LON, NOTES_COL_CREATED_AT}, null, null, null, null, null);

        List<Note> notes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                notes.add(getNoteFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return notes;
    }

    public Note getNote(SQLiteDatabase db, String noteId) {
        Cursor cursor = db.query(NOTES_TABLE_NAME, new String[]{NOTES_COL_ID, NOTES_COL_DESCRIPTION, NOTES_COL_LAT, NOTES_COL_LON, NOTES_COL_CREATED_AT}, NOTES_COL_ID + "=?", new String[]{noteId}, null, null, null);
        cursor.moveToFirst();
        return getNoteFromCursor(cursor);
    }

    private Note getNoteFromCursor(Cursor cursor) {
        return new Note(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3), cursor.getString(4));
    }
}
