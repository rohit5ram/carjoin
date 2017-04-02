package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.ContextThemeWrapper;

import java.util.UUID;

/**
 * Created by rohit on 13/6/15.
 */
public class VehicleDBModel {
    public static final String TABLE_NAME = "VehicleDBModel";

    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_MANUFACTURER = "MANUFACTURER";
    public static final String COL_YEAR_MODEL = "YEAR_MODEL";
    public static final String COL_LICENSE = "LICENSE";
    public static final String COL_FUEL_CAPACITY = "FUEL_CAPACITY";
    public static final String COL_REG_NO = "REG_NO";
    public static final String COL_MILEAGE = "MILEAGE";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_NAME, COL_MANUFACTURER, COL_YEAR_MODEL, COL_LICENSE, COL_FUEL_CAPACITY,
            COL_REG_NO, COL_MILEAGE, COL_LAST_MODIFIED
    };

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_MANUFACTURER + " TEXT NOT NULL DEFAULT '', "
            + COL_YEAR_MODEL + " INT NOT NULL DEFAULT -1, "
            + COL_LICENSE + " TEXT NOT NULL DEFAULT '', "
            + COL_FUEL_CAPACITY + " REAL NOT NULL DEFAULT -1, "
            + COL_REG_NO + " TEXT NOT NULL DEFAULT '', "
            + COL_MILEAGE + " REAL NOT NULL DEFAULT -1, "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public String name = "";
    public String manufacturer = "";
    public int yearModel = -1;
    public String license = "";
    public double fuelCapacity = -1;
    public String regNo = "";
    public double mileage = -1;
    public long lastModified = -1;

    public VehicleDBModel(Cursor cursor) {
        this.id = cursor.getString(0);
        this.name = cursor.getString(1);
        this.manufacturer = cursor.getString(2);
        this.yearModel = cursor.getInt(3);
        this.license = cursor.getString(4);
        this.fuelCapacity = cursor.getDouble(5);
        this.regNo = cursor.getString(6);
        this.mileage = cursor.getDouble(7);
        this.lastModified = cursor.getLong(8);
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_NAME, name);
        values.put(COL_MANUFACTURER, manufacturer);
        values.put(COL_YEAR_MODEL, yearModel);
        values.put(COL_LICENSE, license);
        values.put(COL_FUEL_CAPACITY, fuelCapacity);
        values.put(COL_REG_NO, regNo);
        values.put(COL_MILEAGE, mileage);

        return values;
    }
}
