package io.radev.catchit.updateTimetableAlarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.*
import javax.inject.Inject

/*
 * Created by radek on 05/06/2020.
 * radev.io 2020.
 */

class UpdateTimetableAlarmManager @Inject constructor(@ActivityContext val context: Context) {

    private val alarmMgr: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, UpdateLiveTimetableReceiver::class.java)

    fun startTimetableUpdates(atcocode: String) {
        //for now allow alarm for only one bus/stop
        //later keeep them in the map by atcocode/bus stop combination
        cancelTimetableUpdates()
        // Set the alarm to start after 1 minute.
        alarmIntent.putExtra(UpdateLiveTimetableReceiver.ATCOCODE_DATA, atcocode)
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MINUTE, 1)
        }
        Log.d(
            UpdateLiveTimetableReceiver.TAG,
            "starting timetable update for atcocode: $atcocode"
        )
        // setRepeating() lets you specify a precise custom interval--in this case, 1 minute
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60,
            getPendingIntent()
        )
    }

    fun cancelTimetableUpdates() {
        Log.d(UpdateLiveTimetableReceiver.TAG, "cancelling timetable update")
        alarmMgr.cancel(getPendingIntent())
        getPendingIntent().cancel()
    }

    private fun getPendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
}