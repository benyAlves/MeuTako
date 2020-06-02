package com.udacity.maluleque.meutako.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class NetworkUtils {
    /*
     *
     * This method checks if mobile has internet connection
     *
     * @param context   Android Context to access preferences and resources
     * */
    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network netInfo = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            netInfo = cm.getActiveNetwork();
        } else {
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }


        if (netInfo == null) {
            return false;
        }

        NetworkCapabilities networkCapabilities =
                cm.getNetworkCapabilities(netInfo);

        return networkCapabilities != null
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
