package com.ai.charttest.presentation.screens.start

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.TextView
import androidx.lifecycle.*
import com.ai.charttest.domain.GetPointsUseCase
import com.ai.charttest.domain.common.*

class StartViewModel(
    private val getPointsUseCase: GetPointsUseCase,
    private val failureHandler: IFailureHandler
) : ViewModel(), TextView.OnEditorActionListener {

    private val _points = MutableLiveData<List<Point>>().apply { value = emptyList() }
    private val _loading = MutableLiveData<Boolean>().apply { value = false }

    val number = MutableLiveData<String>().apply { value = "" }
    val requestPointsEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().also { data ->
        data.addSource(_loading) { loading ->
            val number = number.value?.toIntOrNull()
            data.value = number != null && !loading
        }
        data.addSource(number) { numberString ->
            val number = numberString?.toIntOrNull()
            data.value = number != null && !(_loading.value ?: false)
        }
    }
    val points: LiveData<List<Point>> get() = _points

    fun requestPoints() {
        _loading.value = true
        val number = number.value?.toIntOrNull() ?: return
        getPointsUseCase.invoke(viewModelScope, GetPointsParam("1.1", number)) {
            _loading.value = false
            it.fold(failureHandler::handleFailure, ::handleSuccess)
        }
    }

    fun clearPoints() {
        _points.value = emptyList()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == IME_ACTION_DONE) {
            requestPoints()
            return true
        }
        return false
    }

    private fun handleSuccess(response: CompositePointsResponse) {
        if (response.result == 0) {
            _points.value = response.response.points ?: emptyList()
        } else {
            failureHandler.handleFailure(Failure.ApiFailure(response.result))
        }
    }
}