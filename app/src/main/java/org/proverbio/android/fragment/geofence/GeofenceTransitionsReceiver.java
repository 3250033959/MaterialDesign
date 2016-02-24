package org.proverbio.android.fragment.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import org.proverbio.android.context.ApplicationContext;
import org.proverbio.android.material.R;
import org.proverbio.android.util.DialogUtils;
import org.proverbio.android.util.StringConstants;

/**
 * @author Juan Pablo Proverbio
 */
public class GeofenceTransitionsReceiver extends BroadcastReceiver
{
    private static final String TAG = GeofenceTransitionsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || !intent.hasExtra(GeofenceTransitionsIntentService.GEO_FENCE_TRANSITION_KEY))
        {
            Log.d(TAG, "intent is null or GEO_FENCE_TRANSITION_KEY is noy present");
            return;
        }

        //Getting data from extras
        final ParcelableGeofence parcelableGeofence = intent.getParcelableExtra(StringConstants.ITEM_KEY);
        int geofenceTransition = intent.getIntExtra(GeofenceTransitionsIntentService.GEO_FENCE_TRANSITION_KEY, 0);

        if (parcelableGeofence.isValid())
        {
            switch ( geofenceTransition )
            {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    DialogUtils.showOkDialog(ApplicationContext.getInstance().getVisibleActivity(),
                            R.string.entered_geo_fence,
                            "You are near by Geo-fence " + parcelableGeofence.getName() + ", Address: " + parcelableGeofence.getAddress());
                    break;

                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    DialogUtils.showOkDialog(ApplicationContext.getInstance().getVisibleActivity(),
                            R.string.exited_geo_fence,
                            "You have left Geo-fence " + parcelableGeofence.getName() + ", Address: " + parcelableGeofence.getAddress());
                    break;
            }
        }
    }
}
