package io.radev.catchit.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.CatchItApp
import io.radev.catchit.NotificationController
import io.radev.catchit.experimental.LiveTimetableService
import io.radev.catchit.R
import io.radev.catchit.db.CatchItDatabase
import io.radev.catchit.network.ApiService
import io.radev.catchit.updateTimetableAlarm.UpdateTimetableAlarmManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = "main_activity"
    @Inject lateinit var updateTimetableAlarmManager:UpdateTimetableAlarmManager
    @Inject lateinit var notificationController: NotificationController
    @Inject lateinit var database: CatchItDatabase
//    @Inject lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationController.createNotificationChannel(this)

        fab.setOnClickListener { view ->
            updateTimetableAlarmManager.cancelTimetableUpdates()
        }
        doAsync {
            //test database injection works
            Log.d(TAG,database.favouriteLineDao().getAll().size.toString())
        }

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

        updateTimetableAlarmManager.cancelTimetableUpdates()
    }
}


