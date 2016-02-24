package org.proverbio.android.fragment.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.proverbio.android.context.SharedPreferencesManager;
import org.proverbio.android.fragment.location.LocationPermissionManager;
import org.proverbio.android.material.R;
import org.proverbio.android.util.JsonManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 * An IntentService that adds/updates/removes Geo-fences to the device
 */
public class LocationServiceSingleton implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = LocationServiceSingleton.class.getSimpleName();

    //Shared Preference Key
    public static final String GEO_FENCES_PREF_KEY = "g_f_i";
    public static final String GEO_FENCES_ENABLED_PREF_KEY = "g_f_e";
    public static final String GEO_FENCES_COUNT_PREF_KEY = "g_f_c";

    //Static Singleton Instance
    private static volatile LocationServiceSingleton instance;

    //The app Context
    private final Context context;

    //Our Google API Client
    private final GoogleApiClient googleApiClient;

    //It's not final to be initialized lazily getGeofencesList();
    private List<ParcelableGeofence> geofencesList;

    //A Pending Intent to communicate with IntentService {@link GeofenceTransitionsIntentService}
    private PendingIntent geofencePendingIntent;

    //Adding Queue - in the case the client is not connected then will be queued to be saved
    private List<ParcelableGeofence> addingQueue;

    //Removing Queue - in the case the client is not connected then will be queued to be removed
    private List<ParcelableGeofence> removingQueue;

    private LocationServiceSingleton(Context context)
    {
        this.context = context;

        this.geofencesList = new ArrayList<>();

        //Builds our Google API Client
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Connect to Goolge
        googleApiClient.connect();

        //Init the Queues
        getAddingQueue();
        getRemovingQueue();

        //Loads Geo-fences from the SharedPreferences
        getGeofencesList();
    }

    public static synchronized LocationServiceSingleton getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new LocationServiceSingleton(context);
        }

        return instance;
    }

    /**
     * Saves a Geo-fence to the device's GeofencingApi
     * @param parcelableGeofence
     */
    public void saveGeofence(final ParcelableGeofence parcelableGeofence)
    {
        if (parcelableGeofence == null || !parcelableGeofence.isValid())
        {
            Log.e(TAG, "This Geo-fence can't be added because is null or invalid!!");
            return;
        }

        if (!googleApiClient.isConnected())
        {
            addingQueue.add(parcelableGeofence);
            googleApiClient.connect();
            return;
        }

        float defaultRadius = context.getResources().getInteger(R.integer.default_project_radius);

        Geofence geofence = new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(parcelableGeofence.getId())

                // Set the circular region of this geofence.
                .setCircularRegion(
                        parcelableGeofence.getLatitude(),
                        parcelableGeofence.getLongitude(),
                        parcelableGeofence.getRadius() < defaultRadius ? defaultRadius : parcelableGeofence.getRadius() )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Geofence.NEVER_EXPIRE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geo-fence.
                .build();

        //Building Geo-fencing request with the new Geofence
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEO-FENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        geofencingRequestBuilder.addGeofence(geofence);

        if (LocationPermissionManager.isLocationPermissionGranted(context))
        {
            try
            {
                PendingResult<Status> pendingResult =
                        LocationServices.GeofencingApi.addGeofences(
                                googleApiClient,
                                // The GeofenceRequest object.
                                geofencingRequestBuilder.build(),
                                // A pending intent that that is reused when calling removeGeofences(). This
                                // pending intent is used to generate an intent when a matched geofence
                                // transition is observed.
                                getGeofencePendingIntent()
                        );

                pendingResult.setResultCallback(new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        if (status.isSuccess())
                        {
                            getGeofencesList().add(parcelableGeofence);
                            writeToSharedPreferences();
                            Log.d(TAG, "Added Geo-fence successfully. Id: " + parcelableGeofence.getId() + ", Address: " + parcelableGeofence.getAddress());
                        }
                        else
                        {
                            Log.e(TAG, "Couldn't add Geo-fence. Id: " + parcelableGeofence.getId() + ", Address: " + parcelableGeofence.getAddress() + ", Error: " + getGeofenceErrorString(context, status.getStatusCode()));
                        }
                    }
                });
            }
            catch ( SecurityException securityException )
            {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                // This should not ever happen
                logSecurityException( securityException );
            }
        }
        else
        {
            Log.e(TAG, context.getString(R.string.location_permission_declined));
        }

    }


    /**
     * Removes a Geo-fence from device's GeofencingApi and SharedPreferences
     * @param parcelableGeofence
     */
    public void removeGeofence(final ParcelableGeofence parcelableGeofence)
    {
        if ( parcelableGeofence == null || !parcelableGeofence.isValid() )
        {
            Log.e(TAG, "This MapItem can't be added as Geofence because is null or it doesn't have the needed information");
            return;
        }

        if (!googleApiClient.isConnected())
        {
            removingQueue.add(parcelableGeofence);
            googleApiClient.connect();
            return;
        }

        List<String> geofenceToRemoveList = new ArrayList<>();
        geofenceToRemoveList.add(parcelableGeofence.getId());

        if (LocationPermissionManager.isLocationPermissionGranted(context))
        {
            try
            {
                PendingResult<Status> pendingResult = LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceToRemoveList);
                pendingResult.setResultCallback(new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        if (status.isSuccess())
                        {
                            getGeofencesList().remove(parcelableGeofence);
                            writeToSharedPreferences();
                        }
                    }
                });
            }
            catch ( SecurityException securityException )
            {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                logSecurityException( securityException );
            }
        }
    }

    public void removeGoefences()
    {
        if ( !googleApiClient.isConnected() )
        {
            removingQueue.addAll(geofencesList);
            googleApiClient.connect();
            return;
        }

        if (LocationPermissionManager.isLocationPermissionGranted(context))
        {
            try
            {
                PendingResult<Status> pendingResult = LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent());
                pendingResult.setResultCallback(new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        if (status.isSuccess())
                        {
                            getGeofencesList().clear();
                            writeToSharedPreferences();
                            Log.d(TAG, "Removed all Geo-fences successfully");
                        }
                    }
                });
            }
            catch (SecurityException securityException)
            {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                logSecurityException(securityException);
            }
        }
    }

    @Override
    public void onConnected(Bundle params)
    {
        if (!removingQueue.isEmpty())
        {
            for (ParcelableGeofence geofence : removingQueue)
            {
                removingQueue.remove(geofence);
                removeGeofence(geofence);
            }
        }

        if (!addingQueue.isEmpty())
        {
            for (ParcelableGeofence geofence : addingQueue)
            {
                addingQueue.remove(geofence);
                saveGeofence(geofence);
            }
        }
    }

    @Override
    public void onConnectionSuspended( int i )
    {
        Log.d(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Retrieves a Geo-fence by its id
     *
     * @param geofenceId
     * @return - a parcelable geo-fence
     */
    public ParcelableGeofence findGeofenceById(String geofenceId)
    {
        if (!getGeofencesList().isEmpty())
        {
            for ( ParcelableGeofence geofence : getGeofencesList() )
            {
                if (geofence.getId().equals(geofenceId))
                {
                    return geofence;
                }
            }
        }

        return new ParcelableGeofence();
    }

    /**
     * Retrieves a List of Geo-fences from the application's SharedPreferences
     * @return - the saved geofences as a List of ParcelableGeofence
     */
    private List<ParcelableGeofence> loadFromSharedPreferences()
    {
        List<ParcelableGeofence> geofenceList = new CopyOnWriteArrayList<>();

        Set<String> geofenceSet = SharedPreferencesManager.getSetPreferenceValue(context, GEO_FENCES_PREF_KEY);

        if (!geofenceSet.isEmpty())
        {
            for (String item : geofenceSet)
            {
                ParcelableGeofence mapItem = JsonManager.fromJSON(item, ParcelableGeofence.class);
                geofenceList.add(mapItem);
            }
        }

        return geofenceList;
    }

    private void writeToSharedPreferences()
    {
        if (geofencesList.isEmpty())
        {
            Log.d(TAG, "Cleared all Geo-fences from shared preferences");
            SharedPreferencesManager.removePreferenceKey(context, GEO_FENCES_PREF_KEY);
            return;
        }

        Log.d(TAG, "Saving Geo-fences to the application's shared preference");
        SharedPreferencesManager.setPreferenceValue(context, GEO_FENCES_PREF_KEY, geofencesListToSet(getGeofencesList()));
    }

    /**
     * Converts the List of MapItem to a Set of String
     * @return - a Set<String> to save in the application's SharedPreferences
     */
    private static Set<String> geofencesListToSet(List<ParcelableGeofence> geofenceList)
    {
        Set<String> geofencesSet = new LinkedHashSet<>();

        if ( geofenceList != null &&  !geofenceList.isEmpty() )
        {
            for ( ParcelableGeofence fence : geofenceList )
            {
                geofencesSet.add(fence.toString());
            }
        }

        return geofencesSet;
    }

    /**
     * Returns the available Geo-fences
     * @return
     */
    public List<ParcelableGeofence> getGeofencesList()
    {
        if (geofencesList == null || geofencesList.isEmpty())
        {
            geofencesList = loadFromSharedPreferences();
        }

        return geofencesList;
    }

    public List<ParcelableGeofence> getAddingQueue()
    {
        if (addingQueue == null)
        {
            addingQueue = new CopyOnWriteArrayList<>();
        }

        return addingQueue;
    }

    public List<ParcelableGeofence> getRemovingQueue()
    {
        if (removingQueue == null)
        {
            removingQueue = new CopyOnWriteArrayList<>();
        }

        return removingQueue;
    }

    public static String getGeofenceErrorString( Context context, int errorCode )
    {
        Resources resources = context.getResources();
        switch ( errorCode )
        {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return resources.getString( R.string.geofence_not_available );

            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return resources.getString( R.string.geofence_too_many_geofences );

            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return resources.getString( R.string.geofence_too_many_pending_intents );

            default:
                return resources.getString( R.string.unknown_geofence_error );
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove geo-fences. Location Services
     * issues the Intent inside this PendingIntent whenever a geo-fence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geo-fence transitions.
     */
    private PendingIntent getGeofencePendingIntent()
    {
        // Reuse the PendingIntent if we already have it.
        if ( geofencePendingIntent == null )
        {
            Intent intent = new Intent( context, GeofenceTransitionsIntentService.class );
            geofencePendingIntent = PendingIntent.getService( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        }

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        return geofencePendingIntent;
    }

    public static boolean isGeoFencingEnabled(Context context)
    {
        return SharedPreferencesManager.getPreferenceValue(context, GEO_FENCES_ENABLED_PREF_KEY, Boolean.class);
    }

    /**
     * Logs a SecurityException
     * @param securityException
     */
    private void logSecurityException( SecurityException securityException )
    {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    public interface Callback
    {
        void onSuccess();
    }

}









