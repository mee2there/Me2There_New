package com.innovation.me2there;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Vijilesh on 5/16/2015.
 */
public  class LocationProvider {

    public static String    getAddressFromLocation(final double latitude, final double longitude,
                                                   final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();

            }
        } catch (IOException e) {
            Log.e("Main Activity", "Unable connect to Geocoder");
        } finally {


        }
        return result;
    }


    public static String    getCityNameFromLocation(final double latitude, final double longitude,
                                                   final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLocality()).append(",");
                sb.append(address.getCountryName());
                result = sb.toString();

            }
        } catch (IOException e) {
            Log.e("Main Activity", "Unable connect to Geocoder");
        } finally {


        }
        return result;
    }

}
