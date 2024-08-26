package com.example.medimap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class Map extends AppCompatActivity {
    MapView mapView;

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
        // Set up the MapView
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true); // Enable pinch to zoom, etc.

        // Set initial map center and zoom level
        //32.80083539442172, 34.966981024359036
        GeoPoint startPoint = new GeoPoint(32.81990808206112, 35.00006160083927); // San Francisco coordinates
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);
        Drawable customMarkerIcon = ContextCompat.getDrawable(this, R.drawable.ic_location);

        // Add a marker at the specified location
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setIcon(customMarkerIcon);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("hachsharat Hatishuv");
        marker.setIcon(customMarkerIcon);
        mapView.getOverlays().add(marker);

        mapView.getOverlays().add(marker);
        List<GeoPoint> trackingPoints = new ArrayList<>();
        trackingPoints.add(startPoint); // Starting point
        //32.733984958494936, 35.07107631931142
        trackingPoints.add(new GeoPoint(32.74616455590612, 35.025553384666075)); // Example point 1
        trackingPoints.add(new GeoPoint(32.80083539442172, 34.966981024359036)); // Example point 2
        trackingPoints.add(new GeoPoint(32.733984958494936, 35.07107631931142)); // Example point 3

        // Create a Polyline to represent the path
        for (GeoPoint point : trackingPoints) {
            // Create a list of points for the Polyline
            List<GeoPoint> segmentPoints = new ArrayList<>();
            segmentPoints.add(startPoint); // Start point
            segmentPoints.add(point);      // End point

            // Create a Polyline for the segment
            Polyline segmentPath = new Polyline();
            segmentPath.setPoints(segmentPoints);
            segmentPath.setColor(0xFF0000FF); // Blue color for the path

            // Add the Polyline to the map's overlays
            mapView.getOverlays().add(segmentPath);

            // Add a marker for the tracking point
            Marker trackMarker = new Marker(mapView);
            trackMarker.setPosition(point);
            trackMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            trackMarker.setTitle("Tracking Point");
            mapView.getOverlays().add(trackMarker);
    }
}
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // Needed for compass, my location overlays, v6.0.0 and up
    }
}