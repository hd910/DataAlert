package hd.dataalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;
    private TextView locationTextView;
    private List<Geofence> geofenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationTextView = (TextView)findViewById(R.id.locationText);

        Button gpsBtn = (Button) findViewById(R.id.getGPSBtn);
        gpsBtn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                locationTextView.setText("");
                Intent intent = new Intent(MainActivity.this, LocationService.class);
                intent.setAction("startListening");
                startService(intent);
            }
        });

//        Button geofenceBtn = (Button) findViewById(R.id.setGeofenceBtn);
//        geofenceBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                createGeofence();
//            }
//        });


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
                Long longitude = intent.getLongExtra(LocationService.LOCATION_MESSAGE_LON, 0);
                Long latitude = intent.getLongExtra(LocationService.LOCATION_MESSAGE_LAT, 0);
                locationTextView.setText(s);

                Intent i = new Intent(MainActivity.this, LocationService.class);
                i.setAction("stopListening");
                startService(i);
                createGeofence(latitude, longitude);
            }
        };
    }

    private void createGeofence(Long lat, Long lon) {
        //https://developers.google.com/android/reference/com/google/android/gms/location/Geofence.html#GEOFENCE_TRANSITION_ENTER
        geofenceList.add(new Geofence.Builder()
            .setRequestId("CustomGeoFence")
            .setCircularRegion(lat, lon, 100)
            .setExpirationDuration(Double.valueOf("2.592e+8").longValue())
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private boolean checkWifiStatus() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
}
