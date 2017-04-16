package com.pr.carjoin.pojos;

import java.util.ArrayList;

/**
 * Created by vishnu on 28/3/17.
 */

public class Trip {
    private static final String LOG_LABEL = "pojos.Trip";

    public String id;
    public long beginDateTimeMills;
    public boolean completed;
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
    public boolean started;
    public int seatsAvailable;
    public long timeStamp;
    public String vehicleRegId;
    public Owner owner;
    public ArrayList<String> members;

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
