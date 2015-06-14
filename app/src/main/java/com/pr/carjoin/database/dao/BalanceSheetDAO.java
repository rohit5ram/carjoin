package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.BalanceSheet;

/**
 * Created by rohit on 13/6/15.
 */
public class BalanceSheetDAO {
    private static final String LOG_LABEL = "database.dao.BalanceSheetDAO";

    // Making this a singleton instance
    private static BalanceSheetDAO singleton;
    private Context context;
    private DatabaseHelper dbHelper;

    private BalanceSheetDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public static BalanceSheetDAO getInstance(final Context context) {
        if (singleton == null) {
            singleton = new BalanceSheetDAO(context.getApplicationContext());
        }
        return singleton;
    }

    public synchronized Cursor getBalanceSheetCursor() {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    BalanceSheet.TABLE_NAME,
                    BalanceSheet.FIELDS,
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized Cursor getBalanceSheetById(String id) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    BalanceSheet.TABLE_NAME,
                    BalanceSheet.FIELDS,
                    BalanceSheet.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putBalanceSheet(final BalanceSheet balanceSheet) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (balanceSheet.id != null) {
                result += db.update(
                        BalanceSheet.TABLE_NAME,
                        balanceSheet.getContent(),
                        BalanceSheet.COL_ID + " IS ? ",
                        new String[]{balanceSheet.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        BalanceSheet.TABLE_NAME,
                        null,
                        balanceSheet.getContent()
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

    public synchronized String getBalanceSheetId() {
        String balanceSheetId = "";
        Cursor balanceSheetCursor = getBalanceSheetCursor();
        try {
            if (balanceSheetCursor != null && balanceSheetCursor.moveToLast()) {
                BalanceSheet balanceSheet = new BalanceSheet(balanceSheetCursor);
                balanceSheetId = balanceSheet.id;
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (balanceSheetCursor != null) {
                    balanceSheetCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return balanceSheetId;
    }

    public synchronized BalanceSheet getBalanceSheet() {
        Cursor balanceSheetCursor = getBalanceSheetCursor();
        BalanceSheet balanceSheet = null;
        try {
            if (balanceSheetCursor != null && balanceSheetCursor.moveToLast()) {
                balanceSheet = new BalanceSheet(balanceSheetCursor);
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (balanceSheetCursor != null) {
                    balanceSheetCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return balanceSheet;
    }

    public synchronized boolean deleteBalanceSheet(BalanceSheet balanceSheet) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    BalanceSheet.TABLE_NAME,
                    BalanceSheet.COL_ID + " IS ? ",
                    new String[]{String.valueOf(balanceSheet.id)}
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

    public synchronized Cursor getBalanceSheet(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    BalanceSheet.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
