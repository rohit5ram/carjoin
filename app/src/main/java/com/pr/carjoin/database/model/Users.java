package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.UUID;

/**
 * Created by rohit on 31/5/15.
 */
public class Users {
    public static final String TABLE_NAME = "Users";

    public static final String COL_ID = "ID";
    public static final String COL_FIRST_NAME = "FIRST_NAME";
    public static final String COL_LAST_NAME = "LAST_NAME";
    public static final String COL_FB_ID = "FB_ID";
    public static final String COL_GOOGLE_ID = "GOOGLE_ID";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_MOBILE = "MOBILE";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_FIRST_NAME, COL_LAST_NAME, COL_FB_ID, COL_GOOGLE_ID,
            COL_EMAIL, COL_MOBILE, COL_LAST_MODIFIED
    };

    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_FIRST_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_LAST_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_FB_ID + " TEXT NOT NULL DEFAULT '', "
            + COL_GOOGLE_ID + " TEXT NOT NULL DEFAULT '', "
            + COL_EMAIL + " TEXT NOT NULL DEFAULT '', "
            + COL_MOBILE + " TEXT NOT NULL DEFAULT '', "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public String firstName = "";
    public String lastName = "";
    public String fbId = "";
    public String googleId = "";
    public String email = "";
    public int mobile = -1;
    public long lastModified = -1;

    public Users(Cursor cursor) {
        this.id = cursor.getString(0);
        this.firstName = cursor.getString(1);
        this.lastName = cursor.getString(2);
        this.fbId = cursor.getString(3);
        this.googleId = cursor.getString(4);
        this.email = cursor.getString(5);
        this.mobile = cursor.getInt(6);
        this.lastModified = cursor.getLong(15);
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_FIRST_NAME, firstName);
        values.put(COL_LAST_MODIFIED, lastName);
        values.put(COL_FB_ID, fbId);
        values.put(COL_GOOGLE_ID, googleId);
        values.put(COL_EMAIL, email);
        values.put(COL_MOBILE, mobile);

        return values;
    }
}
