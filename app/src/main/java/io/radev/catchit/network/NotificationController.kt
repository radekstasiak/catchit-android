package io.radev.catchit.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import io.radev.catchit.R
import io.radev.catchit.notification.SingleBusNotificationModel
import io.radev.catchit.activity.MainActivity
import javax.inject.Inject

/*
 * Created by radoslaw on 13/06/2020.
 * radev.io 2020.
 */

class NotificationController @Inject constructor() {
    val CHANNEL_ID = "liveTimetableService_id"
    val CHANNEL_NAME = "liveTimetableService_name"
    val CHANNEL_DESCRIPTION = "channel_description"


    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun displayNotification(data: SingleBusNotificationModel, context: Context) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVisibility(VISIBILITY_PUBLIC)
            .setContentTitle("Catch It")
            .setContentText("Bus ${data.line} in ${data.waitTime} towards ${data.direction}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setStyle(NotificationCompat.BigTextStyle().bigText("Next ${data.line} in ${data.waitTime}m\ntowards ${data.direction}"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(1, builder.build())
    }

}