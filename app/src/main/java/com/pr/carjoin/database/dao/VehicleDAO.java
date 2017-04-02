package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.VehicleDBModel;

/**
 * Created by rohit on 14/6/15.
 */
public class VehicleDAO {
    private static final String LOG_LABEL = "database.dao.VehicleDAO";

    // Making this a singleton instance
    private static VehicleDAO singleton;
    private Context context;
    private DatabaseHelper dbHelper;

    private VehicleDAO(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public static VehicleDAO getInstance(final Context context) {
        if (singleton == null) {
            singleton = new VehicleDAO(context.getApplicationContext());
        }
        return singleton;
    }

    public synchronized Cursor getVehicleCursor() {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    VehicleDBModel.TABLE_NAME,
                    VehicleDBModel.FIELDS,
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized Cursor getVehicleById(String id) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    VehicleDBModel.TABLE_NAME,
                    VehicleDBModel.FIELDS,
                    VehicleDBModel.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putVehicle(final VehicleDBModel vehicleDBModel) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (vehicleDBModel.id != null) {
                result += db.update(
                        VehicleDBModel.TABLE_NAME,
                        vehicleDBModel.getContent(),
                        VehicleDBModel.COL_ID + " IS ? ",
                        new String[]{vehicleDBModel.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        VehicleDBModel.TABLE_NAME,
                        null,
                        vehicleDBModel.getContent()
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

    public synchronized String getVehicleId() {
        String vehicleId = "";
        Cursor vehicleCursor = getVehicleCursor();
        try {
            if (vehicleCursor != null && vehicleCursor.moveToLast()) {
                VehicleDBModel vehicleDBModel = new VehicleDBModel(vehicleCursor);
                vehicleId = vehicleDBModel.id;
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (vehicleCursor != null) {
                    vehicleCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return vehicleId;
    }

    public synchronized VehicleDBModel getVehicle() {
        Cursor vehicleCursor = getVehicleCursor();
        VehicleDBModel vehicleDBModel = null;
        try {
            if (vehicleCursor != null && vehicleCursor.moveToLast()) {
                vehicleDBModel = new VehicleDBModel(vehicleCursor);
            }
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        } finally {
            try {
                if (vehicleCursor != null) {
                    vehicleCursor.close();
                }
            } catch (Exception e) {
                Util.logException(e, LOG_LABEL);
            }
        }
        return vehicleDBModel;
    }

    public synchronized boolean deleteVehicle(VehicleDBModel vehicleDBModel) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    VehicleDBModel.TABLE_NAME,
                    VehicleDBModel.COL_ID + " IS ? ",
                    new String[]{String.valueOf(vehicleDBModel.id)}
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

    public synchronized Cursor getVehicle(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    VehicleDBModel.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
