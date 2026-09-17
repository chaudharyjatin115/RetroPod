package com.maxrave.simpmusic

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.maxrave.domain.mediaservice.handler.MediaPlayerHandler
import com.maxrave.logger.Logger
import com.maxrave.media3.di.setServiceActivitySession
import com.maxrave.simpmusic.di.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

class MainActivity : AppCompatActivity() {
    private val mediaPlayerHandler by inject<MediaPlayerHandler>()

    private var mBound = false
    private var shouldUnbind = false
    private val serviceConnection =
        object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?,
            ) {
                setServiceActivitySession(this@MainActivity, MainActivity::class.java, service)
                Logger.w("MainActivity", "onServiceConnected")
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Logger.w("MainActivity", "onServiceDisconnected")
                mBound = false
            }
        }

    override fun onStart() {
        super.onStart()
        startMusicService()
        hideSystemBars()
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onStop() {
        super.onStop()
        if (shouldUnbind) {
            unbindService(serviceConnection)
            shouldUnbind = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(
            module {
                single<AppCompatActivity> { this@MainActivity }
            },
        )
        unloadKoinModules(viewModelModule)
        loadKoinModules(viewModelModule)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
        )
        hideSystemBars()

        setContent {
            App()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }

    override fun onDestroy() {
        unloadKoinModules(viewModelModule)
        super.onDestroy()
    }

    private fun startMusicService() {
        com.maxrave.media3.di.startService(this@MainActivity, serviceConnection)
        shouldUnbind = true
    }
}
