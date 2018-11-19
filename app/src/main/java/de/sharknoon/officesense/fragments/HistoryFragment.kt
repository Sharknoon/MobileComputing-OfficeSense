package de.sharknoon.officesense.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import de.sharknoon.officesense.R
import de.sharknoon.officesense.models.History
import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.networking.getSensorHistory


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
                    view.findViewById(it.graph) as GraphView,
                    getColor(view.context, it.graphColor),
                    getString(it.sensorName)
            )
        }

        initSwipeRefreshLayout(view)
        refreshSensorHistory(view)
    }

    private fun initGraph(graph: GraphView, colorLine: Int, title: String) {

        val series = LineGraphSeries<DataPoint>(
                arrayOf(
                        DataPoint(0.0, 1.0),
                        DataPoint(1.0, 3.0),
                        DataPoint(2.0, 2.0)
                )
        )

        //Add some color
        series.isDrawBackground = true
        series.color = colorLine
        series.backgroundColor = Color.argb(
                47,
                Color.red(colorLine),
                Color.green(colorLine),
                Color.blue(colorLine)
        )

        series.thickness = 4

        graph.addSeries(series)

        //Disable all unnecessary junk
        graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        //graph.gridLabelRenderer.isVerticalLabelsVisible = false
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        //graph.gridLabelRenderer.numVerticalLabels = 2

        graph.title = title
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
            refreshHistory(view, h, R.id.temperatureGraph) { it.temperature }
            //Todo add other values as soon as there are more values
            onFinish.invoke()
        }, { onFinish.invoke() })
    }

    private fun refreshHistory(view: View, h: History, graphID: Int, valueGetter: (History.Value) -> Double) {
        var counter = 0.0
        val graphView = view.findViewById(graphID) as GraphView
        val seriesArray: Array<DataPoint> = h.measurementValues
                .stream()
//                    .sorted { v1, v2 -> v1.id.compareTo(v2.id) }
                .map { DataPoint(counter++, valueGetter.invoke(it)) }
                .toArray { arrayOfNulls<DataPoint>(it) }

        val series = graphView.series[0]
        if (series is LineGraphSeries<*>)
            (series as LineGraphSeries<DataPoint>).resetData(seriesArray)
    }

}
