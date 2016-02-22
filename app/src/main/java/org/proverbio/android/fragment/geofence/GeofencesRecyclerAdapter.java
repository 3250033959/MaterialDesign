package org.proverbio.android.fragment.geofence;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.proverbio.android.material.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Juan Pablo Proverbio
 * @since 1.0
 */
public class GeofencesRecyclerAdapter extends RecyclerView.Adapter<GeofenceViewHolder>
{
    private final Context context;

    private List<ParcelableGeofence> geofencesList;

    public GeofencesRecyclerAdapter(Context context)
    {
        this.context = context;
        this.geofencesList = new ArrayList<>();

        ParcelableGeofence geofence = new ParcelableGeofence();
        geofence.setName("My Geofence");
        geofence.setAddress("27 Union Street, Auckland City");
        geofence.setLatitude(123);
        geofence.setLongitude(97);
        geofence.setRadius(200);
        geofence.setId("geofenceId");

        this.geofencesList.add(geofence);
        this.geofencesList.add(geofence);
        this.geofencesList.add(geofence);
        this.geofencesList.add(geofence);

    }

    @Override
    public GeofenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_toggable_item, parent, false);
        return new GeofenceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GeofenceViewHolder viewHolder, int position)
    {
        ParcelableGeofence selectedItem = geofencesList.get(position);
        viewHolder.getNameView().setText(selectedItem.getName());
        viewHolder.getAddressView().setText(selectedItem.getAddress());
        viewHolder.getLatitudeLongitudeView().setText("Lat: " + selectedItem.getLatitude() + ", Lng: " + selectedItem.getLongitude());
        viewHolder.getRadiusView().setText("Radius: " + selectedItem.getRadius());
    }

    @Override
    public int getItemCount()
    {
        return getGeofencesList().size();
    }



    private List<ParcelableGeofence> getGeofencesList()
    {
        if (geofencesList == null)
        {
            geofencesList = new ArrayList<>();
        }

        return geofencesList;
    }

}
