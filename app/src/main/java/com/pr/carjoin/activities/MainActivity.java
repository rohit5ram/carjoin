package com.pr.carjoin.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener,
        CreateTripDialogFragment.OnButtonClickListener {
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
    private FirebaseUser firebaseUser;
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Geocoder geocoder;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
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
        FloatingActionButton myLocationFab = findViewById(R.id.my_location_fab);
        myLocationFab.setOnClickListener(view -> {
            if (pickUpLocationMarker != null && userCurrentLocation != null) {
                LatLng userCurrentLatLong = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                pickUpLocationMarker.setPosition(userCurrentLatLong);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userCurrentLatLong));
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

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
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
            Toast.makeText(this, "Coming soon...", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "Coming soon...", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_share) {
            sendInvitation();
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(MainActivity.this, ChatMainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_your_trips) {
            Intent intent = new Intent(MainActivity.this, YourTripsActivity.class);
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
        mMap.setOnCameraMoveListener(() -> pickUpLocationMarker.setPosition(mMap.getCameraPosition().target));
        mMap.setOnCameraIdleListener(() -> {
            try {
                List<Address> addressList = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude, 1);
                if (!addressList.isEmpty()) {
                    Log.i(Util.TAG, LOG_LABEL + " Address of the location :: " + addressList.get(0));
                    pickUpLatLng = mMap.getCameraPosition().target;
                    pickLocationAddressView.setText(Util.getAddressAsString(addressList.get(0)));
                }
            } catch (IOException e) {
                Log.e(Util.TAG, LOG_LABEL + e.getMessage());
            }
        });
        enableMyLocation();
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
        mMap.setMyLocationEnabled(true);
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
        switch (operationCode) {
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
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

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
                    List<Address> addresses = getAddresses(place);
                    if (!addresses.isEmpty())
                        pickLocationAddressView.setText(Util.getAddressAsString(addresses.get(0)));
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
                    List<Address> addresses = getAddresses(place);
                    if (!addresses.isEmpty())
                        destinationAddressView.setText(Util.getAddressAsString(addresses.get(0)));
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
        startActivity(intent);
    }

    public List<Address> getAddresses(Place selectedPlace) {
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(selectedPlace.getLatLng().latitude,
                    selectedPlace.getLatLng().longitude, 1);
        } catch (NullPointerException | IOException e) {
            Log.e(Util.TAG, LOG_LABEL + e.getMessage());
        }
        return addresses;
    }
}
