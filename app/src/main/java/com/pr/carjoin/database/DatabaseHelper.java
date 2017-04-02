package com.pr.carjoin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.database.model.BalanceSheet;
import com.pr.carjoin.database.model.Commuters;
import com.pr.carjoin.database.model.Fuel;
import com.pr.carjoin.database.model.Ledger;
import com.pr.carjoin.database.model.TripShare;
import com.pr.carjoin.database.model.TripsDBModel;
import com.pr.carjoin.database.model.UsersDBModel;
import com.pr.carjoin.database.model.VehicleDBModel;

/**
 * Created by rohit on 31/5/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_LABEL = "database.DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "poolDB";
    private static DatabaseHelper singleton;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (context.getResources().getBoolean(R.bool.developer_mode)) {
            Log.w(Util.TAG, LOG_LABEL + " :: [[[[ Recreating the database in dev mode ]]]] :  ");
            this.truncateAllTables();
        }
    }

    public static DatabaseHelper getInstance(final Context context) {
        if (singleton == null) {
            singleton = new DatabaseHelper(context.getApplicationContext());
        }
        return singleton;
    }

    @Override
    public synchronized void close() {
        super.close();
        Log.w(Util.TAG, LOG_LABEL + " Database is closed");
    }

    private boolean truncateAllTables() {
        Log.w(Util.TAG, LOG_LABEL + " Truncating all the tables in the database");
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean result = true;

        try {
            sqLiteDatabase.execSQL("DELETE FROM " + BalanceSheet.TABLE_NAME);
            sqLiteDatabase.execSQL("DELETE FROM " + Commuters.TABLE_NAME);
            sqLiteDatabase.execSQL("DELETE FROM " + Fuel.TABLE_NAME);
            sqLiteDatabase.execSQL("DELETE FROM " + Ledger.TABLE_NAME);
            sqLiteDatabase.execSQL("DELETE FROM " + TripsDBModel.TABLE_NAME);
            sqLiteDatabase.execSQL("DELETE FROM " + TripShare.TABLE_NAME);
            sqLiteDatabase.execSQL("DELETE FROM " + UsersDBModel.TABLE_NAME);
            sqLiteDatabase.execSQL("DELETE FROM " + VehicleDBModel.TABLE_NAME);
            sqLiteDatabase.execSQL("VACUUM");
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
            result = false;
        }
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.w(Util.TAG, LOG_LABEL + " Creating database, Existing contents will be wiped out !");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BalanceSheet.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Commuters.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Fuel.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Ledger.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TripsDBModel.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TripShare.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UsersDBModel.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VehicleDBModel.TABLE_NAME);

        sqLiteDatabase.execSQL(BalanceSheet.CREATE_TABLE);
        sqLiteDatabase.execSQL(Commuters.CREATE_TABLE);
        sqLiteDatabase.execSQL(Fuel.CREATE_TABLE);
        sqLiteDatabase.execSQL(Ledger.CREATE_TABLE);
        sqLiteDatabase.execSQL(TripsDBModel.CREATE_TABLE);
        sqLiteDatabase.execSQL(TripShare.CREATE_TABLE);
        sqLiteDatabase.execSQL(UsersDBModel.CREATE_TABLE);
        sqLiteDatabase.execSQL(VehicleDBModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(Util.TAG, LOG_LABEL
                + " Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");

        // Upgrading all the tables, by deleting and recreating the database tables
        this.upgradeDatabase(sqLiteDatabase);
    }

    private boolean upgradeDatabase(SQLiteDatabase sqLiteDatabase) {
        Log.w(Util.TAG, LOG_LABEL + " Upgrading database. Existing contents will be wiped out !");
        boolean result = false;
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BalanceSheet.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Commuters.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Fuel.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Ledger.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TripsDBModel.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TripShare.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UsersDBModel.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VehicleDBModel.TABLE_NAME);

            sqLiteDatabase.execSQL(BalanceSheet.CREATE_TABLE);
            sqLiteDatabase.execSQL(Commuters.CREATE_TABLE);
            sqLiteDatabase.execSQL(Fuel.CREATE_TABLE);
            sqLiteDatabase.execSQL(Ledger.CREATE_TABLE);
            sqLiteDatabase.execSQL(TripsDBModel.CREATE_TABLE);
            sqLiteDatabase.execSQL(TripShare.CREATE_TABLE);
            sqLiteDatabase.execSQL(UsersDBModel.CREATE_TABLE);
            sqLiteDatabase.execSQL(VehicleDBModel.CREATE_TABLE);
            result = true;
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }
        return result;
    }
}
