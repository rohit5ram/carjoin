package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

/**
 * Created by rohit on 31/5/15.
 */
public class BalanceSheet {
    public static final String TABLE_NAME = "Balance_Sheet";

    public static final String COL_ID = "ID";
    public static final String COL_NAME = "TRIP_ID";
    public static final String COL_NET_BILL = "USER_ID";
    public static final String COL_TOT_PAID = "USER_NAME";
    public static final String COL_BAL = "ROLE";
    public static final String COL_TOT_ONWARD_TRIPS = "ROLE";
    public static final String COL_TOT_RETURN_TRIPS = "ROLE";
    public static final String COL_AVG_TRIP_COST = "ROLE";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_NAME, COL_NET_BILL, COL_TOT_PAID, COL_BAL, COL_TOT_ONWARD_TRIPS,
            COL_TOT_RETURN_TRIPS, COL_AVG_TRIP_COST, COL_LAST_MODIFIED
    };

    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_NET_BILL + " REAL NOT NULL DEFAULT -1, "
            + COL_TOT_PAID + " REAL NOT NULL DEFAULT -1, "
            + COL_BAL + " REAL NOT NULL DEFAULT -1, "
            + COL_TOT_ONWARD_TRIPS + " INTEGER NOT NULL DEFAULT -1, "
            + COL_TOT_RETURN_TRIPS + " INTEGER NOT NULL DEFAULT -1, "
            + COL_AVG_TRIP_COST + " REAL NOT NULL DEFAULT -1, "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public String name = "";
    public long netBill = -1;
    public double totPaid = -1;
    public double bal = -1;
    public int totOnwardTrips = -1;
    public int totReturnTrips = -1;
    public double avgTripCost = -1;
    public long lastModified = -1;

    public BalanceSheet(Cursor cursor) {
        this.id = cursor.getString(0);
        this.name = cursor.getString(1);
        this.netBill = cursor.getLong(2);
        this.totPaid = cursor.getDouble(3);
        this.bal = cursor.getDouble(4);
        this.totOnwardTrips = cursor.getInt(5);
        this.totReturnTrips = cursor.getInt(6);
        this.avgTripCost = cursor.getDouble(7);
        this.lastModified = cursor.getLong(8);
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_NAME, name);
        values.put(COL_NET_BILL, netBill);
        values.put(COL_TOT_PAID, totPaid);
        values.put(COL_BAL, bal);
        values.put(COL_TOT_ONWARD_TRIPS, totOnwardTrips);
        values.put(COL_TOT_RETURN_TRIPS, totReturnTrips);
        values.put(COL_AVG_TRIP_COST, avgTripCost);

        return values;
    }
}
