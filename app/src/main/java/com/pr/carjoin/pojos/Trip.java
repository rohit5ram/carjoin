package com.pr.carjoin.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by vishnu on 28/3/17.
 */

public class Trip {
    private static final String LOG_LABEL = "pojos.Trip";

    @SerializedName("tripId")
    public String id;
    public long beginDateTimeMills;
    public String destAddress;
    public double destLat;
    public double destLong;
    public long endDateTimeMills;
    public double fuelPricePerLitre;
    public double maintenancePercentage;
    public boolean published;
    public String sourceAddress;
    public double sourceLat;
    public double sourceLong;
    public int seatsAvailable;
    public long timeStamp;
    public String vehicleRegId;
    public Owner owner;
    public String status;
    public Map<String, String> members;

    public static class Owner {
        public String id;
        public String name;
        public String email;
        public String photoURL;

        public Owner() {

        }
    }

    public Trip() {
    }
}
