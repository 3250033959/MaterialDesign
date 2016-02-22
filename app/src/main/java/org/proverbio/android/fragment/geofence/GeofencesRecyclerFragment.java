package org.proverbio.android.fragment.geofence;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.proverbio.android.fragment.BaseFragment;
import org.proverbio.android.material.R;
import org.proverbio.android.recycler.DividerItemDecoration;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 *  A ListFragment that allows to enable/disable/add/update/remove Geo-fences
 */
public class GeofencesRecyclerFragment extends BaseFragment implements View.OnClickListener
{
    public static final String TAG = GeofencesRecyclerFragment.class.getSimpleName();

    private GeofencesRecyclerAdapter geofenceRecyclerAdapter;
    private RecyclerView geofencesRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.geofenceRecyclerAdapter = new GeofencesRecyclerAdapter(getContext());
        getContext().getToolbar().setTitle(R.string.geofences_title);
        getContext().getFloatingActionButton().setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        if ( geofencesRecyclerView == null )
        {
            geofencesRecyclerView = (RecyclerView)inflater.inflate(R.layout.fragment_recycler_card_fragment, container, false);
            geofencesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            geofencesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
            geofencesRecyclerView.setItemAnimator(new DefaultItemAnimator());
            geofencesRecyclerView.setAdapter(geofenceRecyclerAdapter);
            getSwipeRefreshLayout().addView(geofencesRecyclerView);
        }

        return getSwipeRefreshLayout();
    }

    @Override
    public void onClick( View v )
    {
        switch ( v.getId() )
        {
            case R.id.floatingActionButton:
                GeofenceComposeFragment geofenceComposeFragment = new GeofenceComposeFragment();

                FragmentTransaction transaction = getContext().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.view_container, geofenceComposeFragment);
                transaction.addToBackStack(GeofenceComposeFragment.TAG);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onResume()
    {
        getContext().getToolbar().setTitle(R.string.geofences_title);
        super.onResume();
    }

}
