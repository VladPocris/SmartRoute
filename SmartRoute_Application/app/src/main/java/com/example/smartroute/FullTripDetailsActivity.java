package com.example.smartroute;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartroute.model.TripStep;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class FullTripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TripStep step;
    private MapView mapView;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_full_trip_details);

        step = (TripStep) getIntent().getSerializableExtra("step");

        TextView title     = findViewById(R.id.trip_step_title);
        TextView distInfo  = findViewById(R.id.distance_info);
        TextView durInfo   = findViewById(R.id.duration_info);
        title.setText(getString(R.string.step_title, step.getFrom(), step.getTo()));
        distInfo.setText(getString(R.string.step_distance, step.getDistanceKm()));
        durInfo.setText(getString(R.string.step_duration, step.getDurationMinutes()));

        Button show = findViewById(R.id.btnShowMap);
        Button close = findViewById(R.id.btnCloseMap);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(s);
        mapView.getMapAsync(this);

        show.setOnClickListener(v -> mapView.setVisibility(View.VISIBLE));
        close.setOnClickListener(v -> mapView.setVisibility(View.GONE));
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        List<LatLng> pts = PolyUtil.decode(step.getEncodedPolyline());
        googleMap.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().addAll(pts).width(8));
        com.google.android.gms.maps.model.LatLngBounds.Builder b = new com.google.android.gms.maps.model.LatLngBounds.Builder();
        for (LatLng p : pts) b.include(p);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 50));
    }

    @Override protected void onResume()   { super.onResume();   mapView.onResume(); }
    @Override protected void onPause()    { super.onPause();    mapView.onPause(); }
    @Override protected void onDestroy()  { super.onDestroy();  mapView.onDestroy(); }
    @Override public    void onLowMemory(){ super.onLowMemory();mapView.onLowMemory(); }
}
