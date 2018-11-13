package de.sharknoon.officesense.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import de.sharknoon.officesense.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val settingsFragment = SettingsFragment()
        settingsFragment.createNotificationChannel(this)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, settingsFragment)
                .commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            if (preferenceScreen != null) {
                val count = preferenceScreen.preferenceCount
                for (i in 0 until count)
                    preferenceScreen.getPreference(i)?.isIconSpaceReserved = false
            }

            val button = findPreference("debug")
            val context = activity ?: return
            button.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                val summaryID = 0
                val groupKey = "com.android.example.WORK_EMAIL"

                val newMessageNotification1 = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("Title 1")
                        .setContentText("You will not believe...")
                        .setGroup(groupKey)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."))

                        .build()

                val newMessageNotification2 = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentTitle("Title 2")
                        .setContentText("Please join us to celebrate the...")
                        .setGroup(groupKey)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."))
                        .build()

                val summaryNotification = NotificationCompat.Builder(context, channelId)
                        .setContentTitle("Summary")
                        //set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setSmallIcon(R.drawable.ic_about)
                        //build summary info into InboxStyle template
                        .setStyle(NotificationCompat.InboxStyle()
                                .addLine("Alex Faarborg Check this out")
                                .addLine("Jeff Chang Launch Party")
                                .setBigContentTitle("2 new messages")
                                .setSummaryText("janedoe@example.com"))
                        //specify which group this notification belongs to
                        .setGroup(groupKey)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build()

                NotificationManagerCompat.from(context).apply {
                    notify(42, newMessageNotification1)
                    notify(43, newMessageNotification2)
                    notify(summaryID, summaryNotification)
                }

                true
            }
        }

        private val channelId: String = "ALARMS_CHANNEL"

        fun createNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelId, name, importance).apply {
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

