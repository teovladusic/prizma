package com.prizma_distribucija.prizma.feature_track_location.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.prizma_distribucija.prizma.core.util.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.prizma_distribucija.prizma.core.util.Constants.REQUEST_PERMISSION_MESSAGE
import pub.devrel.easypermissions.EasyPermissions

class PermissionManagerImpl : PermissionManager {

    companion object {
        private val permissionsNeededIfSdkLessThanQ = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        @RequiresApi(Build.VERSION_CODES.Q)
        private val allPermissionsNeededIfSdkHigherThanQ = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.Q)
        private val permissionsNeededIfSdkHigherThanQ = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // in api level 29 and above you need to call foreground service permission
        // first, then background. Instead the system ignores the request.
        @RequiresApi(Build.VERSION_CODES.Q)
        private val additionalPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    }

    override fun hasPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // sdk less than Q
            // * transforms array to vararg
            EasyPermissions.hasPermissions(context, *permissionsNeededIfSdkLessThanQ)
        } else {
            // sdk higher than Q
            // * transforms array to vararg
            EasyPermissions.hasPermissions(context, *allPermissionsNeededIfSdkHigherThanQ)
        }
    }

    override fun requestPermissionsIfNeeded(context: Context) {
        if (hasPermissions(context)) return

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // sdk less than Q
            // * transforms array to vararg
            EasyPermissions.requestPermissions(
                context as Activity, REQUEST_PERMISSION_MESSAGE,
                REQUEST_CODE_LOCATION_PERMISSION,
                *permissionsNeededIfSdkLessThanQ
            )
        } else {
            // sdk higher than Q
            // * transforms array to vararg
            EasyPermissions.requestPermissions(
                context as Activity, REQUEST_PERMISSION_MESSAGE,
                REQUEST_CODE_LOCATION_PERMISSION,
                *permissionsNeededIfSdkHigherThanQ
            )
        }

        requestAdditionalPermission(context)
    }

    @SuppressLint("InlinedApi", "NewApi")
    override fun requestAdditionalPermission(context: Context) {
        if (EasyPermissions.hasPermissions(context, additionalPermission)) return

        if (!EasyPermissions.hasPermissions(context, *permissionsNeededIfSdkHigherThanQ)) {
            requestPermissionsIfNeeded(context)
            return
        }

        EasyPermissions.requestPermissions(
            context as Activity, REQUEST_PERMISSION_MESSAGE,
            REQUEST_CODE_LOCATION_PERMISSION,
            additionalPermission
        )
    }
}