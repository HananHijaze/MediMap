package com.example.medimap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.medimap.server.Pathloc;
import com.example.medimap.server.PathlocApi;
import com.example.medimap.server.RetrofitClient;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map extends AppCompatActivity implements LocationListener {

    // Constants
    private static final String TAG = "MapActivity"; // Log tag
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Permission request code for location
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 10 seconds between location updates
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters between location updates

    // UI elements
    private MapView mapView;
    private FloatingActionButton fabMyLocation;
    private ImageButton addLocationButton;

    // Location services
    private LocationManager locationManager;
    private PathlocApi pathlocApi;
    private Location userLocation; // Stores the user's current location

    // card view elements
    private CardView locationCardView;
    private TextView locationNameTextView;
    private TextView locationDescriptionTextView;
    private TextView locationDifficultyTextView;
    private TextView locationRatingTextView;

    /**
     * Called when the activity is first created. Initializes the UI and checks for location/network availability.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initializes the UI elements and sets up the location manager and Retrofit API instance.
     */
    private void initializeUI() {
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0); // Set default zoom level

        fabMyLocation = findViewById(R.id.fab_my_location);
        fabMyLocation.setOnClickListener(this::recenterMapOnUserLocation);

        addLocationButton = findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(v -> showAddLocationDialog());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        pathlocApi = RetrofitClient.getRetrofitInstance().create(PathlocApi.class); // Create Retrofit instance
        // Initialize CardView and its contents
        locationCardView = findViewById(R.id.locationCardView);
        locationNameTextView = findViewById(R.id.locationNameTextView);
        locationDescriptionTextView = findViewById(R.id.locationDescriptionTextView);
        locationDifficultyTextView = findViewById(R.id.locationDifficultyTextView);
        locationRatingTextView = findViewById(R.id.locationRatingTextView);
        //rating the path when click on the card view
        /*locationCardView.setOnClickListener(v -> showRatingDialog());*/

    }

    /**
     * Checks if the network is available and the app has location permissions.
     * Prompts the user to enable GPS if necessary.
     */
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

    /**
     * Checks if the app has location permissions. If not, it requests them.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkIfGPSEnabled();
        }
    }

    /**
     * Checks if GPS is enabled. If not, prompts the user to enable it.
     */
    private void checkIfGPSEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledDialog();
        } else {
            requestLocationUpdates();
        }
    }

    /**
     * Displays a dialog asking the user to enable GPS if it is disabled.
     */
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

    /**
     * Requests location updates from both GPS and network providers.
     */
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

    /**
     * Recenters the map on the user's current location.
     *
     * @param view The view that triggered this method (e.g., the FloatingActionButton).
     */
    private void recenterMapOnUserLocation(View view) {
        if (userLocation != null) {
            GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());

            // Center the map on the user's current location
            mapView.getController().setCenter(userGeoPoint);

            // Optionally, set the zoom level here if needed
            mapView.getController().setZoom(15.0); // Example zoom level

            Toast.makeText(this, "Returning to your location", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches all paths from the server and pins them on the map.
     */
    private void fetchAndPinPathsFromServer() {
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

    /**
     * Pins a specific path on the map.
     *
     * @param path The path to be pinned on the map.
     */
    private void pinPathOnMap(Pathloc path) {
        // Create a GeoPoint for the path's location
        GeoPoint pathLocation = new GeoPoint(path.getStartLatitude(), path.getStartLongitude());

        // Create a new marker for the path
        Marker pathMarker = new Marker(mapView);
        pathMarker.setPosition(pathLocation); // Set the marker's position to the path's location
        pathMarker.setIcon(getResources().getDrawable(R.drawable.ic_location)); // Set a custom icon for the marker
        pathMarker.setTitle(path.getName()); // Set the marker's title

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
     * Shows a dialog allowing the user to add a new location.
     */
    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Location");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_location, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.locationNameEditText);
        final Spinner difficultySpinner = dialogView.findViewById(R.id.difficultySpinner);

        Button addButton = dialogView.findViewById(R.id.addButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Set up the spinner for selecting difficulty level
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle the add button click event
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
     * Adds a new location to the map and server.
     *
     * @param locationName The name of the location to be added.
     * @param difficulty   The difficulty level of the location.
     */
    private void addNewLocation(String locationName, String difficulty) {
        if (userLocation != null) {
            GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
            int difficultyLevel = convertDifficultyToInt(difficulty);

            Pathloc newPathloc = new Pathloc(locationName, "User-added location", userLocation.getLatitude(), userLocation.getLongitude(), difficultyLevel);

            // Send the new path to the server
            pathlocApi.createPath(newPathloc).enqueue(new Callback<Pathloc>() {
                @Override
                public void onResponse(Call<Pathloc> call, Response<Pathloc> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(Map.this, "Location added successfully!", Toast.LENGTH_SHORT).show();
                        pinPathOnMap(response.body());
                    } else {
                        Log.e(TAG, "Failed to add location. Code: " + response.code());
                        Toast.makeText(Map.this, "Failed to add location", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Pathloc> call, Throwable t) {
                    Log.e(TAG, "Error occurred: " + t.getMessage(), t);
                    Toast.makeText(Map.this, "Error adding location: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "User location not available.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Converts the difficulty string to an integer for server-side representation.
     *
     * @param difficulty The difficulty level as a string (e.g., "Easy", "Medium", "Hard").
     * @return The corresponding integer for the difficulty level.
     */
    private int convertDifficultyToInt(String difficulty) {
        switch (difficulty) {
            case "Easy":
                return 1;
            case "Medium":
                return 2;
            case "Hard":
                return 3;
            default:
                return 0; // Default or unknown difficulty
        }
    }

    /**
     * Callback method that is triggered when the user's location changes.
     *
     * @param location The new location of the user.
     */

// Declare a variable to keep track of the user's location marker
    private Marker currentLocationMarker;

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location; // Update the user's location

        // Create a GeoPoint for the user's current location
        GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());

        // Remove the existing marker if it exists
        if (currentLocationMarker != null) {
            mapView.getOverlays().remove(currentLocationMarker); // Remove the old marker
        }

        // Create a new marker for the user's location
        currentLocationMarker = new Marker(mapView);
        currentLocationMarker.setPosition(userGeoPoint); // Set the marker's position to the user's location
        currentLocationMarker.setIcon(getResources().getDrawable(R.drawable.address)); // Set a custom icon for the marker
        currentLocationMarker.setTitle("Your Location"); // Set the marker's title

        // Add the new marker to the map's overlays
        mapView.getOverlays().add(currentLocationMarker);

        // Refresh the map to display the marker
        mapView.invalidate();

        // Log the updated location for debugging
        Log.d(TAG, "User location updated: " + location.getLatitude() + ", " + location.getLongitude());
    }




    /**
     * Called when the activity is resumed. Requests location updates.
     */
    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    /**
     * Called when the activity is paused. Stops location updates to save battery.
     */
    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
   /* private void showRatingDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rate, null);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Get references to dialog UI elements
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        Button submitButton = dialogView.findViewById(R.id.submitButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            Toast.makeText(this, "Rating submitted: " + rating, Toast.LENGTH_SHORT).show();
            // Handle the submitted rating here

            dialog.dismiss();// Dismiss the dialog
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss the dialog without action
        });
        // Show the dialog
        dialog.show();
    }*/
}