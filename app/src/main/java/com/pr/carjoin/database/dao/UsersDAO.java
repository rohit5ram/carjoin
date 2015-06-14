package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.Users;

/**
 * Created by rohit on 13/6/15.
 */
public class UsersDAO {
    private static final String LOG_LABEL = "database.dao.UsersDAO";

    // Making this a singleton instance
    private static UsersDAO singleton;
    private Context context;
    private DatabaseHelper dbHelper;

    private UsersDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public static UsersDAO getInstance(final Context context) {
        if (singleton == null) {
            singleton = new UsersDAO(context.getApplicationContext());
        }
        return singleton;
    }

    public synchronized Cursor getUsersCursor() {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Users.TABLE_NAME,
                    Users.FIELDS,
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized Cursor getUsersById(String id) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Users.TABLE_NAME,
                    Users.FIELDS,
                    Users.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putUsers(final Users users) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (users.id != null) {
                result += db.update(
                        Users.TABLE_NAME,
                        users.getContent(),
                        Users.COL_ID + " IS ? ",
                        new String[]{users.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        Users.TABLE_NAME,
                        null,
                        users.getContent()
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

    public synchronized String getUsersId() {
        String usersId = "";
        Cursor usersCursor = getUsersCursor();
        try {
            if (usersCursor != null && usersCursor.moveToLast()) {
                Users users = new Users(usersCursor);
                usersId = users.id;
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (usersCursor != null) {
                    usersCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return usersId;
    }

    public synchronized Users getUsers() {
        Cursor usersCursor = getUsersCursor();
        Users users = null;
        try {
            if (usersCursor != null && usersCursor.moveToLast()) {
                users = new Users(usersCursor);
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (usersCursor != null) {
                    usersCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return users;
    }

    public synchronized boolean deleteUsers(Users users) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    Users.TABLE_NAME,
                    Users.COL_ID + " IS ? ",
                    new String[]{String.valueOf(users.id)}
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

    public synchronized Cursor getUsers(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    Users.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
