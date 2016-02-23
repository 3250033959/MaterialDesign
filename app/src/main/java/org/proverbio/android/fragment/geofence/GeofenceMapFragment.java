package org.proverbio.android.fragment.geofence;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.proverbio.android.fragment.BaseFragment;
import org.proverbio.android.material.R;

import java.util.List;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 *  A fragment that displays Geo-fences on a Google Map
 */
public class GeofenceMapFragment extends BaseFragment implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener
{
    public static final String TAG = GeofenceMapFragment.class.getSimpleName();

    public static final int REQUEST_CODE_PICK_PLACE = 149;

    private MapView mapView;
    private GoogleMap googleMap;

    private List<ParcelableGeofence> geofencesList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        geofencesList = LocationService.getInstance(getContext()).getGeofencesList();

        mapView = new MapView(getContext());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (getSwipeRefreshLayout() == null)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            getSwipeRefreshLayout().setEnabled(false);
            getSwipeRefreshLayout().addView(mapView);
            getContext().getFloatingActionButton().setOnClickListener(this);
        }

        return getSwipeRefreshLayout();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.floatingActionButton:
                new GeofenceComposeFragment().show(getContext().getSupportFragmentManager(), GeofenceComposeFragment.class.getSimpleName());
                break;
        }
    }

    @Override
    public void onMapReady( GoogleMap googleMap )
    {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        for (ParcelableGeofence geofence : geofencesList)
        {
            addMarkerToMap(geofence.getLatitude(), geofence.getLongitude(), geofence.getName(), geofence.getAddress(), false);
        }

        if ( LocationPermissionManager.isLocationPermissionGranted(getContext()))
        {
            this.googleMap.setLocationSource(new LocationSourceImpl(getContext()));
        }
        else
        {
            this.googleMap.setLocationSource(null);

            //Let's give some time to the view to load. It gives a better feel to the user.
            new Handler().postDelayed( new Runnable()
            {
                @Override
                public void run()
                {
                    if ( isVisible() )
                    {
                        LocationPermissionManager.checkSelfLocationPermission( getActivity(),
                                LocationPermissionManager.REQUEST_LOCATION_PERMISSION,
                                GeofenceMapFragment.this.getString(R.string.location_picker_locate) );
                    }
                }
            }, 2000 );
        }
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult( int requestCode, String permissions[], int[] grantResults )
    {
        switch ( requestCode )
        {
            case LocationPermissionManager.REQUEST_LOCATION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        googleMap != null )
                {
                    // permission was granted, yay!
                    this.googleMap.setLocationSource(new LocationSourceImpl(getContext()));
                }
                else
                {
                    Toast.makeText( getContext(), getString( R.string.location_permission_declined ),
                            Toast.LENGTH_SHORT ).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if ( requestCode == REQUEST_CODE_PICK_PLACE )
        {
            if ( resultCode == Activity.RESULT_OK )
            {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                //TODO updateMarkerInMap(place.getLatLng().latitude, place.getLatLng().longitude, (String) place.getName(), (String) place.getAddress(), false);
            }
            else if ( resultCode == PlaceAutocomplete.RESULT_ERROR )
            {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.e(TAG, status.getStatusMessage());
            }
        }
    }

    @Override
    public void onMapLongClick( LatLng latLng )
    {
        //TODO updateMarkerInMap(latLng.latitude, latLng.longitude, "Geo fence name", getAddress(latLng), false );
    }

    private void addMarkerToMap( double latitude, double longitude, String title, String address, boolean noCameraMove )
    {
        LatLng position = new LatLng( latitude, longitude );
        Marker marker = googleMap.addMarker( new MarkerOptions()
                .position( position )
                .title( title )
                .snippet( address )
                .draggable( true ) );
        //TODO marker.setIcon( BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_original) );
        marker.showInfoWindow();

        if ( !noCameraMove )
        {
            googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(position, 17) );
        }
    }

    @Override
    public void onResume()
    {
        if ( mapView != null )
        {
            mapView.onResume();
        }

        getContext().getFloatingActionButton().setVisibility(View.VISIBLE);

        super.onResume();
    }

    @Override
    public void onPause()
    {
        if ( mapView != null )
        {
            mapView.onPause();
        }

        super.onPause();
    }

    @Override
    public void onLowMemory()
    {
        if ( mapView != null )
        {
            mapView.onLowMemory();
        }

        super.onLowMemory();
    }

    @Override
    public void onDestroy()
    {
        if ( mapView != null )
        {
            mapView.onDestroy();
        }

        super.onDestroy();
    }
}

