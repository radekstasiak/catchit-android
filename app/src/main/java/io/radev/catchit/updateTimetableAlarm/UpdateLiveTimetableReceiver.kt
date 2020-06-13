package io.radev.catchit.updateTimetableAlarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.radev.catchit.CatchItApp
import org.jetbrains.anko.doAsync

/*
 * Created by radoslaw on 05/06/2020.
 * radev.io 2020.
 */

class UpdateLiveTimetableReceiver: BroadcastReceiver() {
    companion object{
        val TAG = "updTimetableReceiver"
        val ATCOCODE_DATA = "atcocode_data"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"alarm executed")
        if(intent!=null){
            val atcocode = intent.getStringExtra(ATCOCODE_DATA)
            val request = CatchItApp.apiService.getLiveTimetable(atcocode = atcocode!!)
            doAsync {
                val response = request.execute()
                val msg = "received data for atcocode ${atcocode}: ${response.body()!!.stopName} ${response.body()!!.requestTime}"
                CatchItApp.notificationController.displayNotification(subject = "received data for atcocode ${atcocode}",
                snippet = msg)
                Log.d(TAG,msg)
            }
        }
    }

}