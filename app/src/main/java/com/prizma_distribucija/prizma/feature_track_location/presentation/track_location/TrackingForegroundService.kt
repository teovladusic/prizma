package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.BuildNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackingForegroundService : LifecycleService() {

    companion object {
        lateinit var user: User
        lateinit var timeStarted: String
        lateinit var timeFinished: String
        lateinit var distance: String
        lateinit var avgSpeed: String
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

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
            buildNotificationUseCase().build()
        )
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