package de.sharknoon.officesense.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import de.sharknoon.officesense.R
import de.sharknoon.officesense.models.Humidity
import de.sharknoon.officesense.models.Light
import de.sharknoon.officesense.models.Noise
import de.sharknoon.officesense.models.Temperature
import de.sharknoon.officesense.networking.getSensorValue
import de.sharknoon.officesense.utils.cut

class SensorsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSwipeRefreshLayout(view)
        refreshSensorValues(view)
    }

    private fun initSwipeRefreshLayout(view: View) {
        // Lookup the swipe container view
        val swipeContainer = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            refreshSensorValues(view) { swipeContainer.isRefreshing = false }
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    private fun refreshSensorValues(view: View, onFinish: (() -> Unit)? = { }) {
        var amountSensorsToRefresh = 1
        var successfulSensorValues = amountSensorsToRefresh
        fun onFinishCounter(success: Boolean) {
            amountSensorsToRefresh -= 1
            if (success) {
                successfulSensorValues -= 1
            }
            if (amountSensorsToRefresh <= 0) {
                onFinish?.invoke()
                if (successfulSensorValues <= 0) {
                    Toast.makeText(view.context, "Successfully reloaded", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //Sensor 1 Temperature
        getSensorValue(view.context, Temperature::class.java, { t: Temperature ->
            val textViewTemperature = view.findViewById<TextView>(R.id.textViewTemperature)
            textViewTemperature.text = getString(R.string.unit_temperature, t.temperature.cut(2).toString())
            onFinishCounter(true)
        }, { onFinishCounter(false) })

        //Sensor 2 Light
        getSensorValue(view.context, Light::class.java, { l: Light ->
            val textViewTemperature = view.findViewById<TextView>(R.id.textViewLight)
            textViewTemperature.text = getString(R.string.unit_light, l.light.toString())
            onFinishCounter(true)
        }, { onFinishCounter(false) })

        //Sensor 3 Humidity
        getSensorValue(view.context, Humidity::class.java, { h: Humidity ->
            val textViewTemperature = view.findViewById<TextView>(R.id.textViewHumidity)
            textViewTemperature.text = getString(R.string.unit_humidity, h.humidity.toString())
            onFinishCounter(true)
        }, { onFinishCounter(false) })

        //Sensor 4 Noise
        getSensorValue(view.context, Noise::class.java, { n: Noise->
            val textViewTemperature = view.findViewById<TextView>(R.id.textViewNoise)
            textViewTemperature.text = getString(R.string.unit_noise, n.noise.toString())
            onFinishCounter(true)
        }, { onFinishCounter(false) })
    }

}
