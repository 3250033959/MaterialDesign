package org.proverbio.android.fragment.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.proverbio.android.context.ApplicationContext;
import org.proverbio.android.material.R;
import org.proverbio.android.util.StringConstants;

import java.util.List;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 * An IntentService that gets notified by the Android OS when a Geo-fence transition is detected
 */
public class GeofenceTransitionsIntentService extends IntentService
{
    protected static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    public static final String GEO_FENCE_TRANSITION_ACTION = "android.intent.action.GEO_FENCE_TRANSITION";
    public static final String GEO_FENCE_TRANSITION_KEY = "geo-fence-transition-event";

    public GeofenceTransitionsIntentService()
    {
        // Use the TAG to name the worker thread.
        super( TAG );
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent( Intent intent )
    {
        Log.d(TAG, "Geofencing event has been received");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent( intent );

        if ( geofencingEvent.hasError() )
        {
            String errorMessage = LocationServiceSingleton.getGeofenceErrorString(this, geofencingEvent.getErrorCode());
            Log.e( TAG, errorMessage );
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
        {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            for ( Geofence geofence : triggeringGeofences )
            {
                ParcelableGeofence mapItem = LocationServiceSingleton.getInstance(this).findGeofenceById(geofence.getRequestId());

                if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
                {
                    if ( ApplicationContext.getInstance().getVisibleActivity() != null )
                    {
                        Intent intentUpdate = new Intent();
                        intentUpdate.setAction( GEO_FENCE_TRANSITION_ACTION );
                        intentUpdate.addCategory( Intent.CATEGORY_DEFAULT );
                        intentUpdate.putExtra( StringConstants.ITEM_KEY, mapItem );
                        intentUpdate.putExtra( GEO_FENCE_TRANSITION_KEY, geofenceTransition );
                        sendBroadcast( intentUpdate );
                    }
                    else
                    {
                        /*sendNotification(
                                mapItem,
                                geofenceTransition,
                                getBaseContext().getString( R.string.location_update ),
                                getBaseContext().getString( R.string.entered_site_geofence ).replace( "{}", mapItem.getTitle() ) );*/
                    }
                }
                else if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
                {
                    if ( ApplicationContext.getInstance().getVisibleActivity() != null )
                    {
                        Intent intentUpdate = new Intent();
                        intentUpdate.setAction( GEO_FENCE_TRANSITION_ACTION );
                        intentUpdate.addCategory( Intent.CATEGORY_DEFAULT );
                        intentUpdate.putExtra( StringConstants.ITEM_KEY, mapItem );
                        intentUpdate.putExtra( GEO_FENCE_TRANSITION_KEY, geofenceTransition );
                        sendBroadcast( intentUpdate );
                    }
                    else
                    {
                        /*sendNotification(
                                mapItem,
                                geofenceTransition,
                                getBaseContext().getString( R.string.location_update ),
                                getBaseContext().getString( R.string.exited_site_geofence ).replace( "{}", mapItem.getTitle() ) );*/
                    }
                }
            }
        }
        else
        {
            // Log the error.
            Log.e( TAG, getBaseContext().getString( R.string.geofence_transition_invalid_type, geofenceTransition ) );
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification( ParcelableGeofence parcelableGeofence, int geofenceTransition )
    {
        Intent intent = new Intent();
        intent.setAction( Intent.ACTION_MAIN );
        intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.putExtra( StringConstants.ITEM_KEY, parcelableGeofence );
        intent.putExtra( GEO_FENCE_TRANSITION_KEY, geofenceTransition );
        PendingIntent contentIntent = PendingIntent.getActivity( this, Integer.valueOf( parcelableGeofence.getId() ),
                intent, PendingIntent.FLAG_UPDATE_CURRENT );

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this );

        // Define the notification settings.
        builder.setSmallIcon( R.drawable.ic_action_chat )
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setContentTitle( "Hello" )
                .setContentText( "lsdkgjsldkgjsldk lskdjgsldkg" )
                .setSound( RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) )
                .setContentIntent( contentIntent )
                .setAutoCancel( true );

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE );

        // Issue the notification
        mNotificationManager.notify( Integer.valueOf( parcelableGeofence.getId() ), builder.build() );
    }
}
