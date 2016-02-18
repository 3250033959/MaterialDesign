package org.proverbio.android.fragment.geofence;

import android.content.Context;

import org.proverbio.android.context.SharedPreferencesManager;
import org.proverbio.android.util.JsonManager;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 *
 * An IntentService that adds/updates/removes Geo-fences to the device
 */
public class LocationService
{
    private static final String GEO_FENCES_PREF_KEY = "geo_fences";

    private static volatile LocationService instance;

    private final Context context;

    private List<ParcelableGeofence> geofenceList;

    private LocationService(Context context)
    {
        this.context = context;
    }

    public static LocationService getInstance(Context context)
    {
        synchronized (instance)
        {
            if (instance == null)
            {
                instance = new LocationService(context);
            }
        }

        return instance;
    }

    /**
     * Saves or removes a MapItem from the List of MapItem
     *
     * @param mapItem - a map item that represents a Geofence
     * @param removeOnly - if true the method will only remove the item from the list
     */
    private void saveGeofenceToPrefs( ParcelableGeofence mapItem, boolean removeOnly )
    {
        boolean found = false;

        for ( ParcelableGeofence item : getGeofenceList() )
        {
            if ( item.getId() == mapItem.getId() )
            {
                getGeofenceList().remove( item );

                if ( !removeOnly )
                {
                    getGeofenceList().add( mapItem );
                }

                found = true;
                break;
            }
        }

        if ( !found && !removeOnly )
        {
            getGeofenceList().add( mapItem );
        }

        //TODO saveMapItemsToPrefs();
    }

    /**
     * Converts the List of MapItem to a Set of String
     * @return - a Set<String> to save in the application's SharedPreferences
     */
    private static Set<String> mapItemsListToSet( List<ParcelableGeofence> mapItems )
    {
        Set<String> mapItemsSet = new LinkedHashSet<>();

        if ( mapItems != null &&  !mapItems.isEmpty() )
        {
            for ( ParcelableGeofence mapItem : mapItems )
            {
                mapItemsSet.add( mapItem.toString() );
            }
        }

        return mapItemsSet;
    }



    /**
     * Retrieves a Geo-fence by its id
     *
     * @param geofenceId
     * @return - a parcelable geo-fence
     */
    public ParcelableGeofence findGeofenceById(String geofenceId)
    {
        if (!getGeofenceList().isEmpty())
        {
            for ( ParcelableGeofence geofence : getGeofenceList() )
            {
                if (geofence.getId().equals(geofenceId))
                {
                    return geofence;
                }
            }
        }

        return new ParcelableGeofence();
    }

    /**
     * Returns the available Geo-fences
     * @return
     */
    private List<ParcelableGeofence> getGeofenceList()
    {
        if (geofenceList == null)
        {
            geofenceList = readGeofencesFromPrefs();
        }

        return geofenceList;
    }

    /**
     * Retrieves a List of Geo-fences from the application's SharedPreferences
     * @return - the saved geofences as a List of ParcelableGeofence
     */
    private List<ParcelableGeofence> readGeofencesFromPrefs()
    {
        List<ParcelableGeofence> geofenceList = new CopyOnWriteArrayList<>();

        Set<String> geofenceSet = SharedPreferencesManager.getPreferenceValue(context, GEO_FENCES_PREF_KEY, LinkedHashSet.class);

        if (!geofenceSet.isEmpty())
        {
            for (String item : geofenceSet)
            {
                ParcelableGeofence mapItem = JsonManager.fromJSON(item, ParcelableGeofence.class);
                geofenceList.add(mapItem);
            }
        }

        return geofenceList;
    }

}
