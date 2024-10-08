package com.example.samplestepcounter

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.samplestepcounter.utils.SharedPreferencesUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyTextView = findViewById(R.id.historyTextView)
        displayStepHistory()
    }

    private fun displayStepHistory() {
        val stepsToday = SharedPreferencesUtil.getSteps(this)
        val lastStepTime = SharedPreferencesUtil.getLastStepTime(this)

        val formattedDate =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(lastStepTime))
        historyTextView.text = "Steps: $stepsToday on $formattedDate"
    }
}
