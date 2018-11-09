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

        initTemperatureGraph()
    }

    private fun initTemperatureGraph() {
        val graph: GraphView = view?.findViewById(R.id.temperatureGraph) as GraphView
        val series = LineGraphSeries<DataPoint>(
                arrayOf(
                        DataPoint(0.0, 1.0),
                        DataPoint(1.0, 5.0),
                        DataPoint(2.0, 3.0)
                )
        )
        graph.addSeries(series)
        graph.title = resources.getString(R.string.temperature)

        //Disable all unnecessary junk
        graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        graph.gridLabelRenderer.isVerticalLabelsVisible = false
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false

        //Add some color
        val context = activity?.applicationContext ?: return
        series.isDrawBackground = true
        series.backgroundColor = getColor(context, R.color.colorTemperature2)
        series.color = getColor(context, R.color.colorTemperature)
        series.thickness = 4
    }
}
