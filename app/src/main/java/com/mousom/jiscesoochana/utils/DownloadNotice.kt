package com.mousom.jiscesoochana.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.widget.Toast


object DownloadNotice {

    fun downloadCurrentNotice(uri: Uri, activity: Activity) {
        val request = DownloadManager.Request(uri)
        val noticeNameToSave = URLUtil.guessFileName(uri.toString(), null, "application/pdf")
        request.setTitle(noticeNameToSave)
        request.setDescription("Downloading Notice...")
        val cookie = CookieManager.getInstance().getCookie(uri.toString())
        request.addRequestHeader("cookie", cookie)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, noticeNameToSave)
        val dm = activity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)

        Toast.makeText(activity.baseContext, "Downloading Notice...", Toast.LENGTH_LONG).show()

    }
}