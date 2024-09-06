package com.example.medimap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.example.medimap.server.Pathloc;
import com.example.medimap.server.PathlocApi;

import com.example.medimap.server.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map extends AppCompatActivity implements LocationListener {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 10 seconds
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private MapView mapView;
    private CardView locationCardView;
    private TextView locationNameTextView;
    private TextView locationDescriptionTextView;
    private TextView locationDifficultyTextView;
    private TextView locationRatingTextView;
    private LocationManager locationManager;
    private FloatingActionButton fabMyLocation;
    private Location userLocation;
    private PathlocApi pathlocApi;
    private ImageButton addLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeUI();
        checkNetworkAndLocationAvailability();
    }

    private void initializeUI() {
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);

        locationCardView = findViewById(R.id.locationCardView);
        locationNameTextView = findViewById(R.id.locationNameTextView);
        locationDescriptionTextView = findViewById(R.id.locationDescriptionTextView);
        locationDifficultyTextView = findViewById(R.id.locationDifficultyTextView);
        locationRatingTextView = findViewById(R.id.locationRatingTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        fabMyLocation = findViewById(R.id.fab_my_location);
        fabMyLocation.setOnClickListener(this::recenterMapOnUserLocation);

        addLocationButton = findViewById(R.id.addLocationButton);

        addLocationButton.setOnClickListener(v -> showAddLocationDialog());
    }

    private void checkNetworkAndLocationAvailability() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            Log.d(TAG, "Network is available!");
            Toast.makeText(this, "Network is available!", Toast.LENGTH_SHORT).show();
            checkLocationPermission();
        } else {
            Log.d(TAG, "No internet connection.");
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    private void recenterMapOnUserLocation(View view) {
        if (userLocation != null) {
            GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
            mapView.getController().setCenter(userGeoPoint);
            Toast.makeText(this, "Returning to your location", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkIfGPSEnabled();
        }
    }

    private void checkIfGPSEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledDialog();
        } else {
            requestLocationUpdates();
            fetchAndPinPathsFromServer(); // Fetch and pin paths when network is available
        }
    }

    private void showGPSDisabledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable GPS")
                .setMessage("GPS is required to access your location. Would you like to enable it?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialogInterface, i) -> Log.d(TAG, "User declined to enable GPS."))
                .show();
    }

    private void requestLocationUpdates() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted.", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLocation = location; // Update the user location
        fetchUserLocation(); // Pin user's location on the map
    }

    private void fetchAndPinPathsFromServer() {
        pathlocApi = RetrofitClient.getRetrofitInstance().create(PathlocApi.class);
        Call<List<Pathloc>> call = pathlocApi.getAllPaths();
        call.enqueue(new Callback<List<Pathloc>>() {
            @Override
            public void onResponse(Call<List<Pathloc>> call, Response<List<Pathloc>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pathloc> paths = response.body();
                    for (Pathloc path : paths) {
                        pinPathOnMap(path);
                    }
                } else {
                    Log.e(TAG, "Failed to retrieve paths from the server.");
                    Toast.makeText(Map.this, "Failed to retrieve paths from the server.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pathloc>> call, Throwable t) {
                Log.e(TAG, "Error occurred: " + t.getMessage(), t);
                Toast.makeText(Map.this, "Error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pinPathOnMap(Pathloc path) {
        GeoPoint pathLocation = new GeoPoint(path.getStartLatitude(), path.getStartLongitude());
        Marker pathMarker = new Marker(mapView);
        pathMarker.setPosition(pathLocation);
        pathMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
        pathMarker.setTitle(path.getName());

        pathMarker.setOnMarkerClickListener((marker, mapView) -> {
            locationNameTextView.setText(path.getName());
            locationDescriptionTextView.setText(path.getDescription());
            locationDifficultyTextView.setText("Difficulty: " + path.getDifficulty());
            locationRatingTextView.setText("Rating: " + (path.getRating() != null ? path.getRating() : "N/A"));
            locationCardView.setVisibility(View.VISIBLE);
            return true;
        });

        mapView.getOverlays().add(pathMarker);
        mapView.invalidate();
    }

    private void fetchUserLocation() {
        try {
            if (userLocation != null) {
                GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
                Marker userMarker = new Marker(mapView);
                userMarker.setPosition(userGeoPoint);
                userMarker.setIcon(getResources().getDrawable(R.drawable.address));
                userMarker.setTitle("Your Location");
                mapView.getOverlays().add(userMarker);
                mapView.getController().setCenter(userGeoPoint);
                mapView.invalidate();
            } else {
                Toast.makeText(this, "Unable to determine your location. Ensure GPS is enabled.", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted.", e);
        }
    }

    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Add New Location");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_location, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.locationNameEditText);
        final Spinner difficultySpinner = dialogView.findViewById(R.id.difficultySpinner);

        Button addButton = dialogView.findViewById(R.id.addButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        addButton.setOnClickListener(v -> {
            String locationName = input.getText().toString();
            String difficulty = difficultySpinner.getSelectedItem().toString();
            if (!locationName.isEmpty()) {
                addNewLocation(locationName, difficulty);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void addNewLocation(String locationName, String difficulty) {
        if (userLocation != null) {
            GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
            Drawable customMarkerIcon = ContextCompat.getDrawable(this, R.drawable.ic_location);
            int difficultyInt = convertDifficultyToInt(difficulty);

            Marker newMarker = new Marker(mapView);
            newMarker.setPosition(userGeoPoint);
            newMarker.setIcon(customMarkerIcon);
            newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            newMarker.setTitle(locationName);
            newMarker.setSubDescription("Difficulty: " + difficulty);

            newMarker.setOnMarkerClickListener((marker, mapView) -> {
                displayLocationData(locationName, "Difficulty: " + difficulty, marker.getPosition());

                Pathloc newPathloc = new Pathloc(locationName, "Location Added By User", userLocation.getLatitude(), userLocation.getLongitude(), difficultyInt);
                pathlocApi.createPath(newPathloc).enqueue(new Callback<Pathloc>() {
                    @Override
                    public void onResponse(Call<Pathloc> call, Response<Pathloc> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Map.this, "Path added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Pathloc", "Failed to add path. Response code: " + response.code());
                            try {
                                Log.d("Pathloc", "Error: " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Pathloc> call, Throwable t) {
                        Toast.makeText(Map.this, "Failed to add location: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            });

            mapView.getOverlays().add(newMarker);
            mapView.invalidate();
        } else {
            Toast.makeText(this, "User location not available. Ensure GPS is enabled.", Toast.LENGTH_LONG).show();
        }
    }

    private int convertDifficultyToInt(String difficulty) {
        switch (difficulty) {
            case "Easy":
                return 1;
            case "Medium":
                return 2;
            case "Hard":
                return 3;
            default:
                return 0;
        }
    }

    private void displayLocationData(String locationName, String difficulty, GeoPoint position) {
        locationNameTextView.setText(locationName);
        locationDifficultyTextView.setText(difficulty);
        locationDescriptionTextView.setText("Lat: " + position.getLatitude() + " Lon: " + position.getLongitude());
        locationCardView.setVisibility(View.VISIBLE);
    }
}
