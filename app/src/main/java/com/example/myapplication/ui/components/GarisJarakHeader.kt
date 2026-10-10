package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.sensor.GarisJarakUiState
import com.example.myapplication.sensor.ProximityState
import com.example.myapplication.sensor.SensorStatus
import com.example.myapplication.ui.theme.BrickRedPrimary
import com.example.myapplication.ui.theme.GarisJarakThemeTokens
import com.example.myapplication.ui.theme.MutedSageAccent
import com.example.myapplication.ui.theme.StatusActiveGreen
import com.example.myapplication.ui.theme.StatusWaitingAmber
import com.example.myapplication.ui.theme.TechnicalCodeStyle

/**
 * Header Identitas Desain: GARIS / JARAK — POCKET SENSOR LAB.
 * Menampilkan nama produk, kode eksperimen, dan status ringkas kedua sensor (Motion & Position).
 */
@Composable
fun GarisJarakHeader(
    uiState: GarisJarakUiState,
    modifier: Modifier = Modifier
) {
    val colors = GarisJarakThemeTokens.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Baris Identitas Lab & Status Multi-Sensor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label Eksperimen Lapangan
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.sageLight)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(colors.accentSage)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EXP-02 · MULTI-SENSOR",
                    style = TechnicalCodeStyle,
                    color = colors.accentSage
                )
            }

            // Status Sensor Ringkas (Motion & Position)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Pill Motion
                SensorStatusPill(
                    label = "MOTION",
                    statusText = if (uiState.accelerometer.status == SensorStatus.ACTIVE) "AKTIF" else "WAIT",
                    isActive = uiState.accelerometer.status == SensorStatus.ACTIVE
                )

                // Pill Position
                val (proxText, proxActive) = when (uiState.proximity.state) {
                    ProximityState.NEAR -> Pair("DEKAT", true)
                    ProximityState.FAR -> Pair("JAUH", true)
                    ProximityState.WAITING -> Pair("WAIT", false)
                    ProximityState.UNAVAILABLE -> Pair("N/A", false)
                }

                SensorStatusPill(
                    label = "POS",
                    statusText = proxText,
                    isActive = proxActive,
                    isAlert = uiState.proximity.state == ProximityState.NEAR
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Judul Utama Produk
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "GARIS / JARAK",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "POCKET SENSOR LAB",
                    style = TechnicalCodeStyle,
                    color = colors.accentBrick,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Motion & Position, made visible.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Garis Pembatas Presisi Geometris
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.border)
        )
    }
}

@Composable
private fun SensorStatusPill(
    label: String,
    statusText: String,
    isActive: Boolean,
    isAlert: Boolean = false
) {
    val colors = GarisJarakThemeTokens.colors
    val dotColor = when {
        isAlert -> BrickRedPrimary
        isActive -> StatusActiveGreen
        else -> StatusWaitingAmber
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .background(colors.surface)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "$label: $statusText",
            style = TechnicalCodeStyle,
            color = if (isAlert) BrickRedPrimary else colors.textSecondary
        )
    }
}
