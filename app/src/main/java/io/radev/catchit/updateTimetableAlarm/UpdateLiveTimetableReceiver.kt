package io.radev.catchit.updateTimetableAlarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager
import dagger.hilt.internal.UnsafeCasts
import io.radev.catchit.DateTimeConverter
import io.radev.catchit.NotificationController
import io.radev.catchit.SingleBusNotificationModel
import io.radev.catchit.network.ApiService
import io.radev.catchit.network.toSingleBusNotificationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
 * Created by radoslaw on 05/06/2020.
 * radev.io 2020.
 */

@AndroidEntryPoint
class UpdateLiveTimetableReceiver : BroadcastReceiver() {
    companion object {
        val TAG = "updTimetableReceiver"
        val ATCOCODE_DATA = "atcocode_data"
    }

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var dateTimeConverter: DateTimeConverter

    @Inject
    lateinit var notificationController: NotificationController
    override fun onReceive(context: Context?, intent: Intent?) {
        //workaround as a bug in early version of Hint
        // https://stackoverflow.com/questions/62335727/hilt-injection-not-working-with-broadcastreceiver/62343517#62343517
        // https://github.com/google/dagger/issues/1918
        val injector =
            BroadcastReceiverComponentManager.generatedComponent(context) as UpdateLiveTimetableReceiver_GeneratedInjector
        injector.injectUpdateLiveTimetableReceiver(UnsafeCasts.unsafeCast(this))
        Log.d(TAG, "alarm executed")
        if (intent != null) {
            val atcocode = intent.getStringExtra(ATCOCODE_DATA)
            GlobalScope.launch(Dispatchers.IO) {

                val response = apiService.getLiveTimetable(atcocode = atcocode!!)
                if (response.departures != null && response.departures!!["all"] != null && response.departures!!.getValue(
                        "all"
                    ).isNotEmpty()
                ) {
                    val departureDetails = response.departures!!.getValue(
                        "all"
                    )[0]
                    val msg =
                        "received data for atcocode ${atcocode}: ${response.stopName} $departureDetails"
                    Log.d(TAG, msg)
                    withContext(Dispatchers.Main) {
                        notificationController.displayNotification(
                            data = departureDetails.toSingleBusNotificationModel(
                                dateTimeConverter
                            ), context = context!!
                        )
                    }

                } else {
                    val msg =
                        "no departures, received data for atcocode ${atcocode}: ${response.stopName} ${response.requestTime}"
                    Log.d(TAG, msg)
                    withContext(Dispatchers.Main) {
                        notificationController.displayNotification(
                            data = SingleBusNotificationModel(
                                line = "",
                                direction = "",
                                waitTime = "",
                                error = true
                            ), context = context!!
                        )
                    }
                }
                val msg =
                    "received data for atcocode ${atcocode}: ${response.stopName} ${response.departures!!["all"]!![0]}"


            }
        }
    }

}