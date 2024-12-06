package com.ahmed.localhostui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
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
import com.ahmed.localhostui.ui.theme.Green
import com.ahmed.localhostui.ui.theme.LightBlue
import com.ahmed.localhostui.ui.theme.LocalhostUITheme
import com.ahmed.localhostui.ui.theme.PurpleGrey40
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilesList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalhostUITheme {
                Scaffold { innerPadding ->
                    val ip = intent.getStringExtra("ip") ?: " "
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

                    Results(Modifier.padding(innerPadding), ip, url)
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
                println(url)
                val newUrl = url.split("/")
                val oldUrl = newUrl.slice(0..newUrl.lastIndex - 2).joinToString("/") + "/"
                val intent = Intent(context, FilesList::class.java)
                intent.putExtra("url", oldUrl)
                intent.putExtra("ip", ip)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            },
            isFile = false,
            downloader = { },
        )

        Spacer(Modifier.height(3.dp))
        if (items.isEmpty()) {
            CustomRow(Modifier, "No files found ", true, onclick = { }) { }
        } else if (items.first().title == "No permission") {

            CustomRow(Modifier, "run as admin to access this folder ", false, onclick = { }) { }

        } else LazyColumn(Modifier.fillMaxHeight()) {

            items(items) { item ->

                val context = LocalContext.current

                CustomRow(Modifier, item.title, item.isFile, onclick = {
                    val intent = Intent(context, FilesList::class.java)
                    intent.putExtra("url", url + item.title)
                    intent.putExtra("ip", ip)
                    intent.putExtra("urlOld", url)
                    context.startActivity(intent)
                }, downloader = {
                    if (item.isFile) download(
                        context, "http://$ip:8000/$url${item.title}", item.title
                    )
                    else CoroutineScope(Dispatchers.IO).launch() {
                        getItems(ip, url + item.title, context, item.title)
                    }
                })

                Spacer(Modifier.height(3.dp))
            }
        }
    }
}


@Composable
fun CustomRow(
    modifier: Modifier = Modifier,
    text: String,
    isFile: Boolean = false,
    onclick: () -> Unit,
    downloader: () -> Unit
) {
    val config = LocalConfiguration.current
    val textWidth = config.screenWidthDp * 0.7f
    Row(
        modifier
            .background(PurpleGrey40, MaterialTheme.shapes.medium)
            .fillMaxWidth()
            .border(1.dp, if (isFile) LightBlue else Green, MaterialTheme.shapes.medium)
            .defaultMinSize(minHeight = 45.dp)
            .padding(start = 10.dp)
            .clickable(
                !isFile, onClick = onclick
            ),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (!isFile) text.replace("/", "") else text, Modifier.width(textWidth.dp)
        )
        if (text != "..." && text != "No files found ") IconButton(
            onClick = downloader
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Sharp.ArrowForward,
                contentDescription = null,
                Modifier.rotate(90f)
            )
        }
    }
}