package com.udacity.maluleque.meutako

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val preference = findPreference<Preference>(getString(R.string.share_key))
            preference!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey " + getString(R.string.app_name) + " is an amazing app to track your money. Try it now!")
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                true
            }
            val user = findPreference<EditTextPreference>(getString(R.string.username_key))
            user!!.text = firebaseUser!!.displayName
            val currency = findPreference<EditTextPreference>(getString(R.string.currency_key))
            currency!!.text = Locale.getDefault().country
        }
    }
}