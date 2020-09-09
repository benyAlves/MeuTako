package com.bernardo.maluleque.shibaba.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.bernardo.maluleque.shibaba.MainActivity;
import com.bernardo.maluleque.shibaba.R;


public class TransactionWidgetProvider extends AppWidgetProvider {

    public TransactionWidgetProvider() {
        super();
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String transactions) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, transactions);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String transactions) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.transaction_widget);
        remoteViews.setTextViewText(R.id.textViewTransactions, transactions);

        remoteViews.setOnClickPendingIntent(R.id.transaction_content, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        TransactionViewService.startActionUpdateViewTrsansaction(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

}
