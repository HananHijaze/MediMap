package com.example.medimap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Map extends AppCompatActivity {
    MapView mapView;
    CardView locationCardView;
    TextView locationTitle, locationDescription, locationCoordinates;
    ImageButton addLocationButton;

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

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        locationCardView = findViewById(R.id.locationCardView);
        locationTitle = findViewById(R.id.location_title);
        locationDescription = findViewById(R.id.location_description);
        locationCoordinates = findViewById(R.id.location_coordinates);
        addLocationButton = findViewById(R.id.addLocationButton);

        // Set initial map center and zoom level
        GeoPoint startPoint = new GeoPoint(32.81990808206112, 35.00006160083927);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);

        Drawable customMarkerIcon = ContextCompat.getDrawable(this, R.drawable.ic_location);

        // Add a marker at the specified location
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setIcon(customMarkerIcon);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("hachsharat Hayishuv");
        marker.setIcon(customMarkerIcon);
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            displayLocationData("hachsharat Hayishuv", "Multipurpose building", marker1.getPosition());
            return true;
        });
        mapView.getOverlays().add(marker);

        // Set up the button to add a new location
        addLocationButton.setOnClickListener(v -> showAddLocationDialog());
    }

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

    private void displayLocationData(String title, String description, GeoPoint coordinates) {
        locationTitle.setText(title);
        locationDescription.setText(description);
        locationCoordinates.setText(String.format("Coordinates: %f, %f", coordinates.getLatitude(), coordinates.getLongitude()));

        locationCardView.setVisibility(View.VISIBLE);
    }
}
