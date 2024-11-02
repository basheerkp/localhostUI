package com.ahmed.localhostui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ahmed.localhostui.ui.theme.Black
import com.ahmed.localhostui.ui.theme.Cyan
import com.ahmed.localhostui.ui.theme.LocalhostUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalhostUITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LandingPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LandingPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val config = LocalConfiguration.current
    val width = config.screenWidthDp.dp
    val height = config.screenHeightDp.dp

    val text = remember {
        mutableStateOf("192.168.115.2")
    }

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "ENTER IP ADDRESS \n(type ipconfig in cmd)",
            Modifier.width(width * .9f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(height * 0.02f))

        TextField(value = text.value, onValueChange = { text.value = it })

        Spacer(Modifier.height(height * 0.02f))

        TextButton(
            onClick = {
                context.startActivity(
                    Intent(context, FilesList::class.java).putExtra(
                        "ip",
                        text.value
                    )
                )
            },
            Modifier
                .background(Cyan, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.extraLarge)
                .width(150.dp)
        ) {
            Text(
                text = "Connect",color = Black, modifier = Modifier,
            )
        }
    }
}
