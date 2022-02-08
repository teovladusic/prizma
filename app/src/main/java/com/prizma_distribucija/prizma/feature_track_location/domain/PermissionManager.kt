package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context

interface PermissionManager {

    fun requestPermissionsIfNeeded(context: Context)

    fun hasPermissions(context: Context): Boolean

    fun requestAdditionalPermission(context: Context)
}