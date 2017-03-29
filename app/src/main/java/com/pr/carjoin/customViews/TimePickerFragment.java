package com.pr.carjoin.customViews;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by vishnu on 26/3/17.
 */

public class TimePickerFragment extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {
    private static final String LOG_LABEL = "customViews.TimePickerFragment";
    private TextView textView;

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        if (textView != null) {
            textView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime()));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    public TimePickerFragment setTextView(TextView textView) {
        this.textView = textView;
        return this;
    }
}
