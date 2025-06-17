package com.example.smartroute;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartroute.api.SmartRouteApi;
import com.example.smartroute.model.Trip;
import com.example.smartroute.model.TripRequestDto;
import com.example.smartroute.model.TripStep;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.button.MaterialButton;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PickLocationsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap         mMap;
    private MapView           mapView;
    private SmartRouteApi     api;
    private final List<String> extraLocations = new ArrayList<>();
    private boolean            isEditing      = false;
    private String             editingCode    = null;
    private EditText     startLocation, endLocation, codeInput;
    private View         addButton, removeButton, optimizeBtn, retrieveBtn, planAgainBtn;
    private LinearLayout codeQuery, locationContainer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE_START  = 1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE_END    = 1002;
    private static final int REQUEST_STEPS = 2001;

    @SuppressLint("MissingInflatedId")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_locations);

        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("https://smartrouteapiservice.azurewebsites.net/")
                .baseUrl("https://smartroute-i92g.onrender.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(SmartRouteApi.class);

        if (!Places.isInitialized()) {
            //noinspection SpellCheckingInspection
            Places.initialize(getApplicationContext(),
                    "AIzaSyAdgFBlUwZUtYN77TyWnOHUUvdoWifpfCg");
        }

        startLocation     = findViewById(R.id.startLocation);
        endLocation       = findViewById(R.id.endLocation);
        codeInput         = findViewById(R.id.startLocation2);
        codeQuery         = findViewById(R.id.codeQuery);
        locationContainer = findViewById(R.id.locationContainer);
        addButton    = findViewById(R.id.addButton);
        removeButton = findViewById(R.id.removeButton);
        optimizeBtn  = findViewById(R.id.btnOptimize);
        retrieveBtn  = findViewById(R.id.btnRetrieveTrip);
        planAgainBtn = findViewById(R.id.btnPlanAnotherTrip);
        planAgainBtn.setVisibility(View.GONE);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        startLocation.setFocusable(false);
        startLocation.setOnClickListener(v ->
                launchAutocomplete(AUTOCOMPLETE_REQUEST_CODE_START)
        );
        endLocation.setFocusable(false);
        endLocation.setOnClickListener(v ->
                launchAutocomplete(AUTOCOMPLETE_REQUEST_CODE_END)
        );

        addButton.setOnClickListener(v -> onAddWaypoint());
        removeButton.setOnClickListener(v -> onRemoveWaypoint());

        optimizeBtn.setOnClickListener(v -> onPlanOrUpdate(false));
        retrieveBtn.setOnClickListener(v -> onPlanOrUpdate(true));

        planAgainBtn.setOnClickListener(v -> resetUI());
    }

    private void onAddWaypoint() {
        String o = startLocation.getText().toString().trim();
        if (o.isEmpty()) {
            Toast.makeText(this,"Please set the origin",Toast.LENGTH_SHORT).show();
            return;
        }
        String d = endLocation.getText().toString().trim();
        if (d.isEmpty()) {
            Toast.makeText(this,"Please set the destination",Toast.LENGTH_SHORT).show();
            return;
        }
        if (extraLocations.contains(d)) {
            Toast.makeText(this,"Already added",Toast.LENGTH_SHORT).show();
            return;
        }
        extraLocations.add(d);
        Chip chip = getChip(d);
        locationContainer.addView(chip);
        endLocation.setText("");
    }

    private void onRemoveWaypoint() {
        int c = extraLocations.size();
        if (c==0) return;
        extraLocations.remove(c-1);
        locationContainer.removeViewAt(c-1);
    }

    // Decide POST vs GET vs PUT
    private void onPlanOrUpdate(boolean byCode) {
        if (byCode) {
            String code = codeInput.getText().toString().trim();
            if (!code.matches("\\d{6}")) {
                Toast.makeText(this,"Enter a 6-digit code",Toast.LENGTH_SHORT).show();
                return;
            }
            isEditing   = false;
            editingCode = code;
            enqueueCall(true, code);
        } else {
            enqueueCall(false, null);
        }
    }

    private void enqueueCall(boolean byCode, @Nullable String code) {
        List<String> all = new ArrayList<>();

        if (!byCode) {
            if (endLocation.getText().toString().trim().isEmpty() && !extraLocations.isEmpty()) {
                int idx = extraLocations.size()-1;
                String last = extraLocations.remove(idx);
                locationContainer.removeViewAt(idx);
                endLocation.setText(last);
            }
            String o = startLocation.getText().toString().trim();
            String d = endLocation.getText().toString().trim();
            if (o.isEmpty()||d.isEmpty()) {
                Toast.makeText(this,"Set both origin and destination",Toast.LENGTH_SHORT).show();
                return;
            }
            all.add(o);
            all.addAll(extraLocations);
            all.add(d);
        }

        extraLocations.clear();
        locationContainer.removeAllViews();
        if (mMap!=null) mMap.clear();

        TripRequestDto dto = new TripRequestDto();
        dto.setLocations(all);
        dto.setName("custom");

        Callback<Trip> cb = new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Trip> call, Response<Trip> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(PickLocationsActivity.this,
                            byCode
                                    ? "Trip not found"
                                    : "Sorry, due to geolocation limits the drive path couldn't be established",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                showSummary(resp.body(), all);
            }

            @Override
            public void onFailure(@NonNull Call<Trip> call, @NonNull Throwable t) {
                Toast.makeText(PickLocationsActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
            }
        };

        if (byCode) {
            api.getTrip(code).enqueue(cb);
        } else if (isEditing) {
            api.updateTrip(editingCode, dto).enqueue(cb);
            isEditing = false;
        } else {
            api.createTrip(dto).enqueue(cb);
        }
    }

    private void showSummary(Trip trip, List<String> all) {
        startLocation.setVisibility(View.GONE);
        endLocation  .setVisibility(View.GONE);
        addButton    .setVisibility(View.GONE);
        removeButton .setVisibility(View.GONE);
        optimizeBtn  .setVisibility(View.GONE);
        planAgainBtn .setVisibility(View.VISIBLE);

        View card = LayoutInflater.from(this)
                .inflate(R.layout.activity_item_trip_summary, locationContainer, false);
        TextView tvCode     = card.findViewById(R.id.tvTripCode);
        TextView tvFromTo   = card.findViewById(R.id.tvFromTo);
        TextView tvDistance = card.findViewById(R.id.tvDistance);
        TextView tvDuration = card.findViewById(R.id.tvDuration);
        MaterialButton edit = card.findViewById(R.id.btnEditTrip);

        tvCode.setText(getString(R.string.code, trip.getCode()));

        if (!all.isEmpty()) {
            tvFromTo.setText(getString(R.string.arrow, all.get(0), all.get(all.size() - 1)));
        } else {
            List<TripStep> steps = trip.getSteps();
            String from = steps.get(0).getFrom();
            String to   = steps.get(steps.size()-1).getTo();
            tvFromTo.setText(getString(R.string.arrow, from, to));
        }

        double km = 0;
        //noinspection SpellCheckingInspection
        int mins = 0;
        for (TripStep s : trip.getSteps()) {
            km   += s.getDistanceKm();
            mins += s.getDurationMinutes();
        }
        tvDistance.setText(String.format(getString(R.string._1f_km), km));
        tvDuration.setText(String.format(getString(R.string.d_min), mins));

        MaterialButton details = card.findViewById(R.id.btnShowTripDetails);
        details.setOnClickListener(v -> {
            Intent i = new Intent(PickLocationsActivity.this, TripStepsActivity.class);
            i.putExtra("code", trip.getCode());
            //noinspection deprecation
            startActivityForResult(i, REQUEST_STEPS);
        });
        edit.setOnClickListener(v -> {
            locationContainer.removeAllViews();

            isEditing   = true;
            editingCode = trip.getCode();

            startLocation.setVisibility(View.VISIBLE);
            endLocation  .setVisibility(View.VISIBLE);
            addButton    .setVisibility(View.VISIBLE);
            removeButton .setVisibility(View.VISIBLE);
            optimizeBtn  .setVisibility(View.VISIBLE);
            planAgainBtn .setVisibility(View.GONE);

            List<String> populate = all.isEmpty()
                    ? rebuildFromTrip(trip)
                    : new ArrayList<>(all);

            startLocation.setText(populate.get(0));
            endLocation  .setText(populate.get(populate.size()-1));

            extraLocations.clear();
            for (int i = 1; i < populate.size() - 1; i++) {
                String w = populate.get(i);
                extraLocations.add(w);
                Chip chip = getChip(w);
                locationContainer.addView(chip);
            }

            if (mMap != null) mMap.clear();
        });

        locationContainer.addView(card);
        String poly = trip.getOverviewPolyline();
        if (poly != null && !poly.isEmpty()) {
            List<LatLng> pts = PolyUtil.decode(poly);
            mMap.addPolyline(new PolylineOptions().addAll(pts).width(8));
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            for (LatLng p : pts) b.include(p);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 50));
        }
    }

    @NonNull
    private Chip getChip(String w) {
        Chip chip = new Chip(this);
        chip.setText(w);
        chip.setCloseIconVisible(false);
        chip.setChipBackgroundColorResource(R.color.success_color_dark);
        chip.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 8, 8, 8);
        chip.setLayoutParams(lp);
        return chip;
    }

    private List<String> rebuildFromTrip(Trip trip) {
        List<String> list = new ArrayList<>();
        for (TripStep s : trip.getSteps()) {
            list.add(s.getFrom());
        }
        TripStep last = trip.getSteps().get(trip.getSteps().size() - 1);
        list.add(last.getTo());
        return list;
    }

    private void resetUI() {
        startLocation.setText("");
        endLocation  .setText("");
        codeInput    .setText("");
        startLocation.setVisibility(View.VISIBLE);
        endLocation  .setVisibility(View.VISIBLE);
        addButton    .setVisibility(View.VISIBLE);
        removeButton .setVisibility(View.VISIBLE);
        optimizeBtn  .setVisibility(View.VISIBLE);
        retrieveBtn  .setVisibility(View.VISIBLE);
        codeQuery    .setVisibility(View.VISIBLE);
        planAgainBtn .setVisibility(View.GONE);

        extraLocations.clear();
        locationContainer.removeAllViews();
        if (mMap!=null) {
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(53.3498,-6.2603),5f));
        }
        isEditing = false;
        editingCode = null;
    }

    @Override public void onMapReady(@NonNull GoogleMap gMap) {
        mMap = gMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng start = new LatLng(53.3498,-6.2603);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,5f));
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void launchAutocomplete(int req) {
        //noinspection deprecation
        List<Place.Field> f = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG
        );
        //noinspection deprecation
        Intent i = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, f)
                .setTypeFilter(TypeFilter.CITIES)
                .build(this);
        //noinspection deprecation
        startActivityForResult(i, req);
    }

    @Override protected void onActivityResult(int req,int res,@Nullable Intent data) {
        super.onActivityResult(req,res,data);
        if (res==RESULT_OK && data!=null) {
            Place p = Autocomplete.getPlaceFromIntent(data);
            //noinspection deprecation
            String city = p.getName(), country="";
            if (p.getAddressComponents()!=null) {
                for (AddressComponent ac:p.getAddressComponents().asList()){
                    if (ac.getTypes().contains("country")){
                        country=ac.getName();
                        break;
                    }
                }
            }
            //noinspection SpellCheckingInspection
            String disp = city + (country.isEmpty()?"":" ,"+country);
            EditText tgt = req==AUTOCOMPLETE_REQUEST_CODE_START
                    ? startLocation : endLocation;
            tgt.setText(disp);
        } else if (res==AutocompleteActivity.RESULT_ERROR && data!=null) {
            Status st = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this,"Error: "+st.getStatusMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        if (req == REQUEST_STEPS && res == RESULT_OK) {
            resetUI();
        }
    }

    @Override protected void onResume()   { super.onResume();   mapView.onResume(); }
    @Override protected void onPause()    { super.onPause();    mapView.onPause(); }
    @Override protected void onDestroy()  { super.onDestroy();  mapView.onDestroy(); }
    @Override public    void onLowMemory(){ super.onLowMemory();mapView.onLowMemory(); }
}
