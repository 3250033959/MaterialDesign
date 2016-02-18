package org.proverbio.android.fragment.geofence;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 *  A fragment that displays Geo-fences on a Google Map
 */
public class GeofenceMapFragment extends Fragment implements OnMapReadyCallback
{
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return null;

    }


    @Override
    public void onMapReady( GoogleMap googleMap )
    {

    }



}
