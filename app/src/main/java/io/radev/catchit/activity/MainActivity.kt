package io.radev.catchit.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import io.radev.catchit.viewmodel.DashboardViewModel
import io.radev.catchit.network.NotificationController
import io.radev.catchit.R
import io.radev.catchit.db.CatchItDatabase
import io.radev.catchit.experimental.LiveTimetableService
import io.radev.catchit.alarm.UpdateTimetableAlarmManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = "main_activity"

    @Inject
    lateinit var updateTimetableAlarmManager: UpdateTimetableAlarmManager

    @Inject
    lateinit var notificationController: NotificationController

    @Inject
    lateinit var database: CatchItDatabase

    private val model: DashboardViewModel by viewModels()

    //    @Inject lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationController.createNotificationChannel(this)

        fab.setOnClickListener {
            updateTimetableAlarmManager.cancelTimetableUpdates()
        }

//        BottomNavigationView.OnNavigationItemSelectedListener { item ->
//            when(item.itemId) {
//                R.id.favourites_page -> {
//                    // Respond to navigation item 1 click
//                    true
//                }
//                R.id.search_page -> {
//                    // Respond to navigation item 2 click
//                    true
//                }
//                else -> false
//            }
//        }

        setUpNavigation()
    }

    private fun setUpNavigation() {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(bottom_nav, navHostFragment.navController)
//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.favouriteListFragment,
//            R.id.locationFragment
//        ).build()
//        NavigationUI.setupWithNavController(
//            toolbar,
//            navHostFragment.navController,
//            appBarConfiguration
//        )

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
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


