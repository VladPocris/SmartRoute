package com.example.smartroute;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartroute.model.TripStep;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    private final List<TripStep> steps;

    public StepsAdapter(List<TripStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_trip_step, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TripStep step = steps.get(position);

        holder.tvFromTo.setText(holder.itemView.getContext().getString(R.string.step_title, step.getFrom(), step.getTo()));
        holder.tvDistance.setText(holder.itemView.getContext().getString(R.string.step_distance, step.getDistanceKm()));
        holder.tvDuration.setText(holder.itemView.getContext().getString(R.string.step_duration, step.getDurationMinutes()));

        holder.btnShowMap.setOnClickListener(v -> {
            holder.btnShowMap .setVisibility(View.GONE);
            holder.mapView    .setVisibility(View.VISIBLE);
            holder.btnCloseMap.setVisibility(View.VISIBLE);

            // MUST forward at least onCreate/onResume
            holder.mapView.onCreate(null);
            holder.mapView.onResume();

            holder.mapView.getMapAsync(googleMap -> {
                // 1) enable the same UI on each inline map
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                if (ContextCompat.checkSelfPermission(
                        holder.mapView.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }

                // 2) draw the route polyline
                List<LatLng> pts = PolyUtil.decode(step.getEncodedPolyline());
                googleMap.clear();
                googleMap.addPolyline(new PolylineOptions().addAll(pts).width(8));

                // 3) fit camera
                LatLngBounds.Builder b = new LatLngBounds.Builder();
                for (LatLng p : pts) b.include(p);
                googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(b.build(), 50)
                );
            });
        });

        holder.btnCloseMap.setOnClickListener(v -> {
            holder.mapView.setVisibility(View.GONE);
            holder.btnCloseMap.setVisibility(View.GONE);
            holder.btnShowMap .setVisibility(View.VISIBLE);

            // tidy up
            holder.mapView.onPause();
            holder.mapView.onDestroy();
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView     tvFromTo, tvDistance, tvDuration;
        MaterialButton btnShowMap, btnCloseMap;
        MapView      mapView;

        ViewHolder(View itemView) {
            super(itemView);
            tvFromTo    = itemView.findViewById(R.id.tvFromTo);
            tvDistance  = itemView.findViewById(R.id.tvDistance);
            tvDuration  = itemView.findViewById(R.id.tvDuration);
            btnShowMap  = itemView.findViewById(R.id.btnShowMap);
            mapView     = itemView.findViewById(R.id.stepMapView);
            btnCloseMap = itemView.findViewById(R.id.btnCloseMap);
        }
    }
}
