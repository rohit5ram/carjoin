package com.pr.carjoin.pojos;

/**
 * Created by vishnu on 28/3/17.
 */

public class FirebaseTrip {
    private static final String LOG_LABEL = "pojos.FirebaseTrip";

    public String id;
    public long beginDate;
    public long endDate;
    public String sourceAddress;
    public String destAddress;
    public double sourceLat;
    public double sourceLong;
    public double destLat;
    public double destLong;
    public double fuelPrice;
    public long lastModified;
    public double maintenancePer;
    public boolean published;
    public boolean started;
    public boolean completed;
    public String vehicleColor;
    public String vehicleName;
    public String vehicleNumber;
    public int seatsAvailable;
    public UserDetails userDetails;

    public FirebaseTrip() {
    }
}
