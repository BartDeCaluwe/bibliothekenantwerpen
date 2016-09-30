package com.ap.edu.bart.bibliothekenantwerpen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bib.db";
    private static final String TABLE_BIBLIOTHEKEN = "biblitheken";
    private static final int DATABASE_VERSION = 15;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_BIBLIOTHEKEN + "(_id INTEGER PRIMARY KEY, lat STRING, lng STRING)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIBLIOTHEKEN);
        onCreate(db);
    }

    public ArrayList<Bibliotheek> getAllBibliotheken() {
        ArrayList allBibliotheken = new ArrayList<Bibliotheek>();
        SQLiteDatabase db = this.getReadableDatabase();
        //int count = db.rawQuery("select * from " + TABLE_ZONES, null).getCount();
        //Log.d("edu.ap.mapsaver", "Count : " + count);
        Cursor cursor = db.rawQuery("select * from " + TABLE_BIBLIOTHEKEN, null);
        if (cursor.moveToFirst()) {
            do {
                String lat = cursor.getString(1);
                String lng = cursor.getString(2);
                allBibliotheken.add(new Bibliotheek(lat, lng));
            } while (cursor.moveToNext());
        }

        return allBibliotheken;
    }

    public void saveZones(JSONArray allBibliotheken) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < allBibliotheken.length(); i++) {
            try {
                JSONObject obj = (JSONObject) allBibliotheken.get(i);
                String lat = obj.getString("point_lat");
                String lng = obj.getString("point_lng");

                ContentValues values = new ContentValues();
                values.put("lat", lat);
                values.put("lng", lng);

                db.insert(TABLE_BIBLIOTHEKEN, null, values);
            }
            catch(Exception ex) {
                Log.e("edu.ap.mapsaver", ex.getMessage());
            }
        }
        db.close();
    }

}