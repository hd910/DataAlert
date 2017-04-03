package hd.dataalert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hayde on 20-Mar-17.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> address = null;
            try {
                address = geocoder.getFromLocation(geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String geofenceMessage = "";
            if(address.size() > 0){
                geofenceMessage += isUsingWiFi()?"You are using WiFi \n": "You are using Data \n";
                geofenceMessage += address.get(0).getAddressLine(0)+ " " + address.get(0).getAddressLine(1);

            }
            sendNotification(geofenceMessage, geofenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER?"Geofence Entered":"Geofence Exit");


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM-hh:mm");
            String date = simpleDateFormat.format(new Date());
            String status = geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER? "Enter":"Leave";

            recordEvent(status, date, geofenceMessage);
        }


    }

    private void recordEvent(String status,String date, String geofenceMessage) {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        Event e = new Event(status,date, geofenceMessage);
        db.addEvent(e);
    }

    private void sendNotification(String notificationMessage, String transition) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification  = new Notification.Builder(this)
                .setContentTitle(transition)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

    private boolean checkWifiStatus() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    private boolean isUsingWiFi() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        //WiFi ON -> Need to check internet connectivity
        if(wifi.isWifiEnabled()){
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            //true if internet is connected and using WIFI
            return (networkInfo != null && networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI));

        }

        //WiFi OFF -> need to alert user
        return false;
    }
}
