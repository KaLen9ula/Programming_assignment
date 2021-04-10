package com.example.uakpicomsysio8101.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.uakpicomsysio8101.R
import com.intrusoft.scatter.ChartData
import com.intrusoft.scatter.PieChart
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class GraphicFragment : Fragment() {

    private lateinit var graph: GraphView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_graphic, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
        drawGraph()
        drawPieChart()
    }


    private fun init() {
        graph = requireView().findViewById(R.id.graph)
    }

    private fun drawPieChart() {
        val pieChart = requireView().findViewById(R.id.pie_chart) as PieChart
        val data = ArrayList<ChartData>()
        data.add(ChartData("Blue 45%", 45f, Color.WHITE, Color.BLUE))
        data.add(ChartData("Purple 5%", 5f, Color.WHITE, Color.parseColor("#8b00ff")))
        data.add(ChartData("Yellow 25%", 25f, Color.BLACK, Color.YELLOW))
        data.add(ChartData("Grey 25%", 25f, Color.WHITE, Color.GRAY))
        pieChart.setChartData(data)
    }

    private fun drawGraph() {
        val start = -Math.PI
        val end = Math.PI
        val maxPoints = 100
        val arrOfX: DoubleArray = funcX(start, end, maxPoints)
        val arrOfY = DoubleArray(maxPoints) { cosFun(arrOfX[it]) }
        val series = LineGraphSeries<DataPoint>()

        for (i in 0 until maxPoints)
            series.appendData(DataPoint(arrOfX[i], arrOfY[i]), false, arrOfX.size)

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMaxX(end)
        graph.viewport.setMinX(start)
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMaxY(2.0)
        graph.viewport.setMinY(-2.0)
        graph.addSeries(series)
    }

    private fun cosFun(x: Double) = Math.cos(x)

    private fun funcX(start: Double, stop: Double, num: Int) =
        DoubleArray(num) { start + it * ((stop - start) / (num - 1)) }
}