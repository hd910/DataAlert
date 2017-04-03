package hd.dataalert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Hayde on 03-Apr-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventsManager";
    private static final String TABLE_EVENTS = "events";

    private static final String KEY_ID = "id";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DATE = "date";
    private static final String KEY_DESCRIPTION = "description";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_STATUS + " TEXT," +  KEY_DATE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

        // Create tables again
        onCreate(db);
    }

    //Add event to db
    void addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, event.getStatus());
        values.put(KEY_DATE, event.getDate());
        values.put(KEY_DESCRIPTION, event.getDescription());

        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
    }

    //Returns single event from db
    Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[] { KEY_ID,
                        KEY_DATE, KEY_DESCRIPTION }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Event event = new Event(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return event;
    }

    // Getting All Contacts
    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> eventList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event contact = new Event();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setStatus(cursor.getString(1));
                contact.setDate(cursor.getString(2));
                contact.setDescription(cursor.getString(3));
                // Adding contact to list
                eventList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return eventList;
    }

    //Delete all data
    public void deleteAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_EVENTS);

    }
}
