package org.proverbio.android.fragment.geofence;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.proverbio.android.fragment.base.BaseFragment;
import org.proverbio.android.fragment.location.LocationPermissionManager;
import org.proverbio.android.fragment.location.LocationServiceSingleton;
import org.proverbio.android.material.R;
import org.proverbio.android.util.StringConstants;

import java.util.List;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 *  A fragment that displays Geo-fences on a Google Map
 */
public class GeofenceMapFragment extends BaseFragment implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener
{
    public static final String TAG = GeofenceMapFragment.class.getSimpleName();

    private MapView mapView;
    private GoogleMap googleMap;

    private List<ParcelableGeofence> geofencesList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        geofencesList = LocationServiceSingleton.getInstance(getContext()).getGeofencesList();
        mapView = new MapView(getContext());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.list:
                FragmentTransaction transaction = getContext().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
                transaction.replace(R.id.view_container, new GeofencesListFragment());
                transaction.addToBackStack(GeofencesListFragment.TAG);
                transaction.commit();
                return true;

            case R.id.delete:
                LocationServiceSingleton.getInstance(getContext()).removeGoefences();
                if (googleMap != null)
                {
                    googleMap.clear();
                }
                return true;
        }

        return false;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.floatingActionButton:
                FragmentTransaction transaction = getContext().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
                transaction.replace(R.id.view_container, new GeofenceComposeFragment());
                transaction.addToBackStack(GeofenceComposeFragment.TAG);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onMapReady( GoogleMap googleMap )
    {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.setOnInfoWindowClickListener(this);

        renderGeofencesOnMap();

        if ( !LocationPermissionManager.isLocationPermissionGranted(getContext()))
        {
            //Let's give some time to the view to load. It gives a better feel to the user.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isVisible()) {
                        LocationPermissionManager.checkSelfLocationPermission(getActivity(),
                                LocationPermissionManager.REQUEST_LOCATION_PERMISSION,
                                GeofenceMapFragment.this.getString(R.string.location_picker_locate));
                    }
                }
            }, 2000);
        }
    }

    private void renderGeofencesOnMap()
    {
        if (googleMap == null)
        {
            return;
        }

        googleMap.clear();

        geofencesList = LocationServiceSingleton.getInstance(getContext()).getGeofencesList();

        if (!geofencesList.isEmpty())
        {
            final LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            for (ParcelableGeofence geofence : geofencesList)
            {
                boundsBuilder.include(new LatLng(geofence.getLatitude(), geofence.getLongitude()));
                addMarkerToMap(geofence, true);
            }

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 30));
                }
            }, 300);

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        ParcelableGeofence parcelableGeofence = LocationServiceSingleton.getInstance(getContext()).findGeofenceById(marker.getId());

        GeofenceComposeFragment geofenceComposeFragment = new GeofenceComposeFragment();
        Bundle params = new Bundle();
        params.putParcelable(StringConstants.ITEM_KEY, parcelableGeofence);
        geofenceComposeFragment.setArguments(params);

        FragmentTransaction transaction = getContext().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
        transaction.replace(R.id.view_container, geofenceComposeFragment);
        transaction.addToBackStack(GeofenceComposeFragment.TAG);
        transaction.commit();
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
                   //TODO
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
    public void onMapLongClick( LatLng latLng )
    {
        ParcelableGeofence parcelableGeofence = new ParcelableGeofence();
        parcelableGeofence.setAddress("Address");
        parcelableGeofence.setLatitude(latLng.latitude);
        parcelableGeofence.setLongitude(latLng.longitude);
        parcelableGeofence.setRadius(100);

        GeofenceComposeFragment geofenceComposeFragment = new GeofenceComposeFragment();
        Bundle params = new Bundle();
        params.putParcelable(StringConstants.ITEM_KEY, parcelableGeofence);
        geofenceComposeFragment.setArguments(params);

        FragmentTransaction transaction = getContext().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
        transaction.replace(R.id.view_container, geofenceComposeFragment);
        transaction.addToBackStack(GeofenceComposeFragment.TAG);
        transaction.commit();
    }

    private void addMarkerToMap(ParcelableGeofence geofence, boolean noCameraMove)
    {
        LatLng position = new LatLng(geofence.getLatitude(), geofence.getLongitude());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(geofence.getName())
                .snippet(geofence.getAddress())
                .draggable(true));
        //TODO marker.setIcon( BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_original) );
        marker.showInfoWindow();

        CircleOptions projectCircle = new CircleOptions()
                .center(position)
                .radius(geofence.getRadius())
                .strokeColor( getResources().getColor(R.color.colorPrimaryDark))
                .fillColor(getResources().getColor(R.color.colorPrimaryTransparent));
        googleMap.addCircle(projectCircle);

        if (!noCameraMove)
        {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if ( mapView != null )
        {
            mapView.onResume();
        }

        getContext().getFloatingActionButton().setVisibility(View.VISIBLE);
        renderGeofencesOnMap();
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

    @Override
    public int getTitleResId()
    {
        return R.string.drawer_item_two;
    }

    public boolean isNavigationFragment()
    {
        return true;
    }
}

