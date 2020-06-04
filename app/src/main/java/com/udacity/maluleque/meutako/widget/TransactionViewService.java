package com.udacity.maluleque.meutako.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.udacity.maluleque.meutako.preferences.PreferencesManager;

public class TransactionViewService extends IntentService {

    public static final String ACTION_UPDATE_VIEW_TRANSACTION =
            "action.view_transaction";
    private static final String LAUNCH_PREF = "launch-prefs";

    public TransactionViewService() {
        super("TransactionViewService");
    }

    public static void startActionUpdateViewTrsansaction(Context context) {
        Intent intent = new Intent(context, TransactionViewService.class);
        intent.setAction(ACTION_UPDATE_VIEW_TRANSACTION);
        context.startService(intent);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_VIEW_TRANSACTION.equals(action)) {
                handleActionViewTransaction();
            }
        }
    }

    private void handleActionViewTransaction() {
        PreferencesManager instance = PreferencesManager.getInstance(getSharedPreferences(LAUNCH_PREF, MODE_PRIVATE));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TransactionWidgetProvider.class));
        TransactionWidgetProvider.updateWidgets(this, appWidgetManager, appWidgetIds, instance.getTransactions());
    }

}
