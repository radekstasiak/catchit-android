package io.radev.catchit.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager
import dagger.hilt.internal.UnsafeCasts
import io.radev.catchit.R
import io.radev.catchit.activity.MainActivity
import io.radev.catchit.domain.FavouriteDepartureUpdateState
import io.radev.catchit.domain.UpdateFavouriteDeparturesAlertUseCase
import io.radev.catchit.domain.toUiModel
import io.radev.catchit.viewmodel.FavouriteDepartureAlert
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 * Created by radoslaw on 25/07/2020.
 * radev.io 2020.
 */
@AndroidEntryPoint
class FavouriteDepartureAppWidgetProvider : AppWidgetProvider() {

    private val TAG = "FavDepAppWidgetProv"

    @Inject
    lateinit var updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        //workaround as a bug in early version of Hint
        // https://stackoverflow.com/questions/62335727/hilt-injection-not-working-with-broadcastreceiver/62343517#62343517
        // https://github.com/google/dagger/issues/1918

        val injector =
            BroadcastReceiverComponentManager.generatedComponent(context) as FavouriteDepartureAppWidgetProvider_GeneratedInjector
        injector.injectFavouriteDepartureAppWidgetProvider(UnsafeCasts.unsafeCast(this))

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.favourite_departure_appwidget
            ).apply {
                setOnClickPendingIntent(R.id.refresh_btn, pendingIntent)
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        val adapter = AppWidgetAdapter(context)
    }

    private fun updateFavouriteDeparturesList() {
        GlobalScope.launch {
            val result = updateFavouriteDeparturesAlertUseCase.getFavouriteDeparturesUpdate()
            val alertList = arrayListOf<FavouriteDepartureAlert>()
            for (item in result) {
                when (item) {
                    is FavouriteDepartureUpdateState.Success -> {
                        for (departureAlert in item.list) {
                            alertList.add(departureAlert.toUiModel())
                        }
//                        alertList.addAll(
//                            updateFavouriteDeparturesAlertUseCase.filterFavouriteLines(
//                                item.data
//                            )
//                        )
                    }
                    is FavouriteDepartureUpdateState.ApiError -> Log.d(
                        TAG,
                        "api error ${item.code}"
                    )
                    FavouriteDepartureUpdateState.NetworkError -> Log.d(TAG, "network error}")
                    is FavouriteDepartureUpdateState.UnknownError -> Log.d(
                        TAG,
                        "unknown error ${item.error.message}"
                    )
                }
            }
//            _favouriteDeparturesAlertList.value = alertList.sortedBy { it.timestamp }
        }
    }

}


class AppWidgetAdapter(
    private val context: Context
) : BaseAdapter() {
    private val data: ArrayList<FavouriteDepartureAlert> = arrayListOf()
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

//    override fun getview(position: Int, view: View?, parent: ViewGroup): View {
//        val listItem =
//            view ?: LayoutInflater.from(ctx).inflate(R.layout.item_connection_item, parent, false);
//        if (listItem == null)
//
//        val item = data[position]
//        vh.atcocode.text = "${item.stopName} (${item.atcocode})"
//        vh.nextDeparture.text = String.format(
//            context.getString(R.string.departure_wait_time),
//            item.lineName,
//            item.waitTime,
//            item.direction
//        )
//
//        vh.favIv.setImageDrawable(
//            ContextCompat.getDrawable(
//                context,
//                R.drawable.baseline_favorite_24
//            )
//        )
//        vh.expectedArrival.text =
//            String.format(context.getString(R.string.expected_arrival), item.nextDeparture)
//
//
//    }

    fun setData(newData: ArrayList<FavouriteDepartureAlert>){
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.item_connection_item, parent, false)

        return rowView
    }

    override fun getItem(p0: Int): Any = data[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = data.size
}