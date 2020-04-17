package com.ai.charttest.presentation.screens.chart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ai.charttest.R
import com.ai.charttest.databinding.ChartFragmentBinding
import com.ai.charttest.domain.common.Point
import com.ai.charttest.presentation.common.BaseFragment
import com.ai.charttest.presentation.utils.hideKeyboard
import com.ai.charttest.presentation.utils.shareBitmap
import com.ai.charttest.presentation.utils.snapshot
import com.ai.charttest.presentation.utils.withNotNull
import org.koin.android.viewmodel.ext.android.viewModel


class ChartFragment : BaseFragment<ChartFragmentBinding>() {

    private val viewModel: ChartViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): ChartFragmentBinding {
        return ChartFragmentBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val points: List<Point> =
            arguments?.getParcelableArrayList(ARG_POINTS) ?: emptyList()
        withNotNull(binding) {
            chart.setPoints(points.sortedBy { it.x }.map { Pair(it.x, it.y) })
            with(recyclerView) {
                layoutManager =
                    GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = PointsAdapter().apply { setData(points) }
            }
            setHasOptionsMenu(true)
            setupSupportActionBar(toolbar, hideTitle = true)
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).post {
            activity?.hideKeyboard()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chart, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save_to_file -> {
                val context = context
                val bm = binding?.chart?.snapshot()
                if (context != null && bm != null) {
                    shareBitmap(context, bm)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val MIN_ZOOM = 1f

        const val ARG_POINTS = "ARG_POINTS"
    }
}