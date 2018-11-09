package de.sharknoon.officesense

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries


class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGraph(
                view.findViewById(R.id.temperatureGraph) as GraphView,
                getColor(view.context, R.color.colorTemperature),
                getColor(view.context, R.color.colorTemperature2),
                getString(R.string.temperature)
        )
        initGraph(
                view.findViewById(R.id.lightGraph) as GraphView,
                getColor(view.context, R.color.colorLight),
                getColor(view.context, R.color.colorTemperature2),
                getString(R.string.light)
        )
        initGraph(
                view.findViewById(R.id.humidityGraph) as GraphView,
                getColor(view.context, R.color.colorHumidity),
                getColor(view.context, R.color.colorTemperature2),
                getString(R.string.humidity)
        )
        initGraph(
                view.findViewById(R.id.noiseGraph) as GraphView,
                getColor(view.context, R.color.colorNoise),
                getColor(view.context, R.color.colorTemperature2),
                getString(R.string.noise)
        )
    }

    private fun initGraph(graph: GraphView, colorLine: Int, colorBackground: Int, title: String) {

        val series = LineGraphSeries<DataPoint>(
                arrayOf(
                        DataPoint(0.0, 1.0),
                        DataPoint(1.0, 5.0),
                        DataPoint(2.0, 3.0)
                )
        )
        graph.addSeries(series)

        //Disable all unnecessary junk
        graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        graph.gridLabelRenderer.isVerticalLabelsVisible = false
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false

        //Add some color
        series.isDrawBackground = true
        series.color = colorLine
        series.backgroundColor = colorBackground
        series.thickness = 4

        graph.title = title
    }
}
