package com.prizma_distribucija.prizma.core.util

import android.graphics.Color
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.collections.PolylineManager

object Constants {
    const val USERS_COLLECTION_NAME = "users"
    const val ROUTES_COLLECTION_NAME = "routes"
    const val NO_USER_FOUND_ERROR_MESSAGE = "Kod je neispravan."

    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val REQUEST_PERMISSION_MESSAGE =
        "For normal usage, application needs to use your location in background for tracking and notation of route."

    const val START_TRACKING_BUTTON_TEXT = "Start"

    const val STOP_TRACKING_BUTTON_TEXT = "Stop"

    const val LOCATION_UPDATE_INTERVAL = 3000L
    const val FASTEST_LOCATION_INTERVAL = 1000L

    const val START_SERVICE_ACTION = "StartService"
    const val STOP_SERVICE_ACTION = "StopService"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val POLYLINE_COLOR = Color.GREEN
    const val POLYLINE_WIDTH = 8f
    const val DEFAULT_MAP_ZOOM = 19.5f
}