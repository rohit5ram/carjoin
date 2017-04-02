package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.TripsDBModel;

/**
 * Created by rohit on 13/6/15.
 */
public class TripsDAO {
    private static final String LOG_LABEL = "database.dao.TripsDAO";

    // Making this a singleton instance
    private static TripsDAO singleton;
    private Context context;
    private DatabaseHelper dbHelper;

    private TripsDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public static TripsDAO getInstance(final Context context) {
        if (singleton == null) {
            singleton = new TripsDAO(context.getApplicationContext());
        }
        return singleton;
    }

    public synchronized Cursor getTripsCursor() {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    TripsDBModel.TABLE_NAME,
                    TripsDBModel.FIELDS,
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized Cursor getTripsById(String id) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    TripsDBModel.TABLE_NAME,
                    TripsDBModel.FIELDS,
                    TripsDBModel.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putTrips(final TripsDBModel tripsDBModel) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (tripsDBModel.id != null) {
                result += db.update(
                        TripsDBModel.TABLE_NAME,
                        tripsDBModel.getContent(),
                        TripsDBModel.COL_ID + " IS ? ",
                        new String[]{tripsDBModel.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        TripsDBModel.TABLE_NAME,
                        null,
                        tripsDBModel.getContent()
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

    public synchronized String getTripsId() {
        String tripsId = "";
        Cursor tripsCursor = getTripsCursor();
        try {
            if (tripsCursor != null && tripsCursor.moveToLast()) {
                TripsDBModel tripsDBModel = new TripsDBModel(tripsCursor);
                tripsId = tripsDBModel.id;
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (tripsCursor != null) {
                    tripsCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return tripsId;
    }

    public synchronized TripsDBModel getTrips() {
        Cursor tripsCursor = getTripsCursor();
        TripsDBModel tripsDBModel = null;
        try {
            if (tripsCursor != null && tripsCursor.moveToLast()) {
                tripsDBModel = new TripsDBModel(tripsCursor);
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (tripsCursor != null) {
                    tripsCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return tripsDBModel;
    }

    public synchronized boolean deleteTrips(TripsDBModel tripsDBModel) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    TripsDBModel.TABLE_NAME,
                    TripsDBModel.COL_ID + " IS ? ",
                    new String[]{String.valueOf(tripsDBModel.id)}
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

    public synchronized Cursor getTrips(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    TripsDBModel.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
