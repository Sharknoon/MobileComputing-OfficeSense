package de.sharknoon.officesense.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import com.jakewharton.threetenabp.AndroidThreeTen
import de.sharknoon.officesense.R
import de.sharknoon.officesense.fragments.HistoryFragment
import de.sharknoon.officesense.fragments.HomeFragment
import de.sharknoon.officesense.fragments.SensorsFragment
import de.sharknoon.officesense.service.IndoorClimateObserverService
import de.sharknoon.officesense.utils.openFragment


class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Most important things
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        Intent(this, IndoorClimateObserverService::class.java)
                .also { startService(it) }

        setContentView(R.layout.activity_main)
        createBottomNavigationBar()
        createDrawer()
        createNotificationChannel(this)
    }

    object vars {
        var firstOpened = true
    }

    private fun createBottomNavigationBar() {

        //Creating Fragments
        val homeFragment = HomeFragment()
        val sensorsFragment = SensorsFragment()
        val historyFragment = HistoryFragment()

        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        if (vars.firstOpened) {
            vars.firstOpened = false
            openFragment(homeFragment)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    openFragment(homeFragment)
                    true
                }
                R.id.action_sensors -> {
                    openFragment(sensorsFragment)
                    true
                }
                R.id.action_history -> {
                    openFragment(historyFragment)
                    true
                }
                else -> true
            }
        }

    }


    private fun createDrawer() {
        val dl = findViewById<DrawerLayout>(R.id.activity_main)
        val abdt = ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close)

        dl.addDrawerListener(abdt)
        abdt.syncState()

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val nv = findViewById<NavigationView>(R.id.hamburger_navigation)
        nv.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    dl.closeDrawers()
                    true
                }
                R.id.navigation_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                    dl.closeDrawers()
                    true
                }
                else -> false
            }
        }

        toggle = abdt
    }

    private var toggle: ActionBarDrawerToggle? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle?.onOptionsItemSelected(item) == true) true else super.onOptionsItemSelected(item)
    }


    private fun createNotificationChannel(context: Context) {
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
            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}

const val CHANNEL_ID = "ALARMS_CHANNEL"
