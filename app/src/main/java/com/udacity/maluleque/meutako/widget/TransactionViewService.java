package com.udacity.maluleque.meutako.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udacity.maluleque.meutako.preferences.PreferencesManager;

public class TransactionViewService extends IntentService {

    public static final String ACTION_UPDATE_VIEW_TRANSACTION =
            "action.view_transaction";
    private static final String LAUNCH_PREF = "launch-prefs";
    private static final String TAG = "TransactionViewService";

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


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference widget = database.getReference("widget").child(user.getUid()).child("transactions");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransactionWidgetProvider.updateWidgets(TransactionViewService.this, appWidgetManager, appWidgetIds, dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                TransactionWidgetProvider.updateWidgets(TransactionViewService.this, appWidgetManager, appWidgetIds, instance.getTransactions());
            }
        };

        widget.addListenerForSingleValueEvent(listener);


    }

}
