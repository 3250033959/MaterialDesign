package org.proverbio.android.fragment.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co/>
 */
public class LocationSourceImpl implements LocationSource, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = LocationSourceImpl.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private final Context context;

    private final GoogleApiClient googleApiClient;

    private final LocationRequest locationRequest;

    private LocationSource.OnLocationChangedListener onLocationChangedListener;

    public LocationSourceImpl( Context context )
    {
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder( context )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        googleApiClient.connect();

        locationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locationRequest.setInterval( UPDATE_INTERVAL_IN_MILLISECONDS );

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        locationRequest.setFastestInterval( FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS );
        locationRequest.setPriority( LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener )
    {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate()
    {

    }

    @Override
    public void onLocationChanged(Location location )
    {
        if( onLocationChangedListener != null )
        {
            onLocationChangedListener.onLocationChanged( location );
        }
    }

    @Override
    public void onConnected(Bundle bundle )
    {
        //ALWAYS CHECK PERMISSION FROM PARENT

        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    this );
        }
        catch ( SecurityException se )
        {
            Log.e(TAG, "The Developer forgot to ask for permissions on Marshmallow and above", se);
        }
    }

    @Override
    public void onConnectionSuspended( int i )
    {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult )
    {
        Log.d(TAG, "onConnectionFailed");
    }
}

