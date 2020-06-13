package io.radev.catchit.updateTimetableAlarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.radev.catchit.CatchItApp
import io.radev.catchit.SingleBusNotificationModel
import io.radev.catchit.network.toSingleBusNotificationModel
import org.jetbrains.anko.doAsync

/*
 * Created by radoslaw on 05/06/2020.
 * radev.io 2020.
 */

class UpdateLiveTimetableReceiver : BroadcastReceiver() {
    companion object {
        val TAG = "updTimetableReceiver"
        val ATCOCODE_DATA = "atcocode_data"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "alarm executed")
        if (intent != null) {
            val atcocode = intent.getStringExtra(ATCOCODE_DATA)
            val request = CatchItApp.apiService.getLiveTimetable(atcocode = atcocode!!)
            doAsync {
                val response = request.execute()
                if (response.body()!!.departures != null && response.body()!!.departures!!["all"] != null && response.body()!!.departures!!.getValue("all").isNotEmpty()) {
                    val departureDetails = response.body()!!.departures!!.getValue(
                        "all"
                    )[0]
                    val msg =
                        "received data for atcocode ${atcocode}: ${response.body()!!.stopName} $departureDetails"
                    Log.d(TAG, msg)
                    CatchItApp.notificationController.displayNotification(
                        data = departureDetails.toSingleBusNotificationModel(
                            CatchItApp.dateTimeConverter
                        )
                    )
                } else {
                    val msg =
                        "no departures, received data for atcocode ${atcocode}: ${response.body()!!.stopName} ${response.body()!!.requestTime}"
                    Log.d(TAG, msg)
                    CatchItApp.notificationController.displayNotification(
                        data = SingleBusNotificationModel(
                            line = "",
                            direction = "",
                            waitTime = "",
                            error = true
                        )
                    )
                }
                val msg =
                    "received data for atcocode ${atcocode}: ${response.body()!!.stopName} ${response.body()!!.departures!!["all"]!![0]}"


            }
        }
    }

}