package de.sharknoon.officesense.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.sharknoon.officesense.R
import de.sharknoon.officesense.models.History
import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.networking.DateRanges
import de.sharknoon.officesense.networking.getSensorHistory
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import java.util.stream.Collectors


class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enumValues<Sensors>().forEach {
            initGraph(
                    view.findViewById(it.graph) as LineChart,
                    getColor(view.context, it.graphColor),
                    getString(it.sensorName)
            )
            initGraphButtons(view, it)
        }

        initSwipeRefreshLayout(view)
        refreshSensorHistories(view)
    }

    private fun initGraph(
            chart: LineChart,
            colorLine: Int,
            title: String,
            data: List<Entry> = listOf(
                    Entry(0F, 0F),
                    Entry(1439F, 0F)
            )) {

        val dataSet = LineDataSet(data, title)

        //Add some color
        dataSet.color = colorLine
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)

        val c1 = Color.argb(
                150,
                Color.red(colorLine),
                Color.green(colorLine),
                Color.blue(colorLine)
        )
        val c2 = Color.TRANSPARENT
        val gd = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(c1, c2))
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = gd

        val desc = Description()
        desc.text = ""
        chart.description = desc
        chart.axisRight.isEnabled = false
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setValueFormatter { value, _ -> getTextFromXValue(value) }
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawAxisLine(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.setDrawAxisLine(false)

        chart.legend.isEnabled = false

        chart.xAxis.axisMinimum = 0F

        chart.xAxis.axisMaximum = 1440F

        val lineData = LineData(dataSet)

        chart.data = lineData
        chart.invalidate()
        Log.i("chart", "${chart.xAxis.axisMaximum}")
    }

    private fun initGraphButtons(view: View, s: Sensors) {
        view
                .findViewById<RadioGroup>(s.graphTimeButtons)
                .setOnCheckedChangeListener { _, _ -> refreshSensorHistory(view, s) }
    }

    private fun initSwipeRefreshLayout(view: View) {
        // Lookup the swipe container view
        val swipeContainer = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout

        // Setup refreshSensorHistories listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            refreshSensorHistories(view) { swipeContainer.isRefreshing = false }
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    private fun refreshSensorHistories(view: View, onFinish: (() -> Unit) = {}) {
        enumValues<Sensors>().forEach {
            refreshSensorHistory(view, it, onFinish)
        }
    }

    private fun refreshSensorHistory(view: View, s: Sensors, onFinish: (() -> Unit) = {}) {
        val string = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext).getString("serverURL", "")
                ?: ""

        getSensorHistory(
                string,
                s,
                { h ->
                    redrawSensorHistory(view, h, s)
                    onFinish.invoke()
                },
                { e ->
                    onFinish.invoke()
                    Log.e("networking", "Could not get ${s.name.toLowerCase()}-data from $string/${s.urlName}Per${getDateRange(view, s).url} because of a ${e.javaClass.simpleName}")
                    Toast.makeText(view.context, "Could not get ${s.name.toLowerCase()}-data from $string/${s.urlName}Per${getDateRange(view, s).url} because of a ${e.javaClass.simpleName}", Toast.LENGTH_LONG).show()
                },
                getDateRange(view, s)
        )
    }

    private fun getDateRange(view: View, sensors: Sensors): DateRanges {
        val radioGroup = view.findViewById<RadioGroup>(sensors.graphTimeButtons)
        val checkedRadioButton = view.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
        return DateRanges.values()[radioGroup.indexOfChild(checkedRadioButton)]
    }

    private fun redrawSensorHistory(view: View, h: History, s: Sensors) {
        val data = h.measurementValues
                .stream()
                .map { Entry(getXValueFromDate(it.id.toLocalTime()), s.valueGetter.invoke(it)) }
                .filter { e -> e.y != 0F }
                .collect(Collectors.toList())

        if (data.isEmpty()) return

        initGraph(
                view.findViewById(s.graph) as LineChart,
                getColor(view.context, s.graphColor),
                getString(s.sensorName),
                data
        )

    }

    private fun getXValueFromDate(time: LocalTime) = (time.millisOfDay / 1000.0 / 60).toFloat()

    private fun getTextFromXValue(xValue: Float): String {
        val localTime = LocalTime.fromMillisOfDay((xValue * 1000 * 60).toLong())
        val formatter = DateTimeFormat.forPattern("HH:mm")
        return localTime.toString(formatter)
    }

}
