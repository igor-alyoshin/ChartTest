package com.ai.charttest.domain.common

import kotlinx.coroutines.*
import java.io.IOException

abstract class BaseUseCase<out Type, in Param> where Type : Any, Param : Any {

    private val job = SupervisorJob()

    abstract suspend fun run(param: Param): Either<Failure, Type>

    open operator fun invoke(
        scope: CoroutineScope,
        params: Param,
        onResult: (Either<Failure, Type>) -> Unit = {}
    ) {
        job.cancelChildren()
        scope.launch {
            val result =
                withContext(scope.coroutineContext + Dispatchers.Default + job) {
                    try {
                        retryIO {
                            run(params)
                        }
                    } catch (e: Exception) {
                        Either.Left(CommonFailure(e, params))
                    }
                }
            onResult(result)
        }
    }

    protected open suspend fun doOnStart() {}

    private suspend fun <T> retryIO(
        times: Int = REPEAT_COUNT,
        initialDelayMillis: Long = DEFAULT_INITIAL_DELAY,
        maxDelayMillis: Long = DEFAULT_MAX_DELAY,
        factor: Double = DEFAULT_FACTOR,
        block: suspend () -> T
    ): T {
        var currentDelayMillis = initialDelayMillis
        repeat(times - 1) {
            try {
                return block()
            } catch (ignored: IOException) {
            }
            delay(currentDelayMillis)
            currentDelayMillis = (currentDelayMillis * factor).toLong().coerceAtMost(maxDelayMillis)
        }
        return block()
    }

    data class CommonFailure(val e: Exception, val params: Any) : Failure.FeatureFailure()

    companion object {
        private const val REPEAT_COUNT = 5
        private const val DEFAULT_INITIAL_DELAY = 1000L
        private const val DEFAULT_MAX_DELAY = 5000L
        private const val DEFAULT_FACTOR = 2.0
    }
}