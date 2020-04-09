package com.ai.charttest.domain.common


interface IFailureHandler {
    fun handleFailure(failure: Failure)
}