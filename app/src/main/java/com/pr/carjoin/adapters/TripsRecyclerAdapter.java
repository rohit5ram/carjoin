package com.pr.carjoin.adapters;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pr.carjoin.R;
import com.pr.carjoin.activities.CarJoin;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.Trips;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by vishnu on 29/3/17.
 */

public class TripsRecyclerAdapter extends RecyclerView.Adapter<TripsRecyclerAdapter.ViewHolder> {
    private static final String LOG_LABEL = "adapters.TripsRecyclerAdapter";
    private final Trips trips;
    private final String userId;
    private final String email;
    private static final String DATE_TIME_FORMAT = "dd:MM:yy HH:mm";

    public TripsRecyclerAdapter(Trips trips, String userId, String email) {
        this.trips = trips;
        this.userId = userId;
        this.email = email;
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
        Glide.with(holder.getImageView().getContext()).load(trip.owner.photoURL).into(holder.getImageView());
        holder.getUserName().setText(trip.owner.name);
        holder.getUserEmail().setText(trip.owner.email);
        holder.getStartDate().append(" : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.beginDateTimeMills)));
        holder.getEndDate().append("   : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.endDateTimeMills)));

        holder.getRequest().setOnClickListener(v -> {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.getRequest().setVisibility(View.GONE);
            new UpdateTrip("requestTrip", trip.id, userId, email, new WeakReference<>(TripsRecyclerAdapter.this), new WeakReference<>(holder)).execute();
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private Button request;
        private TextView userName, startDate, endDate, userEmail;
        private RelativeLayout layout;
        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.user_photo);
            request = itemView.findViewById(R.id.status);
            userName = itemView.findViewById(R.id.user_name);
            startDate = itemView.findViewById(R.id.start_date_text_view);
            endDate = itemView.findViewById(R.id.end_date_text_view);
            userEmail = itemView.findViewById(R.id.user_email);
            layout = itemView.findViewById(R.id.button_layout);
            progressBar = itemView.findViewById(R.id.loading);
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

        public RelativeLayout getLayout() {
            return layout;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }
    }

    static class UpdateTrip extends AsyncTask<Void, Void, Boolean> {

        private final String path;
        private final String tripId;
        private final String userId;
        private final String email;
        private final WeakReference<TripsRecyclerAdapter> recyclerAdapterWeakReference;
        private final WeakReference<ViewHolder> viewHolderWeakReference;

        UpdateTrip(String path, String tripId, String userId, String email, WeakReference<TripsRecyclerAdapter> recyclerAdapterWeakReference, WeakReference<ViewHolder> viewHolderWeakReference) {
            this.path = path;
            this.tripId = tripId;
            this.userId = userId;
            this.email = email;
            this.recyclerAdapterWeakReference = recyclerAdapterWeakReference;
            this.viewHolderWeakReference = viewHolderWeakReference;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("us-central1-carjoin-5429b.cloudfunctions.net")
                    .addPathSegment("carjoin")
                    .addPathSegment(path)
                    .build();
            Log.i(LOG_LABEL, "URL :: " + url.toString());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", userId);
                jsonObject.put("tripId", tripId);
                jsonObject.put("email", email);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

                Response response = CarJoin.client.newBuilder().build().newCall(new Request.Builder().url(url).post(requestBody).build()).execute();
                ResponseBody body = response.body();
                if (response.isSuccessful() && body != null) {
                    try {
                        String responseString = body.string();
                        Log.i(LOG_LABEL, "RESPONSE ::" + responseString);
                        Result result = new Gson().fromJson(responseString, Result.class);
                        return result != null && result.result == 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            recyclerAdapterWeakReference.get().updateUI(viewHolderWeakReference.get(), aBoolean);
        }
    }

    private void updateUI(ViewHolder viewHolder, Boolean aBoolean) {
        if(!aBoolean){
            Toast.makeText(viewHolder.endDate.getContext(), "No seats Available", Toast.LENGTH_SHORT).show();
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.getRequest().setVisibility(View.VISIBLE);
        }else{
            viewHolder.getLayout().setVisibility(View.GONE);
        }
    }

    static class Result {
        int result;
    }
}
