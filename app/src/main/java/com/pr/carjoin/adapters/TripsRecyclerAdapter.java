package com.pr.carjoin.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pr.carjoin.Constants;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.TripQueue;
import com.pr.carjoin.pojos.TripQueueMap;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by vishnu on 29/3/17.
 */

public class TripsRecyclerAdapter extends RecyclerView.Adapter<TripsRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    private static final String LOG_LABEL = "adapters.TripsRecyclerAdapter";
    private final HashMap<Trip, TripQueueMap> tripTripQueueMapHashMap;
    private final ArrayList<Trip> trips;
    private static final String DATE_TIME_FORMAT = "dd:MM:yy HH:mm";
    private final WeakReference<Activity> activityWeakReference;

    public TripsRecyclerAdapter(Activity activity, HashMap<Trip, TripQueueMap> tripTripQueueMapHashMap) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.tripTripQueueMapHashMap = tripTripQueueMapHashMap;
        this.trips = new ArrayList<>(tripTripQueueMapHashMap.keySet());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_trip_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trip trip = trips.get(holder.getAdapterPosition());
        TripQueueMap tripQueueMap = tripTripQueueMapHashMap.get(trip);
        final Context context = holder.getImageView().getContext();
        Glide.with(holder.getImageView().getContext()).load(trip.owner.photoURL).into(holder.getImageView());
        holder.getUserName().setText(trip.owner.name);
        holder.getUserEmail().setText(trip.owner.email);
        holder.getStartDate().append(" : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.beginDateTimeMills)));
        holder.getEndDate().append("   : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.endDateTimeMills)));

        TripQueue tripQueue = checkIfUserAlreadyExistsInTripQueue(tripQueueMap);
        if (tripQueue != null) {
            switch (tripQueue.status) {
                case Constants.PENDING:
                    holder.getRequest().setText(context.getString(R.string.string_pending));
                    break;
                case Constants.ACCEPTED:
                    holder.getRequest().setText(context.getString(R.string.string_accepted));
                    break;
                case Constants.REJECTED:
                    holder.getRequest().setText(context.getString(R.string.string_rejected));
                    break;
            }
        } else {
            holder.getRequest().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.setOnClickListener(null);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Util.TRIP_QUEUE)
                            .child(trip.id).child(firebaseUser.getUid());
                    TripQueue newTripQueue = new TripQueue();
                    newTripQueue.name = firebaseUser.getDisplayName();
                    newTripQueue.status = Constants.PENDING;
                    newTripQueue.type = Constants.COMMUTER;
                    databaseReference.setValue(newTripQueue).addOnCompleteListener(activityWeakReference.get(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ((Button) v).setText(context.getString(R.string.string_pending));
                            }
                        }
                    });
                }
            });
        }
    }

    private TripQueue checkIfUserAlreadyExistsInTripQueue(HashMap<String, TripQueue> tripQueueHashMap) {
        if (tripQueueHashMap != null && tripQueueHashMap.size() > 0) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (tripQueueHashMap.keySet().contains(userId)) {
                return tripQueueHashMap.get(userId);
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private Button request;
        private TextView userName, startDate, endDate, userEmail;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (CircleImageView) itemView.findViewById(R.id.user_photo);
            request = (Button) itemView.findViewById(R.id.status);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            startDate = (TextView) itemView.findViewById(R.id.start_date_text_view);
            endDate = (TextView) itemView.findViewById(R.id.end_date_text_view);
            userEmail = (TextView) itemView.findViewById(R.id.user_email);
        }

        public CircleImageView getImageView() {
            return imageView;
        }

        public Button getRequest() {
            return request;
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getStartDate() {
            return startDate;
        }

        public TextView getEndDate() {
            return endDate;
        }

        public TextView getUserEmail() {
            return userEmail;
        }
    }
}
