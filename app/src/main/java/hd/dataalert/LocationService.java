package hd.dataalert;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hayde on 19-Mar-17.
 */

public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    private LocalBroadcastManager broadcastManager;

    static final public String LOCATION_RESULT = "com.hd.dataalert.location";
    static final public String LOCATION_MESSAGE = "com.hd.dataalert.locationMessage";

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent.getAction().equals("startListening")) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        255);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            }
        }
        else {
            if (intent.getAction().equals("stopListening")) {
                locationManager.removeUpdates(this);
                locationManager = null;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            displayLocation(location);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void displayLocation(Location location) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        String locationAddress ="";
        if(address.size() > 0){
            locationAddress = address.get(0).getAddressLine(0)+ " " + address.get(0).getAddressLine(1);
        }
        
        sendResult(locationAddress);

    }


    public void sendResult(String message) {
        Intent intent = new Intent(LOCATION_RESULT);
        if(message != null)
            intent.putExtra(LOCATION_MESSAGE, message);
        broadcastManager.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
