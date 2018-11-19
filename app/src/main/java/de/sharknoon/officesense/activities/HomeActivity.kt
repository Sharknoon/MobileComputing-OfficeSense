package de.sharknoon.officesense.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import de.sharknoon.officesense.R
import de.sharknoon.officesense.fragments.HistoryFragment
import de.sharknoon.officesense.fragments.HomeFragment
import de.sharknoon.officesense.fragments.SensorsFragment
import de.sharknoon.officesense.utils.openFragment
import net.danlew.android.joda.JodaTimeAndroid


class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JodaTimeAndroid.init(this)

        setContentView(R.layout.activity_main)

        createBottomNavigationBar()

        createDrawer()

        createNotificationChannel(this)
    }

    private fun createBottomNavigationBar() {
        val bottomNavigation = findViewById<AHBottomNavigation>(R.id.bottom_navigation)

        // Create items
        val itemHome = AHBottomNavigationItem(R.string.title_home, R.drawable.ic_home, R.color.colorPrimary)
        val itemSensors = AHBottomNavigationItem(R.string.title_sensors, R.drawable.ic_sensors, R.color.colorPrimaryDark)
        val itemHistory = AHBottomNavigationItem(R.string.title_history, R.drawable.ic_history, R.color.colorAccent)

        // Add items
        bottomNavigation.addItem(itemHome)
        bottomNavigation.addItem(itemSensors)
        bottomNavigation.addItem(itemHistory)

        //Bring the fancy colors to life
        bottomNavigation.isColored = true

        //Shows only the title when it is alive
        bottomNavigation.titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE_FORCE

        //Creating Fragments
        val homeFragment = HomeFragment()
        val sensorsFragment = SensorsFragment()
        val historyFragment = HistoryFragment()

        bottomNavigation.setOnTabSelectedListener(AHBottomNavigation.OnTabSelectedListener { position, _ ->
            when (position) {
                0 -> {
                    openFragment(homeFragment)
                    return@OnTabSelectedListener true
                }
                1 -> {
                    openFragment(sensorsFragment)
                    return@OnTabSelectedListener true
                }
                2 -> {
                    openFragment(historyFragment)
                    return@OnTabSelectedListener true
                }
            }
            false
        })

        openFragment(homeFragment)
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
                    true
                }
                R.id.navigation_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
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


    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("ALARMS_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
