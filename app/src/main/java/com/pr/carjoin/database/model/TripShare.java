package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

/**
 * Created by rohit on 31/5/15.
 */
public class TripShare {
    public static final String TABLE_NAME = "Trip_Share";

    public static final String COL_ID = "ID";
    public static final String COL_TRIP_ID = "TRIP_ID";
    public static final String COL_TRIP_SHARE = "TRIP_SHARE";
    public static final String COL_BENEFICIARY = "BENEFICIARY";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_TRIP_ID, COL_TRIP_SHARE, COL_BENEFICIARY, COL_LAST_MODIFIED
    };

    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_TRIP_ID + " TEXT NOT NULL DEFAULT '', "
            + COL_TRIP_SHARE + " REAL NOT NULL DEFAULT -1, "
            + COL_BENEFICIARY + " TEXT NOT NULL DEFAULT '', "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public String tripId = "";
    public double tripShare = -1;
    public String beneficiary = "";
    public long lastModified = -1;

    public TripShare(Cursor cursor) {
        this.id = cursor.getString(0);
        this.tripId = cursor.getString(1);
        this.tripShare = cursor.getDouble(2);
        this.beneficiary = cursor.getString(3);
        this.lastModified = cursor.getLong(15);
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_TRIP_ID, tripId);
        values.put(COL_TRIP_SHARE, tripShare);
        values.put(COL_BENEFICIARY, beneficiary);
        values.put(COL_LAST_MODIFIED, lastModified);

        return values;
    }
}
