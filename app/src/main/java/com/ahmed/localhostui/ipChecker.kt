package com.ahmed.localhostui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.IOException


suspend fun getFiles(ip: String = "192.168.115.2", url: String = ""): List<Item> =
    withContext(Dispatchers.IO) {
        val fileList = mutableListOf<Item>()
        val folderList = mutableListOf<Item>()
        try {
            val response =
                Jsoup.connect("http://$ip:8000/${url.replace(" ", "%20")}").timeout(135000)
                    .get()
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
            } else
                folderList + fileList
        } catch (e: IOException) {
            e.printStackTrace()
            folderList + fileList
        }
    }

