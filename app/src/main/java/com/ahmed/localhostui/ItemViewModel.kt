package com.ahmed.localhostui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Item(
    val title: String, val isFile: Boolean
)

class ItemsViewModels : ViewModel() {
    private var _items = mutableStateListOf<Item>()
    val items: List<Item> = _items


    suspend fun loadItems(ip: String, url: String) {
        _items += getFiles(ip,url)
    }

    fun sortItems() {
        _items.sortBy { it.isFile }

    }
}