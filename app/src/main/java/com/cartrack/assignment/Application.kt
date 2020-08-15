package com.cartrack.assignment

import androidx.multidex.MultiDexApplication
import com.cartrack.assignment.di.appModule
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
            modules(appModule)
        }

        setupStetho()
    }

    private fun setupStetho() {
        Stetho.initializeWithDefaults(this)
    }
}