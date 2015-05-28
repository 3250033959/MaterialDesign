package org.proverbio.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.proverbio.android.drawer.NavigationDrawerFragment;
import org.proverbio.android.drawer.NavigationDrawerAdapter;
import org.proverbio.android.fragment.ImageGridFragment;
import org.proverbio.android.fragment.CardGridFragment;
import org.proverbio.android.material.R;

/**
 * @author Juan Pablo Proverbio <proverbio8@gmail.com>
 */
public class HomeActivity extends BaseActivity implements NavigationDrawerAdapter.DrawerCallback, View.OnClickListener
{
    /**
     * The drawer
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * A floating action button
     */
    private ImageButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        floatingActionButton = (ImageButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), getToolbar());
        getToolbar().setTitle(R.string.app_name);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerPositionChanged(int position)
    {
        Fragment fragment = null;
        String fragmentTag = "";

        switch (position)
        {
            case 0:
                Toast.makeText(this, getString(R.string.profile), Toast.LENGTH_SHORT).show();
                break;

            case 1:
                fragment = getSupportFragmentManager().findFragmentByTag(CardGridFragment.TAG);
                fragmentTag = CardGridFragment.TAG;
                if (fragment == null)
                {
                    fragment = new CardGridFragment();
                }
                break;

            case 2:
                fragment = getSupportFragmentManager().findFragmentByTag(ImageGridFragment.TAG);
                fragmentTag = ImageGridFragment.TAG;
                if (fragment == null)
                {
                    fragment = new ImageGridFragment();
                }
                break;

            default:
                fragment = new ImageGridFragment();
                break;

        }

        if ( fragment != null )
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            transaction.replace(R.id.view_container, fragment, fragmentTag);
            transaction.addToBackStack(fragmentTag);
            transaction.commit();
        }
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.floatingActionButton:
                Toast.makeText(this, getString(R.string.floating_button), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public ImageButton getFloatingActionButton()
    {
        return floatingActionButton;
    }
}
