package org.proverbio.android.fragment.geofence;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import org.proverbio.android.material.R;
import org.proverbio.android.util.DialogUtils;

/**
 * @author proverbio on 23/02/16.
 */
public class LocationPermissionManager
{
    public static final int REQUEST_LOCATION_PERMISSION = 155;
    private static final String[] LOCATION_PERMISSIONS = new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

    /**
     * This method is responsible for checking if we have permission to access Location services
     *
     * @param activity - the activity to show a dialog if needed
     * @param fragment - the fragment to use for checking the permission
     */
    public static void checkSelfLocationPermission( Activity activity, final Fragment fragment, final int requestCode )
    {
        if ( ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( fragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            //Do I need to show explanation dialog
            if ( fragment.shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_COARSE_LOCATION ) ||
                    fragment.shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION ) )
            {
                DialogUtils.showMessageOkCancel(activity, fragment.getContext().getString(R.string.location_permission_explanation),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.requestPermissions(LOCATION_PERMISSIONS, requestCode);
                            }
                        });
                return;
            }
            else
            {
                // No explanation needed, we can request the permission.
                fragment.requestPermissions( LOCATION_PERMISSIONS, requestCode );
            }
        }
        else
        {
            fragment.onRequestPermissionsResult( requestCode, LOCATION_PERMISSIONS, new int[] { PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED } );
        }
    }

    /**
     * This method is responsible for checking if we have permission to access Location services
     *
     * @param activity - the activity to show a dialog if needed
     * @param fragment - the fragment to use for checking the permission
     */
    public static void checkSelfLocationPermission( Activity activity, final Fragment fragment, final int requestCode, String message )
    {
        if ( ContextCompat.checkSelfPermission( fragment.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( fragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            //Do I need to show explanation dialog
            if ( fragment.shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_COARSE_LOCATION ) ||
                    fragment.shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION ) )
            {
                DialogUtils.showMessageOkCancel( activity, message,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick( DialogInterface dialog, int which )
                            {
                                fragment.requestPermissions( LOCATION_PERMISSIONS, requestCode );
                            }
                        });
                return;
            }
            else
            {
                // No explanation needed, we can request the permission.
                fragment.requestPermissions( LOCATION_PERMISSIONS, requestCode );
            }
        }
        else
        {
            fragment.onRequestPermissionsResult( requestCode, LOCATION_PERMISSIONS, new int[] { PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED } );
        }
    }

    /**
     * This method is responsible for checking if we have permission to access Location services
     *
     * @param activity - the activity to show a dialog if needed
     */
    public static void checkSelfLocationPermission( final Activity activity, final int requestCode, String message )
    {
        if ( ContextCompat.checkSelfPermission( activity, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( activity, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            //Do I need to show explanation dialog
            if ( ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale( activity, Manifest.permission.ACCESS_FINE_LOCATION ) )
            {
                DialogUtils.showMessageOkCancel( activity, message,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick( DialogInterface dialog, int which )
                            {
                                ActivityCompat.requestPermissions( activity, LOCATION_PERMISSIONS, requestCode );
                            }
                        });
                return;
            }
            else
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions( activity, LOCATION_PERMISSIONS, requestCode );
            }
        }
        else
        {
            activity.onRequestPermissionsResult( requestCode, LOCATION_PERMISSIONS, new int[] { PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED } );
        }
    }

    /**
     * This method is responsible for checking if we have permission to access Location services
     *
     * @param activity - the activity to show a dialog if needed
     * @param fragment - the fragment to use for checking the permission
     */
    public static void checkSelfLocationPermission( Activity activity, final Fragment fragment )
    {
        checkSelfLocationPermission( activity, fragment, REQUEST_LOCATION_PERMISSION );
    }

    public static boolean isLocationPermissionGranted( Context context )
    {
        return ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED;
    }
}


