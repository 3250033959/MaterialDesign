package org.proverbio.android.fragment.location;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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

import org.proverbio.android.activity.BaseActivity;
import org.proverbio.android.fragment.geofence.ParcelableGeofence;
import org.proverbio.android.material.R;
import org.proverbio.android.util.StringConstants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co/>
 */
public class LocationPickerActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMyLocationButtonClickListener
{
    public static final String TAG = LocationPickerActivity.class.getSimpleName();

    public static final int REQUEST_CODE_PICK_LOCATION = 144;
    public static final int REQUEST_CODE_PICK_PLACE = 145;

    private MapView mapView;

    private GoogleMap googleMap;

    private ViewGroup addressLayout;
    private TextView addressView;

    private ViewGroup latitudeLayout;
    private TextView latitudeView;

    private ViewGroup longitudeLayout;
    private TextView longitudeView;

    private ParcelableGeofence selectedLocation;

    private boolean moveCameraToLocation;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        //How to set statusBar color programatically? Here we go...
        //Only on Android Lollipop and above
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor( ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        super.onCreate(savedInstanceState);
        getToolbar().setTitle(R.string.location_picker_title);

        mapView = (MapView)findViewById( R.id.map );
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync( this );

        addressLayout = ( ViewGroup )findViewById( R.id.addressLayout );
       // addressLayout.setOnClickListener( this );
        addressView = ( TextView ) findViewById( R.id.address );
        latitudeLayout = ( ViewGroup )findViewById( R.id.latitudeLayout );
       // latitudeLayout.setOnClickListener( this );
        latitudeView = ( TextView )findViewById( R.id.latitude );
        longitudeLayout = ( ViewGroup )findViewById( R.id.longitudeLayout );
       // longitudeLayout.setOnClickListener( this );
        longitudeView = ( TextView )findViewById( R.id.longitude );

        if (getIntent() != null && getIntent().hasExtra( StringConstants.ITEM_KEY))
        {
            selectedLocation = getIntent().getParcelableExtra(StringConstants.ITEM_KEY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu( final Menu menu )
    {
        super.onCreateOptionsMenu( menu );
        getMenuInflater().inflate( R.menu.menu_location_search, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch ( item.getItemId() )
        {
            case R.id.search:
                try
                {
                    Intent intent = new PlaceAutocomplete.IntentBuilder( PlaceAutocomplete.MODE_OVERLAY ).build( LocationPickerActivity.this );
                    startActivityForResult(intent, REQUEST_CODE_PICK_PLACE);
                }
                catch ( GooglePlayServicesRepairableException e )
                {
                    // TODO: Handle the error.
                } catch ( GooglePlayServicesNotAvailableException e )
                {
                    // TODO: Handle the error.
                }
                return true;

            case R.id.done:
                if (selectedLocation != null && selectedLocation.isValid())
                {
                    Intent intent = new Intent();
                    intent.putExtra(StringConstants.ITEM_KEY, selectedLocation);
                    setResult( Activity.RESULT_OK, intent );
                    finish();
                }
                else
                {
                    Toast.makeText( this, "You need an address, latitude and longitude in order to save this location", Toast.LENGTH_SHORT ).show();
                }
                return true;
        }

        return false;
    }


    @Override
    public void onMapReady( GoogleMap googleMap )
    {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled( false );
        this.googleMap.setOnMapClickListener( this );
        this.googleMap.setOnMapLongClickListener( this );
        this.googleMap.setOnMyLocationChangeListener( this );
        this.googleMap.setOnMyLocationButtonClickListener( this );

        if ( selectedLocation != null )
        {
            updateMarkerInMap( selectedLocation.getLatitude(), selectedLocation.getLongitude(), selectedLocation.getName(), selectedLocation.getAddress(), false );
            moveCameraToLocation = false;
        }
        else
        {
            moveCameraToLocation = true;
        }

        if ( LocationPermissionManager.isLocationPermissionGranted( LocationPickerActivity.this ) )
        {
            this.googleMap.setLocationSource( new LocationSourceImpl( LocationPickerActivity.this ) );
            this.googleMap.setMyLocationEnabled( true );
            this.googleMap.getUiSettings().setMyLocationButtonEnabled( true );
        }
        else
        {
            this.googleMap.setMyLocationEnabled( false );
            this.googleMap.getUiSettings().setMyLocationButtonEnabled( false );
            this.googleMap.setLocationSource(null);

            //Let's give some time to the view to load. It gives a better feel to the user.
            new Handler().postDelayed( new Runnable()
            {
                @Override
                public void run()
                {
                    if ( !isFinishing() )
                    {
                        LocationPermissionManager.checkSelfLocationPermission( LocationPickerActivity.this,
                                LocationPermissionManager.REQUEST_LOCATION_PERMISSION,
                                LocationPickerActivity.this.getString( R.string.location_picker_locate ) );
                    }
                }
            }, 2000 );
        }
    }

    @Override
    public void onMyLocationChange( Location location )
    {
        if ( moveCameraToLocation )
        {
            String address = LocationServiceSingleton.getInstance(this).getAddressByLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            selectedLocation = new ParcelableGeofence(address, location.getLatitude(), location.getLongitude());
            updateMarkerInMap(location.getLatitude(), location.getLongitude(), "You are by here",
                    address, false );
            moveCameraToLocation = false;
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
                    this.googleMap.setLocationSource( new LocationSourceImpl( LocationPickerActivity.this ) );
                    this.googleMap.setMyLocationEnabled( true );
                    this.googleMap.getUiSettings().setMyLocationButtonEnabled( true );
                }
                else
                {
                    Toast.makeText( this, getString( R.string.location_permission_declined ),
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
            if ( resultCode == RESULT_OK )
            {
                moveCameraToLocation = false;
                Place place = PlaceAutocomplete.getPlace(this, data);
                selectedLocation = new ParcelableGeofence((String) place.getAddress(), (String)place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
                updateMarkerInMap(place.getLatLng().latitude, place.getLatLng().longitude, ( String ) place.getName(), ( String ) place.getAddress(), false);
            }
            else if ( resultCode == PlaceAutocomplete.RESULT_ERROR )
            {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e( TAG, status.getStatusMessage() );
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        moveCameraToLocation = true;
        return true;
    }

    @Override
    public void onMapClick( LatLng latLng )
    {
        String address = LocationServiceSingleton.getInstance(this).getAddressByLatLng(latLng);
        selectedLocation = new ParcelableGeofence(address, latLng.latitude, latLng.longitude);
        updateMarkerInMap(latLng.latitude, latLng.longitude, address, address, false );
    }

    @Override
    public void onMapLongClick( LatLng latLng )
    {
        String address = LocationServiceSingleton.getInstance(this).getAddressByLatLng(latLng);
        selectedLocation = new ParcelableGeofence(address, latLng.latitude, latLng.longitude);
        updateMarkerInMap(latLng.latitude, latLng.longitude, address, address, false );
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_location_picker;
    }

    private void updateMarkerInMap( double latitude, double longitude, String title, String address, boolean noCameraMove )
    {
        googleMap.clear();

        LatLng position = new LatLng( latitude, longitude );
        Marker marker = googleMap.addMarker( new MarkerOptions()
                .position( position )
                .title( title )
                .snippet( address )
                .draggable( true ) );
       //TODO marker.setIcon( BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_original) );
        marker.showInfoWindow();

        addressView.setText( address );
        latitudeView.setText( String.valueOf( latitude ) );
        longitudeView.setText( String.valueOf( longitude ) );

        if ( !noCameraMove )
        {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        }
    }

    @Override
    public void onResume()
    {
        if ( mapView != null )
        {
            mapView.onResume();
        }

        getToolbar().setTitle(R.string.location_picker_title);

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
