package com.example.medimap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

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

public class Map extends AppCompatActivity {

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
    private Location userLocation;

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, fetch location
            fetchUserLocation(mapController);
        }

        // Adding new location by the user
        addLocationButton = findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(v -> showAddLocationDialog());
    }

    private void fetchUserLocation(IMapController mapController) {
        try {
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (userLocation == null) {
                userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (userLocation != null) {
                double userLatitude = userLocation.getLatitude();
                double userLongitude = userLocation.getLongitude();

                // Insert user's location on the map
                GeoPoint userGeoPoint = new GeoPoint(userLatitude, userLongitude);
                mapController.setCenter(userGeoPoint);

                Marker userMarker = new Marker(mapView);
                userMarker.setPosition(userGeoPoint);
                userMarker.setIcon(getResources().getDrawable(R.drawable.address)); // Use a custom icon for the user location
                userMarker.setTitle("Your Location");
                mapView.getOverlays().add(userMarker);

                // Get all paths from server and insert them on the map
                getAllPathsFromServer();
            } else {
                Toast.makeText(this, "Unable to determine your location.", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_LONG).show();
        }
    }

    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Location");

        // Set up the layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_location, null);
        builder.setView(dialogView);

        // Find the EditText and Spinner in the dialog layout
        final EditText input = dialogView.findViewById(R.id.locationNameEditText);
        final Spinner difficultySpinner = dialogView.findViewById(R.id.difficultySpinner);

        // Set up the Spinner with difficulty levels using built-in Android layouts
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        // Find the buttons in the dialog layout
        Button addButton = dialogView.findViewById(R.id.addButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Handle button clicks
        addButton.setOnClickListener(v -> {
            String locationName = input.getText().toString();
            int difficulty = Integer.parseInt(difficultySpinner.getSelectedItem().toString());

            if (!locationName.isEmpty() && userLocation != null) {
                double latitude = userLocation.getLatitude();
                double longitude = userLocation.getLongitude();

                // Create a new Pathloc object
                Pathloc newPathloc = new Pathloc(locationName, "User added location", latitude, longitude, difficulty);

                // Post the new Pathloc to the server
                addNewPathlocToServer(newPathloc);

                dialog.dismiss();
            } else {
                Toast.makeText(Map.this, "Please enter a name for the location.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void addNewPathlocToServer(Pathloc newPathloc) {
        pathlocApi = RetrofitClient.getRetrofitInstance().create(PathlocApi.class);
        Call<Pathloc> call = pathlocApi.createPath(newPathloc);
        call.enqueue(new Callback<Pathloc>() {
            @Override
            public void onResponse(Call<Pathloc> call, Response<Pathloc> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Map.this, "Location added successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally, add the new location to the map
                    insertNewLocationOnMap(response.body());
                } else {
                    Toast.makeText(Map.this, "Failed to add location.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pathloc> call, Throwable t) {
                Toast.makeText(Map.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertNewLocationOnMap(Pathloc pathloc) {
        GeoPoint newLocation = new GeoPoint(pathloc.getStartLatitude(), pathloc.getStartLongitude());
        Marker newMarker = new Marker(mapView);
        newMarker.setPosition(newLocation);
        newMarker.setIcon(getResources().getDrawable(R.drawable.ic_location)); // Use a different icon for user-added locations
        newMarker.setTitle(pathloc.getName());

        newMarker.setOnMarkerClickListener((marker, mapView) -> {
            showLocationInfo(pathloc);
            return true;
        });

        mapView.getOverlays().add(newMarker);
        mapView.invalidate();
    }

    private void getAllPathsFromServer() {
        pathlocApi = RetrofitClient.getRetrofitInstance().create(PathlocApi.class);

        Call<List<Pathloc>> call = pathlocApi.getAllPaths();
        call.enqueue(new Callback<List<Pathloc>>() {
            @Override
            public void onResponse(Call<List<Pathloc>> call, Response<List<Pathloc>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pathloc> paths = response.body();
                    insertPathsOnMap(paths);
                } else {
                    Toast.makeText(Map.this, "Failed to retrieve paths from server.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pathloc>> call, Throwable t) {
                Toast.makeText(Map.this, "Error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void insertPathsOnMap(List<Pathloc> paths) {
        for (Pathloc path : paths) {
            GeoPoint pathLocation = new GeoPoint(path.getStartLatitude(), path.getStartLongitude());
            Marker pathMarker = new Marker(mapView);
            pathMarker.setPosition(pathLocation);
            pathMarker.setIcon(getResources().getDrawable(R.drawable.ic_location)); // Use a different icon for path locations
            pathMarker.setTitle(path.getName());

            // Set the marker click listener to show the card view with location info
            pathMarker.setOnMarkerClickListener((marker, mapView) -> {
                showLocationInfo(path);
                return true;
            });

            mapView.getOverlays().add(pathMarker);
        }

        // Refresh the map to display the markers
        mapView.invalidate();
    }

    private void showLocationInfo(Pathloc path) {
        locationNameTextView.setText(path.getName());
        locationDescriptionTextView.setText(path.getDescription());
        locationDifficultyTextView.setText("Difficulty: " + path.getDifficulty());
        locationRatingTextView.setText("Rating: " + (path.getRating() != null ? path.getRating() : "N/A"));

        locationCardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                IMapController mapController = mapView.getController();
                fetchUserLocation(mapController);
            } else {
                Toast.makeText(this, "Location permission is required to display your location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
