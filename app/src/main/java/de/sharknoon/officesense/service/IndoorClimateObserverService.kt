package de.sharknoon.officesense.service

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import de.sharknoon.officesense.R
import de.sharknoon.officesense.activities.CHANNEL_ID
import de.sharknoon.officesense.logic.Adjustments
import de.sharknoon.officesense.logic.checkForRoomClimate
import de.sharknoon.officesense.networking.getSensorValues

class IndoorClimateObserverService : IntentService("IndoorClimateObserverService") {

    private val groupKey = "de.sharknoon.officesense.CLIMATE_WARNINGS"

    override fun onHandleIntent(intent: Intent?) {
        val showNotifications = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("notifications", true)
        if (!showNotifications) return

        val summaryNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Abnormal sensor values")
                //set content text to support devices running API level < 24
                .setContentText("Some sensor values are not in their optimal range")
                .setSmallIcon(R.drawable.ic_logo)
                //specify which group this notification belongs to
                .setGroup(groupKey)
                //set this notification as the summary for the group
                .setGroupSummary(true)
                .build()

        val url = PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("serverURL", "")
                ?: ""
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        getSensorValues(url, { v ->
            checkForRoomClimate(v).entries
                    .stream()
                    .map {
                        val sensorName = getString(it.key.sensorName)
                        val adjustment = it.value.name.toLowerCase()
                        val formattedCurrentValue = getString(it.key.unit, it.key.currentValueStringGetter.invoke(v))
                        val (adjustmentVerb, formattedRecommendedValue) = when (it.value) {
                            Adjustments.TOO_HIGH -> Pair("at most", getString(it.key.unit, it.key.maxValue))
                            Adjustments.TOO_LOW -> Pair("at least", getString(it.key.unit, it.key.minValue))
                        }
                        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                .setSmallIcon(it.key.icon)
                                .setContentTitle("The $sensorName is $adjustment")
                                .setContentText(
                                        "Warning: The ${sensorName}sensor senses $formattedCurrentValue, recommended is $adjustmentVerb $formattedRecommendedValue"
                                )
                                .setGroup(groupKey)
                                .build()
                    }
                    .forEach {
                        notificationManager.notify(Math.random().times(10000).toInt(), it)
                    }



            notificationManager.notify(Math.random().times(10000).toInt(), summaryNotification)

        })
    }
}
