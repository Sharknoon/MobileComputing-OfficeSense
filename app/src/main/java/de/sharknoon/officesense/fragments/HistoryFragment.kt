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
import android.widget.Button
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
import de.sharknoon.officesense.networking.DateRanges.*
import de.sharknoon.officesense.networking.getSensorsHistory
import de.sharknoon.officesense.utils.cut
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.format.TextStyle
import java.util.*
import java.util.stream.Collectors


class HistoryFragment : Fragment() {

    private var currentDateRange = DAY
    private var currentDate = LocalDate.now()

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
        initCurrentDateButton(view)
        refreshSensorHistories(view)
    }

    private fun initGraph(
            view: View,
            sensor: Sensors,
            data: List<Entry> = listOf(
                    Entry(0F, 0F)
//                    Entry(1439F, 0F)
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

        //Line for referenceValue
        val referenceData = getReferenceData(sensor, getDateFromXValue(data[0].x), getDateFromXValue(data.last().x))

        val lineData = LineData(dataSet, referenceData.first(), referenceData.last())
        val chart = view.findViewById(sensor.graph) as LineChart
        chart.data = lineData

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
        chart.axisLeft.setValueFormatter { value, _ -> getTextFromYValue(value, sensor) }

        chart.legend.isEnabled = false

        chart.invalidate()
    }

    fun getReferenceData(s: Sensors, dt1: LocalDateTime, dt2: LocalDateTime): List<LineDataSet> {
        val startMinEntry = Entry(getXValueFromDate(dt1), s.minValue.toFloat())
        val endMinEntry = Entry(getXValueFromDate(dt2), s.minValue.toFloat())
        val startMaxEntry = Entry(getXValueFromDate(dt1), s.maxValue.toFloat())
        val endMaxEntry = Entry(getXValueFromDate(dt2), s.maxValue.toFloat())
        val dataSetMin = LineDataSet(listOf(startMinEntry, endMinEntry), getString(s.sensorName) + "minimum")
        val dataSetMax = LineDataSet(listOf(startMaxEntry, endMaxEntry), getString(s.sensorName) + "maximum")
        dataSetMin.color = Color.BLACK
        dataSetMax.color = Color.BLACK
        dataSetMin.setDrawCircles(false)
        dataSetMax.setDrawCircles(false)
        dataSetMin.setDrawValues(false)
        dataSetMax.setDrawValues(false)
        return listOf(dataSetMin, dataSetMax)
    }

    private fun initDateRangeButtons(view: View) {
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioButtons)

        radioGroup.setOnCheckedChangeListener { _, _ ->
            currentDateRange = getDateRange(view)
            refreshSensorHistories(view)
        }

    }

    private fun initCurrentDateButton(view: View) {
        val button = view.findViewById<Button>(R.id.buttonCurrentDate)
        button.text = currentDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.localDateConsumer = {
            currentDate = it
            button.text = currentDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            refreshSensorHistories(view)
        }
        button.setOnClickListener {
            datePickerFragment.show(fragmentManager, "datePicker")
        }
    }

    private fun initSwipeRefreshLayout(view: View) {
        // Lookup the swipe container view
        val swipeContainer = view.findViewById(R.id.swipeContainerHistory) as SwipeRefreshLayout

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
        val url = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext).getString("serverURL", "")
                ?: ""

        getSensorsHistory(
                url,
                { h ->
                    Log.i("networking", "Successfully got history-data from $url/historyPer${currentDateRange.getName()}")
                    redrawSensorsHistory(view, h)
                    onFinish.invoke()
                },
                { e ->
                    onFinish.invoke()
                    resetSensorsHistory(view)
                    Log.e("networking", "Could not get history-data: $e")
                    Toast.makeText(view.context, "Could not get history-data: $e", Toast.LENGTH_LONG).show()
                },
                currentDateRange,
                currentDate
        )
    }

    private fun getDateRange(view: View): DateRanges {
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioButtons)
        val checkedRadioButton = view.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
        return DateRanges.values()[radioGroup.indexOfChild(checkedRadioButton)]
    }

    private fun redrawSensorsHistory(view: View, h: History) {
        for (s in enumValues<Sensors>()) {
            val data = h.measurementValues
                    .stream()
                    .sorted { o1, o2 -> o1.id.compareTo(o2.id) }
                    .map { Entry(getXValueFromDate(it.id), s.historyValueGetter.invoke(it)) }
                    .collect(Collectors.toList())

            if (data.isEmpty()) continue

            initGraph(
                    view,
                    s,
                    data
            )
        }
    }

    private fun resetSensorsHistory(view: View) {
        for (s in enumValues<Sensors>()) {
            initGraph(view, s)
        }
    }

    private fun getXValueFromDate(dateTime: LocalDateTime) = (dateTime.toEpochSecond(ZoneOffset.UTC) / 60).toFloat()

    private fun getDateFromXValue(xValue: Float) = LocalDateTime.ofEpochSecond((xValue * 60).toLong(), 0, ZoneOffset.UTC)

    private fun getTextFromXValue(xValue: Float): String {
        val localDateTime = getDateFromXValue(xValue)
        return when (currentDateRange) {
            DAY -> localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            WEEK -> localDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            MONTH -> localDateTime.dayOfMonth.toString()
            YEAR -> localDateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    private fun getTextFromYValue(xValue: Float, sensor: Sensors) = getString(sensor.unit, xValue.cut(1).toString())

}
