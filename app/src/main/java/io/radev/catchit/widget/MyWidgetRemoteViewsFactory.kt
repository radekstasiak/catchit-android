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
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/*
 * Created by radoslaw on 25/07/2020.
 * radev.io 2020.
 */
const val WIDGET_TAG = "widgetTag"

//https://stackoverflow.com/questions/51580982/widget-listview-not-refreshing-when-ondatasetchanged-method-contains-async-netwo
class FavoDepRemoteViewsFactory(
    val context: Context,
    val intent: Intent?,
    private val updateFavouriteDeparturesAlertUseCase: UpdateFavouriteDeparturesAlertUseCase
) :
    RemoteViewsService.RemoteViewsFactory {
    //    private var data: ArrayList<FavouriteDepartureAlert> = arrayListOf(FavouriteDepartureAlert(atcocode = "",lineName = "",waitTime = "",nextDeparture = "test",direction = "",timestamp = 1L,stopName = ""))
    private var data: ArrayList<FavouriteDepartureAlert> = arrayListOf()

    override fun onCreate() {
//        updateFavouriteDeparturesList()
    }

    override fun getLoadingView(): RemoteViews? = null

//    override fun getLoadingView(): RemoteViews = RemoteViews(
//        context.packageName,
//        R.layout.item_connection_item
//    )

    override fun getItemId(p0: Int): Long = p0.toLong()


    override fun onDataSetChanged() {
        Log.d(WIDGET_TAG, "onDataSetChanged")
        GlobalScope.launch {
            Log.d(WIDGET_TAG, "onDataSetChanged Global launch")
            data = updateFavouriteDeparturesList()
            Log.d(WIDGET_TAG, "onDataSetChanged after")
        }

    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(position: Int): RemoteViews {
        Log.d(WIDGET_TAG, "getViewAt $position")
        Log.d(WIDGET_TAG, "data size ${data.size}")
        val item = data[position]
        val view = RemoteViews(
            context.packageName,
            R.layout.item_widget_row
        )
        val nextDeparture = String.format(
            context.getString(R.string.departure_wait_time),
            item.lineName,
            item.waitTime,
            item.direction
        )
        val expectedArrival =
            String.format(context.getString(R.string.expected_arrival), item.nextDeparture)
//        view.setTextViewText(R.id.tv_operator, item.atcocode)
//        view.setTextViewText(R.id.next_departure, nextDeparture)
        view.setTextViewText(R.id.expected_arrival, item.nextDeparture)

        return view;
    }

    override fun getCount(): Int {
        Log.d(WIDGET_TAG, "onDataSetChanged after")
        return data.size
    }

    // Technically, we have two types of views (the dark and light background views)
    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {
        data.clear()
    }

    private fun test(){

    }
    private suspend fun updateFavouriteDeparturesList(): ArrayList<FavouriteDepartureAlert> {
        val result = updateFavouriteDeparturesAlertUseCase.getFavouriteDeparturesUpdate()
        return suspendCancellableCoroutine<ArrayList<FavouriteDepartureAlert>> { cont: CancellableContinuation<ArrayList<FavouriteDepartureAlert>> ->
            val alertList = arrayListOf<FavouriteDepartureAlert>()
            for (item in result) {
                when (item) {
                    is FavouriteDepartureUpdateState.Success -> {
                        Log.d(WIDGET_TAG, "updateFavouriteDeparturesList Success")
                        for (departureAlert in item.list) {
                            alertList.add(departureAlert.toUiModel())
                        }

                    }
                    is FavouriteDepartureUpdateState.ApiError -> Log.d(WIDGET_TAG, "ApiError")
                    FavouriteDepartureUpdateState.NetworkError -> Log.d(WIDGET_TAG, "NetworkError")
                    is FavouriteDepartureUpdateState.UnknownError -> Log.d(
                        WIDGET_TAG,
                        "UnknownError"
                    )
                }
            }
            cont.resume(alertList, {})
        }

    }


//    private fun updateFavouriteDeparturesList() {
//        Log.d(WIDGET_TAG,"updateFavouriteDeparturesList")
//        GlobalScope.launch {
//            val result = updateFavouriteDeparturesAlertUseCase.getFavouriteDeparturesUpdate()
//            val alertList = arrayListOf<FavouriteDepartureAlert>()
//            for (item in result) {
//                when (item) {
//                    is FavouriteDepartureUpdateState.Success -> {
//                        Log.d(WIDGET_TAG,"updateFavouriteDeparturesList Success")
//                        for (departureAlert in item.list) {
//                            alertList.add(departureAlert.toUiModel())
//                        }
////                        alertList.addAll(
////                            updateFavouriteDeparturesAlertUseCase.filterFavouriteLines(
////                                item.data
////                            )
////                        )
//                    }
//                    is FavouriteDepartureUpdateState.ApiError -> Log.d(WIDGET_TAG,"ApiError")
//                    FavouriteDepartureUpdateState.NetworkError -> Log.d(WIDGET_TAG,"NetworkError")
//                    is FavouriteDepartureUpdateState.UnknownError -> Log.d(WIDGET_TAG,"UnknownError")
//                }
//            }
////            data.clear()
//            data =alertList
////            onDataSetChanged()
//        }
//    }

}