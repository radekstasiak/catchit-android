package io.radev.catchit.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.radev.catchit.R
import io.radev.catchit.domain.FavouriteDepartureUpdateState
import io.radev.catchit.domain.UpdateFavouriteDeparturesAlertUseCase
import io.radev.catchit.domain.toUiModel
import io.radev.catchit.viewmodel.FavouriteDepartureAlert
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*
 * Created by radoslaw on 25/07/2020.
 * radev.io 2020.
 */

class FavoDepRemoteViewsFactory(
    val context: Context,
    val intent: Intent?,
    private val updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase
) :
    RemoteViewsService.RemoteViewsFactory {
    private val data: ArrayList<FavouriteDepartureAlert> = arrayListOf()
    private val TAG = "FavoDepRemoteViewsFact"

    override fun onCreate() {
        updateFavouriteDeparturesList()
    }

    override fun getLoadingView(): RemoteViews = RemoteViews(
        context.packageName,
        R.layout.item_connection_item
    )

    override fun getItemId(p0: Int): Long = p0.toLong()


    override fun onDataSetChanged() {
        updateFavouriteDeparturesList()
    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(position: Int): RemoteViews {
        val item = data[position]
        val view = RemoteViews(
            context.packageName,
            R.layout.item_connection_item
        )
        val nextDeparture = String.format(
            context.getString(R.string.departure_wait_time),
            item.lineName,
            item.waitTime,
            item.direction
        )
        val expectedArrival =
            String.format(context.getString(R.string.expected_arrival), item.nextDeparture)
        view.setTextViewText(R.id.tv_operator, item.atcocode)
        view.setTextViewText(R.id.next_departure, nextDeparture)
        view.setTextViewText(R.id.expected_arrival, expectedArrival)

        return view;
    }

    override fun getCount(): Int = data.size

    // Technically, we have two types of views (the dark and light background views)
    override fun getViewTypeCount(): Int = 1


    override fun onDestroy() {
        data.clear()
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
            data.clear()
            data.addAll(alertList)
        }
    }

}