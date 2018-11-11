package de.sharknoon.officesense.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import de.sharknoon.officesense.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsFragment = SettingsFragment()
        settingsFragment.createNotificationChannel(this)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, settingsFragment)
                .commit()

    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"
            val button = findPreference("debug")
            val context = view?.context ?: return
            button.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                val newMessageNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("Title")
                        .setContentText("Text")
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_temperature))
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("This here is the real big shi..äh Text"))

                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build()

                with(NotificationManagerCompat.from(context)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(42, newMessageNotification)
                }

                true
            }
        }

        private val CHANNEL_ID: String = "ALARMS_CHANNEL"

        fun createNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}

