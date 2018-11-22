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
import android.widget.ToggleButton
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
import de.sharknoon.officesense.networking.DateRanges.*
import de.sharknoon.officesense.networking.getSensorHistory
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*
import java.util.stream.Collectors


class HistoryFragment : Fragment() {

    var currentDateRange = DAY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enumValues<Sensors>().forEach {
            initGraph(
                    view,
                    it
            )
        }

        initSwipeRefreshLayout(view)
        initDateRangeButtons(view)
        refreshSensorHistories(view)
    }

    private fun initGraph(
            view: View,
            sensor: Sensors,
            data: List<Entry> = listOf(
                    Entry(0F, 0F),
                    Entry(1439F, 0F)
            )) {

        val dataSet = LineDataSet(data, getString(sensor.sensorName))

        //Add some color
        dataSet.color = getColor(view.context, sensor.graphColor)
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)

        val c1 = Color.argb(
                150,
                Color.red(dataSet.color),
                Color.green(dataSet.color),
                Color.blue(dataSet.color)
        )
        val c2 = Color.TRANSPARENT
        val gd = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(c1, c2))
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = gd

        val desc = Description()
        desc.text = ""
        val chart = view.findViewById(sensor.graph) as LineChart
        chart.description = desc
        chart.axisRight.isEnabled = false
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setValueFormatter { value, _ -> getTextFromXValue(value) }
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawAxisLine(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.setDrawAxisLine(false)
        chart.axisLeft.setValueFormatter { value, _ -> getTextFromYValue(value, sensor) }

        chart.legend.isEnabled = false

        val lineData = LineData(dataSet)

        chart.data = lineData
        chart.invalidate()
    }

    private fun initDateRangeButtons(view: View) {
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioButtons)

        radioGroup.setOnCheckedChangeListener { rg, checkedId ->
            for (j in 0 until rg.childCount) {
                val toggleButton = rg.getChildAt(j) as ToggleButton
                toggleButton.isChecked = toggleButton.id == checkedId
            }
        }

        for (j in 0 until radioGroup.childCount) {
            val toggleButton = radioGroup.getChildAt(j) as ToggleButton
            toggleButton.setOnClickListener(this::onToggle)
        }
    }

    fun onToggle(view: View) {
        (view.parent as RadioGroup).check(view.id)
        currentDateRange = getDateRange(view)
        refreshSensorHistories(view)
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
        val url = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext).getString("serverURL", "")
                ?: ""

        getSensorHistory(
                url,
                s,
                { h ->
                    redrawSensorHistory(view, h, s)
                    onFinish.invoke()
                },
                { e ->
                    onFinish.invoke()
                    Log.e("networking", "Could not get ${s.name.toLowerCase()}-data from $url/${s.getURLName()}Per${currentDateRange.getName()} because of a ${e.javaClass.simpleName}")
                    Toast.makeText(view.context, "Could not get ${s.name.toLowerCase()}-data from $url/${s.getURLName()}Per${currentDateRange.getName()} because of a ${e.javaClass.simpleName}", Toast.LENGTH_LONG).show()
                },
                currentDateRange
        )
    }

    private fun getDateRange(view: View): DateRanges {
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioButtons)
        val checkedRadioButton = view.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
        return DateRanges.values()[radioGroup.indexOfChild(checkedRadioButton)]
    }

    private fun redrawSensorHistory(view: View, h: History, s: Sensors) {
        val data = h.measurementValues
                .stream()
                .sorted { o1, o2 -> o1.id.compareTo(o2.id) }
                .map { Entry(getXValueFromDate(it.id), s.valueGetter.invoke(it)) }
                .filter { e -> e.y != 0F }
                .collect(Collectors.toList())

        if (data.isEmpty()) return

        initGraph(
                view,
                s,
                data
        )

    }

    private fun getXValueFromDate(dateTime: LocalDateTime) = (dateTime.toEpochSecond(ZoneOffset.UTC) / 60).toFloat()


    private fun getTextFromXValue(xValue: Float): String {
        val localDateTime = LocalDateTime.ofEpochSecond((xValue * 60).toLong(), 0, ZoneOffset.UTC)
        return when (currentDateRange) {
            DAY -> localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            WEEK -> localDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            MONTH -> localDateTime.dayOfMonth.toString()
            YEAR -> localDateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    private fun getTextFromYValue(xValue: Float, sensor: Sensors) = getString(sensor.unit, xValue.toString())

}
