package com.example.medimap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.example.medimap.server.Pathloc;
import com.example.medimap.server.PathlocApi;
import com.example.medimap.NetworkUtils;
import com.example.medimap.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map extends AppCompatActivity {

    private static final String TAG = "MapActivity";  // Define a log tag
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private ImageButton addLocationButton;
    private PathlocApi pathlocApi;
    private CardView locationCardView;
    private TextView locationNameTextView;
    private TextView locationDescriptionTextView;
    private TextView locationDifficultyTextView;
    private TextView locationRatingTextView;
    private LocationManager locationManager;
    private FloatingActionButton fabMyLocation;  // Button to return to user location
    private Location userLocation;  // Store user location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize map
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Initialize CardView and its contents
        locationCardView = findViewById(R.id.locationCardView);
        locationNameTextView = findViewById(R.id.locationNameTextView);
        locationDescriptionTextView = findViewById(R.id.locationDescriptionTextView);
        locationDifficultyTextView = findViewById(R.id.locationDifficultyTextView);
        locationRatingTextView = findViewById(R.id.locationRatingTextView);

        // Initialize Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // FloatingActionButton to go back to user's location
        fabMyLocation = findViewById(R.id.fab_my_location);

        // Initialize Add Location Button
        addLocationButton = findViewById(R.id.addLocationButton);

        // Check for internet connection and location permissions
        if (NetworkUtils.isNetworkAvailable(this)) {
            Log.d(TAG, "Network is available!");
            Toast.makeText(this, "Network is available!", Toast.LENGTH_SHORT).show();
            checkLocationPermission();
            fetchAndPinPathsFromServer(); // Fetch and pin paths when network is available
        } else {
            // No network connection
            Log.d(TAG, "No internet connection.");
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show();
        }

        // Recenter map to user's location when FloatingActionButton is clicked
        fabMyLocation.setOnClickListener(view -> {
            if (userLocation != null) {
                // Recenter the map on the user's current location
                GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
                mapController.setCenter(userGeoPoint);
                Toast.makeText(this, "Returning to your location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Set listener for add location button
        addLocationButton.setOnClickListener(v -> showAddLocationDialog());
    }

    /**
     * This method checks whether the app has permission to access the device's location.
     * If not, it requests location permission from the user.
     * If permission is already granted, it proceeds to check if GPS is enabled.
     */
    private void checkLocationPermission() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            Log.d(TAG, "Requesting location permission.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with location-based logic
            Log.d(TAG, "Location permission already granted.");
            checkIfGPSEnabled();
        }
    }

    /**
     * Fetches all paths from the server and pins them on the map.
     */
    private void fetchAndPinPathsFromServer() {
        Log.d(TAG, "Fetching paths from the server...");
        pathlocApi = RetrofitClient.getRetrofitInstance().create(PathlocApi.class);
        Call<List<Pathloc>> call = pathlocApi.getAllPaths();
        call.enqueue(new Callback<List<Pathloc>>() {
            @Override
            public void onResponse(Call<List<Pathloc>> call, Response<List<Pathloc>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Pin each path on the map
                    List<Pathloc> paths = response.body();
                    Log.d(TAG, "Successfully retrieved " + paths.size() + " paths from the server.");
                    for (Pathloc path : paths) {
                        pinPathOnMap(path);  // Correctly use path's GeoPoint
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

    /**
     * Pins a given Pathloc object on the map.
     *
     * @param path The path object to be pinned on the map.
     */
    private void pinPathOnMap(Pathloc path) {
        Log.d(TAG, "Pinning path: " + path.getName());

        // Create GeoPoint for the path
        GeoPoint pathLocation = new GeoPoint(path.getStartLatitude(), path.getStartLongitude());

        // Create and configure marker for the path
        Marker pathMarker = new Marker(mapView);
        pathMarker.setPosition(pathLocation);
        pathMarker.setIcon(getResources().getDrawable(R.drawable.ic_location)); // Use a different icon for path locations
        pathMarker.setTitle(path.getName());

        // Set click listener for the marker
        pathMarker.setOnMarkerClickListener((marker, mapView) -> {
            // Update the CardView fields with location details
            locationNameTextView.setText(path.getName());
            locationDescriptionTextView.setText(path.getDescription());
            locationDifficultyTextView.setText("Difficulty: " + path.getDifficulty());
            locationRatingTextView.setText("Rating: " + (path.getRating() != null ? path.getRating() : "N/A"));

            // Make the CardView visible
            locationCardView.setVisibility(View.VISIBLE);

            // Return true to indicate that the click event was handled
            return true;
        });

        // Add the marker to the map's overlays
        mapView.getOverlays().add(pathMarker);

        // Refresh the map to display the marker
        mapView.invalidate();
    }

    /**
     * This method checks if the GPS is enabled.
     * If it's not, it prompts the user to enable it by showing a dialog.
     */
    private void checkIfGPSEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "GPS is disabled, prompting user to enable GPS.");
            showGPSDisabledDialog();
        } else {
            // GPS is enabled, proceed with fetching the user's location
            Log.d(TAG, "GPS is enabled.");
            fetchUserLocation();
        }
    }

    /**
     * Show a dialog that informs the user that GPS is disabled and provides an option to open
     * the device's location settings to enable GPS.
     */
    private void showGPSDisabledDialog() {
        Log.d(TAG, "Showing GPS disabled dialog.");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable GPS")
                .setMessage("GPS is required to access your location. Would you like to enable it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Redirect the user to the location settings
                        Log.d(TAG, "User agreed to enable GPS.");
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "User declined to enable GPS.");
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission was granted, proceed with checking if GPS is enabled
                Log.d(TAG, "Location permission granted.");
                checkIfGPSEnabled();
            } else {
                // Permission denied, show a message to the user
                Log.d(TAG, "Location permission denied.");
                Toast.makeText(this, "Location permission is required to use this feature.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This method fetches the user's current location and places a marker on the map.
     */
    private void fetchUserLocation() {
        try {
            // Get the user's last known location from the GPS provider or network provider
            Log.d(TAG, "Fetching user's current location.");
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (userLocation == null) {
                userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (userLocation != null) {
                // Get the latitude and longitude from the user's location
                double userLatitude = userLocation.getLatitude();
                double userLongitude = userLocation.getLongitude();

                Log.d(TAG, "User's location fetched: Latitude = " + userLatitude + ", Longitude = " + userLongitude);

                // Create a GeoPoint for the user's location
                GeoPoint userGeoPoint = new GeoPoint(userLatitude, userLongitude);

                // Create a marker to pin the user's location on the map
                Marker userMarker = new Marker(mapView);
                userMarker.setPosition(userGeoPoint);
                userMarker.setIcon(getResources().getDrawable(R.drawable.address)); // Use your custom icon
                userMarker.setTitle("Your Location");

                // Add the marker to the map's overlays
                mapView.getOverlays().add(userMarker);

                // Center the map on the user's location
                IMapController mapController = mapView.getController();
                mapController.setCenter(userGeoPoint);

                // Refresh the map to display the marker
                mapView.invalidate();

                // Inform the user that the location has been pinned
                Toast.makeText(this, "Your location has been pinned on the map.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Unable to determine user's location. GPS might be disabled.");
                Toast.makeText(this, "Unable to determine your location. Ensure GPS is enabled.", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted.", e);
            Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show the Add Location Dialog.
     */
    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Add New Location");

        // Set up the layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_location, null);
        builder.setView(dialogView);

        // Find the EditText and Spinner in the dialog layout
        final EditText input = dialogView.findViewById(R.id.locationNameEditText);
        final Spinner difficultySpinner = dialogView.findViewById(R.id.difficultySpinner);

        // Find the buttons in the dialog layout
        Button addButton = dialogView.findViewById(R.id.addButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Set up the Spinner with difficulty levels using built-in Android layouts
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle button clicks
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

    /**
     * Adds a new location marker to the map.
     *
     * @param locationName The name of the location.
     * @param difficulty   The difficulty level of the location.
     */
    private void addNewLocation(String locationName, String difficulty) {
        // Get the current center of the map
        GeoPoint currentPoint = (GeoPoint) mapView.getMapCenter();

        Drawable customMarkerIcon = ContextCompat.getDrawable(this, R.drawable.ic_location);

        Marker newMarker = new Marker(mapView);
        newMarker.setPosition(currentPoint);
        newMarker.setIcon(customMarkerIcon);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        newMarker.setTitle(locationName);
        newMarker.setSubDescription("Difficulty: " + difficulty);
        newMarker.setOnMarkerClickListener((marker, mapView) -> {
            displayLocationData(locationName, "Difficulty: " + difficulty, marker.getPosition());
            return true;
        });

        mapView.getOverlays().add(newMarker);
        mapView.invalidate(); // Refresh the map to show the new marker
    }

    /**
     * Displays location data in the CardView.
     */
    private void displayLocationData(String locationName, String difficulty, GeoPoint position) {
        locationNameTextView.setText(locationName);
        locationDifficultyTextView.setText(difficulty);
        locationDescriptionTextView.setText("Lat: " + position.getLatitude() + " Lon: " + position.getLongitude());
        locationCardView.setVisibility(View.VISIBLE);
    }
}
