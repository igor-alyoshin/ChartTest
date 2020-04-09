package com.ai.charttest.presentation.common

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

interface BindableAdapter<T> {
    fun setData(data: T)
}

@BindingAdapter("data")
fun <T> RecyclerView.setRecyclerViewData(data: T) {
    if (adapter is BindableAdapter<*>) {
        (adapter as BindableAdapter<T>).setData(data)
    }
}

@BindingAdapter("setCustomOnEditorActionListener")
fun EditText.setCustomOnEditorActionListener(listener: TextView.OnEditorActionListener) {
    setOnEditorActionListener(listener)
}