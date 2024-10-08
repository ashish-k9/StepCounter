package com.example.samplestepcounter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepCount = 0
    private lateinit var dailyGoalInput: EditText
    private lateinit var goalStatus: TextView
    private var dailyGoal: Int = 0


    private lateinit var stepCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        stepCountText = findViewById(R.id.stepCount)
        dailyGoalInput = findViewById(R.id.dailyGoalInput)
        goalStatus = findViewById(R.id.goalStatus)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(
            sensorEventListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )
        findViewById<Button>(R.id.resetButton).setOnClickListener {
            resetStepCount()
        }

        dailyGoalInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                val goal = dailyGoalInput.text.toString().toIntOrNull()
                if (goal != null) {
                    dailyGoal = goal
                    goalStatus.text = "Goal: $dailyGoal steps"
                    dailyGoalInput.text.clear() // Clear input field
                } else {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        findViewById<Button>(R.id.historyButton).setOnClickListener {
            val steps = getStepsFromHistory()
            AlertDialog.Builder(this)
                .setTitle("Today's Steps")
                .setMessage("You have taken $steps steps today.")
                .setPositiveButton("OK", null)
                .show()
        }

    }

    private val sharedPreferences by lazy {
        getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
    }


    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                stepCount = event.values[0].toInt()
                stepCountText.text = "Steps: $stepCount"
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    private fun saveStepsToHistory(steps: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("steps_today", steps)
        editor.apply()
    }

    private fun getStepsFromHistory(): Int {
        return sharedPreferences.getInt("steps_today", 0)
    }

    private fun resetStepCount() {
        stepCount = 0
        stepCountText.text = "Steps: $stepCount"
    }

    override fun onPause() {
        super.onPause()
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
