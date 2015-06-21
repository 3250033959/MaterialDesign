
package org.proverbio.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.proverbio.android.ApplicationContext;
import org.proverbio.android.material.R;

/**
 * @author Juan Pablo Proverbio <proverbio8@gmail.com>
 */
public abstract class BaseActivity extends AppCompatActivity
{
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        //Gets {@see Toolbar} instance from inflated layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null)
        {
            //Sets our Toolbar instance as our application's ActionBar
            setSupportActionBar(toolbar);

            //Enables Home as Up - Arrow or Drawer icon
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume()
    {
        ApplicationContext.getInstance().setCurrentActivity(this);
        super.onResume();
    }

    @Override
    public void onStop()
    {
        ApplicationContext.getInstance().setCurrentActivity(null);
        super.onStop();
    }

    protected abstract int getLayoutResource();

    public Toolbar getToolbar()
    {
        return toolbar;
    }
}
