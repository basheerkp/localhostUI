package com.ahmed.localhostui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Environment
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.random.Random

fun download(context: Context, link: String, filename: String) {

    val client = OkHttpClient()
    val request = Request.Builder().url(link).build()
    val notificationId = Random(15).nextInt()
    val channelId = "download_channel$notificationId"

    val channel = NotificationChannel(
        channelId, "Download Channel", NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = "Notification for download"
    }

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)


    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body ?: return@launch
                val inputStream: InputStream = body.byteStream()
                val file = File("/storage/emulated/0/Download/$filename")
                file.createNewFile()

                val totalSize = body.contentLength()
                var downloadedSize = 0L

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val builder = NotificationCompat.Builder(context, channelId)
                    .setContentTitle("Downloading $filename")
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setProgress(100, 0, false)
                    .setOngoing(true)
                notificationManager.notify(notificationId, builder.build())

                file.outputStream().use { output ->
                    val buffer = ByteArray(4 * 1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedSize += bytesRead

                        val progress = (downloadedSize * 100 / totalSize).toInt()
                        builder.setProgress(100, progress, false)
                            .setContentText("Speed : $bytesRead kbps")
                        notificationManager.notify(notificationId, builder.build())
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun getItems(
    ip: String = "192.168.", url: String = "", context: Context, folderName: String
) {
    val folder = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        folderName
    )
    if (!folder.exists()) {
        val success = folder.mkdir()
        if (success) {
            println("Folder '$folderName' created successfully.")
        } else {
            println("Failed to create folder '$folderName'.")
        }
    } else {
        println("Folder '$folderName' already exists.")
    }

    val fileList = mutableListOf<Item>()
    val folderList = mutableListOf<Item>()
    try {
        val response =
            Jsoup.connect("http://$ip:8000/${url.replace(" ", "%20")}").timeout(135000).get()
        val items = response.select("a")
        items.forEach { item ->
            val nameLength = item.text().length
            if (item.text()[nameLength - 1] == '/') folderList.add(Item(item.text(), false))
            else fileList.add(Item(item.text(), true))
        }
        folderList + fileList
    } catch (e: HttpStatusException) {
        if (e.message?.slice(0..9) == "HTTP error") {
            folderList.add(Item("No permission", true))
            folderList
        } else folderList + fileList
    } catch (e: IOException) {
        e.printStackTrace()
        folderList + fileList
    }
    folderList.forEach {
        getItems(ip, url + it.title, context, folderName + it.title)
    }
    fileList.forEach { file ->
        download(context, "http://$ip:8000/$url${file.title}", folderName + file.title)
    }
}