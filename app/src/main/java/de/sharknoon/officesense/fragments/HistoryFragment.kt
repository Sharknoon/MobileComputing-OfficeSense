package de.sharknoon.officesense.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.sharknoon.officesense.R
import de.sharknoon.officesense.models.History
import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.models.Value
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
        }

        initSwipeRefreshLayout(view)
        refreshSensorHistory(view)
    }

    private fun initGraph(chart: LineChart, colorLine: Int, title: String) {

        val data = listOf(
                Entry(0.0F, 1.0F),
                Entry(1.0F, 3.0F),
                Entry(2.0F, 2.0F)
        )

        val dataSet = LineDataSet(data, title)

        //Add some color
        dataSet.color = colorLine
        dataSet.setCircleColor(colorLine)
        dataSet.circleHoleColor = colorLine
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

        val lineData = LineData(dataSet)

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

        chart.legend.isEnabled = false

//
//        series.isDrawBackground = true
//        series.color = colorLine
//        series.backgroundColor = Color.argb(
//                47,
//                Color.red(colorLine),
//                Color.green(colorLine),
//                Color.blue(colorLine)
//        )
//
//        series.thickness = 4
//
//        graph.addSeries(series)
//
//        //Disable all unnecessary junk
//        graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
//        //graph.gridLabelRenderer.isVerticalLabelsVisible = false
//        graph.gridLabelRenderer.isHorizontalLabelsVisible = false
//        //graph.gridLabelRenderer.numVerticalLabels = 2
//
//        graph.title = title
    }

    private fun initSwipeRefreshLayout(view: View) {
        // Lookup the swipe container view
        val swipeContainer = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout

        // Setup refreshSensorHistory listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            refreshSensorHistory(view) { swipeContainer.isRefreshing = false }
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
    }

    private fun refreshSensorHistory(view: View, onFinish: (() -> Unit) = {}) {
        getSensorHistory(view.context, { h ->
            enumValues<Sensors>().forEach {
                refreshHistory(view, h, it.graph, it.valueGetter::invoke)
            }
            onFinish.invoke()
        }, { onFinish.invoke() })
    }

    private fun refreshHistory(view: View, h: History, graphID: Int, valueGetter: (Value) -> Float) {
        val chart = view.findViewById(graphID) as LineChart
        val data = h.measurementValues
                .stream()
                //.sorted { v1, v2 -> v1.id.compareTo(v2.id) }
                .map { Entry(getXValueFromDate(it.id.toLocalTime()), valueGetter.invoke(it)) }
                .collect(Collectors.toList())
        if (data.isEmpty()) return
        val dataSet = chart.data.getDataSetByIndex(0) as DataSet<*>
        dataSet.values = data
        //chart.invalidate()
    }

    private fun getXValueFromDate(time: LocalTime) = time.millisOfDay * 1000.0F * 60

    private fun getTextFromXValue(xValue: Float): String {
        val localTime = LocalTime.fromMillisOfDay((xValue / 1000 / 60).toLong())
        val formatter = DateTimeFormat.forPattern("HH:mm")
        return localTime.toString(formatter)
    }

}
