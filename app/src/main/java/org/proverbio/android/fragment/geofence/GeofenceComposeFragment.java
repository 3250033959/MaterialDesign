package org.proverbio.android.fragment.geofence;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.proverbio.android.fragment.BaseFragment;
import org.proverbio.android.material.R;

/**
 * @author Juan Pablo Proverbio
 */
public class GeofenceComposeFragment extends BaseFragment
{
    public static final String TAG = GeofenceComposeFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getContext().getSupportActionBar().setDisplayShowHomeEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getContext().getToolbar().setElevation(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        if (getView() == null)
        {
            getSwipeRefreshLayout().setEnabled(false);
            View view = inflater.inflate(R.layout.fragment_geofence_compose, container, false);
            return view;
        }

        return getView();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getContext().getToolbar().setElevation(getContext().getResources().getDimension(R.dimen.toolbar_elevation));
        }
    }
}
