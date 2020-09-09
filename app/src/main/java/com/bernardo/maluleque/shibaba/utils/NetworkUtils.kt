package com.bernardo.maluleque.shibaba.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object NetworkUtils {
    /*
     *
     * This method checks if mobile has internet connection
     *
     * @param context   Android Context to access preferences and resources
     * */
    fun hasInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: Network? = null
        netInfo =
                cm.activeNetwork
        if (netInfo == null) {
            return false
        }
        val networkCapabilities = cm.getNetworkCapabilities(netInfo)
        return (networkCapabilities != null
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }
}