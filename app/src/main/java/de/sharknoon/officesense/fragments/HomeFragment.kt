package de.sharknoon.officesense.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import de.sharknoon.officesense.R
import de.sharknoon.officesense.logic.Adjustments
import de.sharknoon.officesense.logic.checkForRoomClimateAdjustments
import de.sharknoon.officesense.logic.checkForRoomClimatePoints
import de.sharknoon.officesense.logic.checkForRoomClimateState
import de.sharknoon.officesense.models.RoomStates
import de.sharknoon.officesense.networking.getSensorValues

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSwipeRefreshLayout(view)
        refreshRoomClimateState(view)
    }

    private fun changeRoomClimate(newState: RoomStates) {
        val imageView = view?.findViewById<ImageView>(R.id.smiley_image_view)
        imageView?.setImageResource(newState.icon)

        val textView = view?.findViewById<TextView>(R.id.info_text_view)
        textView?.text = newState.status
    }

    private fun changeAdjustments(adjustments: String) {
        val textView = view?.findViewById<TextView>(R.id.info2_text_view)
        textView?.text = adjustments
    }

    private fun initSwipeRefreshLayout(view: View) {
        // Lookup the swipe container view
        val swipeContainer = view.findViewById(R.id.swipeContainerHome) as SwipeRefreshLayout

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            refreshRoomClimateState(view) { swipeContainer.isRefreshing = false }
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    private fun refreshRoomClimateState(v: View, onComplete: () -> Unit = {}) {
        val url = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext).getString("serverURL", "")
                ?: ""

        getSensorValues(
                url,
                {
                    val points = checkForRoomClimatePoints(it)
                    Toast.makeText(v.context, "You scored $points Points", Toast.LENGTH_LONG).show()
                    val roomClimate = checkForRoomClimateState(points)
                    changeRoomClimate(roomClimate)

                    var s = ""
                    for (adjustment in checkForRoomClimateAdjustments(it)) {
                        val sensorName = getString(adjustment.key.sensorName)
                        val a = adjustment.value.name.toLowerCase().replace('_', ' ')
                        val formattedCurrentValue = getString(adjustment.key.unit, adjustment.key.currentValueStringGetter.invoke(it))
                        val (adjustmentVerb, formattedRecommendedValue) = when (adjustment.value) {
                            Adjustments.TOO_HIGH -> Pair("at most", getString(adjustment.key.unit, adjustment.key.maxValue))
                            Adjustments.TOO_LOW -> Pair("at least", getString(adjustment.key.unit, adjustment.key.minValue))
                        }

                        s += "The $sensorName is $a\n" +
                                "Recommended is $adjustmentVerb $formattedRecommendedValue ($formattedCurrentValue)\n"
                    }
                    changeAdjustments(s)

                    onComplete.invoke()
                },
                {
                    Toast.makeText(v.context, "Could not get room climate: $it", Toast.LENGTH_SHORT).show()
                    onComplete.invoke()
                }
        )
        onComplete.invoke()
    }

}
