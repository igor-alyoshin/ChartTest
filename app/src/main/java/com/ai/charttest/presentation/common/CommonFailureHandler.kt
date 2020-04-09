package com.ai.charttest.presentation.common

import android.util.Log
import com.ai.charttest.domain.common.BaseUseCase
import com.ai.charttest.domain.common.Failure
import com.ai.charttest.domain.common.IFailureHandler
import com.ai.charttest.presentation.utils.fromBase64
import com.ai.charttest.presentation.utils.showToast

class CommonFailureHandler : IFailureHandler {
    override fun handleFailure(failure: Failure) {
        when (failure) {
            is BaseUseCase.CommonFailure -> {
                showToast(failure.e)
                Log.e(CommonFailureHandler::class.java.simpleName, "Error", failure.e)
            }
            is Failure.ApiFailure -> {
                val builder = StringBuilder("result: ${failure.result}")
                val message = when (failure.result) {
                    -100 -> failure.message
                    else -> failure.message?.fromBase64() ?: failure.message
                }
                if (!message.isNullOrBlank()) builder.append(" message: $message")
                showToast(builder)
            }
        }
    }
}