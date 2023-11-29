package com.devinaxo.gatodex_java;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CatDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gatodex.db";
    private static final int DATABASE_VERSION = 2;

    // Table name and columns
    private static final String TABLE_CAT = "cat";
    static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_PATH = "path";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_PLACE_MET = "place_met";
    private static final String COLUMN_DATE_MET = "date_met";


    // SQL statement
    private static final String SQL_CREATE_CAT_TABLE =
            "CREATE TABLE " + TABLE_CAT + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NICKNAME + " TEXT," +
                    COLUMN_TYPE + " TEXT," +
                    COLUMN_PLACE_MET + " TEXT," +
                    COLUMN_IMAGE_PATH + " TEXT," +
                    COLUMN_DATE_MET + " TEXT)";

    public CatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
        onCreate(db);
    }

    // Add a new cat to the database
    public long addCat(Cat cat) {
        SQLiteDatabase db = getWritableDatabase();
        long newRowId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NICKNAME, cat.getNickname());
            values.put(COLUMN_TYPE, cat.getType());
            values.put(COLUMN_PLACE_MET, cat.getPlaceMet());
            values.put(COLUMN_DATE_MET, cat.getDateMet());
            values.put(COLUMN_IMAGE_PATH, cat.getImagePath());

            newRowId = db.insert(TABLE_CAT, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return newRowId;
    }


    // Delete a cat from the database
    public void deleteCat(long catId) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(catId)};
        db.delete(TABLE_CAT, selection, selectionArgs);
    }

    // Fetch all cats from the database
    public Cursor getAllCats() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                COLUMN_ID + " as _id",
                COLUMN_NICKNAME,
                COLUMN_TYPE,
                COLUMN_PLACE_MET,
                COLUMN_IMAGE_PATH,
                COLUMN_DATE_MET};

        return db.query(TABLE_CAT, projection, null, null, null, null, null);
    }

    public Cat getCatById(long catId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {COLUMN_ID, COLUMN_NICKNAME, COLUMN_TYPE, COLUMN_PLACE_MET, COLUMN_IMAGE_PATH, COLUMN_DATE_MET};
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(catId)};

        Cursor cursor = db.query(
                TABLE_CAT,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Cat cat = null;

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
            int nicknameIndex = cursor.getColumnIndexOrThrow(COLUMN_NICKNAME);
            int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_TYPE);
            int placeMetIndex = cursor.getColumnIndexOrThrow(COLUMN_PLACE_MET);
            int dateMetIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE_MET);
            int pathIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH);

            long id = cursor.getLong(idIndex);
            String nickname = cursor.getString(nicknameIndex);
            String type = cursor.getString(typeIndex);
            String placeMet = cursor.getString(placeMetIndex);
            String dateMet = cursor.getString(dateMetIndex);
            String path = cursor.getString(pathIndex);

            Log.d("DB_DEBUG", "id: " + id);
            Log.d("DB_DEBUG", "nickname: " + nickname);
            Log.d("DB_DEBUG", "type: " + type);
            Log.d("DB_DEBUG", "placeMet: " + placeMet);
            Log.d("DB_DEBUG", "dateMet: " + dateMet);
            Log.d("DB_DEBUG", "path: " + path);

            cat = new Cat(id, nickname, type, placeMet, path, dateMet);
        }

        if (cursor != null) {
            cursor.close();
        }

        return cat;
    }

}
