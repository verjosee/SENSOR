package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.sensor.AccelerometerState
import com.example.myapplication.sensor.ArenaObjectState
import com.example.myapplication.sensor.GarisJarakUiState
import com.example.myapplication.sensor.ProximityState
import com.example.myapplication.sensor.ProximityStateData
import com.example.myapplication.sensor.SensorController
import com.example.myapplication.sensor.SensorStatus
import com.example.myapplication.ui.GarisJarakScreen
import com.example.myapplication.ui.theme.GarisJarakTheme

/**
 * MainActivity: Titik Masuk (Entry Point) Aplikasi Android "GARIS / JARAK".
 *
 * Mengelola siklus hidup (lifecycle) perangkat keras sensor:
 * - onResume() -> Mendaftarkan listener sensor saat aplikasi tampil di layar depan.
 * - onPause()  -> Melepas listener sensor saat aplikasi dijeda/beralih untuk hemat daya baterai.
 */
class MainActivity : ComponentActivity() {

    // SensorController mengelola Accelerometer (Motion) dan Proximity (Position)
    private lateinit var sensorController: SensorController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi controller dengan context aplikasi
        sensorController = SensorController(this)

        setContent {
            // Mengamati StateFlow dari SensorController secara reaktif
            val uiState by sensorController.uiState.collectAsState()

            GarisJarakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GarisJarakScreen(
                        uiState = uiState,
                        onRecalibrate = { sensorController.recalibrate() },
                        onResetOrb = { sensorController.resetOrb() },
                        onOpenDetails = { sensorController.setDetailsVisible(true) },
                        onCloseDetails = { sensorController.setDetailsVisible(false) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * PERSYARATAN LIFECYCLE 1: onResume()
     * Daftarkan listener sensor saat aplikasi aktif di layar depan (foreground).
     */
    override fun onResume() {
        super.onResume()
        sensorController.startListening()
    }

    /**
     * PERSYARATAN LIFECYCLE 2: onPause()
     * Lepas listener sensor saat aplikasi dijeda atau beralih ke background
     * guna mencegah kebocoran memori (memory leak) dan pemborosan daya baterai.
     */
    override fun onPause() {
        super.onPause()
        sensorController.stopListening()
    }
}

/**
 * Preview Composable untuk Android Studio Tooling.
 */
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GarisJarakPreview() {
    val sampleState = GarisJarakUiState(
        accelerometer = AccelerometerState(
            rawX = -0.35f,
            rawY = 1.15f,
            rawZ = 9.75f,
            filteredX = -0.32f,
            filteredY = 1.12f,
            filteredZ = 9.78f,
            magnitude = 9.81f,
            pitch = 6.5f,
            roll = -1.9f,
            status = SensorStatus.ACTIVE
        ),
        proximity = ProximityStateData(
            distanceCm = 0.0f,
            maxRangeCm = 5.0f,
            isNear = true,
            state = ProximityState.NEAR,
            status = SensorStatus.ACTIVE
        ),
        arenaObject = ArenaObjectState(
            normalizedX = 0.15f,
            normalizedY = -0.08f,
            isNearFieldActive = true
        )
    )

    GarisJarakTheme {
        Scaffold { padding ->
            GarisJarakScreen(
                uiState = sampleState,
                onRecalibrate = {},
                onResetOrb = {},
                onOpenDetails = {},
                onCloseDetails = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}