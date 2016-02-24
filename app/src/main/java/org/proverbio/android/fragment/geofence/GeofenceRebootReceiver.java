package org.proverbio.android.fragment.geofence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.proverbio.android.fragment.location.LocationPermissionManager;
import org.proverbio.android.fragment.location.LocationServiceSingleton;

import java.util.List;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co/>
 *
 *  Handles reboot and if there were geo-fences then it will re-add them to the devices as they
 *  don't survive the device reboot
 */
public class GeofenceRebootReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        List<ParcelableGeofence> geofenceList = LocationServiceSingleton.getInstance(context).getGeofencesList();

        if (LocationPermissionManager.isLocationPermissionGranted(context)
                && !geofenceList.isEmpty())
        {
            //Connect to Google API if not already connected
            LocationServiceSingleton.getInstance(context).connect();

            //Wait 2 seconds and then save Geo-fences from SharedPreferences to device
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    LocationServiceSingleton.getInstance(context).saveGeofences();
                }
            }, 2000);
        }

        setResultCode( Activity.RESULT_OK );
    }
}
