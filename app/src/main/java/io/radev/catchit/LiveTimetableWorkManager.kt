package io.radev.catchit

import android.content.Context
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.android.synthetic.main.fragment_second.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit

/*
 * Created by radek on 03/06/2020.
 * radev.io 2020.
 */

class UpdateTimetableWorker(appContext: Context, workerParameters: WorkerParameters): Worker(appContext, workerParameters){
    companion object{
        val TAG = "updateTimetableWorker"
        val ATCOCODE = "atcocode"
    }
    override fun doWork(): Result {
        val atcocode = inputData.getString(ATCOCODE)
        val request = CatchItApp.apiService.getLiveTimetable(atcocode = atcocode!!)
        doAsync {
            val response = request.execute()
            Log.d(TAG,"received data: ${response.body()!!.requestTime}")
        }

        return Result.success()
    }



}