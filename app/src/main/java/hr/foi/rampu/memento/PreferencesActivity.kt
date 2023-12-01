package hr.foi.rampu.memento

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

const val RESULT_LANG_CHANGED = AppCompatActivity.RESULT_FIRST_USER

class PreferencesActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        fun switchDarkMode(isDarkModeSelected: Boolean?) {
            if (isDarkModeSelected == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "preference_dark_mode" -> switchDarkMode(sharedPreferences?.getBoolean(key, false))
            "preference_language" -> notifyLanguageChangedAndClose()
        }
    }

    private fun notifyLanguageChangedAndClose() {
        setResult(RESULT_LANG_CHANGED)
        finish()
    }
}
