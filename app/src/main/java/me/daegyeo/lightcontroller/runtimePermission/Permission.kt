package me.daegyeo.movingumbrella.runtimePermission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission(val context: Context, val permission: String){
    companion object {
        fun requestPermissions(activity: Activity, permissions: Array<String>, code: Int) {
            ActivityCompat.requestPermissions(activity, permissions, code)
        }
    }

    fun isGrant(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
