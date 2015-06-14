package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.Fuel;

/**
 * Created by rohit on 13/6/15.
 */
public class FuelDAO {
    private static final String LOG_LABEL = "database.dao.FuelDAO";

    // Making this a singleton instance
    private static FuelDAO singleton;
    private Context context;
    private DatabaseHelper dbHelper;

    private FuelDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public static FuelDAO getInstance(final Context context) {
        if (singleton == null) {
            singleton = new FuelDAO(context.getApplicationContext());
        }
        return singleton;
    }

    public synchronized Cursor getFuelCursor() {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Fuel.TABLE_NAME,
                    Fuel.FIELDS,
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized Cursor getFuelById(String id) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Fuel.TABLE_NAME,
                    Fuel.FIELDS,
                    Fuel.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putFuel(final Fuel fuel) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (fuel.id != null) {
                result += db.update(
                        Fuel.TABLE_NAME,
                        fuel.getContent(),
                        Fuel.COL_ID + " IS ? ",
                        new String[]{fuel.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        Fuel.TABLE_NAME,
                        null,
                        fuel.getContent()
                );

                if (rowId > -1) {
                    success = true;
                }
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return success;
    }

    public synchronized String getFuelId() {
        String fuelId = "";
        Cursor fuelCursor = getFuelCursor();
        try {
            if (fuelCursor != null && fuelCursor.moveToLast()) {
                Fuel fuel = new Fuel(fuelCursor);
                fuelId = fuel.id;
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (fuelCursor != null) {
                    fuelCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return fuelId;
    }

    public synchronized Fuel getFuel() {
        Cursor fuelCursor = getFuelCursor();
        Fuel fuel = null;
        try {
            if (fuelCursor != null && fuelCursor.moveToLast()) {
                fuel = new Fuel(fuelCursor);
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (fuelCursor != null) {
                    fuelCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return fuel;
    }

    public synchronized boolean deleteFuel(Fuel fuel) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    Fuel.TABLE_NAME,
                    Fuel.COL_ID + " IS ? ",
                    new String[]{String.valueOf(fuel.id)}
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        boolean success = false;
        if (result > 0) {
            success = true;
        }
        return success;
    }

    public synchronized Cursor getFuel(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Fuel.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
