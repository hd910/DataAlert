package hd.dataalert;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ResultCallback<Status>, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private BroadcastReceiver receiver;
    private TextView locationTextView;
    private List<Geofence> geofenceList;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        geofenceList = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        locationTextView = (TextView) findViewById(R.id.locationText);

        Button gpsBtn = (Button) findViewById(R.id.getGPSBtn);
        gpsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                locationTextView.setText("");
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                intent.setAction("startListening");
                startService(intent);
            }
        });

        Button geofenceBtn = (Button) findViewById(R.id.setGeofenceBtn);
        geofenceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                removeGeofence();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wifiNeeded = checkWifiStatus();
                Snackbar.make(view, wifiNeeded ? "Wifi is Enabled" : "Wifi is Off", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                checkWifiStatus();
            }
        });


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(LocationService.LOCATION_MESSAGE);
                Double longitude = intent.getDoubleExtra(LocationService.LOCATION_MESSAGE_LON, 0);
                Double latitude = intent.getDoubleExtra(LocationService.LOCATION_MESSAGE_LAT, 0);
                locationTextView.setText(s);

                Intent i = new Intent(MainActivity.this, LocationService.class);
                i.setAction("stopListening");
                startService(i);
                createGeofence(latitude, longitude);
            }
        };
    }

    private void createGeofence(Double lat, Double lon) {
        //https://developers.google.com/android/reference/com/google/android/gms/location/Geofence.html#GEOFENCE_TRANSITION_ENTER
        Snackbar.make(findViewById(android.R.id.content), "Creating geofence", Snackbar.LENGTH_LONG)
                .show();

        geofenceList.add(new Geofence.Builder()
                .setRequestId("CustomGeofence")
                .setCircularRegion(lat, lon, 100)
                .setExpirationDuration(Double.valueOf("7.2e+6").longValue())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        255);
        } else {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
//        if (mGeofencePendingIntent != null) {
//            return mGeofencePendingIntent;
//        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private void removeGeofence(){
        Snackbar.make(findViewById(android.R.id.content), "Removing geofence", Snackbar.LENGTH_LONG)
                .show();
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }

    private boolean checkWifiStatus() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(LocationService.LOCATION_RESULT)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
