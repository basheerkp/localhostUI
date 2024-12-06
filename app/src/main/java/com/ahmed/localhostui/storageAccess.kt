package com.ahmed.localhostui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {
    private val MANAGE_EXTERNAL_STORAGE_REQUEST = 100
    fun hasPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android.os.Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                activity.startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MANAGE_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    fun handlePermissionRequest(context: Context, requestCode: Int) {
        if (requestCode == MANAGE_EXTERNAL_STORAGE_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (android.os.Environment.isExternalStorageManager()) Toast.makeText(
                    context,
                    "File Access Granted",
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    Toast.makeText(context, "File Access Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}