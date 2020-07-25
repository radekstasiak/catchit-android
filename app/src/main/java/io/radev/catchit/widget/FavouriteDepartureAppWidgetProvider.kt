package io.radev.catchit.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
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

            val widgetServiceIntent = Intent(context, FavouriteDeparturesWidgetService::class.java)
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.favourite_departure_appwidget
            ).apply {
                setOnClickPendingIntent(R.id.refresh_btn, pendingIntent)
                setRemoteAdapter(R.id.fav_dep_appwidget_lv, widgetServiceIntent)
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }

}
