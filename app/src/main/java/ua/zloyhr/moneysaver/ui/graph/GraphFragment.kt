package ua.zloyhr.moneysaver.ui.graph

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ua.zloyhr.moneysaver.R
import ua.zloyhr.moneysaver.data.TimePeriod
import ua.zloyhr.moneysaver.databinding.FragmentGraphBinding

@AndroidEntryPoint
class GraphFragment : Fragment(R.layout.fragment_graph) {
    private val viewModel: GraphViewModel by viewModels()
    private lateinit var binding: FragmentGraphBinding
    private lateinit var adapter: TimeSampleListAdapter
    private lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentGraphBinding.bind(view)
        adapter = TimeSampleListAdapter()


        preferences = view.context.getSharedPreferences("graphPref", Context.MODE_PRIVATE)
        viewModel.onLoadPreferences(preferences)


        viewModel.getItems()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.timeSamples.collect {
                adapter.submitList(it)

                val pieDataSet = PieDataSet(viewModel.getPieEntries(it), "Money statistic")
                pieDataSet.colors =
                    listOf(Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.DKGRAY)
                val labels = viewModel.getLabelOfEntries(it)
                val barDataSet = BarDataSet(viewModel.getBarEntries(it), "Money statistic")
                barDataSet.isHighlightEnabled = false
                val data = BarData(barDataSet)

                binding.barChart.apply {
                    description.isEnabled = false
                    setDrawValueAboveBar(false)
                    setDrawBarShadow(false)
                    setDrawMarkers(false)
                    setDrawGridBackground(false)

                    isScaleYEnabled = false
                    isDoubleTapToZoomEnabled = false

                    xAxis.apply {
                        setDrawGridLines(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        setDrawLabels(true)
                        setDrawAxisLine(true)
                        labelCount = 3
                        valueFormatter = IndexAxisValueFormatter(labels)
                    }

                    legend.isEnabled = false
                    setData(data)
                    invalidate()
                }
            }
        }
        binding.apply {
            rvTimeSampleList.adapter = adapter
            rvTimeSampleList.layoutManager =
                LinearLayoutManager(this@GraphFragment.requireContext())
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_graph_menu, menu)

        val sampleDay = menu.findItem(R.id.miSampleDateDay)
        val sampleWeek = menu.findItem(R.id.miSampleDateWeek)
        val sampleMonth = menu.findItem(R.id.miSampleDateMonth)
        val sampleYear = menu.findItem(R.id.miSampleDateYear)

        sampleDay.setOnMenuItemClickListener {
            viewModel.onSampleChange(TimePeriod.DAY)
            viewModel.onSavePreferences(preferences)
            true
        }

        sampleWeek.setOnMenuItemClickListener {
            viewModel.onSampleChange(TimePeriod.WEEK)
            viewModel.onSavePreferences(preferences)
            true
        }

        sampleMonth.setOnMenuItemClickListener {
            viewModel.onSampleChange(TimePeriod.MONTH)
            viewModel.onSavePreferences(preferences)
            true
        }

        sampleYear.setOnMenuItemClickListener {
            viewModel.onSampleChange(TimePeriod.YEAR)
            viewModel.onSavePreferences(preferences)
            true
        }

    }

}
