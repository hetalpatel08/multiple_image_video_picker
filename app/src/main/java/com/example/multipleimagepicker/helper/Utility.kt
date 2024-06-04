package com.example.multipleimagepicker.helper

import android.Manifest.permission
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object Utility {
    const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    fun checkPermission(context: Context?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as AppCompatActivity?)!!,
                        permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder = AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Some permissions required")
                    alertBuilder.setMessage("To access your photo we require storage and camera permission, Please allow to continue.")
                    alertBuilder.setPositiveButton(
                        "Allow"
                    ) { _: DialogInterface?, _: Int ->
                        ActivityCompat.requestPermissions(
                            (context as Activity?)!!,
                            arrayOf(permission.READ_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.show()
                    /*   Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                 nbutton.setBackgroundColor(Color.BLACK);*/
                    val pButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    pButton.setTextColor(Color.BLACK)
                } else {
                    ActivityCompat.requestPermissions(
                        (context as AppCompatActivity?)!!, arrayOf(
                            permission.READ_EXTERNAL_STORAGE
                        ), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    fun checkStoragePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.setData(uri)
                context.startActivity(intent)
            } else {
                return true
            }
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
        return false
    }
}
