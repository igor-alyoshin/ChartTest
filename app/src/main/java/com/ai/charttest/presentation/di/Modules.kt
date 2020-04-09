package com.ai.charttest.presentation.di

import com.ai.charttest.domain.GetPointsUseCase
import com.ai.charttest.presentation.common.CommonFailureHandler
import com.ai.charttest.presentation.screens.chart.ChartViewModel
import com.ai.charttest.presentation.screens.start.StartViewModel
import com.ai.charttest.domain.common.IFailureHandler
import com.squareup.moshi.Moshi
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Moshi.Builder().build()
    }
    single<IFailureHandler> {
        CommonFailureHandler()
    }
}

val viewModelModule = module {
    viewModel { StartViewModel(get(), get()) }
    viewModel { ChartViewModel() }
}

val useCaseModule = module {
    factory { GetPointsUseCase(get(), get()) }
}