package com.pr.carjoin.customViews;

import com.google.common.base.Function;
import com.google.firebase.database.DataSnapshot;
import com.pr.carjoin.pojos.Trip;

/**
 * Created by rams on 6/25/2017.
 */

public class UpcomingTripsFragment extends MyTripsFragment {

    @Override
    protected Function<? super DataSnapshot, Trip> getFunction() {
        return new Function<DataSnapshot, Trip>() {
            @javax.annotation.Nullable
            @Override
            public Trip apply(@javax.annotation.Nullable DataSnapshot input) {
                Trip trip = input.getValue(Trip.class);
                if (trip != null) trip.id = input.getKey();
                return (trip != null &&
                        trip.members.contains(getCurrentUser().getUid()) &&
                        trip.beginDateTimeMills >= System.currentTimeMillis()) ? trip : null;
            }
        };
    }
}
