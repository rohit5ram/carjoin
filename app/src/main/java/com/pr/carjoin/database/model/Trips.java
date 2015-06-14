package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

/**
 * Created by rohit on 31/5/15.
 */
public class Trips {
    public static final String TABLE_NAME = "Trips";

    public static final String COL_ID = "ID";
    public static final String COL_START_KMS_READING = "START_KMS_READING";
    public static final String COL_SOURCE_LAT = "SOURCE_LAT";
    public static final String COL_SOURCE_LONG = "SOURCE_LONG";
    public static final String COL_SOURCE_ADDRESS = "SOURCE_ADDRESS";
    public static final String COL_END_KMS_READING = "END_KMS_READING";
    public static final String COL_DEST_LAT = "DEST_LAT";
    public static final String COL_DEST_LONG = "DEST_LONG";
    public static final String COL_DEST_ADDRESS = "DEST_ADDRESS";
    public static final String COL_TOT_KMS = "TOT_KMS";
    public static final String COL_FUEL_ID = "FUEL_ID";
    public static final String COL_FUEL_NAME = "FUEL_NAME";
    public static final String COL_FUEL_CONSUMED_LTS = "FUEL_CONSUMED_LTS";
    public static final String COL_TRIP_FUEL_COST = "TRIP_FUEL_COST";
    public static final String COL_TRIP_TOT_COST = "TRIP_TOT_COST";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_START_KMS_READING, COL_SOURCE_LAT, COL_SOURCE_LONG, COL_SOURCE_ADDRESS,
            COL_END_KMS_READING, COL_DEST_LAT, COL_DEST_LONG, COL_DEST_ADDRESS, COL_TOT_KMS,
            COL_FUEL_ID, COL_FUEL_NAME, COL_FUEL_CONSUMED_LTS, COL_TRIP_FUEL_COST,
            COL_TRIP_TOT_COST, COL_LAST_MODIFIED
    };

    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_START_KMS_READING + " INTEGER NOT NULL DEFAULT -1, "
            + COL_SOURCE_LAT + " REAL NOT NULL DEFAULT -1, "
            + COL_SOURCE_LONG + " REAL NOT NULL DEFAULT -1, "
            + COL_SOURCE_ADDRESS + " TEXT NOT NULL DEFAULT '', "
            + COL_END_KMS_READING + " INTEGER NOT NULL DEFAULT -1, "
            + COL_DEST_LAT + " REAL NOT NULL DEFAULT -1, "
            + COL_DEST_LONG + " REAL NOT NULL DEFAULT -1, "
            + COL_DEST_ADDRESS + " TEXT NOT NULL DEFAULT '', "
            + COL_TOT_KMS + " INTEGER NOT NULL DEFAULT -1, "
            + COL_FUEL_ID + " TEXT NOT NULL DEFAULT '', "
            + COL_FUEL_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_FUEL_CONSUMED_LTS + " REAL NOT NULL DEFAULT -1, "
            + COL_TRIP_FUEL_COST + " REAL NOT NULL DEFAULT -1, "
            + COL_TRIP_TOT_COST + " REAL NOT NULL DEFAULT -1, "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public int startKmsReading = -1;
    public double sourceLat = -1;
    public double sourceLong = -1;
    public String sourceAddress = "";
    public int endKmsReading = -1;
    public double destLat = -1;
    public double destLong = -1;
    public String destAddress = "";
    public int totKms = -1;
    public String fuelId = "";
    public String fuelName = "";
    public double fuelConsumedInLts = -1;
    public double tripFuelCost = -1;
    public double tripTotCost = -1;
    public long lastModified = -1;

    public Trips(Cursor cursor) {
        this.id = cursor.getString(0);
        this.startKmsReading = cursor.getInt(1);
        this.sourceLat = cursor.getDouble(2);
        this.sourceLong = cursor.getDouble(3);
        this.sourceAddress = cursor.getString(4);
        this.endKmsReading = cursor.getInt(5);
        this.destLat = cursor.getDouble(6);
        this.destLong = cursor.getDouble(7);
        this.destAddress = cursor.getString(8);
        this.totKms = cursor.getInt(9);
        this.fuelId = cursor.getString(10);
        this.fuelName = cursor.getString(11);
        this.fuelConsumedInLts = cursor.getDouble(12);
        this.tripFuelCost = cursor.getDouble(13);
        this.tripTotCost = cursor.getDouble(14);
        this.lastModified = cursor.getLong(15);
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_START_KMS_READING, startKmsReading);
        values.put(COL_SOURCE_LAT, sourceLat);
        values.put(COL_SOURCE_LONG, sourceLong);
        values.put(COL_SOURCE_ADDRESS, sourceAddress);
        values.put(COL_END_KMS_READING, endKmsReading);
        values.put(COL_DEST_LAT, destLat);
        values.put(COL_DEST_LONG, destLong);
        values.put(COL_DEST_ADDRESS, destAddress);
        values.put(COL_TOT_KMS, totKms);
        values.put(COL_FUEL_ID, fuelId);
        values.put(COL_FUEL_NAME, fuelName);
        values.put(COL_FUEL_CONSUMED_LTS, fuelConsumedInLts);
        values.put(COL_TRIP_FUEL_COST, tripFuelCost);
        values.put(COL_TRIP_TOT_COST, tripTotCost);

        return values;
    }
}
