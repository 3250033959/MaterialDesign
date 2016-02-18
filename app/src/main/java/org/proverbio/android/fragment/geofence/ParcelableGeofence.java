package org.proverbio.android.fragment.geofence;

import android.os.Parcel;
import android.text.TextUtils;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import org.proverbio.android.util.JsonManager;

/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 * @since 1.0
 */
public class ParcelableGeofence implements SafeParcelable
{
    private String id;
    private String name;
    private String address;
    private long latitude;
    private long longitude;
    private float radius;

    public ParcelableGeofence()
    {
        super();
    }

    public ParcelableGeofence(String id, String name, String address, long latitude, long longitude, float radius)
    {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public ParcelableGeofence(Parcel in)
    {
        this.id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.latitude = in.readLong();
        this.longitude = in.readLong();
        this.radius = in.readFloat();
    }

    public String getId()
    {
        if (TextUtils.isEmpty(id))
        {
            id = "";
        }

        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public long getLatitude()
    {
        return latitude;
    }

    public void setLatitude(long latitude)
    {
        this.latitude = latitude;
    }

    public long getLongitude()
    {
        return longitude;
    }

    public void setLongitude(long longitude)
    {
        this.longitude = longitude;
    }

    public float getRadius()
    {
        return radius;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeLong(latitude);
        dest.writeLong(longitude);
        dest.writeFloat(radius);
    }

    @Override
    public String toString() {
        return JsonManager.toJSON(this);
    }

    public static final Creator CREATOR = new Creator()
    {
        public ParcelableGeofence createFromParcel(Parcel in)
        {
            return new ParcelableGeofence(in);
        }

        public ParcelableGeofence[] newArray(int size)
        {
            return new ParcelableGeofence[size];
        }
    };
}
