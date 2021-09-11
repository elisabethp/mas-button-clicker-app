package com.example.firstprogrammingassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private String databaseURL = "https://first-project-2fb84-default-rtdb.firebaseio.com/";
    private DatabaseReference database = FirebaseDatabase.getInstance(databaseURL).getReference();
    private Button button;
    private Button resetButton;
    private TextView count;
    private TextView weather;
    private LocationManager locationManager;
    private LocationListener locationListener;

    ValueEventListener countListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Integer countInt = dataSnapshot.getValue(Integer.class);
            Log.w("MainAct#", "count:" + countInt);
            count.setText(countInt.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = findViewById(R.id.count);
        button = findViewById(R.id.button);
        resetButton = findViewById(R.id.button_reset);
        button.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        database.child("button_count").addValueEventListener(countListener);

        weather = findViewById(R.id.weather);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {//Can add more as per requirement

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);
            }
        }
        else {
            locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                updateButtonClick(false);
                break;
            case R.id.button_reset:
                updateButtonClick(true);
        }
    }

    private void updateButtonClick(boolean reset) {
        if (reset) {
            database.child("button_count").setValue(0);
            return;
        }
        int curr_count = Integer.parseInt((String)count.getText());
        database.child("button_count").setValue(curr_count + 1);
    }

    private void updateWeather(double latitude, double longitude) {// Instantiate the RequestQueue.
        String API_KEY = "bb199e7d799dfc4b62044c27b6992a2e";
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String description = response.getJSONArray("weather").getJSONObject(0).getString("description");
                        if (description != null) {
                            weather.setText(description);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        updateWeather(latitude, longitude);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}
}