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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 10 seconds
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private MapView mapView;
    private FloatingActionButton fabMyLocation;
    private ImageButton addLocationButton;
    private LocationManager locationManager;
    private PathlocApi pathlocApi;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeUI();
        checkNetworkAndLocationAvailability();
        fetchAndPinPathsFromServer();
    }

    private void initializeUI() {
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);

        fabMyLocation = findViewById(R.id.fab_my_location);
        fabMyLocation.setOnClickListener(this::recenterMapOnUserLocation);

        addLocationButton = findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(v -> showAddLocationDialog());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        pathlocApi = RetrofitClient.getRetrofitInstance().create(PathlocApi.class);
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

    private void recenterMapOnUserLocation(View view) {
        if (userLocation != null) {
            GeoPoint userGeoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
            mapView.getController().setCenter(userGeoPoint);
            Toast.makeText(this, "Returning to your location", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show();
        }
    }

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

    private void pinPathOnMap(Pathloc path) {
        GeoPoint pathLocation = new GeoPoint(path.getStartLatitude(), path.getStartLongitude());
        Marker pathMarker = new Marker(mapView);
        pathMarker.setPosition(pathLocation);
        pathMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
        pathMarker.setTitle(path.getName());

        pathMarker.setOnMarkerClickListener((marker, mapView) -> {
            Toast.makeText(Map.this, "Selected Path: " + path.getName(), Toast.LENGTH_SHORT).show();
            return true;
        });

        mapView.getOverlays().add(pathMarker);
        mapView.invalidate();
    }

    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            int difficultyLevel = convertDifficultyToInt(difficulty);

            Pathloc newPathloc = new Pathloc(locationName, "User-added location", userLocation.getLatitude(), userLocation.getLongitude(), difficultyLevel);

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

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location; // Update the user's location
        Log.d(TAG, "User location updated: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
}