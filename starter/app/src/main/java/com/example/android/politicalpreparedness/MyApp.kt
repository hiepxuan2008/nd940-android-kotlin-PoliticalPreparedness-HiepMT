package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(
                listOf(
                    databaseModule,
                    apiModule,
                    repositoryModule,
                    launchModule,
                    electionModule,
                    voterInfoModule,
                    representativeModule
                )
            )
        }
    }
}