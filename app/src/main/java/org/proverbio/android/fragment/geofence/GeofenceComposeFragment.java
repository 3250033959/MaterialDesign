package org.proverbio.android.fragment.geofence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.proverbio.android.fragment.base.BaseFragment;
import org.proverbio.android.fragment.location.LocationPickerActivity;
import org.proverbio.android.fragment.location.LocationServiceSingleton;
import org.proverbio.android.material.R;
import org.proverbio.android.util.StringConstants;
import org.proverbio.android.util.Utils;
import org.proverbio.android.util.Validator;

import java.util.Random;

/**
 * @author Juan Pablo Proverbio
 */
public class GeofenceComposeFragment extends BaseFragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback

{
    public static final String TAG = GeofenceComposeFragment.class.getSimpleName();

    private ViewGroup content;
    private EditText nameView;
    private TextView locationView;
    private TextView radiusLabelView;
    private AppCompatSeekBar radiusView;
    private MapView mapView;
    private GoogleMap googleMap;

    private ParcelableGeofence parcelableGeofence;
    private ParcelableGeofence selectedLocation;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {

        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(StringConstants.ITEM_KEY))
        {
            parcelableGeofence = getArguments().getParcelable(StringConstants.ITEM_KEY);
        }

        if (parcelableGeofence == null)
        {
            parcelableGeofence = new ParcelableGeofence();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        super.onCreateView(inflater, container, savedInstanceState);

        if (content == null)
        {
            getSwipeRefreshLayout().setEnabled(false);
            content = (ViewGroup)inflater.inflate(R.layout.fragment_geofence_compose, container, false);
            nameView = (EditText)content.findViewById(R.id.name);

            locationView = (TextView)content.findViewById(R.id.location);
            locationView.setOnClickListener(this);

            mapView = (MapView)content.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
            mapView.setVisibility(View.INVISIBLE);

            radiusLabelView = (TextView)content.findViewById(R.id.radiusLabel);
            radiusView = (AppCompatSeekBar)content.findViewById(R.id.radius);
            radiusView.setOnSeekBarChangeListener(this);
            radiusView.setProgress(100);
            getSwipeRefreshLayout().addView(content);
        }

        if (!TextUtils.isEmpty(parcelableGeofence.getName()))
        {
            nameView.setText(parcelableGeofence.getName());
        }

        if (!TextUtils.isEmpty(parcelableGeofence.getAddress()))
        {
            locationView.setText(parcelableGeofence.getAddress());
        }

        radiusView.setProgress(parcelableGeofence.getRadius() >= 100 ? (int)parcelableGeofence.getRadius() : 100);

        return getSwipeRefreshLayout();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (selectedLocation != null)
        {
            googleMap.clear();
            addMarkerToMap(selectedLocation.getLatitude(), selectedLocation.getLongitude(), selectedLocation.getAddress(), selectedLocation.getAddress(), false);
            mapView.setVisibility(View.VISIBLE);
        }
        /*else if (parcelableGeofence.isValid())
        {
            googleMap.clear();
            addMarkerToMap(selectedLocation.getLatitude(), selectedLocation.getLongitude(), selectedLocation.getName(), selectedLocation.getAddress(), false);
            mapView.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.menu_compose, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.done:
                if (Validator.hasText(nameView) & Validator.hasText(locationView))
                {
                    Utils.hideKeyboard(nameView, LayoutInflater.from(getContext()));

                    parcelableGeofence.setName(nameView.getText().toString());
                    parcelableGeofence.setRadius(radiusView.getProgress());

                    if (selectedLocation != null)
                    {
                        parcelableGeofence.setAddress(selectedLocation.getAddress());
                        parcelableGeofence.setLatitude(selectedLocation.getLatitude());
                        parcelableGeofence.setLongitude(selectedLocation.getLongitude());
                    }

                    if (TextUtils.isEmpty(parcelableGeofence.getId()))
                    {
                        parcelableGeofence.setId(String.valueOf(new Random().nextInt(10000)));
                    }

                    LocationServiceSingleton.getInstance(getContext()).saveGeofence(parcelableGeofence);
                    getContext().onBackPressed();
                }
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.location:
                Intent intent = new Intent(getContext(), LocationPickerActivity.class);

                if (selectedLocation != null)
                {
                    intent.putExtra(StringConstants.ITEM_KEY, selectedLocation);
                }

                startActivityForResult(intent, LocationPickerActivity.REQUEST_CODE_PICK_LOCATION);
                break;
        }
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
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ( requestCode == LocationPickerActivity.REQUEST_CODE_PICK_LOCATION &&
                resultCode == Activity.RESULT_OK  && data != null && data.hasExtra(StringConstants.ITEM_KEY))
        {
            selectedLocation = data.getParcelableExtra(StringConstants.ITEM_KEY);
            locationView.setText(selectedLocation.getAddress());
            googleMap.clear();
            addMarkerToMap(selectedLocation.getLatitude(), selectedLocation.getLongitude(), selectedLocation.getAddress(), selectedLocation.getAddress(), false);
            mapView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getTitleResId()
    {
        return R.string.fragment_compose_geofence_title;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        radiusLabelView.setText( progress + " metres" );
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onResume()
    {
        if ( mapView != null )
        {
            mapView.onResume();
        }

        super.onResume();
    }

    @Override
    public void onPause()
    {
        if ( mapView != null )
        {
            mapView.onPause();
        }

        Utils.hideKeyboard(nameView, LayoutInflater.from(getContext()));

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
