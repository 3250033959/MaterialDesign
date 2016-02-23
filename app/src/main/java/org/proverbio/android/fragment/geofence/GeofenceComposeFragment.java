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

import org.proverbio.android.fragment.base.BaseFragment;
import org.proverbio.android.material.R;
import org.proverbio.android.util.StringConstants;
import org.proverbio.android.util.Validator;

/**
 * @author Juan Pablo Proverbio
 */
public class GeofenceComposeFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener

{
    public static final String TAG = GeofenceComposeFragment.class.getSimpleName();

    private ViewGroup content;
    private EditText nameView;
    private TextView locationView;
    private TextView radiusLabelView;
    private AppCompatSeekBar radiusView;

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
            content = (ViewGroup)inflater.inflate(R.layout.fragment_geofence_compose, container, false);
            nameView = (EditText)content.findViewById(R.id.name);
            locationView = (TextView)content.findViewById(R.id.location);
            locationView.setOnClickListener(this);
            radiusLabelView = (TextView)content.findViewById(R.id.radiusLabel);
            radiusView = (AppCompatSeekBar)content.findViewById(R.id.radius);
            radiusView.setOnSeekBarChangeListener(this);
            radiusView.setProgress(100);
            getFragmentLayout().addView(content);
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

        return getFragmentLayout();
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
                    parcelableGeofence.setName(nameView.getText().toString());
                    parcelableGeofence.setRadius(radiusView.getProgress());

                    if (selectedLocation != null)
                    {
                        parcelableGeofence.setAddress(selectedLocation.getAddress());
                        parcelableGeofence.setLatitude(selectedLocation.getLatitude());
                        parcelableGeofence.setLongitude(selectedLocation.getLongitude());
                    }

                    if (LocationService.getInstance(getContext()).saveGeofence(parcelableGeofence))
                    {
                        getContext().onBackPressed();
                    }
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
                Intent intent = new Intent( getContext(), LocationPickerActivity.class );

                if ( parcelableGeofence != null )
                {
                    intent.putExtra( StringConstants.ITEM_KEY, parcelableGeofence );
                }

                startActivityForResult( intent, LocationPickerActivity.REQUEST_CODE_PICK_LOCATION );
                break;
        }
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if ( requestCode == LocationPickerActivity.REQUEST_CODE_PICK_LOCATION &&
                resultCode == Activity.RESULT_OK  && data != null && data.hasExtra( StringConstants.ITEM_KEY ) )
        {
            selectedLocation = data.getParcelableExtra( StringConstants.ITEM_KEY );
            locationView.setText( selectedLocation.getAddress() );
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
}
