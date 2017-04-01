package com.pr.carjoin.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pr.carjoin.Constants;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.pojos.FirebaseTrip;
import com.pr.carjoin.pojos.Request;
import com.pr.carjoin.pojos.UserDetails;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by vishnu on 29/3/17.
 */

public class TripsRecyclerAdapter extends RecyclerView.Adapter<TripsRecyclerAdapter.ViewHolder> {
    private static final String LOG_LABEL = "adapters.TripsRecyclerAdapter";
    private final List<FirebaseTrip> trips;
    private static final String DATE_TIME_FORMAT = "dd:MM:yy HH:mm";
    private final WeakReference<Activity> activityWeakReference;

    public TripsRecyclerAdapter(List<FirebaseTrip> trips, Activity activity) {
        this.trips = trips;
        this.activityWeakReference = new WeakReference<Activity>(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_trip_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FirebaseTrip trip = trips.get(holder.getAdapterPosition());
        final Context context = holder.getImageView().getContext();
        Glide.with(holder.getImageView().getContext()).load(trip.userDetails.photoUrl).into(holder.getImageView());
        holder.getUserName().setText(trip.userDetails.name);
        holder.getUserEmail().setText(trip.userDetails.email);
        holder.getStartDate().append(" : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.beginDate)));
        holder.getEndDate().append("   : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.endDate)));

        Request request = checkIfUserAlreadyExistsInRequests(trip.requests);
        if (request != null) {
            switch (request.status) {
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
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Util.TRIPS)
                            .child(trip.id).child("requests");
                    Request newRequest = new Request();
                    newRequest.userDetails = new UserDetails(FirebaseAuth.getInstance().getCurrentUser());
                    newRequest.userDetails.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    newRequest.status = Constants.PENDING;
                    if (trip.requests == null) {
                        trip.requests = new ArrayList<>();
                        trip.requests.add(newRequest);
                    } else {
                        trip.requests.add(newRequest);
                    }
                    databaseReference.setValue(trip.requests).addOnCompleteListener(activityWeakReference.get(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                holder.getRequest().setText(context.getString(R.string.string_pending));
                                v.setOnClickListener(null);
                            }
                        }
                    });
                }
            });
        }
    }

    private Request checkIfUserAlreadyExistsInRequests(ArrayList<Request> requests) {
        if (requests != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            for (Request request : requests) {
                if (request.userDetails.id.equals(userId)) {
                    return request;
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private Button request;
        private TextView userName, startDate, endDate, userEmail;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (CircleImageView) itemView.findViewById(R.id.user_photo);
            request = (Button) itemView.findViewById(R.id.request_button);
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
