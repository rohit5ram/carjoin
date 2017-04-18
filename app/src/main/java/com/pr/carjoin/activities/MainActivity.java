package com.pr.carjoin.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pr.carjoin.Constants;
import com.pr.carjoin.PermissionUtils;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.chat.ChatMainActivity;
import com.pr.carjoin.customViews.CreateTripDialogFragment;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.TripQueue;
import com.pr.carjoin.pojos.Vehicle;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener,
        CreateTripDialogFragment.OnButtonClickListener {
    private static final String LOG_LABEL = "activities.MainActivity";
    private FirebaseUser firebaseUser;
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Geocoder geocoder;
    private Marker pickUpLocationMarker;
    private TextView pickLocationAddressView, pickLocationHeading, destinationAddressView;
    private LinearLayout destinationLayout;
    private Button createTripButton, findTripButton;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.location_pick_up_address_layout) {
                launchPlacePicker(PICK_UP_PLACE_PICKER_REQUEST_CODE);
            } else if (view.getId() == R.id.location_destination_address_layout) {
                launchPlacePicker(DESTINATION_PLACE_PICKER_REQUEST_CODE);
            }
        }
    };
    private LatLng pickUpLatLng, destLatLng;
    private Location userCurrentLocation;

    /**
     * TripQueue code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private static final int PICK_UP_PLACE_PICKER_REQUEST_CODE = 1;
    private static final int DESTINATION_PLACE_PICKER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        createLocationRequest();
        mGoogleApiClient.connect();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_main);
        pickLocationHeading = (TextView) findViewById(R.id.location_pick_up_heading);
        LinearLayout pickUpLocationLayout = (LinearLayout) findViewById(R.id.location_pick_up_address_layout);
        pickUpLocationLayout.setOnClickListener(clickListener);
        createTripButton = (Button) findViewById(R.id.create_trip);
        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validSourceDestination()) {
                    showCreateTripDialog();
                }
            }
        });
        findTripButton = (Button) findViewById(R.id.find_trip);
        findTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validSourceDestination()) {
                    launchListTripActivity();
                }
            }
        });
        destinationLayout = (LinearLayout) findViewById(R.id.location_destination_address_layout);
        destinationLayout.setOnClickListener(clickListener);
        destinationAddressView = (TextView) findViewById(R.id.location_destination_address);
        destinationAddressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    // should remove the vishnu@yantranet.com
                    if (firebaseUser != null && firebaseUser.getEmail() != null) {
                        if (firebaseUser.getEmail().equals("rohit5ram@gmail.com")
                                || firebaseUser.getEmail().equals("vishnu@yantranet.com"))
                            createTripButton.setVisibility(View.VISIBLE);
                        else findTripButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    createTripButton.setVisibility(View.GONE);
                    findTripButton.setVisibility(View.GONE);
                }
            }
        });
        FloatingActionButton myLocationFab = (FloatingActionButton) findViewById(R.id.my_location_fab);
        myLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pickUpLocationMarker != null && userCurrentLocation != null) {
                    LatLng userCurrentLatLong = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                    pickUpLocationMarker.setPosition(userCurrentLatLong);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userCurrentLatLong));
                }
            }
        });
        pickLocationAddressView = (TextView) findViewById(R.id.location_pick_up_address);
        geocoder = new Geocoder(this, Locale.getDefault());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNavHeader(navigationView.getHeaderView(0));

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {
            // Handle the camera action
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this, ChatMainActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_my_trip){
            Intent intent = new Intent(MainActivity.this, MyTripsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pickUpLocationMarker = mMap.addMarker(new MarkerOptions().title("PickUp Location").position(new LatLng(0, 0)).draggable(true));
        pickUpLocationMarker.showInfoWindow();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                pickUpLocationMarker.setPosition(mMap.getCameraPosition().target);
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    List<Address> addressList = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude,
                            mMap.getCameraPosition().target.longitude, 1);
                    if (!addressList.isEmpty()) {
                        Log.i(Util.TAG, LOG_LABEL + " Address of the location :: " + addressList.get(0));
                        pickUpLatLng = mMap.getCameraPosition().target;
                        pickLocationAddressView.setText(Util.getAddressAsString(addressList.get(0)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        enableMyLocation();
    }

    private void setNavHeader(View headerView) {
        if (firebaseUser != null) {
            ImageView imageView = (ImageView) headerView.findViewById(R.id.nav_header_imageView);
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageView);

            TextView userNameView = (TextView) headerView.findViewById(R.id.nav_header_title);
            userNameView.setText(firebaseUser.getDisplayName());

            TextView emailView = (TextView) headerView.findViewById(R.id.nav_header_sub_title);
            emailView.setText(firebaseUser.getEmail());
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(6000000);
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, this);
        if (location != null) {
            Log.i(Util.TAG, LOG_LABEL + " current location of the device is :: " + location.toString());
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
            pickUpLocationMarker.setPosition(coordinates);
            userCurrentLocation = location;
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(Util.TAG, LOG_LABEL + " connection to google API is failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(Util.TAG, LOG_LABEL + " connected to google API");
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Util.TAG, LOG_LABEL + " connection to google API is suspended");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String operationCode = intent.getStringExtra(Util.OPERATION_CODE);
        switch (operationCode){
            case "5000":

                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(Util.TAG, LOG_LABEL + " location of the device is changed");
        userCurrentLocation = location;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showDestinationLayout();
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        showDestinationLayout();
    }

    private void showDestinationLayout() {
        pickLocationHeading.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.VISIBLE);
    }

    private void launchPlacePicker(int requestCode) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), requestCode);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Place selectedPlace = PlacePicker.getPlace(this, data);
            try {
                List<Address> addresses = geocoder.getFromLocation(selectedPlace.getLatLng().latitude,
                        selectedPlace.getLatLng().longitude, 1);
                switch (requestCode) {
                    case PICK_UP_PLACE_PICKER_REQUEST_CODE:
                        pickUpLatLng = selectedPlace.getLatLng();
                        pickLocationAddressView.setText(Util.getAddressAsString(addresses.get(0)));
                        showDestinationLayout();
                        break;
                    case DESTINATION_PLACE_PICKER_REQUEST_CODE:
                        destLatLng = selectedPlace.getLatLng();
                        destinationAddressView.setText(Util.getAddressAsString(addresses.get(0)));
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validSourceDestination() {
        if (pickUpLatLng != null && destLatLng != null) {
            return true;
        } else {
            if (pickUpLatLng == null) {
                Toast.makeText(this, "Enter PickUp Location", Toast.LENGTH_SHORT).show();
            }

            if (destLatLng == null) {
                Toast.makeText(this, "Enter Destination Location", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private void launchNavigationActivity() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" +
                "saddr=" + pickUpLatLng.latitude + "," + pickUpLatLng.longitude + "&daddr=" +
                destLatLng.latitude + "," + destLatLng.longitude));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    private void createTrip(Trip trip) {
        if (firebaseUser != null) {
            trip.published = true;
            trip.sourceLat = pickUpLatLng.latitude;
            trip.sourceLong = pickUpLatLng.longitude;
            trip.sourceAddress = String.valueOf(pickLocationAddressView.getText());
            trip.destLat = destLatLng.latitude;
            trip.destLong = destLatLng.longitude;
            trip.destAddress = String.valueOf(destinationAddressView.getText());
            trip.timeStamp = new Date().getTime();
            trip.owner = new Trip.Owner();
            trip.owner.id = firebaseUser.getUid();
            trip.owner.name = firebaseUser.getDisplayName();
            trip.owner.email = firebaseUser.getEmail();
            trip.owner.photoURL = String.valueOf(firebaseUser.getPhotoUrl());
            trip.status = Constants.CREATED;
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Util.TRIPS).push();
            final String tripId = database.getKey();
            database.setValue(trip).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        createTripQueue(tripId, firebaseUser.getUid(), firebaseUser.getDisplayName());
                    }
                }
            });
        }
    }

    private void createTripQueue(String tripId, String firebaseUserId, String firebaseUserName) {
        TripQueue tripQueue = new TripQueue();
        tripQueue.name = firebaseUserName;
        tripQueue.status = "";
        tripQueue.type = Constants.OWNER;
        FirebaseDatabase.getInstance().getReference().child(Util.TRIP_QUEUE).child(tripId)
                .child(firebaseUserId).setValue(tripQueue);
    }

    private void showCreateTripDialog() {
        CreateTripDialogFragment createTripDialogFragment = new CreateTripDialogFragment();
        createTripDialogFragment.registerCallback(this);
        createTripDialogFragment.show(getSupportFragmentManager(), LOG_LABEL);
    }

    @Override
    public void onPositiveButtonClick(Trip trip, Vehicle vehicle) {
        createTrip(trip);
        pushVehicle(trip.vehicleRegId, vehicle);
    }

    private void pushVehicle(String vehicleRegID, Vehicle vehicle) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Util.VEHICLES).child(vehicleRegID).setValue(vehicle);
    }

    @Override
    public void onNegativeButtonClick() {

    }

    private void launchListTripActivity() {
        Intent intent = new Intent(this, ListTripActivity.class);
        startActivity(intent);
    }
}
