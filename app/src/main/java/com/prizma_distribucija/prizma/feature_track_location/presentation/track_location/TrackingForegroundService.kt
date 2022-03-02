package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.BuildNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingForegroundService : LifecycleService() {

    companion object {
        lateinit var user: User
    }

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var locationTracker: LocationTracker

    @Inject
    lateinit var timer: Timer

    @Inject
    lateinit var buildNotificationUseCase: BuildNotificationUseCase

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var notificationManager: NotificationManager

    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = buildNotificationUseCase(notificationManager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == Constants.START_SERVICE_ACTION) {
                startServiceAndTracking()
            }

            if (it.action == Constants.STOP_SERVICE_ACTION) {
                stopServiceAndTracking()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startServiceAndTracking() {
        if (permissionManager.hasPermissions(this)) {
            locationTracker.startTracking(fusedLocationProviderClient)
            timer.startCounting()
            startService()
        }
    }


    private fun startService() {
        startForeground(
            Constants.NOTIFICATION_ID,
            notificationBuilder.build()
        )
        updateTime()
    }

    private fun updateTime() = CoroutineScope(Dispatchers.Default).launch {
        while (timer.isTimerEnabled) {
            notificationBuilder.setContentText(timer.formattedTimePassed.value)
            notificationManager.notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
            delay(1000)
        }
    }

    private fun stopServiceAndTracking() {
        locationTracker.stopTracking()
        timer.stopCounting()
        killService()
    }

    private fun killService() {
        stopForeground(true)
        stopSelf()
    }
}