package com.example.behindu.view;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;

import com.example.behindu.R;
import com.example.behindu.model.LastLocation;
import com.example.behindu.services.LocationService;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Common {
    private static final String KEY_REQUEST_LOCATION_UPDATES = "LocationUpdateEnable" ;

    public static String getLocationText(Location mLocation,Context ctx) {
       /* return mLocation == null ? ("Unknown")
                : mLocation.getLatitude() +
                "/" +
                mLocation.getLongitude();*/
        // LastLocation location = mLastLocation.get(position);
        String address = null;
        List<Address> addressesList;
        try {
            Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
            addressesList = gcd.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            if (addressesList != null) {
                address = addressesList.get(0).getAdminArea() + ",\n" + addressesList.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

        public static CharSequence getLocationTitle(LocationService locationService,String title) {
        return String.format(title+" %1$s",
                DateFormat.getDateInstance().format(new Date()));
    }

    public static void setRequestLocationUpdates(Context ctx, boolean value) {
        PreferenceManager.
                getDefaultSharedPreferences(ctx)
                .edit()
                .putBoolean(KEY_REQUEST_LOCATION_UPDATES,value)
                .apply();
    }

    public static boolean requestLocationUpdates(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
                .getBoolean(KEY_REQUEST_LOCATION_UPDATES,false);
    }
}
