package com.ai.charttest.presentation.screens.chart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ai.charttest.databinding.CellItemBinding
import com.ai.charttest.domain.common.Point
import com.ai.charttest.presentation.common.BindableAdapter
import java.util.*


class PointsAdapter : RecyclerView.Adapter<CellViewHolder>(),
    BindableAdapter<List<Point>> {

    private val items = ArrayList<Cell>()

    override fun setData(data: List<Point>) {
        items.clear()
        items.addAll(data.map { arrayListOf(X(it.x), Y(it.y)) }.flatten())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        return CellViewHolder(CellItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class Y(value: Float) : Cell(value)
class X(value: Float) : Cell(value)
sealed class Cell(val value: Float)