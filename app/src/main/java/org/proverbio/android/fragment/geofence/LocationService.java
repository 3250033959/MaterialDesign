package org.proverbio.android.fragment.geofence;

import android.content.Context;

import org.proverbio.android.context.SharedPreferencesManager;
import org.proverbio.android.util.JsonManager;

import java.util.ArrayList;
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

    private List<ParcelableGeofence> geofencesList;

    private LocationService(Context context)
    {
        this.context = context;

        this.geofencesList = new ArrayList<>();

        ParcelableGeofence geofence = new ParcelableGeofence();
        geofence.setName("My Geofence");
        geofence.setAddress("27 Union Street, Auckland City");
        geofence.setLatitude(-36.8548985);
        geofence.setLongitude(174.7576697);
        geofence.setRadius(200);
        geofence.setId("geofenceId");

        this.geofencesList.add(geofence);
        geofence.setLatitude(-36.8548985);
        geofence.setLongitude(174.7576697);
        this.geofencesList.add(geofence);
        geofence.setLatitude(-36.851052);
        geofence.setLongitude(174.749795);
        this.geofencesList.add(geofence);
        geofence.setLatitude(-36.863242);
        geofence.setLongitude(174.737821);
        this.geofencesList.add(geofence);
    }

    public static synchronized LocationService getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new LocationService(context);
        }

        return instance;
    }

    public boolean saveGeofence(ParcelableGeofence geofence)
    {
        return true;
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

        for ( ParcelableGeofence item : getGeofencesList() )
        {
            if ( item.getId() == mapItem.getId() )
            {
                getGeofencesList().remove( item );

                if ( !removeOnly )
                {
                    getGeofencesList().add( mapItem );
                }

                found = true;
                break;
            }
        }

        if ( !found && !removeOnly )
        {
            getGeofencesList().add( mapItem );
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
        if (!getGeofencesList().isEmpty())
        {
            for ( ParcelableGeofence geofence : getGeofencesList() )
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
    public List<ParcelableGeofence> getGeofencesList()
    {
        if (geofencesList == null)
        {
            geofencesList = readGeofencesFromPrefs();
        }

        return geofencesList;
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
