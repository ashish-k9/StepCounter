package com.example.samplestepcounter.utils


import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtil {

    private const val PREF_NAME = "StepCounterPrefs"
    private const val KEY_STEPS_TODAY = "steps_today"
    private const val KEY_LAST_STEP_TIME = "last_step_time"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveSteps(context: Context, steps: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(KEY_STEPS_TODAY, steps)
        editor.putLong(KEY_LAST_STEP_TIME, System.currentTimeMillis())
        editor.apply()
    }

    fun getSteps(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_STEPS_TODAY, 0)
    }

    fun getLastStepTime(context: Context): Long {
        return getSharedPreferences(context).getLong(KEY_LAST_STEP_TIME, 0)
    }
}
