package org.proverbio.android.context;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;


import java.lang.ref.WeakReference;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 */
public class ApplicationContext extends Application
{
    /**
     * The Application instance
     */
    private static volatile ApplicationContext instance;

    /**
     * A weak reference to the visible activity
     */
    private WeakReference<AppCompatActivity> currentActivity;

    /**
     * The app has been created
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    /**
     * The app is about to finish
     */
    @Override
    public void onTerminate()
    {
        instance = null;
        super.onTerminate();
    }

    /**
     * Returns this instance
     * @return
     */
    public static ApplicationContext getInstance()
    {
        return instance;
    }

    /**
     * Returns the current activity instance
     * @return
     */
    public AppCompatActivity getCurrentActivity()
    {
        return currentActivity == null ? null : currentActivity.get();
    }

    /**
     * Updates the Application references to the current activity
     * Null if there are no current activity
     * @param currentActivity
     */
    public void setCurrentActivity(AppCompatActivity currentActivity)
    {
        if (currentActivity != null)
        {
            this.currentActivity = new WeakReference<>(currentActivity);
        }
        else
        {
            this.currentActivity = null;
        }
    }
}
