package com.example.smsash

import android.Manifest
import android.content.Context
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.telephony.SmsManager
import android.widget.Toast
import androidx.compose.ui.Alignment
import android.content.pm.PackageManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smsash.ui.theme.SMSASHTheme
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            SMSASHTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SendSMS(context = LocalContext.current)
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }
}

@Composable
fun SendSMS(context: Context) {
    val message = "AMIGOS"
    val phoneNumber = "24200"
    var smsCount by remember { mutableStateOf("0") }
    val coroutineScope = rememberCoroutineScope()
    var isButtonEnabled by remember { mutableStateOf(true) }
    val isActionInProgress by remember { mutableStateOf(false) }
    // State variable to show/hide the alert dialog
    var showAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color(0xFFE8F5E9)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.animales_sin_hogar_logo),
            contentDescription = "Animales sin Hogar Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = "Gracias por colaborar con Animales sin Hogar",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = smsCount,
            onValueChange = { smsCount = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            label = { Text("Cuántos SMS querés enviar?") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Presioná para enviar")
        Button(
            onClick = {
                isButtonEnabled = false
                coroutineScope.launch(Dispatchers.IO) {
                    sendSmsMultipleTimes(phoneNumber, message, smsCount.toInt())
                }
                isButtonEnabled = true
            },
            enabled = isButtonEnabled && !isActionInProgress
        ) {
            Text(text = "Enviar ${smsCount} SMS")
        }

        if (showAlert) {
            AlertDialog(
                onDismissRequest = {
                    showAlert = false
                },
                title = {
                    Text("")
                },
                text = {
                    Text("Enviando ${smsCount} SMS")
                },
                confirmButton = {
                    Button(onClick = {
                        showAlert = false
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

suspend fun sendSmsMultipleTimes(phoneNumber: String, message: String, count: Int) {
    val smsManager = SmsManager.getDefault()
    for (i in 0 until count) {
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        delay(1500) // Delay for 1.5 second
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SMSASHTheme {
        SendSMS(context = LocalContext.current)
    }
}