package com.ahmed.localhostui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ahmed.localhostui.ui.theme.LocalhostUITheme
import com.ahmed.localhostui.ui.theme.PurpleGrey40

class FilesList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalhostUITheme {
                Scaffold { innerPadding ->
                    val ip = intent.getStringExtra("ip") ?: " "
                    val urlOld = intent.getStringExtra("urlOld") ?: ""
                    if (ip == " ") {
                        Toast.makeText(LocalContext.current, "No IP entered", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(
                            Intent(
                                LocalContext.current, MainActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                    }
                    val url = intent.getStringExtra("url") ?: ""

                    Results(Modifier.padding(innerPadding), ip, url, urlOld = urlOld)
                }
            }
        }
    }
}


@Composable

fun Results(
    modifier: Modifier = Modifier,
    ip: String,
    url: String = "",
    viewModel: ItemsViewModels = viewModel(),
    urlOld: String
) {
    val context = LocalContext.current

    LaunchedEffect(ip) {
        viewModel.loadItems(ip, url)
    }
    val items = viewModel.items

    Column(modifier.padding(horizontal = 5.dp)) {

        CustomRow(
            Modifier, "...",
            onclick = {
                val intent = Intent(context, FilesList::class.java)
                intent.putExtra("url", urlOld)
                intent.putExtra("ip", ip)
                if (url == "") intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            },
        )

        Spacer(Modifier.height(3.dp))
        if (items.isEmpty()) {
            CustomRow(Modifier, "No files found ", false) { }
        } else if (items.first().title == "No permission") {

            CustomRow(Modifier, "run as admin to access this folder ", false) { }

        } else LazyColumn(Modifier.fillMaxHeight()) {
            items(items.size) { item ->

                CustomRow(Modifier, items[item].title, items[item].isFile, {
                    val intent = Intent(context, FilesList::class.java)
                    intent.putExtra("url", url + items[item].title)
                    intent.putExtra("ip", ip)
                    intent.putExtra("urlOld", url)
                    context.startActivity(intent)
                })

                Spacer(Modifier.height(3.dp))
            }
        }
    }
}


@Composable
fun CustomRow(
    modifier: Modifier = Modifier, text: String, clickable: Boolean = false, onclick: () -> Unit
) {
    val config = LocalConfiguration.current
    val textWidth = config.screenWidthDp * 0.7f
    Row(
        modifier
            .background(PurpleGrey40, MaterialTheme.shapes.medium)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 45.dp)
            .padding(start = 10.dp)
            .clickable(
                !clickable, onClick = onclick
            ),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (!clickable) text.slice(0..text.length - 2) else text,
            Modifier.width(textWidth.dp)
        )
        if (text != "...") IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.AutoMirrored.Sharp.ArrowForward,
                contentDescription = null,
                Modifier.rotate(90f)
            )
        }
    }
}