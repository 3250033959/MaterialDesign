package org.proverbio.android.context;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 *  Centralises the app SharedPreferences access
 */
public class SharedPreferencesManager
{
    /**
     * The Shared Preferences file name
     */
    private static final String PREFS_FILE_NAME = "prefs";

    private SharedPreferencesManager()
    {
        super();
    }

    /**
     * Returns the value of the received key from Shared Preferences
     * @param key
     * @param returnType
     * @return
     */
    public static Object getPreferenceValue(Context context, String key, Class<?> returnType)
    {
        if (String.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return getSharedPreferences(context).getString(key, "");
        }
        else if (Boolean.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return getSharedPreferences(context).getBoolean(key, false);
        }
        else if (Float.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return getSharedPreferences(context).getFloat(key, 0f);
        }
        else if (Integer.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return getSharedPreferences(context).getInt(key, 0);
        }
        else if (Long.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return getSharedPreferences(context).getLong(key, 0l);
        }
        else if (HashSet.class.getSimpleName().equals(returnType.getSimpleName()))
        {
            return getSharedPreferences(context).getStringSet(key, new HashSet<String>());
        }
        else
        {
            return getSharedPreferences(context).getAll();
        }
    }

    /**
     * Removes a shared preference value from by received key
     * @param key
     * @return
     */
    public static void removePreferenceKey(Context context, String key)
    {
        getSharedPreferences(context).edit().remove(key).commit();
    }

    /**
     * Set the value of a key from the Shared Preferences
     * @param key
     * @param value
     * @return
     */
    public static void setPreferenceValue(Context context, String key, Object value)
    {
        if (String.class.isInstance(value))
        {
            getSharedPreferences(context).edit().putString(key, (String) value).commit();
        }
        else if (Boolean.class.isInstance(value))
        {
            getSharedPreferences(context).edit().putBoolean(key, (Boolean) value).commit();
        }
        else if (Float.class.isInstance(value))
        {
            getSharedPreferences(context).edit().putFloat(key, (Float) value).commit();
        }
        else if (Integer.class.isInstance(value))
        {
            getSharedPreferences(context).edit().putInt(key, (Integer) value).commit();
        }
        else if (Long.class.isInstance(value))
        {
            getSharedPreferences(context).edit().putLong(key, (Long) value).commit();
        }
        else if (HashSet.class.isInstance(value))
        {
            getSharedPreferences(context).edit().putStringSet(key, (HashSet<String>) value).commit();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context)
    {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }
}
