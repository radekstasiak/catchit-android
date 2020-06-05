package io.radev.catchit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.jetbrains.anko.doAsync

/*
 * Created by radek on 05/06/2020.
 * radev.io 2020.
 */

class UpdateLiveTimetableReceiver: BroadcastReceiver() {
    companion object{
        val TAG = "updTimetableReceiver"
        val ATCOCODE_DATA = "atcocode_data"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(UpdateLiveTimetableReceiver.TAG,"alarm executed")
        if(intent!=null){
            val atcocode = intent.getStringExtra(ATCOCODE_DATA)
            val request = CatchItApp.apiService.getLiveTimetable(atcocode = atcocode!!)
            doAsync {
                val response = request.execute()
                Log.d(UpdateLiveTimetableReceiver.TAG,"received data: ${response.body()!!.requestTime}")
            }
        }

    }

}