package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.TripShare;

/**
 * Created by rohit on 13/6/15.
 */
public class TripShareDAO {
    private static final String LOG_LABEL = "database.dao.TripShareDAO";

    // Making this a singleton instance
    private static TripShareDAO singleton;
    private Context context;
    private DatabaseHelper dbHelper;

    private TripShareDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public static TripShareDAO getInstance(final Context context) {
        if (singleton == null) {
            singleton = new TripShareDAO(context.getApplicationContext());
        }
        return singleton;
    }

    public synchronized Cursor getTripShareCursor() {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    TripShare.TABLE_NAME,
                    TripShare.FIELDS,
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized Cursor getTripShareById(String id) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    TripShare.TABLE_NAME,
                    TripShare.FIELDS,
                    TripShare.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putTripShare(final TripShare tripShare) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (tripShare.id != null) {
                result += db.update(
                        TripShare.TABLE_NAME,
                        tripShare.getContent(),
                        TripShare.COL_ID + " IS ? ",
                        new String[]{tripShare.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        TripShare.TABLE_NAME,
                        null,
                        tripShare.getContent()
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

    public synchronized String getTripShareId() {
        String tripShareId = "";
        Cursor tripShareCursor = getTripShareCursor();
        try {
            if (tripShareCursor != null && tripShareCursor.moveToLast()) {
                TripShare tripShare = new TripShare(tripShareCursor);
                tripShareId = tripShare.id;
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (tripShareCursor != null) {
                    tripShareCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return tripShareId;
    }

    public synchronized TripShare getTripShare() {
        Cursor tripShareCursor = getTripShareCursor();
        TripShare tripShare = null;
        try {
            if (tripShareCursor != null && tripShareCursor.moveToLast()) {
                tripShare = new TripShare(tripShareCursor);
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (tripShareCursor != null) {
                    tripShareCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return tripShare;
    }

    public synchronized boolean deleteTripShare(TripShare tripShare) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    TripShare.TABLE_NAME,
                    TripShare.COL_ID + " IS ? ",
                    new String[]{String.valueOf(tripShare.id)}
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

    public synchronized Cursor getTripShare(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    TripShare.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
