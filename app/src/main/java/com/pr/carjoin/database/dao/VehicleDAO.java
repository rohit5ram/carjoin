package com.pr.carjoin.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pr.carjoin.Util;
import com.pr.carjoin.database.DatabaseHelper;
import com.pr.carjoin.database.model.Vehicle;

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
                    Vehicle.TABLE_NAME,
                    Vehicle.FIELDS,
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
                    Vehicle.TABLE_NAME,
                    Vehicle.FIELDS,
                    Vehicle.COL_ID + " IS ? ",
                    new String[]{id},
                    null, null, null, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }

    public synchronized boolean putVehicle(final Vehicle vehicle) {
        boolean success = false;
        int result = 0;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (vehicle.id != null) {
                result += db.update(
                        Vehicle.TABLE_NAME,
                        vehicle.getContent(),
                        Vehicle.COL_ID + " IS ? ",
                        new String[]{vehicle.id}
                );
            }

            if (result > 0) {
                success = true;
            } else {
                final long rowId = db.insert(
                        Vehicle.TABLE_NAME,
                        null,
                        vehicle.getContent()
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
                Vehicle vehicle = new Vehicle(vehicleCursor);
                vehicleId = vehicle.id;
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

    public synchronized Vehicle getVehicle() {
        Cursor vehicleCursor = getVehicleCursor();
        Vehicle vehicle = null;
        try {
            if (vehicleCursor != null && vehicleCursor.moveToLast()) {
                vehicle = new Vehicle(vehicleCursor);
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
        return vehicle;
    }

    public synchronized boolean deleteVehicle(Vehicle vehicle) {
        int result = -1;
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(
                    Vehicle.TABLE_NAME,
                    Vehicle.COL_ID + " IS ? ",
                    new String[]{String.valueOf(vehicle.id)}
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
                    Vehicle.TABLE_NAME,
                    projection,
                    selection, selectionArgs, null, null, sortOrder, null
            );
        } catch (Exception e) {
            Util.logException(e, LOG_LABEL);
        }

        return cursor;
    }
}
