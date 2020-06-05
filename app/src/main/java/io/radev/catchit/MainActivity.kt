package io.radev.catchit

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


//        val updateLiveTimetableWorkRequest = PeriodicWorkRequestBuilder<UpdateTimetableWorker>(1, TimeUnit.MINUTES)
//            .addTag(UpdateTimetableWorker.TAG)
//            .setInputData(
//                workDataOf(UpdateTimetableWorker.ATCOCODE to "450010441")
//            )
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//            )
//            .build()
//
//        WorkManager.getInstance(this).enqueue(updateLiveTimetableWorkRequest)

        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, UpdateLiveTimetableReceiver::class.java)
        alarmIntent.putExtra(UpdateLiveTimetableReceiver.ATCOCODE_DATA,"450010441")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)


        // Set the alarm to start after 1 minute.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MINUTE, 1)
        }
        // setRepeating() lets you specify a precise custom interval--in this case, 1 minute
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60,
            pendingIntent
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent(this, LiveTimetableService::class.java).also { intent ->
            stopService(intent)
        }
    }
}


