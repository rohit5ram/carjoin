package com.pr.carjoin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pr.carjoin.R;
import com.pr.carjoin.pojos.FirebaseTrip;

import java.text.SimpleDateFormat;
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

    public TripsRecyclerAdapter(List<FirebaseTrip> trips) {
        this.trips = trips;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_trip_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FirebaseTrip trip = trips.get(holder.getAdapterPosition());
        Glide.with(holder.getImageView().getContext()).load(trip.userDetails.photoUrl).into(holder.getImageView());
        holder.getUserName().setText(trip.userDetails.name);
        holder.getUserEmail().setText(trip.userDetails.email);
        holder.getStartDate().append(" : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.beginDate)));
        holder.getEndDate().append("   : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.endDate)));
        holder.getIgnore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trips.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private Button ignore, request;
        private TextView userName, startDate, endDate, userEmail;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (CircleImageView) itemView.findViewById(R.id.user_photo);
            ignore = (Button) itemView.findViewById(R.id.ignore_button);
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

        public Button getIgnore() {
            return ignore;
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
