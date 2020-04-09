package com.ai.charttest.presentation

import android.app.Application
import com.ai.charttest.presentation.di.appModule
import com.ai.charttest.presentation.di.networkModule
import com.ai.charttest.presentation.di.useCaseModule
import com.ai.charttest.presentation.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(arrayListOf(appModule, networkModule, viewModelModule, useCaseModule))
        }
    }

    companion object {
        lateinit var instance: App
    }
}