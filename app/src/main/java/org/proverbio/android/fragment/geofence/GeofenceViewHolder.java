package org.proverbio.android.fragment.geofence;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.proverbio.android.material.R;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co/>
 */
public class GeofenceViewHolder extends RecyclerView.ViewHolder
{
    private final ImageView iconView;
    private final TextView nameView;
    private final TextView addressView;
    private final TextView latitudeView;
    private final TextView longitudeView;
    private final TextView radiusView;

    public GeofenceViewHolder(View itemView)
    {
        super(itemView);
        iconView = (ImageView)itemView.findViewById(R.id.icon);
        nameView = (TextView)itemView.findViewById(R.id.title);
        addressView = (TextView)itemView.findViewById(R.id.address);
        latitudeView = (TextView)itemView.findViewById(R.id.latitude);
        longitudeView = (TextView)itemView.findViewById(R.id.longitude);
        radiusView = (TextView)itemView.findViewById(R.id.radius);
    }

    public ImageView getIconView()
    {
        return iconView;
    }

    public TextView getNameView()
    {
        return nameView;
    }

    public TextView getAddressView()
    {
        return addressView;
    }

    public TextView getLatitudeView()
    {
        return latitudeView;
    }

    public TextView getLongitudeView()
    {
        return longitudeView;
    }

    public TextView getRadiusView()
    {
        return radiusView;
    }
}
