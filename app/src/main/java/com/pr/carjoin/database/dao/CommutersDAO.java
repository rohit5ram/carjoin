package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.Commuters;

/**
 * Created by rohit on 13/6/15.
 */
public class CommutersDAO {
    private static final String LOG_LABEL = "database.dao.CommutersDAO";

    // Making this a singleton instance
    private static CommutersDAO singleton;
    private Context context;
    private DatabaseHelper dbHelper;

    private CommutersDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public static CommutersDAO getInstance(final Context context) {
        if (singleton == null) {
            singleton = new CommutersDAO(context.getApplicationContext());
        }
        return singleton;
    }

    public synchronized Cursor getCommutersCursor() {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Commuters.TABLE_NAME,
                    Commuters.FIELDS,
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized Cursor getCommutersById(String id) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Commuters.TABLE_NAME,
                    Commuters.FIELDS,
                    Commuters.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putCommuters(final Commuters commuters) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (commuters.id != null) {
                result += db.update(
                        Commuters.TABLE_NAME,
                        commuters.getContent(),
                        Commuters.COL_ID + " IS ? ",
                        new String[]{commuters.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        Commuters.TABLE_NAME,
                        null,
                        commuters.getContent()
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

    public synchronized String getCommutersId() {
        String commutersId = "";
        Cursor commutersCursor = getCommutersCursor();
        try {
            if (commutersCursor != null && commutersCursor.moveToLast()) {
                Commuters commuters = new Commuters(commutersCursor);
                commutersId = commuters.id;
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (commutersCursor != null) {
                    commutersCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return commutersId;
    }

    public synchronized Commuters getCommuters() {
        Cursor commutersCursor = getCommutersCursor();
        Commuters commuters = null;
        try {
            if (commutersCursor != null && commutersCursor.moveToLast()) {
                commuters = new Commuters(commutersCursor);
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (commutersCursor != null) {
                    commutersCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return commuters;
    }

    public synchronized boolean deleteCommuters(Commuters commuters) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    Commuters.TABLE_NAME,
                    Commuters.COL_ID + " IS ? ",
                    new String[]{String.valueOf(commuters.id)}
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

    public synchronized Cursor getCommuters(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Commuters.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
