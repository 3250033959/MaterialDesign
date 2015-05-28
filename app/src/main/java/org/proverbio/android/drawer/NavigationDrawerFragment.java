package org.proverbio.android.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.proverbio.android.material.R;
import org.proverbio.android.recycler.RecyclerItem;
import org.proverbio.android.view.ScrimInsetsFrameLayout;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Juan Pablo Proverbio <proverbio8@gmail.com>
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerAdapter.DrawerCallback
{
    /**
     * The preferences file name
     */
    private static final String PREFERENCES_FILE = "app_prefs";

    /**
     * A flag to tell if the user has seen at least once the drawer layout
     */
    private boolean isDrawerLearnt;

    /**
     * The isDrawerLearn key used to save value to prefs.
     */
    private static final String DRAWER_LEARNT = "drawer_learnt";

    /**
     * The current drawer recycler view position
     */
    private int drawerPosition;

    /**
     * The drawer position key used to save value to prefs
     */
    private static final String STATE_SELECTED_POSITION = "drawer_position";

    /**
     *  The drawer that wraps the app view
     */
    private DrawerLayout drawerLayout;

    /**
     * A {@see RecyclerView} which will contain the drawer content
     */
    private RecyclerView drawerList;

    /**
     * A {@see RecyclerAdapter} for the drawer
     */
    private NavigationDrawerAdapter drawerAdapter;

    /**
     * The RecyclerAdapter callback
     */
    private NavigationDrawerAdapter.DrawerCallback drawerCallback;

    /**
     *  This class provides a handy way to tie together the functionality of
     * {@link android.support.v4.widget.DrawerLayout} and {@see Toolbar} to implement the recommended design for navigation drawers.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;


    private boolean isFromSavedInstanceState;

    private View mFragmentContainerView;

    /**
     * The fragment view
     */
    private View fragmentView;

    /**
     * A reference to the host activity
     */
    private AppCompatActivity ownerActivity;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if ( activity instanceof AppCompatActivity && activity instanceof NavigationDrawerAdapter.DrawerCallback)
        {
            ownerActivity = (AppCompatActivity)activity;
            drawerCallback = (NavigationDrawerAdapter.DrawerCallback)activity;
        }
        else
        {
            throw new ClassCastException("Activity should extend AppCompatActivity and implement RecyclerAdapter.RecyclerAdapterCallback");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        isDrawerLearnt = Boolean.valueOf(readSharedSetting(getActivity(), DRAWER_LEARNT, "false"));
        if (savedInstanceState != null)
        {
            drawerPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            isFromSavedInstanceState = true;
        }

        drawerAdapter = new NavigationDrawerAdapter(getMenu(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (fragmentView == null)
        {
            fragmentView = inflater.inflate(R.layout.fragment_navigation_google, container, false);

            drawerList = (RecyclerView) fragmentView.findViewById(R.id.drawerList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ownerActivity);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            drawerList.setLayoutManager(layoutManager);
            drawerList.setHasFixedSize(true);
            drawerList.setAdapter(drawerAdapter);
        }

        selectItem(drawerPosition);

        drawerList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return fragmentView;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar)
    {
        mFragmentContainerView = getActivity().findViewById(fragmentId);

        if(mFragmentContainerView.getParent() instanceof ScrimInsetsFrameLayout)
        {
            mFragmentContainerView = (View) mFragmentContainerView.getParent();
        }

        this.drawerLayout = drawerLayout;
        this.drawerLayout.setStatusBarBackgroundColor(getResources().getColor(android.R.color.white));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), this.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                ownerActivity.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;

                if (!isDrawerLearnt)
                {
                    isDrawerLearnt = true;
                    saveSharedSetting(getActivity(), DRAWER_LEARNT, "true");
                }

                ownerActivity.invalidateOptionsMenu();
            }
        };

        if (!isDrawerLearnt && !isFromSavedInstanceState)
            this.drawerLayout.openDrawer(mFragmentContainerView);

        this.drawerLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mActionBarDrawerToggle.syncState();
            }
        });

        this.drawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void openDrawer()
    {
        drawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer()
    {
        drawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        drawerCallback = null;
    }

    public List<RecyclerItem> getMenu()
    {
        List<RecyclerItem> items = new ArrayList<RecyclerItem>();
        items.add(new RecyclerItem("Juan Pablo Proverbio", "proverbio8@gmail.com", null));
        items.add(new RecyclerItem("RecyclerView", "9+"));
        items.add(new RecyclerItem("GridView", null));
        return items;
    }

    /**
     * Changes the icon of the drawer to back
     */
    public void showBackButton()
    {
        ownerActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Changes the icon of the drawer to menu
     */
    public void showDrawerButton()
    {
        ownerActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mActionBarDrawerToggle.syncState();
    }

    void selectItem(int position)
    {
        drawerPosition = position;
        if (drawerLayout != null)
        {
            drawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (drawerCallback != null)
        {
            drawerCallback.onDrawerPositionChanged(position);
        }
        drawerAdapter.selectPosition(position);
    }

    public boolean isDrawerOpen()
    {
        return drawerLayout != null && drawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, drawerPosition);
    }

    @Override
    public void onDrawerPositionChanged(int position)
    {
        selectItem(position);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue)
    {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue)
    {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    /* Getters and Setters */
    public DrawerLayout getDrawerLayout()
    {
        return drawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout)
    {
        this.drawerLayout = drawerLayout;
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle()
    {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle)
    {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }
}
