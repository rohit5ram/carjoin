package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.method.DateTimeKeyListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by rohit on 13/6/15.
 */
public class Fuel {
    public static final String TABLE_NAME = "Fuel";

    public static final String COL_ID = "ID";
    public static final String COL_FUEL_NAME = "FUEL_NAME";
    public static final String COL_LITRE_PRICE = "PRICE_LT";
    public static final String COL_DATE_TIME = "FB_ID";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_FUEL_NAME, COL_LITRE_PRICE, COL_DATE_TIME, COL_LAST_MODIFIED
    };

    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_FUEL_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_LITRE_PRICE + " REAL NOT NULL DEFAULT -1, "
            + COL_DATE_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public String fuelName = "";
    public double litrePrice = -1;
    public Date dateTime = new Date();
    public long lastModified = -1;

    public Fuel(Cursor cursor) throws ParseException {
        this.id = cursor.getString(0);
        this.fuelName = cursor.getString(1);
        this.litrePrice = cursor.getDouble(2);
        this.dateTime = DateFormat.getInstance().parse(cursor.getString(3));
        this.lastModified = cursor.getLong(15);
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_FUEL_NAME, fuelName);
        values.put(COL_LITRE_PRICE, litrePrice);
        values.put(COL_DATE_TIME, dateTime.toString());
        values.put(COL_LAST_MODIFIED, lastModified);

        return values;
    }


}
