package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import android.content.Context
import com.prizma_distribucija.prizma.feature_track_location.domain.PermissionManager

class PermissionManagerFakeImpl : PermissionManager {

    companion object {
        var hasPermissions = false
        var havePermissionsBeenRequested = false
        var hasAdditionalPermissionBeenRequested = false

        fun setDefaults() {
            hasPermissions = false
            havePermissionsBeenRequested = false
        }
    }

    override fun hasPermissions(context: Context) = hasPermissions

    override fun requestPermissionsIfNeeded(context: Context) {
        if (hasPermissions == false) {
            havePermissionsBeenRequested = true
        }
    }

    override fun requestAdditionalPermission(context: Context) {
        hasAdditionalPermissionBeenRequested = true
        return
    }
}