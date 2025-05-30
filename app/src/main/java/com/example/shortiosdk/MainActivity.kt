package com.example.shortiosdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shortiosdk.ui.theme.ShortIOSDKTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.shortio.ShortIOParametersModel
import com.github.shortio.ShortioSdk
import kotlin.concurrent.thread


class MainActivity : ComponentActivity() {

    private var latestUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        latestUri = intent?.data

        enableEdgeToEdge()
        setContent {
            val uriState = remember { mutableStateOf(latestUri) }
            LaunchedEffect(Unit) {
                latestUri?.let { Log.d("DeepLink", "Initial URI: $it") }
            }

            ShortIOSDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LinkShortnerTitle()

                        uriState.value?.let { uri ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Opened from Deep Link:\n$uri")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        LinkShorteningButton()
                    }
                }
            }
            intentHandler = { uri ->
                uriState.value = uri
                Log.d("DeepLink", "New Intent URI: $uri")
            }
        }
    }

    private var intentHandler: ((Uri?) -> Unit)? = null

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data
        latestUri = uri
        intentHandler?.invoke(uri)
    }
}


@Composable
fun LinkShorteningButton() {
    Button(
        onClick = {
            thread {
                try {
                    val params = ShortIOParametersModel(
                        originalURL = "https://demodeeplinkapp.short.gy/",
                        domain = "demodeeplinkapp.short.gy"
                    )

                    val response = ShortioSdk.shortenUrl("pk_VPfQI2HDiStIVUB0", params)
                    Log.d("ShortIO", "Shortened URL: ${response?.shortURL}")
                } catch (e: Exception) {
                    Log.e("ShortIO", "Error: ${e.message}", e)
                }
            }
        }
    ) {
        Text(text = "Shorten the Link")
    }
}

@Composable
fun LinkShortnerTitle() {
    Text(
        text = "Link Shortener",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )
}
