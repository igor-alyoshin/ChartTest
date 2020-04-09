package com.ai.charttest.presentation.screens.start

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ai.charttest.R
import com.ai.charttest.databinding.StartFragmentBinding
import com.ai.charttest.presentation.common.BaseFragment
import com.ai.charttest.presentation.screens.chart.ChartFragment
import com.ai.charttest.presentation.utils.showKeyboard
import org.koin.android.viewmodel.ext.android.viewModel

class StartFragment : BaseFragment<StartFragmentBinding>() {

    private val viewModel: StartViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): StartFragmentBinding {
        return StartFragmentBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.points.observe(viewLifecycleOwner, Observer { points ->
            if (points.isNotEmpty()) {
                viewModel.clearPoints()
                binding?.enterNumberField?.clearFocus()
                findNavController().navigate(
                    R.id.chartFragment, bundleOf(ChartFragment.ARG_POINTS to points)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.showKeyboard(binding?.enterNumberField)
        }, 500)
    }
}