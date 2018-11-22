package de.sharknoon.officesense.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import de.sharknoon.officesense.R
import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.networking.getSensorValues
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
        val url = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext).getString("serverURL", "")
                ?: ""

        getSensorValues(url, { v ->
            enumValues<Sensors>().forEach {
                val textView = view.findViewById<TextView>(it.textView)
                textView.text = getString(it.unit, it.valuesGetter.invoke(v).cut(2).toString())
                Toast.makeText(view.context, "Successfully reloaded", Toast.LENGTH_SHORT).show()
                onFinish?.invoke()
            }
        }, {
            Toast.makeText(view.context, "Error reloading because of a ${it.javaClass.simpleName}", Toast.LENGTH_SHORT).show()
            onFinish?.invoke()
        })

    }

}
