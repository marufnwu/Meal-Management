package com.logicline.mydining.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import com.logicline.mydining.R
import com.logicline.mydining.utils.MyExtensions.shortToast


class MyDownloadManager {
    companion object{

        fun download(url:String, title:String="", context:Context){

            val filename = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url))


            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI )
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(filename)
            request.setDescription("Downloading report pdf")
            request.setAllowedOverRoaming(false)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                filename
            )
            val manager  = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
            context.shortToast("Downloading")
        }
    }
}