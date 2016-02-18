package org.proverbio.android.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.proverbio.android.context.SharedPreferencesManager;
import org.proverbio.android.fragment.GraphGridFragment;
import org.proverbio.android.fragment.ImagesGridFragment;
import org.proverbio.android.material.R;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    /**
     * The isDrawerLearn key used to save value to prefs.
     */
    private static final String DRAWER_LEARNT = "drawer_learnt";

    /**
     * The drawer position key used to save value to prefs
     */
    private static final String STATE_SELECTED_POSITION = "drawer_position";

    /**
     * The DrawerLayout
     */
    private DrawerLayout drawerLayout;

    /**
     * The NavigationView
     */
    private NavigationView navigationView;

    /**
     * The ActionBarDrawerToggle that connects {@see android.support.v7.widget.Toolbar} and {@see DrawerLayout}
     */
    private ActionBarDrawerToggle actionBarDrawerToggle;

    /**
     * A flag used to tell if the user has learnt the drawer
     */
    private boolean isNavigationDrawerLearnt;

    /**
     * The current {@see NavigationView} position
     */
    private int drawerSelectedPosition;

    /**
     * A floating action button
     */
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getToolbar().setTitle(R.string.app_name);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);

                if (!isNavigationDrawerLearnt)
                {
                    isNavigationDrawerLearnt = true;
                    SharedPreferencesManager.setPreferenceValue(HomeActivity.this, DRAWER_LEARNT, true);
                }

                invalidateOptionsMenu();
            }
        };

        isNavigationDrawerLearnt = (Boolean)SharedPreferencesManager.getPreferenceValue(this, DRAWER_LEARNT, Boolean.class);

        if (!isNavigationDrawerLearnt)
            this.drawerLayout.openDrawer(Gravity.LEFT);
            this.drawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    actionBarDrawerToggle.syncState();
                }
            });

        this.drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //Inflating {@see FloatingActionButton}
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setEnabled(true);
        floatingActionButton.setOnClickListener(this);

        if (savedInstanceState != null)
        {
            drawerSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        onNavigationItemSelected(navigationView.getMenu().getItem(drawerSelectedPosition));
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_main_topdrawer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.navigation_item_3:
                break;

            case R.id.action_settings:
                Toast.makeText(this, "Hello world", Toast.LENGTH_SHORT).show();
                break;

            default:
                Log.d( HomeActivity.class.getSimpleName(), "hello id: " + item.getItemId() );
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.profileLayout:
                Toast.makeText(this, getString(R.string.profile), Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(Gravity.LEFT);

                break;

            case R.id.floatingActionButton:
                Toast.makeText(this, getString(R.string.floating_button), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        Fragment fragment = null;
        String fragmentTag = "";

        switch (menuItem.getItemId())
        {
            case R.id.navigation_item_1:
                fragment = getSupportFragmentManager().findFragmentByTag(ImagesGridFragment.TAG);
                fragmentTag = ImagesGridFragment.TAG;
                if (fragment == null)
                {
                    fragment = new ImagesGridFragment();
                }
                break;

            case R.id.navigation_item_2:
                fragment = getSupportFragmentManager().findFragmentByTag(GraphGridFragment.TAG);
                fragmentTag = GraphGridFragment.TAG;
                if (fragment == null)
                {
                    fragment = new GraphGridFragment();
                }
                break;

            case R.id.navigation_item_3:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(Gravity.LEFT);

        if ( fragment != null )
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            transaction.replace(R.id.view_container, fragment, fragmentTag);
            transaction.addToBackStack(fragmentTag);
            transaction.commit();
        }

        return true;
    }

    public FloatingActionButton getFloatingActionButton()
    {
        return floatingActionButton;
    }
}
