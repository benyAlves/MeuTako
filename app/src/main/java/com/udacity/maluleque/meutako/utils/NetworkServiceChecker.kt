package com.udacity.maluleque.meutako.utils

import android.content.Context
import android.os.AsyncTask
import android.util.Log

class NetworkServiceChecker(var context: Context, var networkNotifier: NetworkNotifier) : AsyncTask<Void?, Void?, Boolean>() {

    override fun onPreExecute() {
        super.onPreExecute()
        Log.d(TAG, "onPreExecute starting to check internet connection")
    }

    override fun doInBackground(vararg voids: Void?): Boolean {
        return NetworkUtils.hasInternetConnection(context)
    }

    override fun onPostExecute(hasActiveConnection: Boolean) {
        networkNotifier.notifyInternetConnection(hasActiveConnection)
        Log.d(TAG, "onPostExecute hasConnection: $hasActiveConnection")
    }

    companion object {
        private const val TAG = "NetworkServiceChecker"
    }


}