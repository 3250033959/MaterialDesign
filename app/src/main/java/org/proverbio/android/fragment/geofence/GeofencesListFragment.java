package org.proverbio.android.fragment.geofence;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.proverbio.android.fragment.base.BaseFragment;
import org.proverbio.android.material.R;
import org.proverbio.android.recycler.DividerItemDecoration;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 *  A ListFragment that allows to enable/disable/add/update/remove Geo-fences
 */
public class GeofencesListFragment extends BaseFragment implements View.OnClickListener
{
    public static final String TAG = GeofencesListFragment.class.getSimpleName();

    private GeofencesListAdapter geofenceRecyclerAdapter;
    private RecyclerView geofencesRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.geofenceRecyclerAdapter = new GeofencesListAdapter(getContext(), LocationServiceSingleton.getInstance(getContext()).getGeofencesList());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.menu_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.delete)
        {
            LocationServiceSingleton.getInstance(getContext()).removeGoefences();
            geofenceRecyclerAdapter.setGeofencesList(LocationServiceSingleton.getInstance(getContext()).getGeofencesList());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getTitleResId()
    {
        return R.string.drawer_item_two;
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
        getContext().getFloatingActionButton().setVisibility(View.VISIBLE);
        geofenceRecyclerAdapter.setGeofencesList(LocationServiceSingleton.getInstance(getContext()).getGeofencesList());
        super.onResume();
    }

    @Override
    public void onRefresh()
    {
        geofenceRecyclerAdapter.setGeofencesList(LocationServiceSingleton.getInstance(getContext()).getGeofencesList());
        getSwipeRefreshLayout().setRefreshing(false);
    }
}
