package com.example.samplestepcounter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.samplestepcounter.utils.SharedPreferencesUtil
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepCount = 0
    private lateinit var dailyGoalInput: TextInputEditText
    private lateinit var goalStatus: TextView
    private var dailyGoal: Int = 0
    private lateinit var stepCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        checkNotificationPermission()

        stepCountText = findViewById(R.id.stepCount)
        dailyGoalInput = findViewById(R.id.dailyGoalInput)
        goalStatus = findViewById(R.id.goalStatus)

        initSensorManager()

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            resetStepCount()
        }

        dailyGoalInput.setOnEditorActionListener { _, actionId, event ->
            handleDailyGoalInput(actionId, event)
        }

        findViewById<Button>(R.id.historyButton).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            } else {
                startInactivityService()
            }
        } else {
            startInactivityService()
        }
    }

    private fun initSensorManager() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(
            sensorEventListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )
        stepCount = SharedPreferencesUtil.getSteps(this) // Load steps from history
        stepCountText.text = "Steps: $stepCount" // Display the retrieved steps
    }

    private fun handleDailyGoalInput(actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
            (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
        ) {
            val goal = dailyGoalInput.text.toString().toIntOrNull()
            if (goal != null) {
                dailyGoal = goal
                val formattedGoal = NumberFormat.getInstance().format(dailyGoal)
                goalStatus.text = "Goal: $formattedGoal steps"
                dailyGoalInput.text?.clear()
            } else {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
            return true
        }
        return false
    }

    private fun startInactivityService() {
        Intent(this, InactivityService::class.java).also { intent ->
            startService(intent)
        }
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                stepCount = event.values[0].toInt()
                stepCountText.text = "Steps: $stepCount"
                saveStepsToHistory(stepCount)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun saveStepsToHistory(steps: Int) {
        SharedPreferencesUtil.saveSteps(this, steps)
    }

    private fun resetStepCount() {
        stepCount = 0
        stepCountText.text = "Steps: $stepCount"
        SharedPreferencesUtil.saveSteps(this, stepCount) // Reset in preferences
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "activity_channel",
                "Activity Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for activity reminders"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
