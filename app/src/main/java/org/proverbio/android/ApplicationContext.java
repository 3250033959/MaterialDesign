package org.proverbio.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;


import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * @author Juan Pablo Proverbio <proverbio8@gmail.com/>
 */
public class ApplicationContext extends Application
{
    /**
     * The Application instance
     */
    private static volatile ApplicationContext instance;

    /**
     * The Shared Preferences file name
     */
    private static final String PREFS_FILE_NAME = "preferences";

    /**
     * Our app shared preferences instance
     */
    private SharedPreferences sharedPreferences;

    /**
     * A reference to the current activity
     */
    private WeakReference<AppCompatActivity> currentActivity;


    /**
     * The app has been created
     */
    @Override
    public void onCreate()
    {
        instance = this;

    }

    /**
     * The app is about to finish
     */
    @Override
    public void onTerminate()
    {

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

    /**
     * Returns the value of the received key from Shared Preferences
     * @param key
     * @param returnType
     * @return
     */
    public Object getPreferenceValue(String key, Class<?> returnType)
    {
        if (sharedPreferences == null)
        {
            sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        }

        if (String.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return sharedPreferences.getString(key, "");
        }
        else if (Boolean.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return sharedPreferences.getBoolean(key, false);
        }
        else if (Float.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return sharedPreferences.getFloat(key, 0f);
        }
        else if (Integer.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return sharedPreferences.getInt(key, 0);
        }
        else if (Long.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return sharedPreferences.getLong(key, 0l);
        }
        else if (HashSet.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return sharedPreferences.getStringSet(key, new HashSet<String>());
        }
        else
        {
            return sharedPreferences.getAll();
        }
    }

    /**
     * Removes a shared preference value from by received key
     * @param key
     * @return
     */
    public SharedPreferences removePreferenceKey(String key)
    {
        sharedPreferences.edit().remove(key).commit();
        return sharedPreferences;
    }

    /**
     * Set the value of a key from the Shared Preferences
     * @param key
     * @param value
     * @return
     */
    public SharedPreferences setPreferenceValue(String key, Object value)
    {
        if (sharedPreferences == null)
        {
            sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        }

        if (String.class.isInstance(value))
        {
            sharedPreferences.edit().putString(key, (String) value).commit();
            return sharedPreferences;
        }
        else if (Boolean.class.isInstance(value))
        {
            sharedPreferences.edit().putBoolean(key, (Boolean) value).commit();
            return sharedPreferences;
        }
        else if (Float.class.isInstance(value))
        {
            sharedPreferences.edit().putFloat(key, (Float) value).commit();
            return sharedPreferences;
        }
        else if (Integer.class.isInstance(value))
        {
            sharedPreferences.edit().putInt(key, (Integer) value).commit();
            return sharedPreferences;
        }
        else if (Long.class.isInstance(value))
        {
            sharedPreferences.edit().putLong(key, (Long) value).commit();
            return sharedPreferences;
        }
        else if (HashSet.class.isInstance(value))
        {
            sharedPreferences.edit().putStringSet(key, (HashSet<String>) value).commit();
            return sharedPreferences;
        }

        return sharedPreferences;
    }
}
