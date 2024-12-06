package com.ahmed.localhostui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.ahmed.localhostui.ui.theme.LightBlue
import com.ahmed.localhostui.ui.theme.LocalhostUITheme
import com.ahmed.localhostui.ui.theme.White

class MainActivity : ComponentActivity() {
    val access = mutableStateOf(PermissionUtils.hasPermission(this))


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalhostUITheme {
                val activity = this
                when (access.value) {
                    false -> {
                        PermissionUtils.handlePermissionRequest(this, 100)
                        AlertDialog(modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp), content = {
                            Column(
                                Modifier.background(LightBlue, MaterialTheme.shapes.extraLarge),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text("Storage access is required to run this app")
                                TextButton(modifier = Modifier
                                    .border(
                                        1.dp, White, MaterialTheme.shapes.medium
                                    )
                                    .background(Black, MaterialTheme.shapes.medium),
                                    onClick = {
                                        PermissionUtils.requestPermission(
                                            activity
                                        )
                                    }) { Text("Enable", color = White) }
                            }
                        }, onDismissRequest = {})
                    }

                    true -> Toast.makeText(
                        LocalContext.current, "Access Granted", Toast.LENGTH_SHORT
                    ).show()
                }
                isBatteryOptimizationDisabled(this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LandingPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Recheck if the permission was granted after returning from settings
        if (PermissionUtils.hasPermission(this)) {
            // Permission granted, you can proceed
            access.value = true
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
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
        mutableStateOf("192.168.1.7")
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
                        "ip", text.value
                    )
                )
            },
            Modifier
                .background(Cyan, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.extraLarge)
                .width(150.dp)
        ) {
            Text(
                text = "Connect", color = Black, modifier = Modifier,
            )
        }
    }
}

fun isBatteryOptimizationDisabled(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(context.packageName))

            disableBatteryOptimization(context)
    }
}

fun disableBatteryOptimization(context: Context) {
    val packageName = context.packageName

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context, "Unable to open battery optimization settings.", Toast.LENGTH_LONG
            ).show()
        }
    } else {
        Toast.makeText(
            context,
            "Battery optimization is not applicable on this Android version.",
            Toast.LENGTH_LONG
        ).show()
    }
}