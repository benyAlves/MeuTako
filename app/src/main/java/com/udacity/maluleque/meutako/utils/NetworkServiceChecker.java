package com.udacity.maluleque.meutako.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkServiceChecker extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "NetworkServiceChecker";
    NetworkNotifier networkNotifier;
    Context context;

    public NetworkServiceChecker(Context context, NetworkNotifier networkNotifier) {
        this.context = context;
        this.networkNotifier = networkNotifier;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute starting to check internet connection");
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return NetworkUtils.hasInternetConnection(context);
    }

    protected void onPostExecute(Boolean hasActiveConnection) {
        networkNotifier.notifyInternetConnection(hasActiveConnection);
        Log.d(TAG, "onPostExecute hasConnection: " + hasActiveConnection);
    }
}
