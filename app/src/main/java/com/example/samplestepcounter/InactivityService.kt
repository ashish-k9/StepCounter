package com.example.samplestepcounter

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class InactivityService : Service() {
    private lateinit var handler: Handler
    private var lastStepUpdateTime: Long = System.currentTimeMillis()

    private val checkInactivity = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            Log.d("InactivityService", "Checking inactivity...")

            // Check if the user has been inactive for, say, 30 minutes (1800000 milliseconds)
            if (currentTime - lastStepUpdateTime > 180000) { // 30 minutes
                sendNotification()
            }
            handler.postDelayed(this, 60000) // Check every minute
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler = Handler(Looper.getMainLooper())
        handler.post(checkInactivity)
        return START_STICKY
    }

    fun updateLastStepUpdateTime() {
        lastStepUpdateTime = System.currentTimeMillis()
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
