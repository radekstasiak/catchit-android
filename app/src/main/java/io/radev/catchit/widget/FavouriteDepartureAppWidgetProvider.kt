package io.radev.catchit.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import io.radev.catchit.R
import io.radev.catchit.activity.MainActivity

/*
 * Created by radoslaw on 25/07/2020.
 * radev.io 2020.
 */

class FavouriteDepartureAppWidgetProvider : AppWidgetProvider() {

    private val TAG = "FavDepAppWidgetProv"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            val pendingIntent2: PendingIntent = Intent(context, javaClass)
                .let { intent ->
                    PendingIntent.getBroadcast(context, 0, intent, 0)
                }

            val widgetServiceIntent = Intent(context, FavouriteDeparturesWidgetService::class.java)
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.favourite_departure_appwidget
            ).apply {
//                setOnClickPendingIntent(R.id.refresh_btn, pendingIntent2)
                setRemoteAdapter(R.id.fav_dep_appwidget_lv, widgetServiceIntent)
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.d(WIDGET_TAG, "onUpdate FavouriteDepartureAppWidgetProvider")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }

    private fun onUpdate(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.fav_dep_appwidget_lv)
        Log.d(WIDGET_TAG, "private onUpdate FavouriteDepartureAppWidgetProvider")
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    @SuppressLint("CheckResult")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d(WIDGET_TAG, "onReceive FavouriteDepartureAppWidgetProvider")
        if (context != null) {
            onUpdate(context)
        }

    }

}
