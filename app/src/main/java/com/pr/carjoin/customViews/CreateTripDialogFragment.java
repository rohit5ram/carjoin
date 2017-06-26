package com.pr.carjoin.customViews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.Vehicle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vishnu on 25/3/17.
 */

public class CreateTripDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String LOG_LABEL = "customViews.CreateTripDialogFragment";
    private TextView startTime, endTime, startDate, endDate;
    private EditText vehicleName, vehicleNumber, vehicleColor, fuelPrice,
            maintenancePer, seats, vehicleType, mileage;
    private OnButtonClickListener onButtonClickListener;
    private long startDateTimeInMills = 0, endDateTimeInMills = 0;

    public void registerCallback(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_trip_form, null);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.format(date);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat.format(date);
        startTime = (TextView) rootView.findViewById(R.id.start_time);
        startTime.setOnClickListener(this);
        startTime.setText(timeFormat.format(date));
        startDate = (TextView) rootView.findViewById(R.id.start_date);
        startDate.setText(dateFormat.format(date));
        startDate.setOnClickListener(this);
        endTime = (TextView) rootView.findViewById(R.id.end_time);
        endTime.setOnClickListener(this);
        endTime.setText(timeFormat.format(date));
        endDate = (TextView) rootView.findViewById(R.id.end_date);
        endDate.setText(dateFormat.format(date));
        endDate.setOnClickListener(this);
        vehicleName = (EditText) rootView.findViewById(R.id.vehicle_name);
        vehicleNumber = (EditText) rootView.findViewById(R.id.vehicle_number);
        vehicleColor = (EditText) rootView.findViewById(R.id.vehicle_color);
        fuelPrice = (EditText) rootView.findViewById(R.id.fuel_price);
        maintenancePer = (EditText) rootView.findViewById(R.id.maintenance_per);
        mileage = (EditText) rootView.findViewById(R.id.mileage);
        seats = (EditText) rootView.findViewById(R.id.seats_available);
        vehicleType = (EditText) rootView.findViewById(R.id.vehicle_type);

        Button createButton = (Button) rootView.findViewById(R.id.create_action);
        createButton.setOnClickListener(this);
        Button cancelButton = (Button) rootView.findViewById(R.id.cancel_action);
        cancelButton.setOnClickListener(this);
        return rootView;
    }

    private boolean validateDetails() {
        try {
            //startDateTime
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            String selectedDateTime = String.valueOf(startDate.getText()).concat(" ")
                    .concat(String.valueOf(startTime.getText()));
            calendar.setTime(format.parse(selectedDateTime));
            startDateTimeInMills = calendar.getTimeInMillis();

            //endDateTime
            selectedDateTime = String.valueOf(endDate.getText()).concat(" ")
                    .concat(String.valueOf(endTime.getText()));
            calendar.setTime(format.parse(selectedDateTime));
            endDateTimeInMills = calendar.getTimeInMillis();

            if (endDateTimeInMills <= startDateTimeInMills) {
                Toast.makeText(getActivity(), "EndTime should be greater than StartTime", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            Log.e(Util.TAG, LOG_LABEL + e.getMessage());
            return false;
        }
        if (TextUtils.isEmpty(vehicleName.getText())) {
            vehicleName.setError("Shouldn't be Empty");
            return false;
        }
        if (TextUtils.isEmpty(vehicleNumber.getText())) {
            vehicleNumber.setError("Shouldn't be Empty");
            return false;
        }
        if (TextUtils.isEmpty(vehicleColor.getText())) {
            vehicleColor.setError("Shouldn't be Empty");
            return false;
        }
        if (TextUtils.isEmpty(fuelPrice.getText())) {
            fuelPrice.setError("Shouldn't be Empty");
            return false;
        }
        if (TextUtils.isEmpty(maintenancePer.getText())) {
            maintenancePer.setError("Shouldn't be Empty");
            return false;
        }
        if (TextUtils.isEmpty(seats.getText())) {
            seats.setError("Shouldn't be Empty");
            return false;
        }
        if (TextUtils.isEmpty(vehicleType.getText())) {
            vehicleType.setError("Shouldn't be Empty");
            return false;
        }
        return true;
    }

    private Trip getTripDetails() {
        Trip trip = new Trip();
        trip.beginDateTimeMills = startDateTimeInMills;
        trip.endDateTimeMills = endDateTimeInMills;
        trip.fuelPricePerLitre = Double.parseDouble(fuelPrice.getText().toString());
        trip.maintenancePercentage = Double.parseDouble(maintenancePer.getText().toString());
        trip.vehicleRegId = vehicleNumber.getText().toString().toUpperCase();
        trip.seatsAvailable = Integer.parseInt(seats.getText().toString());
        return trip;
    }

    private Vehicle getVehicleDetails() {
        Vehicle vehicle = new Vehicle();
        vehicle.color = vehicleColor.getText().toString();
        vehicle.name = vehicleName.getText().toString();
        vehicle.type = vehicleType.getText().toString();
        vehicle.owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
        vehicle.mileage = Double.parseDouble(mileage.getText().toString());
        return vehicle;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.create_action:
                    if (onButtonClickListener != null && validateDetails()) {
                        onButtonClickListener.onPositiveButtonClick(getTripDetails(), getVehicleDetails());
                        dismiss();
                    }
                    break;
                case R.id.cancel_action:
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onNegativeButtonClick();
                    }
                    dismiss();
                    break;
                case R.id.start_time:
                case R.id.end_time:
                    new TimePickerFragment().setTextView((TextView) v).show(getActivity().getSupportFragmentManager(), LOG_LABEL);
                    break;
                case R.id.start_date:
                case R.id.end_date:
                    new DatePickerFragment().setTextView((TextView) v).show(getActivity().getSupportFragmentManager(), LOG_LABEL);
                    break;
            }
        } catch (ClassCastException ignored) {

        }
    }

    public interface OnButtonClickListener {
        void onPositiveButtonClick(Trip trip, Vehicle vehicle);

        void onNegativeButtonClick();
    }
}
