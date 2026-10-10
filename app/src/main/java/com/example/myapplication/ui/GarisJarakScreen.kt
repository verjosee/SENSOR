package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.sensor.GarisJarakUiState
import com.example.myapplication.ui.components.GarisJarakHeader
import com.example.myapplication.ui.components.GeometricArena
import com.example.myapplication.ui.components.LabActionControls
import com.example.myapplication.ui.components.MotionTelemetryPanel
import com.example.myapplication.ui.components.PositionSensorPanel
import com.example.myapplication.ui.components.SensorKnowledgeSheet
import com.example.myapplication.ui.theme.GarisJarakThemeTokens
import com.example.myapplication.ui.theme.TechnicalCodeStyle

/**
 * Layar Utama: GARIS / JARAK — POCKET SENSOR LAB.
 * Menghubungkan seluruh modul antarmuka ke dalam satu tampilan responsif dan dapat di-scroll.
 */
@Composable
fun GarisJarakScreen(
    uiState: GarisJarakUiState,
    onRecalibrate: () -> Unit,
    onResetOrb: () -> Unit,
    onOpenDetails: () -> Unit,
    onCloseDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header Identitas Garis / Jarak
            GarisJarakHeader(uiState = uiState)

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Area Eksperimen: Arena Geometris (Fokus Visual Utama)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                GeometricArena(arenaState = uiState.arenaObject)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Kontrol Operasional Nyata (Kalibrasi, Reset, Details)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                LabActionControls(
                    calibrationData = uiState.calibration,
                    onRecalibrate = onRecalibrate,
                    onResetOrb = onResetOrb,
                    onOpenDetails = onOpenDetails
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Panel Sensor 1: Accelerometer (Kategori Motion)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                MotionTelemetryPanel(data = uiState.accelerometer)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Panel Sensor 2: Proximity (Kategori Position)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                PositionSensorPanel(
                    proximityData = uiState.proximity,
                    lightFallbackData = uiState.lightFallback
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Catatan Kaki Teknis
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(colors.accentBrick)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MOTION & POSITION · LIFECYCLE-AWARE · KOTLIN & COMPOSE",
                    style = TechnicalCodeStyle,
                    color = colors.textMuted
                )
            }
        }

        // Modal Bottom Sheet Detail Sains Sensor
        SensorKnowledgeSheet(
            isVisible = uiState.isDetailsVisible,
            onDismiss = onCloseDetails
        )
    }
}
