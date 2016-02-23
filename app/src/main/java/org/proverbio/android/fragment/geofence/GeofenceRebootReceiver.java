package org.proverbio.android.fragment.geofence;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co/>
 *
 *  Handles reboot and if there were geo-fences then it will re-add them to the devices as they
 *  don't survive the device reboot
 */
public class GeofenceRebootReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

    }
}
