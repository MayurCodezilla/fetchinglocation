package com.example.textviewvibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.Context;

import android.content.DialogInterface;

import android.content.Intent;

import android.content.IntentSender;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.Uri;

import android.os.Bundle;

import android.os.Looper;
import android.provider.Settings;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationSettingsRequest;

import com.google.android.gms.location.LocationSettingsResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;

import com.karumi.dexter.MultiplePermissionsReport;

import com.karumi.dexter.PermissionToken;

import com.karumi.dexter.listener.PermissionRequest;

import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    private Button btn_get;


    FusedLocationProviderClient mFusedLocationClient;

    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;


    @Override


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,  0, (LocationListener) this);
        }
        btn_get = findViewById(R.id.btn_get);


        btn_get.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    enableLocationSettings();

                } else {

                    requestAppPermissions();

                }

            }

        });

        /////

        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        ///


        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }

    /////



    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {

            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");

                            Toast.makeText(MainActivity.this, ""+latitudeTextView.getText()+ " "+longitTextView.getText(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(100);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");

            Toast.makeText(MainActivity.this, ""+latitudeTextView.getText()+ " "+longitTextView.getText(), Toast.LENGTH_SHORT).show();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,  0, (LocationListener) this);
        }

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }


    ////
    private void requestAppPermissions() {

        Dexter.withActivity(MainActivity.this)

                .withPermissions(

                        Manifest.permission.ACCESS_FINE_LOCATION,

                        Manifest.permission.ACCESS_COARSE_LOCATION)

                .withListener(new MultiplePermissionsListener() {

                    @Override

                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            startService(new Intent(MainActivity.this, ForegroundService.class));
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }

                    }

                    @Override

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }

                })

                .onSameThread()

                .check();

    }

    private void showSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Need Permissions");

        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");

        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                openSettings();

            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }

        });

        builder.show();

    }

    private void openSettings() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        Uri uri = Uri.fromParts("package", getPackageName(), null);

        intent.setData(uri);

        startActivityForResult(intent, 101);

    }

    protected void enableLocationSettings() {

        LocationRequest locationRequest = LocationRequest.create()

                .setInterval(1000)

                .setFastestInterval(3000)

                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()

                .addLocationRequest(locationRequest);

        LocationServices

                .getSettingsClient(this)

                .checkLocationSettings(builder.build())

                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {

                    // startUpdatingLocation(...);

                })

                .addOnFailureListener(this, ex -> {

                    if (ex instanceof ResolvableApiException) {

                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.

                        try {

                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().

                            ResolvableApiException resolvable = (ResolvableApiException) ex;

                            resolvable.startResolutionForResult(this, 123);

                        } catch (IntentSender.SendIntentException sendEx) {


                        }

                    }

                });

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {


        Dexter.withActivity(MainActivity.this)

                .withPermissions(

                        Manifest.permission.ACCESS_FINE_LOCATION,

                        Manifest.permission.ACCESS_COARSE_LOCATION)

                .withListener(new MultiplePermissionsListener() {

                    @Override

                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            startService(new Intent(MainActivity.this, ForegroundService.class));
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }

                    }

                    @Override

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }

                })

                .onSameThread()

                .check();


//        Toast.makeText(this, "background", Toast.LENGTH_SHORT).show();

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {

        stopService(new Intent(MainActivity.this, ForegroundService.class));
//        Toast.makeText(this, "foreground", Toast.LENGTH_SHORT).show();
    }

}