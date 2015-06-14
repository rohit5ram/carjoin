package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

/**
 * Created by rohit on 31/5/15.
 */
public class Commuters {
    public static final String TABLE_NAME = "Commuters";

    public static final String COL_ID = "ID";
    public static final String COL_TRIP_ID = "TRIP_ID";
    public static final String COL_USER_ID = "USER_ID";
    public static final String COL_USER_NAME = "USER_NAME";
    public static final String COL_ROLE = "ROLE";
    public static final String COL_BOARDING_LAT = "BOARDING_LAT";
    public static final String COL_BOARDING_LONG = "BOARDING_LONG";
    public static final String COL_BOARDING_TIME = "BOARDING_TIME";
    public static final String COL_DEBOARDING_LAT = "DEBOARDING_LAT";
    public static final String COL_DEBOARDING_LONG = "DEBOARDING_LONG";
    public static final String COL_DEBOARDING_TIME = "DEBOARDING_TIME";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_TRIP_ID, COL_USER_ID, COL_USER_NAME, COL_ROLE, COL_BOARDING_LAT,
            COL_BOARDING_LONG, COL_BOARDING_TIME, COL_DEBOARDING_LAT, COL_DEBOARDING_LONG,
            COL_DEBOARDING_TIME, COL_LAST_MODIFIED
    };

    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_TRIP_ID + " TEXT NOT NULL DEFAULT '', "
            + COL_USER_ID + " TEXT NOT NULL DEFAULT '', "
            + COL_USER_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_ROLE + " TEXT NOT NULL DEFAULT '', "
            + COL_BOARDING_LAT + " REAL NOT NULL DEFAULT -1, "
            + COL_BOARDING_LONG + " REAL NOT NULL DEFAULT -1, "
            + COL_BOARDING_TIME + " INTEGER NOT NULL DEFAULT -1, "
            + COL_DEBOARDING_LAT + " REAL NOT NULL DEFAULT -1 "
            + COL_DEBOARDING_LONG + " REAL NOT NULL DEFAULT -1, "
            + COL_DEBOARDING_TIME + " REAL NOT NULL DEFAULT -1, "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public String tripId = "";
    public String userId = "";
    public String userName = "";
    public String role = "";
    public double boardingLat = -1;
    public double boardingLong = -1;
    public double boardingTime = -1;
    public double deboardingLat = -1;
    public double deboardingLong = -1;
    public double deboardingTime = -1;
    public long lastModified = -1;

    public Commuters(Cursor cursor) {
        this.id = cursor.getString(0);
        this.tripId = cursor.getString(1);
        this.userId = cursor.getString(2);
        this.userName= cursor.getString(3);
        this.role = cursor.getString(4);
        this.boardingLat = cursor.getLong(5);
        this.boardingLong = cursor.getLong(6);
        this.boardingTime = cursor.getDouble(7);
        this.deboardingLat = cursor.getDouble(8);
        this.deboardingLong = cursor.getDouble(9);
        this.deboardingTime = cursor.getDouble(10);
        this.lastModified = cursor.getLong(11);
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_TRIP_ID, tripId);
        values.put(COL_USER_ID, userId);
        values.put(COL_USER_NAME, userName);
        values.put(COL_ROLE, role);
        values.put(COL_BOARDING_LAT, boardingLat);
        values.put(COL_BOARDING_LONG, boardingLong);
        values.put(COL_BOARDING_TIME, boardingTime);
        values.put(COL_DEBOARDING_LAT, deboardingLat);
        values.put(COL_DEBOARDING_LONG, deboardingLong);
        values.put(COL_DEBOARDING_TIME, deboardingTime);

        return values;
    }
}
