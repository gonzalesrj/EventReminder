package com.russel.eventreminder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "EventReminder.db";
    public static final String TABLE_NAME = "EventTable";
    public static final String EVENT_ID = "ID";
    public static final String EVENT_NAME = "EventName";
    public static final String EVENT_DATE = "EventDate";
    public static final String EVENT_DESCRIPTION = "EventDescription";
    public static final String EVENT_REMINDER = "EventReminder";
    public static final String EVENT_REPEAT = "EventRepeat";
    public static final String EVENT_IMAGE = "EventImage";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE" + TABLE_NAME + " (" + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EVENT_NAME + " TEXT," +
            EVENT_DATE + " TEXT," +
            EVENT_DESCRIPTION + " TEXT," +
            EVENT_REMINDER + " TEXT," +
            EVENT_REPEAT + " TEXT," +
            EVENT_IMAGE + " BLOB)";
    public static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String INSERT_TABLE_QUERY = "INSERT INTO " + TABLE_NAME + " (" + EVENT_NAME + "," +
            EVENT_DATE + ", " +
            EVENT_DESCRIPTION + ", " +
            EVENT_REMINDER + ", " +
            EVENT_REPEAT + ", " +
            EVENT_IMAGE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_TABLE_QUERY = "UPDATE " + TABLE_NAME + " SET " +
            EVENT_NAME + " = ?, " +
            EVENT_DATE + " = ?, " +
            EVENT_DESCRIPTION + " = ?, " +
            EVENT_REMINDER + " = ?, " +
            EVENT_REPEAT + " = ?, " +
            EVENT_IMAGE + " = ? " +
            "WHERE " + EVENT_ID + " = ?";
    public static final String DELETE_TABLE_QUERY = "DELETE FROM " + TABLE_NAME + " WHERE " + EVENT_ID + " = ?";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
        Log.i("Table...", "Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE_QUERY);
        onCreate(sqLiteDatabase);
    }

    public void insertData(String eventName, String eventDate, String eventDescription, String eventReminder, String eventRepeat, byte[] eventImage) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_TABLE_QUERY);
        sqLiteStatement.clearBindings();

        sqLiteStatement.bindString(1, eventName);
        sqLiteStatement.bindString(2, eventDate);
        sqLiteStatement.bindString(3, eventDescription);
        sqLiteStatement.bindString(4, eventReminder);
        sqLiteStatement.bindString(5, eventRepeat);
        sqLiteStatement.bindBlob(6, eventImage);

        sqLiteStatement.execute();
        sqLiteDatabase.close();
    }

    public void updateData(int id, String eventName, String eventDate, String eventDescription, String eventReminder, String eventRepeat, byte[] eventImage) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(UPDATE_TABLE_QUERY);

        sqLiteStatement.bindString(1, eventName);
        sqLiteStatement.bindString(2, eventDate);
        sqLiteStatement.bindString(3, eventDescription);
        sqLiteStatement.bindString(4, eventReminder);
        sqLiteStatement.bindString(5, eventRepeat);
        sqLiteStatement.bindBlob(6, eventImage);
        sqLiteStatement.bindDouble(7, id);

        sqLiteStatement.execute();
        sqLiteDatabase.close();

    }

    public void deleteData(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(DELETE_TABLE_QUERY);
        sqLiteStatement.clearBindings();
        sqLiteStatement.bindDouble(1, id);

        sqLiteStatement.execute();
        sqLiteDatabase.close();
    }

    public Cursor getData(String sql) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(sql, null);
    }

}
