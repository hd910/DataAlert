package hd.dataalert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.io.IOException;
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
                geofenceMessage = address.get(0).getAddressLine(0)+ " " + address.get(0).getAddressLine(1);
            }
            sendNotification(geofenceMessage, geofenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER?"Geofence Entered":"Geofence Exit");
        }


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
}
