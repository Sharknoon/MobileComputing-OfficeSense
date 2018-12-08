package de.sharknoon.officesense.activities

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
            var counter = 1
            button.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val groupKey = "com.android.example.WORK_EMAIL"

                val newMessageNotification1 = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("Title 1")
                        .setContentText("You will not believe...")
                        .setGroup(groupKey)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."))

                        .build()

                val newMessageNotification2 = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_home_filled)
                        .setContentTitle("Title 2")
                        .setContentText("Please join us to celebrate the...")
                        .setGroup(groupKey)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."))
                        .build()

                val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Summary")
                        //set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setSmallIcon(R.drawable.ic_about_filled)
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
                    notify(counter++, newMessageNotification1)
                    notify(counter++, newMessageNotification2)
                    notify(42, summaryNotification)
                }

                true
            }
        }

    }

}

