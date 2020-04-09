package com.ai.charttest.presentation.screens.chart

import androidx.recyclerview.widget.RecyclerView
import com.ai.charttest.R
import com.ai.charttest.databinding.CellItemBinding
import com.ai.charttest.presentation.utils.getString

class CellViewHolder(private val binding: CellItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(cell: Cell) {
        val format = when (cell) {
            is X -> R.string.x_format
            is Y -> R.string.y_format
        }
        binding.value.text = String.format(getString(format), cell.value)
    }
}