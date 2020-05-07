package com.pr.carjoin.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.pr.carjoin.chat.ChatMainActivity.ANONYMOUS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener,
        CreateTripDialogFragment.OnButtonClickListener, OnFailureListener, OnSuccessListener{
    private static final String LOG_LABEL = "activities.MainActivity";
    /**
     * TripQueue code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_INVITE = 2;
    private static final int PICK_UP_PLACE_PICKER_REQUEST_CODE = 3;
    private static final int DESTINATION_PLACE_PICKER_REQUEST_CODE = 4;
    private static final int REQUEST_CHECK_SETTINGS = 5;
    private FirebaseUser firebaseUser;
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Marker pickUpLocationMarker;
    private TextView pickLocationAddressView, pickLocationHeading, destinationAddressView;
    private LinearLayout destinationLayout;
    private Button createTripButton, findTripButton;
    private View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.location_pick_up_address_layout) {
            launchPlacePicker(PICK_UP_PLACE_PICKER_REQUEST_CODE);
        } else if (view.getId() == R.id.location_destination_address_layout) {
            launchPlacePicker(DESTINATION_PLACE_PICKER_REQUEST_CODE);
        }
    };
    private LatLng pickUpLatLng, destLatLng;
    private Location userCurrentLocation;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_main);
        pickLocationHeading = findViewById(R.id.location_pick_up_heading);
        LinearLayout pickUpLocationLayout = findViewById(R.id.location_pick_up_address_layout);
        pickUpLocationLayout.setOnClickListener(clickListener);
        createTripButton = findViewById(R.id.create_trip);
        createTripButton.setOnClickListener(v -> {
            if (validSourceDestination()) {
                showCreateTripDialog();
            }
        });
        findTripButton = findViewById(R.id.find_trip);
        findTripButton.setOnClickListener(v -> {
            if (validSourceDestination()) {
                launchListTripActivity();
            }
        });
        destinationLayout = findViewById(R.id.location_destination_address_layout);
        destinationLayout.setOnClickListener(clickListener);
        destinationAddressView = findViewById(R.id.location_destination_address);
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
                    if (isSignedIn() && firebaseUser.getEmail() != null) {
                        if (firebaseUser.getEmail().equals("rohit5ram@gmail.com")
                                || firebaseUser.getEmail().equals("vishnu.ganta22@gmail.com"))
                            createTripButton.setVisibility(View.VISIBLE);
                        else findTripButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    createTripButton.setVisibility(View.GONE);
                    findTripButton.setVisibility(View.GONE);
                }
            }
        });
        FloatingActionButton myLocationFab = findViewById(R.id.my_location_fab);
        myLocationFab.setOnClickListener(view -> {
            if (pickUpLocationMarker != null && userCurrentLocation != null) {
                LatLng userCurrentLatLong = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                pickUpLocationMarker.setPosition(userCurrentLatLong);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userCurrentLatLong));
            }
        });
        pickLocationAddressView = findViewById(R.id.location_pick_up_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNavHeader(navigationView.getHeaderView(0));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null && firebaseUser.getEmail() != null) {
                if (firebaseUser.getEmail().equals("rohit5ram@gmail.com") || firebaseUser.getEmail().equals("vishnu.ganta22@gmail.com")){
                    Toast.makeText(this, "Driver's Can't Pay...", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(MainActivity.this, PayPalActivity.class);
                    startActivity(intent);
                }
            }
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "Coming soon...", Toast.LENGTH_LONG).show();
        }  else if (id == R.id.nav_chat) {
            Intent intent = new Intent(MainActivity.this, ChatMainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_your_trips) {
            Intent intent = new Intent(MainActivity.this, YourTripsActivity.class);
            startActivity(intent);
        } else if(id == R.id.sign_out) {
            GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()).signOut().addOnCompleteListener(task -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        mMap.setOnCameraMoveListener(() -> pickUpLocationMarker.setPosition(mMap.getCameraPosition().target));
        enableMyLocation();
        createLocationRequest();
    }

    private void setNavHeader(View headerView) {
        if (isSignedIn()) {
            ImageView imageView = headerView.findViewById(R.id.nav_header_imageView);
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageView);

            TextView userNameView = headerView.findViewById(R.id.nav_header_title);
            userNameView.setText(firebaseUser.getDisplayName());

            TextView emailView = headerView.findViewById(R.id.nav_header_sub_title);
            emailView.setText(firebaseUser.getEmail());
        }
    }

    private boolean isSignedIn() {
        return firebaseUser != null;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void createLocationRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(600000);
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder.addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, this);
        task.addOnFailureListener(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String operationCode = intent.getStringExtra(Util.OPERATION_CODE);
        switch (operationCode) {
            case "5000":

                break;
        }
    }

    private void onLocationChanged(Location location) {
        Log.i(Util.TAG, LOG_LABEL + " location of the device is changed");
        userCurrentLocation = location;

        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
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
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_INVITE:
                if (resultCode == RESULT_OK) {
                    // Check how many invitations were sent and log.
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    Log.d(Util.TAG, "Invitations sent: " + ids.length);
                } else {
                    // Sending failed or it was canceled, show failure message to the user
                    Log.d(Util.TAG, "Failed to send invitation.");
                }
                break;
            case PICK_UP_PLACE_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(LOG_LABEL, "Place: " + place.getName() + ", " + place.getId());
                    pickUpLatLng = place.getLatLng();
                    pickLocationAddressView.setText(place.getAddress());
                    showDestinationLayout();
                } else {
                    // Sending failed or it was canceled, show failure message to the user
                    Log.d(Util.TAG, "Failed to determine pick up location");
                }
                break;
            case DESTINATION_PLACE_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(LOG_LABEL, "Place: " + place.getName() + ", " + place.getId());
                    destLatLng = place.getLatLng();
                    destinationAddressView.setText(place.getAddress());
                } else {
                    // Sending failed or it was canceled, show failure message to the user
                    Log.d(Util.TAG, "Failed to determine destination location");
                }
                break;
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
        if (isSignedIn()) {
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
        intent.putExtra("pLat", pickUpLatLng.latitude);
        intent.putExtra("pLon", pickUpLatLng.longitude);
        intent.putExtra("dLat", destLatLng.latitude);
        intent.putExtra("dLon", destLatLng.longitude);
        startActivity(intent);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (e instanceof ResolvableApiException) {
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                // Ignore the error.
            }
        }
    }

    @Override
    public void onSuccess(Object o) {
        initLocationData();
        startLocationUpdates();
    }

    private void initLocationData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    private void startLocationUpdates() {
        if(fusedLocationClient != null && locationCallback != null){
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private void stopLocationUpdates(){
        if(fusedLocationClient != null && locationCallback != null){
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
