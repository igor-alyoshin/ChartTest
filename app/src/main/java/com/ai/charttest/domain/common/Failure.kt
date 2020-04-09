package com.ai.charttest.domain.common

sealed class Failure {
    class ApiFailure(val result: Int, val message: String? = null) : FeatureFailure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure: Failure()
}