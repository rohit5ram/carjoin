package com.pr.carjoin.database.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by rohit on 31/5/15.
 */
public class Ledger {
    public static final String TABLE_NAME = "Ledger";

    public static final String COL_ID = "ID";
    public static final String COL_TRANSACTION_DATE_TIME = "TRANSACTION_DATE";
    public static final String COL_USER_ID = "USER_ID";
    public static final String COL_USER_NAME = "USER_NAME";
    public static final String COL_AMOUNT = "AMOUNT";
    public static final String COL_MODE_OF_PAYMENT = "MODE_OF_PAYMENT";
    public static final String COLL_COLLECTION_POINT = "COLLECTION_POINT";
    public static final String COL_REMARKS = "REMARKS";
    public static final String COL_LAST_MODIFIED = "LAST_MODIFIED";

    public static final String[] FIELDS = {
            COL_ID, COL_TRANSACTION_DATE_TIME, COL_USER_ID, COL_USER_NAME, COL_AMOUNT,
            COL_MODE_OF_PAYMENT, COLL_COLLECTION_POINT, COL_REMARKS, COL_LAST_MODIFIED
    };

    public static final java.lang.String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " TEXT PRIMARY KEY, "
            + COL_TRANSACTION_DATE_TIME + " TEXT NOT NULL DEFAULT '', "
            + COL_USER_ID + " TEXT NOT NULL DEFAULT '', "
            + COL_USER_NAME + " TEXT NOT NULL DEFAULT '', "
            + COL_AMOUNT + " REAL NOT NULL DEFAULT -1, "
            + COL_MODE_OF_PAYMENT + " TEXT NOT NULL DEFAULT '', "
            + COLL_COLLECTION_POINT + " TEXT NOT NULL DEFAULT '', "
            + COL_REMARKS + " TEXT NOT NULL DEFAULT '', "
            + COL_LAST_MODIFIED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    public String id = UUID.randomUUID().toString();
    public Date transactionDateTime = new Date();
    public String userId = "";
    public String userName = "";
    public long amount = -1;
    public String modeOfPayment = "";
    public String collectionPoint = "";
    public String remarks = "";
    public long lastModified = -1;

    public Ledger(Cursor cursor) throws ParseException {
        this.id = cursor.getString(0);
        this.transactionDateTime = DateFormat.getInstance().parse(cursor.getString(1));
        this.userId = cursor.getString(2);
        this.userName = cursor.getString(3);
        this.amount = cursor.getLong(4);
        this.modeOfPayment = cursor.getString(5);
        this.collectionPoint = cursor.getString(6);
        this.remarks = cursor.getString(7);
        this.lastModified = Timestamp.valueOf(cursor.getString(8)).getTime();
    }

    public ContentValues getContent() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_TRANSACTION_DATE_TIME, transactionDateTime.toString());
        values.put(COL_USER_ID, userId);
        values.put(COL_USER_NAME, userName);
        values.put(COL_AMOUNT, amount);
        values.put(COL_MODE_OF_PAYMENT, modeOfPayment);
        values.put(COLL_COLLECTION_POINT, collectionPoint);
        values.put(COL_REMARKS, remarks);
        return values;
    }
}
