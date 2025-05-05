package com.example.smartroute;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartroute.api.ApiClient;
import com.example.smartroute.api.SmartRouteApi;
import com.example.smartroute.model.TripStep;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripStepsActivity extends AppCompatActivity {
    private RecyclerView    rv;
    private SmartRouteApi   api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_steps);

        // 1) grab the code
        String code = getIntent().getStringExtra("code");
        if (code == null) {
            Toast.makeText(this, "No trip code provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) retrofit instance (or via your ApiClient)
        api = ApiClient.get();

        // 3) RecyclerView
        rv = findViewById(R.id.stepsList);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 4) Finish button
        MaterialButton finishBtn = findViewById(R.id.btnFinishTrip);
        finishBtn.setOnClickListener(v -> api.deleteTrip(code).enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> resp
            ) {
                if (resp.isSuccessful()) {
                    Toast.makeText(
                            TripStepsActivity.this,
                            "Trip finished",
                            Toast.LENGTH_SHORT
                    ).show();
                    setResult(RESULT_OK);
                    finish();  // back to main, fresh
                } else {
                    Toast.makeText(
                            TripStepsActivity.this,
                            "Failed to finish trip",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(
                        TripStepsActivity.this,
                        "Network error",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }));

        // 5) Load the steps list
        api.getSteps(code).enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<TripStep>> call,
                    @NonNull Response<List<TripStep>> resp
            ) {
                if (resp.isSuccessful() && resp.body() != null) {
                    rv.setAdapter(new StepsAdapter(resp.body()));
                } else {
                    Toast.makeText(
                            TripStepsActivity.this,
                            "Failed to load steps",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TripStep>> call, @NonNull Throwable t) {
                Toast.makeText(
                        TripStepsActivity.this,
                        "Network error",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
