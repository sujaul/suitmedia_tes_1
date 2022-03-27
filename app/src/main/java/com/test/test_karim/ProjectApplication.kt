package com.test.test_karim

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.dk.dksmtd.di.mainModule
import com.galee.core.Constant
import com.galee.core.Core
import com.test.test_karim.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ProjectApplication : Application() {

    companion object {
        fun getDB(context: Context) : AppDatabase?{
            return AppDatabase.getInstance(context)
        }

        fun initCrashReport(){
            CaocConfig.Builder.create()
                    .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
//                    .enabled(false) //default: true
                    .showErrorDetails(true) //default: true
                    .showRestartButton(true) //default: true
                    .logErrorOnRestart(true) //default: true
                    .trackActivities(true) //default: false
                    //.errorActivity(LoginActivity::class.java)
                    .minTimeBetweenCrashesMs(2000) //default: 3000
                    .apply()
        }
    }

    override fun onCreate() {
        super.onCreate()
        getDB(this)
        initCrashReport()
        val settingPref = getSharedPreferences("setting_api.conf", Context.MODE_PRIVATE)
        if (!settingPref.contains("api")) {
            val apiAddress = Constant.BASE_URL
            settingPref.edit().putString("api", apiAddress).apply()
        }
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@ProjectApplication)
            modules(mainModule)
        }

        Core(this).initApp(BuildConfig.DEBUG)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}