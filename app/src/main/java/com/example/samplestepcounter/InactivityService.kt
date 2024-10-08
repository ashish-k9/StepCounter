package com.example.samplestepcounter

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class InactivityService : Service() {
    private lateinit var handler: Handler
    private val checkInactivity = object : Runnable {
        override fun run() {
            // Check if the user has been inactive for a certain period (e.g., 30 minutes)
            // Send notification if inactive
            // This logic needs to be implemented
            handler.postDelayed(this, 1800000) // 30 minutes
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler = Handler(Looper.getMainLooper())
        handler.post(checkInactivity)
        return START_STICKY
    }

    private fun sendNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, "activity_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setContentTitle("Time to Move!")
            .setContentText("You've been inactive for a while. Time to get moving!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onDestroy() {
        handler.removeCallbacks(checkInactivity)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
